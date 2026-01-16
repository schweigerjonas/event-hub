package de.othr.event_hub.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.othr.event_hub.service.LocationService;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validate(@RequestParam("query") String query) {
        if (query == null || query.trim().length() < 3) {
            return ResponseEntity.ok(Map.of("valid", false, "candidates", java.util.List.of()));
        }
        String normalized = query.trim();
        boolean coordinates = locationService.findCoordinates(normalized).isPresent();
        return ResponseEntity.ok(Map.of("valid", coordinates));
    }
}
