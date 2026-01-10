package de.othr.event_hub.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import de.othr.event_hub.dto.WeatherDto;
import de.othr.event_hub.dto.WeatherResponse;
import de.othr.event_hub.service.WeatherService;

@Service
public class WeatherServiceImpl implements WeatherService {
    private final RestClient client;

    public WeatherServiceImpl(RestClient.Builder builder) {
        this.client = builder.baseUrl("https://api.open-meteo.com/v1").build();
    }

    public WeatherResponse getEventWeather(double lat, double lng, LocalDateTime eventTime) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/forecast").queryParam("latitude", lat).queryParam("longitude", lng)
                        .queryParam("start_date", eventTime.toLocalDate())
                        .queryParam("end_date", eventTime.toLocalDate())
                        .queryParam("hourly", "temperature_2m,weather_code,precipitation_probability")
                        .queryParam("timezone", "auto").build())
                .retrieve().body(WeatherResponse.class);
    }

    public boolean isForecastAvailable(LocalDateTime eventTime) {
        LocalDate today = LocalDate.now();
        LocalDate maxForecastDate = today.plusDays(7);
        LocalDate eventDate = eventTime.toLocalDate();

        return !eventDate.isBefore(today) && !eventDate.isAfter(maxForecastDate);
    }

    public String getWeatherIcon(int weatherCode) {
        return switch (weatherCode) {
            case 0 -> "sunny";
            case 1, 2, 3 -> "partly_cloudy_day";
            case 45, 48 -> "foggy";
            case 51, 53, 55 -> "rainy";
            case 56, 57 -> "rainy_snow";
            case 61, 63, 65 -> "rainy_light";
            case 71, 73, 75 -> "snowing";
            case 80, 81, 82 -> "rainy_heavy";
            case 85, 86 -> "snowing_heavy";
            case 95 -> "thunderstorm";
            case 96, 99 -> "weather_hail";
            default -> "question_mark";
        };
    }

    public WeatherDto mapToDto(WeatherResponse response, LocalDateTime eventTime) {
        int hourIndex = eventTime.getHour();

        double temp = response.hourly().temperatures().get(hourIndex);
        int weatherCode = response.hourly().weatherCodes().get(hourIndex);
        int precipitationProbability = response.hourly().precipitationProbabilities().get(hourIndex);

        String weatherIconName = getWeatherIcon(weatherCode);

        return new WeatherDto(temp, weatherIconName, precipitationProbability);
    }
}
