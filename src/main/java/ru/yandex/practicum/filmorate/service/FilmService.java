package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;

    public void likeFilm(int userId, int filmId) {
        User user = userStorage.findById(userId);
        Film film = filmStorage.getFilm(filmId);

        likeStorage.addLike(userId, filmId);
    }

    public void deleteLike(int userId, int filmId) {
        userStorage.findById(userId);
        filmStorage.getFilm(filmId);

        likeStorage.deleteLike(userId, filmId);
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
        List<Integer> genreIds = genreStorage.getAllGenres()
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
        return mpaStorage.getMpas();
    }

    public MPARating getMpaById(int id) {
        MPARating mpaRating = mpaStorage.getMpa(id);

        if (mpaRating == null) {
            throw new NotFoundException("MPA rating with ID = " + id + " wasn't found");
        } else {
            return mpaRating;
        }
    }

    public Genre getGenre(int id) {
        return genreStorage.getGenre(id);
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Film getFilm(int id) {
        Film film = filmStorage.getFilm(id);
        Set<Genre> genres = genreStorage.getByFilmId(id);
        film.setGenres(genres);
        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        List filmsByGenre = genreStorage.getAllGenresOfAllFilms();

        films.stream()
                .peek(film -> filmsByGenre.stream().filter(film.getId()))
        return films;
    }

    public void deleteFilm(int id) {
        filmStorage.delete(id);
    }
}
