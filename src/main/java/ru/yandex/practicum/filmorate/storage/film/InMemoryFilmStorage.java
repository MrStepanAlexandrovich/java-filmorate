package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

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
    public List<Film> getFilms() {
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

    public List<Genre> getGenres() {
        return List.of();
    }
}
