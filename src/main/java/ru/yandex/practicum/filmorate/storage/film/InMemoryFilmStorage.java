package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final List<Film> films = new ArrayList<>();
    private int counter = 0;

    @Override
    public void delete(int id) {
        films.remove(find(id));
    }

    @Override
    public Film add(Film film) {
        film.setId(++counter);
        films.add(film);
        log.info("Film was successfully added!");
        return film;
    }

    @Override
    public Film update(Film film) {
        films.set(films.indexOf(film), film);
        log.info("Film was successfully updated!");
        return film;
    }


    @Override
    public List<Film> getAllFilms() {
        return films;
    }

    public Film find(int id) {
        Optional<Film> filmOptional = films.stream()
                .filter(film1 -> film1.getId() == id)
                .findAny();

        if (filmOptional.isPresent()) {
            return filmOptional.get();
        } else {
            return null;
        }
    }

    @Override
    public Film getFilm(int id) {
        return null;
    }

    @Override
    public List<MPARating> getMpas() {
        return List.of();
    }

    @Override
    public MPARating getMpa(int id) {
        return null;
    }

    @Override
    public Genre getGenre(int id) {
        return null;
    }

    @Override
    public List<Genre> getAllGenres() {
        return List.of();
    }

    @Override
    public void deleteLike(int userId, int filmId) {

    }

    @Override
    public List<Film> getMostLikedFilms(int count) {
        return List.of();
    }
}
