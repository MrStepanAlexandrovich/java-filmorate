package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;
import org.antlr.v4.runtime.misc.Pair;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    Genre getGenre(int id);

    List<Genre> getAllGenres();

    Set<Genre> getByFilmId(int filmId);

    List<Pair<Integer, Genre>> getAllGenresOfAllFilms();
}
