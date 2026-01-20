package de.othr.event_hub.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WeatherResponse(Double lat, Double lng, HourlyData hourly) {
    public record HourlyData(
            List<String> time,
            @JsonProperty("temperature_2m") List<Double> temperatures,
            @JsonProperty("weather_code") List<Integer> weatherCodes,
            @JsonProperty("precipitation_probability") List<Integer> precipitationProbabilities) {
    }
}
