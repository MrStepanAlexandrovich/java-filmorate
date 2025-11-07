package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.film.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
public class FilmService {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, JdbcTemplate jdbcTemplate,
                       MpaDbStorage mpaDbStorage, GenreDbStorage genreDbStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    public void likeFilm(int userId, int filmId) {
        User user = userStorage.findById(userId);
        Film film = filmStorage.getFilm(filmId);

        film.getLikedUsers().add(user);

        String sqlQuery = "INSERT INTO liked_films(user_id, film_id) VALUES(?, ?)";

        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    public void deleteLike(int userId, int filmId) {
        filmStorage.deleteLike(userId, filmId);
    }

    public List<Film> getMostLikedFilms(int count) {
        return filmStorage.getMostLikedFilms(count);
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

        getFilm(film.getId()); //проверка существования фильма

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


    public boolean genresAreValid(Film film) {
        List<Integer> genreIds = genreDbStorage.getAllGenres()
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
        return mpaDbStorage.getMpas();
    }

    public MPARating getMpaById(int id) {
        MPARating mpaRating = mpaDbStorage.getMpa(id);

        if (mpaRating == null) {
            throw new NotFoundException("MPA rating with ID = " + id + " wasn't found");
        } else {
            return mpaRating;
        }
    }

    public Genre getGenre(int id) {
        return genreDbStorage.getGenre(id);
    }

    public List<Genre> getAllGenres() {
        return genreDbStorage.getAllGenres();
    }

    public Film getFilm(int id) {
        Film film = filmStorage.getFilm(id);
        Set<Genre> genres = genreDbStorage.getByFilmId(id);
        film.setGenres(genres);
        return film;
    }
}
