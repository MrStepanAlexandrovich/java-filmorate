package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

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
        String sqlQuery = "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES(?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpaId());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
                "mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpaId(), film.getId());
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
        return jdbcTemplate.queryForObject(sqlQuery, new FilmRowMapper());
    }
}
