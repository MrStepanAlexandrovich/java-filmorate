package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
@AllArgsConstructor
public class Film {
    private int id;

    @NotBlank(message = "Name field must not be empty!")
    private String name;

    @Size(max = 200)
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @NotNull(message = "Duration must not be null!")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Duration duration;

    public boolean releaseDateIsValid() {
        return releaseDate.isAfter(LocalDate.of(1895, 1, 28))
                || releaseDate.equals(LocalDate.of(1895, 1, 28));
    }

    public boolean durationIsValid() {
        return duration.isPositive();
    }
}
