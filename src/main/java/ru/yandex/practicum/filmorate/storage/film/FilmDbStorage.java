package ru.yandex.practicum.filmorate.storage.film;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

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
        String sqlQuery1 = "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES(?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery1, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId());
        String sqlQuery2 = "SELECT * FROM films WHERE id IN (SELECT MAX(id) FROM films)";
        return jdbcTemplate.queryForObject(sqlQuery2, new FilmRowMapper());
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
                "mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT * FROM films";
        return jdbcTemplate.query(sqlQuery, new FilmRowMapper());
    }

    @Override
    public Film find(int filmId) {
        String sqlQuery = "SELECT * FROM films WHERE id = ?";
        Film film = null;
        try {
            film = jdbcTemplate.queryForObject(sqlQuery, new FilmRowMapper(), filmId);
        } catch (DataAccessException e) {
            log.info("Query \"SELECT * FROM films WHERE id = "+ filmId + "\" returned empty result!");
        }
        return film;
    }

    public List<Genre> getGenres() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, new GenreRowMapper());
    }
}
