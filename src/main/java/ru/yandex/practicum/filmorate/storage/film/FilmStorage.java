package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.List;

public interface FilmStorage {
    void delete(int id);

    Film add(Film film);

    Film update(Film film);

    List<Film> getFilms();

    Film find(int filmId);

    List<Genre> getGenres();

    List<MPARating> getMpas();

    MPARating getMpa(int id);
}
