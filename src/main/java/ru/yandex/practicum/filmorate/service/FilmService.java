package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

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
        Film film = filmStorage.findById(filmId);

        film.getLikedUsers().add(userId);
        user.getLikedFilms().add(filmId);
    }

    public void deleteLike(int userId, int filmId) {
        User user = userStorage.findById(userId);
        Film film = filmStorage.findById(filmId);

        film.getLikedUsers().remove(user);
        user.getLikedFilms().remove(film);
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
}
