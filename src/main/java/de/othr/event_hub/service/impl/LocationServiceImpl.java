package de.othr.event_hub.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public boolean isLocationValid(String location) {
        return findCoordinates(location).isPresent();
    }

    @Override
    public Optional<LocationCoordinates> findCoordinates(String location) {
        if (location == null || location.isBlank()) {
            return Optional.empty();
        }
        try {
            String url = UriComponentsBuilder.fromHttpUrl(NOMINATIM_URL)
                .queryParam("format", "json")
                .queryParam("limit", "1")
                .queryParam("q", location)
                .toUriString();

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
        } catch (Exception ex) {
            return Optional.empty();
        }
        return Optional.empty();
    }
}
