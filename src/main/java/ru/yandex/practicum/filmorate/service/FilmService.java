package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
public class FilmService {
    FilmStorage filmStorage;
    UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void likeFilm(int userId, int filmId) {
        User user = userStorage.findById(userId);
        Film film = filmStorage.find(filmId);

        film.getLikedUsers().add(userId);
    }

    public void deleteLike(int userId, int filmId) {
        filmStorage.find(filmId)
                .getLikedUsers()
                .remove(userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        List<Film> sortedFilms = new ArrayList<>(filmStorage.getFilms());
        sortedFilms.sort(Comparator.comparing(Film::getLikesAmount));

        if (sortedFilms.size() < count) {
            return sortedFilms.reversed();
        } else {
            return sortedFilms.subList(0, count).reversed();
        }
    }

    public Film addFilm(Film film) {
        if (!film.getGenres().isEmpty()) {
            if (!genresAreValid(film)) {
                log.info("Non-existent genre was found");
                throw new NotFoundException("Non-existent genre was found");
            }
        }

        if (!film.releaseDateIsValid()) {
            log.info("Film wasn't added because it's too old!");
            throw new ValidationException("Film wasn't added because it's too old!");
        } else if (!film.durationIsValid()) {
            log.info("Film wasn't added because duration is negative!");
            throw new ValidationException("Film wasn't added because duration is negative!");
        } else if (!film.getMpa().idIsValid()) {
            log.info("That MPA rating wasn't found");
            throw new NotFoundException("That MPA rating wasn't found");
        } else {
            return filmStorage.add(film);
        }
    }

    public Film updateFilm(Film film) {
        if (!film.getGenres().isEmpty()) {
            if (!genresAreValid(film)) {
                log.info("Non-existent genre was found");
                throw new NotFoundException("Non-existent genre was found");
            }
        }

        findById(film.getId());

        if (film.getId() == null) {
            log.info("Film must have an ID!");
            throw new ValidationException("Film must have an ID!");
        }

        if (!film.releaseDateIsValid()) {
            log.info("Film wasn't updated because it's too old!");
            throw new ValidationException("Film wasn't updated because it's too old!");
        } else if (!film.durationIsValid()) {
            log.info("Film wasn't updated because duration is negative!");
            throw new ValidationException("Film wasn't updated because duration is negative!");
        } else if (!film.getMpa().idIsValid()) {
            log.info("That MPA rating wasn't found");
            throw new NotFoundException("That MPA rating wasn't found");
        } else {
            return filmStorage.update(film);
        }
    }

    public Film findById(int id) {
        Film film = filmStorage.find(id);
        if (film != null) {
            return film;
        } else {
            log.info("Film with ID = " + id + " wasn't found");
            throw new NotFoundException("Film with ID = " + id + " wasn't found");
        }
    }

    public boolean genresAreValid(Film film) {
        List<Integer> genreIds = filmStorage.getGenres()
                .stream()
                .map(Genre::getId)
                .toList();
        boolean genresExist = film.getGenres()
                .stream()
                .map(Genre::getId)
                .allMatch(genreIds::contains);

        return genresExist;
    }

    public List<MPARating> getMpas() {
        return filmStorage.getMpas();
    }

    public MPARating getMpaById(int id) {
        MPARating mpaRating = filmStorage.getMpa(id);

        if (mpaRating == null) {
            throw new NotFoundException("MPA rating with ID = " + id + " wasn't found");
        } else {
            return mpaRating;
        }
    }
}
