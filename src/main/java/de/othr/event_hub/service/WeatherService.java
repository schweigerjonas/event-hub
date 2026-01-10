package de.othr.event_hub.service;

import java.time.LocalDateTime;

import de.othr.event_hub.dto.WeatherDto;
import de.othr.event_hub.dto.WeatherResponse;

public interface WeatherService {
    public WeatherResponse getEventWeather(double lat, double lng, LocalDateTime eventTime);

    public boolean isForecastAvailable(LocalDateTime eventTime);

    public String getWeatherIcon(int weatherCode);

    public WeatherDto mapToDto(WeatherResponse response, LocalDateTime eventTime);
}
