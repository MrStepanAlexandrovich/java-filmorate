package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.Date;

/**
 * Film.
 */
@Getter
@Setter
public class Film {
    private int id;
    private String name;
    private String description;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private Date releaseDate;

    @JsonFormat(pattern = "MINUTES")
    private Duration duration;
}
