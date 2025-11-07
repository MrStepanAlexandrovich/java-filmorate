package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    public Genre getGenre(int id);

    public List<Genre> getAllGenres();

    public Set<Genre> getByFilmId(int filmId);
}
