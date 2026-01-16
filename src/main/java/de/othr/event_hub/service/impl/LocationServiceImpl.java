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
    private static final String USER_AGENT = "event-hub/1.0 (oth.eventhub@gmail.com)";

    @Override
    public Optional<LocationCoordinates> findCoordinates(String location) {
        String normalized = normalizeQuery(location);
        if (normalized.isBlank()) {
            return Optional.empty();
        }
        for (String query : buildCoordinateQueries(normalized)) {
            Optional<LocationCoordinates> nominatim = findNominatimCoordinates(query);
            if (nominatim.isPresent()) {
                return nominatim;
            }
        }
        return Optional.empty();
    }

    private Optional<LocationCoordinates> findNominatimCoordinates(String location) {
        try {
            String normalized = normalizeQuery(location);
            if (normalized.isBlank()) {
                return Optional.empty();
            }
            List<String[]> structuredQueries = buildNominatimStructuredQueries(normalized);
            boolean hasHouseNumber = hasHouseNumberToken(normalized);
            if (hasHouseNumber) {
                for (String[] structured : structuredQueries) {
                    Optional<LocationCoordinates> structuredResult = fetchNominatimCoordinates(
                        buildNominatimStructuredUrl(structured[0], structured[1], structured[2]));
                    if (structuredResult.isPresent()) {
                        return structuredResult;
                    }
                }
            }
            Optional<LocationCoordinates> direct = fetchNominatimCoordinates(buildNominatimQueryUrl(normalized));
            if (direct.isPresent()) {
                return direct;
            }
            if (!hasHouseNumber) {
                for (String[] structured : structuredQueries) {
                    Optional<LocationCoordinates> structuredResult = fetchNominatimCoordinates(
                        buildNominatimStructuredUrl(structured[0], structured[1], structured[2]));
                    if (structuredResult.isPresent()) {
                        return structuredResult;
                    }
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
        String streetValue = street;
        if (houseNumber != null && !houseNumber.isBlank()) {
            streetValue = houseNumber + " " + street;
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(NOMINATIM_URL)
            .queryParam("format", "json")
            .queryParam("limit", "1")
            .queryParam("countrycodes", "de")
            .queryParam("street", streetValue)
            .queryParam("city", city);
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

    private boolean hasHouseNumberToken(String query) {
        return query != null && query.matches(".*\\b\\d+[a-zA-Z]?(?:-\\d+[a-zA-Z]?)?\\b.*");
    }

    private List<String> buildCoordinateQueries(String query) {
        Set<String> queries = new LinkedHashSet<>();
        addQueryVariants(queries, query);
        String swapped = swapQuery(query);
        if (!swapped.equalsIgnoreCase(query)) {
            addQueryVariants(queries, swapped);
        }
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

    private boolean containsDigit(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isDigit(value.charAt(i))) {
                return true;
            }
        }
        return false;
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
