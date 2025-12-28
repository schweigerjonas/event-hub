package de.othr.event_hub.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventFormDto {

    @NotBlank(message = "Bitte geben Sie einen Namen an.")
    private String name;

    @NotBlank(message = "Bitte geben Sie einen Ort an.")
    private String location;

    @NotNull(message = "Bitte geben Sie die Dauer an.")
    @Positive(message = "Die Dauer muss groesser als 0 sein.")
    private Integer durationMinutes;

    @NotNull(message = "Bitte geben Sie die maximale Teilnehmerzahl an.")
    @Positive(message = "Die maximale Teilnehmerzahl muss groesser als 0 sein.")
    private Integer maxParticipants;

    private boolean paid;

    @PositiveOrZero(message = "Der Preis muss mindestens 0 sein.")
    private Double costs;

    private String description;

    @NotNull(message = "Bitte geben Sie einen Startzeitpunkt an.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime eventTime;
}
