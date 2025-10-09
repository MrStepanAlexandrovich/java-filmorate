package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    void delete(int id);
    Film add(Film film);
    Film update(Film film);
    List<Film> getFilms();
}
