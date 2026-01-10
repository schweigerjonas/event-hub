package de.othr.event_hub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDto {
    private double temperature;
    private String weatherIconName;
    private int precipitationProbability;
}
