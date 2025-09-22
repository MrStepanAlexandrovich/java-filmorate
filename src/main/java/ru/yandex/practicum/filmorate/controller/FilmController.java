package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private int counter = 0;
    private List<Film> films = new ArrayList<>();

    @GetMapping
    public List<Film> getFilms() {
        log.info("Getting films...");
        return films;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        if (film.releaseDateIsValid()) {
            if (film.durationIsValid()) {
                film.setId(++counter);
                films.add(film);
                log.info("Film was successfully added!");
                return film;
            } else {
                log.info("Film wasn't added because duration is negative!");
                throw new ValidationException("Film wasn't added because duration is negative!");
            }
        } else {
            log.info("Film wasn't added because it's too old!");
            throw new ValidationException("Film wasn't added because it's too old!");
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        Optional<Film> filmOptional = findFilmById(film.getId());
        if (filmOptional.isPresent()) {
            if (film.releaseDateIsValid()) {
                if (film.durationIsValid()) {
                    films.set(films.indexOf(filmOptional.get()), film);
                    log.info("Film was successfully updated!");
                    return film;
                } else {
                    log.info("Film wasn't updated because duration is negative!");
                    throw new ValidationException("Film wasn't updated because duration is negative!");
                }
            } else {
                log.info("Film wasn't updated because it's too old!");
                throw new ValidationException("Film wasn't updated because it's too old!");
            }
        } else {
            log.info("Film with such ID wasn't found!");
            throw new ValidationException("Film with such ID wasn't found!");
        }
    }

    private Optional<Film> findFilmById(int id) {
        Optional<Film> filmOptional = films.stream()
                .filter(film1 -> film1.getId() == id)
                .findAny();

        return filmOptional;
    }
}
