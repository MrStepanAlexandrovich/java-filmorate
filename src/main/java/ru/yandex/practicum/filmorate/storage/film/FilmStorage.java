package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.List;

public interface FilmStorage {
    void delete(int id);

    Film add(Film film);

    Film update(Film film);

    Film getFilm(int id);

    List<Film> getAllFilms();

    List<Film> getMostLikedFilms(int count);
}
