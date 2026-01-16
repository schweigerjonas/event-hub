package de.othr.event_hub.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import de.othr.event_hub.service.LocationCoordinates;
import de.othr.event_hub.service.LocationService;

@Service
public class LocationServiceImpl implements LocationService {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    private static final String PHOTON_URL = "https://photon.komoot.io/api/";
    private static final String USER_AGENT = "event-hub/1.0 (oth.eventhub@gmail.com)";

    @Override
    public Optional<LocationCoordinates> findCoordinates(String location) {
        String normalized = normalizeQuery(location);
        if (normalized.isBlank()) {
            return Optional.empty();
        }
        for (String query : buildCoordinateQueries(normalized)) {
            Optional<LocationCoordinates> photon = findPhotonCoordinates(query);
            if (photon.isPresent()) {
                return photon;
            }
            Optional<LocationCoordinates> nominatim = findNominatimCoordinates(query);
            if (nominatim.isPresent()) {
                return nominatim;
            }
        }
        return Optional.empty();
    }

    private Optional<LocationCoordinates> findPhotonCoordinates(String query) {
        if (query == null || query.isBlank()) {
            return Optional.empty();
        }
        String url = UriComponentsBuilder.fromUriString(PHOTON_URL)
            .queryParam("q", query)
            .queryParam("limit", 10)
            .queryParam("lang", "de")
            .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", USER_AGENT);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<?, ?> body = response.getBody();
        if (body == null) {
            return Optional.empty();
        }
        Object featuresObj = body.get("features");
        if (!(featuresObj instanceof List<?> features) || features.isEmpty()) {
            return Optional.empty();
        }
        List<String> queryTokens = tokenizePhotonQueryTokens(query);
        String cityToken = extractPhotonCityToken(queryTokens);
        LocationCoordinates fallback = null;
        LocationCoordinates germanFallback = null;
        LocationCoordinates cityMismatchFallback = null;
        for (Object featureObj : features) {
            if (!(featureObj instanceof Map<?, ?> featureMap)) {
                continue;
            }
            Object geometryObj = featureMap.get("geometry");
            if (!(geometryObj instanceof Map<?, ?> geometry)) {
                continue;
            }
            Object coordsObj = geometry.get("coordinates");
            if (!(coordsObj instanceof List<?> coords) || coords.size() < 2) {
                continue;
            }
            double lon = Double.parseDouble(coords.get(0).toString());
            double lat = Double.parseDouble(coords.get(1).toString());
            LocationCoordinates candidate = new LocationCoordinates(lat, lon);
            Object propsObj = featureMap.get("properties");
            if (propsObj instanceof Map<?, ?> props) {
                boolean isGerman = "de".equalsIgnoreCase(String.valueOf(props.get("countrycode")));
                boolean cityMatch = true;
                String candidateCity = extractPhotonCandidateCity(props);
                if (!cityToken.isBlank()) {
                    if (candidateCity == null) {
                        candidateCity = extractPhotonCandidateRegion(props);
                    }
                    if (candidateCity != null) {
                        String candidateCityNormalized = normalizeForSearch(candidateCity);
                        if (!candidateCityNormalized.contains(cityToken)) {
                            cityMatch = false;
                        }
                    }
                }
                if (cityMatch) {
                    if (germanFallback == null && isGerman) {
                        germanFallback = candidate;
                    }
                    if (!queryTokens.isEmpty()) {
                        String candidateText = buildPhotonCandidateText(props);
                        if (!candidateText.isBlank() && matchesPhotonTokens(candidateText, queryTokens)) {
                            return Optional.of(candidate);
                        }
                    }
                } else if (cityMismatchFallback == null && isGerman) {
                    cityMismatchFallback = candidate;
                }
            }
            if (fallback == null) {
                fallback = candidate;
            }
        }
        if (germanFallback != null) {
            return Optional.of(germanFallback);
        }
        if (cityMismatchFallback != null) {
            return Optional.of(cityMismatchFallback);
        }
        return Optional.ofNullable(fallback);
    }

    private Optional<LocationCoordinates> findNominatimCoordinates(String location) {
        try {
            String normalized = normalizeQuery(location);
            if (normalized.isBlank()) {
                return Optional.empty();
            }
            Optional<LocationCoordinates> direct = fetchNominatimCoordinates(buildNominatimQueryUrl(normalized));
            if (direct.isPresent()) {
                return direct;
            }
            for (String[] structured : buildNominatimStructuredQueries(normalized)) {
                Optional<LocationCoordinates> structuredResult = fetchNominatimCoordinates(
                    buildNominatimStructuredUrl(structured[0], structured[1], structured[2]));
                if (structuredResult.isPresent()) {
                    return structuredResult;
                }
            }
        } catch (Exception ex) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    private String normalizeQuery(String location) {
        if (location == null) {
            return "";
        }
        return location.replace(",", " ").replaceAll("\\s+", " ").trim();
    }

    private String buildNominatimQueryUrl(String query) {
        return UriComponentsBuilder.fromUriString(NOMINATIM_URL)
            .queryParam("format", "json")
            .queryParam("limit", "1")
            .queryParam("countrycodes", "de")
            .queryParam("q", query)
            .toUriString();
    }

    private String buildNominatimStructuredUrl(String street, String city, String houseNumber) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(NOMINATIM_URL)
            .queryParam("format", "json")
            .queryParam("limit", "1")
            .queryParam("countrycodes", "de")
            .queryParam("street", street)
            .queryParam("city", city);
        if (houseNumber != null && !houseNumber.isBlank()) {
            builder.queryParam("housenumber", houseNumber);
        }
        return builder.toUriString();
    }

    private Optional<LocationCoordinates> fetchNominatimCoordinates(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", USER_AGENT);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
        List<?> results = response.getBody();
        if (results == null || results.isEmpty()) {
            return Optional.empty();
        }
        Object first = results.get(0);
        if (first instanceof Map<?, ?> map) {
            Object latObj = map.get("lat");
            Object lonObj = map.get("lon");
            if (latObj != null && lonObj != null) {
                double lat = Double.parseDouble(latObj.toString());
                double lon = Double.parseDouble(lonObj.toString());
                return Optional.of(new LocationCoordinates(lat, lon));
            }
        }
        return Optional.empty();
    }

    private List<String[]> buildNominatimStructuredQueries(String query) {
        List<String[]> structured = new ArrayList<>();
        String[] parts = query.split("\\s+");
        if (parts.length < 2) {
            return structured;
        }
        addNominatimStructuredQuery(structured, parts, true);
        addNominatimStructuredQuery(structured, parts, false);
        return structured;
    }

    private void addNominatimStructuredQuery(List<String[]> structured, String[] parts, boolean cityLast) {
        String city = cityLast ? parts[parts.length - 1] : parts[0];
        if (containsDigit(city)) {
            return;
        }
        String[] streetParts = cityLast
            ? java.util.Arrays.copyOf(parts, parts.length - 1)
            : java.util.Arrays.copyOfRange(parts, 1, parts.length);
        if (streetParts.length == 0) {
            return;
        }
        String street = String.join(" ", streetParts).trim();
        if (street.isBlank()) {
            return;
        }
        String houseNumber = extractNominatimHouseNumber(streetParts);
        if (houseNumber != null) {
            String streetName = String.join(" ", java.util.Arrays.copyOf(streetParts, streetParts.length - 1)).trim();
            if (!streetName.isBlank()) {
                structured.add(new String[]{streetName, city, houseNumber});
                structured.add(new String[]{streetName, city, ""});
            }
        }
        structured.add(new String[]{street, city, ""});
    }

    private String extractNominatimHouseNumber(String[] streetParts) {
        if (streetParts.length == 0) {
            return null;
        }
        String last = streetParts[streetParts.length - 1];
        if (last.matches("^[0-9]+[a-zA-Z]?(?:-[0-9]+[a-zA-Z]?)?$")) {
            return last;
        }
        return null;
    }

    private List<String> buildCoordinateQueries(String query) {
        Set<String> queries = new LinkedHashSet<>();
        addQueryVariants(queries, query);
        String withoutCityToken = removeTrailingCityToken(query);
        if (!withoutCityToken.equalsIgnoreCase(query)) {
            addQueryVariants(queries, withoutCityToken);
        }
        String lastToFront = rotateLastTokenToFront(query);
        if (!lastToFront.equalsIgnoreCase(query)) {
            addQueryVariants(queries, lastToFront);
        }
        String swapped = swapQuery(query);
        if (!swapped.equalsIgnoreCase(query)) {
            addQueryVariants(queries, swapped);
        }
        queries.add(query + " deutschland");
        return new ArrayList<>(queries);
    }

    private void addQueryVariants(Set<String> queries, String query) {
        queries.add(query);
        queries.addAll(buildStreetVariants(query));
        queries.addAll(buildCompoundStreetVariants(query));
        String asciiVariant = normalizeForSearch(query);
        if (!asciiVariant.equalsIgnoreCase(query)) {
            queries.add(asciiVariant);
            queries.addAll(buildStreetVariants(asciiVariant));
            queries.addAll(buildCompoundStreetVariants(asciiVariant));
        }
        for (String commaVariant : buildCommaVariants(query)) {
            queries.add(commaVariant);
        }
        if (!asciiVariant.equalsIgnoreCase(query)) {
            for (String commaVariant : buildCommaVariants(asciiVariant)) {
                queries.add(commaVariant);
            }
        }
    }

    private Set<String> buildStreetVariants(String query) {
        Set<String> variants = new LinkedHashSet<>();
        String strasse = query.replaceAll("(?i)stra\u00dfe", "strasse");
        String strasseToEszett = query.replaceAll("(?i)strasse", "stra\u00dfe");
        String abbrev = query.replaceAll("(?i)\\bstr\\.?\\b", "stra\u00dfe");
        variants.add(strasse);
        variants.add(strasseToEszett);
        variants.add(abbrev);
        variants.remove(query);
        variants.removeIf(String::isBlank);
        return variants;
    }

    private Set<String> buildCompoundStreetVariants(String query) {
        Set<String> variants = new LinkedHashSet<>();
        String[] parts = query.trim().split("\\s+");
        if (parts.length < 2) {
            return variants;
        }
        for (int i = 1; i < parts.length; i++) {
            String token = parts[i];
            if (!isStreetToken(token)) {
                continue;
            }
            String base = parts[i - 1];
            if (base.isBlank()) {
                continue;
            }
            String mergedEszett = base + "straße";
            String mergedAscii = base + "strasse";
            String mergedStr = base + normalizeStreetToken(token);
            variants.add(joinWithReplacement(parts, i - 1, mergedStr, i));
            variants.add(joinWithReplacement(parts, i - 1, mergedEszett, i));
            variants.add(joinWithReplacement(parts, i - 1, mergedAscii, i));
        }
        for (int i = 0; i < parts.length; i++) {
            String token = parts[i];
            List<String> split = splitStreetToken(token);
            if (split.isEmpty()) {
                continue;
            }
            List<String> replaced = new ArrayList<>();
            for (int j = 0; j < parts.length; j++) {
                if (j == i) {
                    replaced.addAll(split);
                } else {
                    replaced.add(parts[j]);
                }
            }
            String variant = String.join(" ", replaced).trim();
            if (!variant.isBlank()) {
                variants.add(variant);
            }
        }
        variants.remove(query);
        variants.removeIf(String::isBlank);
        return variants;
    }

    private String joinWithReplacement(String[] parts, int keepIndex, String merged, int dropIndex) {
        List<String> replaced = new ArrayList<>();
        for (int i = 0; i < parts.length; i++) {
            if (i == keepIndex) {
                replaced.add(merged);
                continue;
            }
            if (i == dropIndex) {
                continue;
            }
            replaced.add(parts[i]);
        }
        return String.join(" ", replaced).trim();
    }

    private boolean isStreetToken(String token) {
        String normalized = token.toLowerCase();
        return "straße".equals(normalized)
            || "strasse".equals(normalized)
            || "str.".equals(normalized)
            || "str".equals(normalized);
    }

    private String normalizeStreetToken(String token) {
        String normalized = token.toLowerCase();
        if ("str.".equals(normalized) || "str".equals(normalized)) {
            return "strasse";
        }
        return normalized;
    }

    private List<String> splitStreetToken(String token) {
        String normalized = token.toLowerCase();
        if (normalized.endsWith("straße")) {
            String base = token.substring(0, token.length() - "straße".length()).trim();
            if (!base.isBlank()) {
                return List.of(base, "Straße");
            }
        }
        if (normalized.endsWith("strasse")) {
            String base = token.substring(0, token.length() - "strasse".length()).trim();
            if (!base.isBlank()) {
                return List.of(base, "Strasse");
            }
        }
        return List.of();
    }

    private String removeTrailingCityToken(String query) {
        String[] parts = query.trim().split("\\s+");
        if (parts.length < 3) {
            return query;
        }
        String last = parts[parts.length - 1];
        if (containsDigit(last)) {
            return query;
        }
        String[] remaining = java.util.Arrays.copyOf(parts, parts.length - 1);
        return String.join(" ", remaining).trim();
    }

    private List<String> buildCommaVariants(String query) {
        List<String> variants = new ArrayList<>();
        String trimmed = query.trim();
        int lastSpace = trimmed.lastIndexOf(' ');
        if (lastSpace < 1) {
            return variants;
        }
        String lastToken = trimmed.substring(lastSpace + 1).trim();
        if (lastToken.isBlank() || containsDigit(lastToken)) {
            return variants;
        }
        String rest = trimmed.substring(0, lastSpace).trim();
        if (rest.isBlank()) {
            return variants;
        }
        variants.add(rest + ", " + lastToken);
        variants.add(lastToken + ", " + rest);
        return variants;
    }

    private String rotateLastTokenToFront(String query) {
        String trimmed = query.trim();
        int lastSpace = trimmed.lastIndexOf(' ');
        if (lastSpace < 1) {
            return trimmed;
        }
        String lastToken = trimmed.substring(lastSpace + 1).trim();
        if (lastToken.isBlank() || containsDigit(lastToken)) {
            return trimmed;
        }
        String rest = trimmed.substring(0, lastSpace).trim();
        if (rest.isBlank()) {
            return trimmed;
        }
        return lastToken + " " + rest;
    }

    private boolean containsDigit(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isDigit(value.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private List<String> tokenizePhotonQueryTokens(String query) {
        String normalized = normalizeForSearch(query);
        String[] parts = normalized.split("\\s+");
        List<String> tokens = new ArrayList<>();
        for (String part : parts) {
            if (part.length() >= 2) {
                tokens.add(part);
            }
        }
        return tokens;
    }

    private String extractPhotonCityToken(List<String> tokens) {
        if (tokens.isEmpty()) {
            return "";
        }
        String lastToken = tokens.get(tokens.size() - 1);
        if (containsDigit(lastToken)) {
            return tokens.get(0);
        }
        return lastToken;
    }

    private String extractPhotonCandidateCity(Map<?, ?> props) {
        Object value = props.get("city");
        if (value == null) {
            value = props.get("town");
        }
        if (value == null) {
            value = props.get("village");
        }
        if (value == null) {
            value = props.get("municipality");
        }
        if (value == null) {
            value = props.get("locality");
        }
        if (value == null) {
            value = props.get("hamlet");
        }
        if (value == null) {
            value = props.get("suburb");
        }
        if (value == null) {
            value = props.get("neighbourhood");
        }
        if (value == null) {
            value = props.get("county");
        }
        return value != null ? value.toString() : null;
    }

    private String extractPhotonCandidateRegion(Map<?, ?> props) {
        Object value = props.get("district");
        if (value == null) {
            value = props.get("county");
        }
        if (value == null) {
            value = props.get("state");
        }
        return value != null ? value.toString() : null;
    }

    private boolean matchesPhotonTokens(String candidateText, List<String> tokens) {
        String normalized = normalizeForSearch(candidateText);
        for (String token : tokens) {
            if (normalized.contains(token)) {
                continue;
            }
            if (containsDigit(token)) {
                String digits = token.replaceAll("\\D+", "");
                if (!digits.isBlank() && matchesPhotonNumberToken(normalized, digits)) {
                    continue;
                }
            }
            return false;
        }
        return true;
    }

    private String buildPhotonCandidateText(Map<?, ?> props) {
        StringBuilder builder = new StringBuilder();
        appendPhotonIfPresent(builder, props.get("name"));
        appendPhotonIfPresent(builder, props.get("street"));
        appendPhotonIfPresent(builder, props.get("housenumber"));
        appendPhotonIfPresent(builder, props.get("city"));
        appendPhotonIfPresent(builder, props.get("district"));
        appendPhotonIfPresent(builder, props.get("state"));
        appendPhotonIfPresent(builder, props.get("county"));
        appendPhotonIfPresent(builder, props.get("postcode"));
        appendPhotonIfPresent(builder, props.get("country"));
        return builder.toString().trim();
    }

    private void appendPhotonIfPresent(StringBuilder builder, Object value) {
        if (value == null) {
            return;
        }
        String text = value.toString().trim();
        if (text.isBlank()) {
            return;
        }
        if (!builder.isEmpty()) {
            builder.append(' ');
        }
        builder.append(text);
    }

    private String normalizeForSearch(String value) {
        String normalized = normalizeQuery(value).toLowerCase();
        normalized = normalized.replaceAll("\\bstr\\.?\\b", "strasse");
        normalized = normalized.replace("\u00df", "ss");
        normalized = normalized.replace("\u00e4", "ae");
        normalized = normalized.replace("\u00f6", "oe");
        normalized = normalized.replace("\u00fc", "ue");
        return normalized;
    }

    private boolean matchesPhotonNumberToken(String candidateText, String digits) {
        String[] parts = candidateText.split("\\s+");
        for (String part : parts) {
            String token = part.replaceAll("[^a-z0-9]", "");
            if (token.startsWith(digits)) {
                return true;
            }
        }
        return false;
    }

    private String swapQuery(String query) {
        String trimmed = query.trim();
        int firstSpace = trimmed.indexOf(' ');
        if (firstSpace < 1) {
            return trimmed;
        }
        String first = trimmed.substring(0, firstSpace).trim();
        String rest = trimmed.substring(firstSpace + 1).trim();
        if (rest.isBlank()) {
            return trimmed;
        }
        return rest + " " + first;
    }
}
