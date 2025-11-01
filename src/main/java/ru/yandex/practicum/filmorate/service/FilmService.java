package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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
        if (!film.releaseDateIsValid()) {
            log.info("Film wasn't added because it's too old!");
            throw new ValidationException("Film wasn't added because it's too old!");
        } else if (!film.durationIsValid()) {
            log.info("Film wasn't added because duration is negative!");
            throw new ValidationException("Film wasn't added because duration is negative!");
        } else {
            return filmStorage.add(film);
        }
    }

    public Film updateFilm(Film film) {
        if (filmStorage.find(film.getId()) == null) {
            log.info("Film with ID = " + film.getId() + " wasn't found!");
            throw new NotFoundException("Film with ID = " + film.getId() + " wasn't found!");
        } else if (!film.releaseDateIsValid()) {
            log.info("Film wasn't updated because it's too old!");
            throw new ValidationException("Film wasn't updated because it's too old!");
        } else if (film.durationIsValid()) {
            log.info("Film wasn't updated because duration is negative!");
            throw new ValidationException("Film wasn't updated because duration is negative!");
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
}
