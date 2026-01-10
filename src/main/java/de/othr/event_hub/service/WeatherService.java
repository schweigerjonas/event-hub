package de.othr.event_hub.service;

import java.time.LocalDateTime;

import de.othr.event_hub.dto.WeatherResponse;

public interface WeatherService {
    public WeatherResponse getEventWeather(Double lat, Double lng, LocalDateTime eventTime);
}
