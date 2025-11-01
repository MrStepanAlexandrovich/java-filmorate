package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Film {
    @NonNull
    private int id;

    @NonNull
    @NotBlank(message = "Name field must not be empty!")
    private String name;

    @NonNull
    @Size(max = 200)
    private String description;

    @NonNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @NonNull
    @NotNull(message = "Duration must not be null!")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Duration duration;

    private Set<Integer> likedUsers = new HashSet<>();

    @NotNull
    private Rating rating;

    private Genre[] genres;

    public boolean releaseDateIsValid() {
        return releaseDate.isAfter(LocalDate.of(1895, 1, 28))
                || releaseDate.equals(LocalDate.of(1895, 1, 28));
    }

    public boolean durationIsValid() {
        return duration.isPositive();
    }

    public int getLikesAmount() {
        return likedUsers.size();
    }
}
