package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Primary
public class FilmDbStorage implements FilmStorage {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void delete(int id) {
        String sqlQuery = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film add(Film film) {
        List<Integer> genresId = film.getGenres()
                .stream()
                .map(Genre::getId)
                .toList();

        String addUserQuery = "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES(?, ?, ?, ?, ?)";
        jdbcTemplate.update(addUserQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration().toMinutes(), film.getMpa().getId());

        String sqlQuery4 = "SELECT * FROM films WHERE id IN (SELECT MAX(id) FROM films)";
        Film filmFromDb = jdbcTemplate.queryForObject(sqlQuery4, new FilmRowMapper());


        String addGenresOfFilmQuery = "INSERT INTO films_genres(genre_id, film_id) VALUES(?, ?)";

        for (Integer id : genresId) {
            jdbcTemplate.update(addGenresOfFilmQuery, id, filmFromDb.getId());
        }

        List<Integer> genreIds = film.getGenres()
                .stream()
                .map(Genre::getId)
                .toList();

        List<Genre> genres = filmFromDb.getGenres();

        String getGenreByIdQuery = "SELECT * FROM genres WHERE id = ?";

        for (Integer id : genreIds) {
            genres.add(jdbcTemplate.queryForObject(getGenreByIdQuery, new GenreRowMapper(), id));
        }

        return filmFromDb;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
                "mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration().toMinutes(), film.getMpa().getId(), film.getId());
        return film;
    }

    @Override
    public List<Film> getFilms() {
        List<Film> films = new ArrayList<>();
        String sqlQuery = "SELECT * FROM films";
        films = jdbcTemplate.query(sqlQuery, new FilmRowMapper());

        return films.stream()
                .peek(film -> film.setGenres(getGenres()))
                .collect(Collectors.toList());
    }

    @Override
    public Film find(int filmId) {
        String sqlQuery = "SELECT * FROM films WHERE id = ?";
        Film film = null;
        try {
            film = jdbcTemplate.queryForObject(sqlQuery, new FilmRowMapper(), filmId);
        } catch (DataAccessException e) {
            log.info("Query \"SELECT * FROM films WHERE id = " + filmId + "\" returned empty result!");
        }

        return film;
    }

    public List<Genre> getGenres() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, new GenreRowMapper());
    }

    public List<MPARating> getMpas() {
        String sqlQuery = "SELECT * FROM MPA";
        return jdbcTemplate.query(sqlQuery, new MPARowMapper());
    }

    public MPARating getMpa(int id) {
        String sqlQuery = "SELECT * FROM MPA WHERE id = ?";
        MPARating mpaRating = null;
        try {
            mpaRating = jdbcTemplate.queryForObject(sqlQuery, new MPARowMapper(), id);
        } catch (DataAccessException e) {
            log.info("MPA rating with ID = " + id + " wasn't found");
        }

        return mpaRating;
    }

    private List<Genre> getGenresById(Film film) {
        List<Genre> genres = new ArrayList<>();
        String sqlQuery = "SELECT genre_id FROM films_genres WHERE film_id = ?";
        try {
            genres = jdbcTemplate.query(sqlQuery, new GenreRowMapper(), film.getId());
        } catch (DataAccessException e) {
            log.trace("Film with ID = " + film.getId() + " doesn't have genres");
        }

        return genres;
    }
}
