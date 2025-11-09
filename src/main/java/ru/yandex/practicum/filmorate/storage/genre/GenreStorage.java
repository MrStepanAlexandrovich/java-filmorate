package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    Genre getGenre(int id);

    List<Genre> getAllGenres();

    Set<Genre> getGenresOfFilms(int filmId);
}
