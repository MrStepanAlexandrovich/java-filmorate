package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    FilmStorage filmStorage;
    FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Getting films...");
        return filmStorage.getFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmStorage.update(film);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id) {
        filmStorage.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void userLikesFilm(
            @PathVariable(name = "id") int filmId,
            @PathVariable int userId
    ) {
        filmService.likeFilm(userId, filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void userDeletesLike(
            @PathVariable(name = "id") int filmId,
            @PathVariable int userId
    ) {
        filmService.deleteLike(userId, filmId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10") int count) {
        return filmService.getMostPopularFilms(count);
    }
}
