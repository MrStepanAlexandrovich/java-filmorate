package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void delete(int id) {
        String sqlQuery = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(con -> {
            var ps = con.prepareStatement(sqlQuery);
            ps.setInt(1, id);
            return ps;
        });
    }

    @Override
    public Film add(Film film) {
        String addUserQuery = "INSERT INTO films(name, description, release_date, duration, mpa_id) " +
                "VALUES(?, ?, ?, ?, ?)";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            var ps = con.prepareStatement(addUserQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration().toMinutes());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Set<Integer> genresId = film.getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        String addGenresOfFilmQuery = "INSERT INTO films_genres(genre_id, film_id) VALUES(?, ?)";

        for (Integer id : genresId) {
            jdbcTemplate.update(con -> {
                var ps = con.prepareStatement(addGenresOfFilmQuery);
                ps.setInt(1, id);
                ps.setInt(2, keyHolder.getKeyAs(Integer.class));
                return ps;
            });
        }

        film.setId(keyHolder.getKeyAs(Integer.class));
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
                "mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(con -> {
            var ps = con.prepareStatement(sqlQuery);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration().toMinutes());
            ps.setInt(5, film.getMpa().getId());
            ps.setInt(6, film.getId());
            return ps;
        });
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT f.*, m.rating " +
                " FROM films AS f " +
                " JOIN MPA AS m ON f.MPA_ID = m.ID";

        List<Film> films = jdbcTemplate.query(con -> con.prepareStatement(sqlQuery), new FilmRowMapper());

        return films;
    }

    @Override
    public Film getFilm(int id) {
        String sqlQuery = "SELECT f.*, m.rating " +
                "FROM films AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.ID " +
                "WHERE f.id = ?";
        ;

        Film film = jdbcTemplate.query(con -> {
                    var ps = con.prepareStatement(sqlQuery);
                    ps.setInt(1, id);
                    return ps;
                }, new FilmRowMapper())
                .stream()
                .findFirst()
                .orElseThrow(() -> {
                    log.info("Film with ID = " + id + " wasn't found");
                    return new NotFoundException("Film with ID = " + id + " wasn't found");
                });

        return film;
    }

    @Override
    public List<Film> getMostLikedFilms(int count) {
        String sqlQuery = "SELECT l.FILM_ID, COUNT(l.FILM_ID), f.*, m.RATING " +
                "FROM LIKED_FILMS AS l " +
                "LEFT JOIN FILMS AS f ON l.FILM_ID = f.ID " +
                "LEFT JOIN MPA m ON f.MPA_ID = m.id " +
                "GROUP BY l.FILM_ID " +
                "ORDER BY COUNT(l.FILM_ID) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(con -> {
            var ps = con.prepareStatement(sqlQuery);
            ps.setInt(1, count);
            return ps;
        }, new FilmRowMapper());
    }
}