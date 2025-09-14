package ru.yandex.practicum.filmorate.controller;

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
    private static int counter = 1;
    List<Film> films = new ArrayList<>();

    @GetMapping
    public List<Film> getFilms() {
        log.info("Getting films...");
        return films;
    }

    @PostMapping
    public void addFilm(@RequestBody Film film) {
           film.setId(counter++);
           films.add(film);
           log.info("Film was successfully added!");
    }

    @PutMapping
    public void updateFilm(@RequestBody Film film) {
        Optional<Film> filmOptional = findFilmById(film.getId());
        if (filmOptional.isPresent()) {
            Film oldFilm = filmOptional.get();
            films.set(films.indexOf(oldFilm), film);
            log.info("Film was successfully updated!");
        } else {
            log.info("Film with such ID wasn't found!");
        }
    }

    private Optional<Film> findFilmById(int id) {
        Optional<Film> filmOptional = films.stream()
                .filter(film1 -> film1.getId() == id)
                .findAny();

        return filmOptional;
    }
}
