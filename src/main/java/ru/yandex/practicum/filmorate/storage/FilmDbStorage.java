package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Repository
public class FilmDbStorage implements FilmStorage {
    @Override
    public void delete(int id) {

    }

    @Override
    public Film add(Film film) {
        return null;
    }

    @Override
    public Film update(Film film) {
        return null;
    }

    @Override
    public List<Film> getFilms() {
        return List.of();
    }

    @Override
    public Film findById(int filmId) {
        return null;
    }
}
