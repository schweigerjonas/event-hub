package de.othr.event_hub.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import de.othr.event_hub.dto.WeatherResponse;
import de.othr.event_hub.service.WeatherService;

@Service
public class WeatherServiceImpl implements WeatherService {
    @Value("${open-meteo.base-url")
    private String baseUrl;

    private final RestClient client;

    public WeatherServiceImpl(RestClient.Builder builder) {
        this.client = builder.baseUrl(baseUrl).build();
    }

    public WeatherResponse getEventWeather(Double lat, Double lng, LocalDateTime eventTime) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/forecast").queryParam("latitude", lat).queryParam("longitude", lng)
                        .queryParam("start_date", eventTime.now().toLocalDate())
                        .queryParam("hourly", "temperature_2m,weather_code,precipitation_probability")
                        .queryParam("timezone", "auto").build())
                .retrieve().body(WeatherResponse.class);
    }

}
