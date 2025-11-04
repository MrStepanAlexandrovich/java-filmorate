package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.*;
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


        Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
                genres.addAll(filmFromDb.getGenres());

        String getGenreByIdQuery = "SELECT * FROM genres WHERE id = ?";

        for (Integer id : genreIds) {
            genres.add(jdbcTemplate.queryForObject(getGenreByIdQuery, new GenreRowMapper(), id));
        }

        filmFromDb.setGenres(genres);

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
    public List<Film> getAllFilms() {
        List<Film> films = new ArrayList<>();
        String sqlQuery = "SELECT * FROM films";
        films = jdbcTemplate.query(sqlQuery, new FilmRowMapper());

        return films.stream()
                .peek(film -> {
                    String getGenresIdQuery = "SELECT genre_id FROM films_genres WHERE film_id = ?";
                    List<Integer> genreIds = jdbcTemplate.queryForList(getGenresIdQuery, Integer.class, film.getId());
                    Set<Genre> genres = new HashSet<>();
                    String getGenresByIdQuery = "SELECT * FROM genres WHERE id = ?";
                    for (Integer id : genreIds) {
                        genres.add(jdbcTemplate.queryForObject(getGenresByIdQuery, new GenreRowMapper(), id));
                    }

                    film.setGenres(genres);
                })
                .collect(Collectors.toList());
    }

    /*@Override
    public Film find(int filmId) {
        String sqlQuery = "SELECT * FROM films WHERE id = ?";
        Film film = null;
        try {
            film = jdbcTemplate.queryForObject(sqlQuery, new FilmRowMapper(), filmId);
        } catch (DataAccessException e) {
            log.info("Query \"SELECT * FROM films WHERE id = " + filmId + "\" returned empty result!");
        }

        return film;
    }*/

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

    public Genre getGenre(int id) {
        String sqlQuery = "SELECT * FROM genres WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, new GenreRowMapper(), id);
        } catch (DataAccessException e) {
            log.info("Genre with ID = " + id + " wasn't found");
            throw new NotFoundException("Genre with ID = " + id + " wasn't found");
        }
    }

    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, new GenreRowMapper());
    }

    @Override
    public Film getFilm(int id) {
        Film film;
        String sqlQuery = "SELECT * FROM films WHERE id = ?";

        try {
            film = jdbcTemplate.queryForObject(sqlQuery, new FilmRowMapper(), id);
        } catch (DataAccessException e) {
            log.info("Film with ID = " + id + " wasn't found");
            throw new NotFoundException("Film with ID = " + id + " wasn't found");
        }

        String sqlQuery2 = "SELECT g.id, g.name\n" +
                "FROM FILMS_GENRES AS f\n" +
                "LEFT JOIN GENRES AS g on g.ID = f.GENRE_ID\n" +
                "WHERE f.FILM_ID = ?";

        Set<Genre> genres = null;
        try {
            genres = jdbcTemplate.query(sqlQuery2, new GenreRowMapper(), id)
                    .stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .collect(Collectors.toSet());
        } catch (DataAccessException e) {
            log.info("Genres weren't found");
        }

        film.setGenres(genres);

        film.setMpa(getMpa(film.getMpa().getId()));
        return film;
    }

    @Override
    public void deleteLike(int userId, int filmId) {
        String sqlQuery = "DELETE FROM liked_films WHERE user_id = ? AND film_id = ?";
        try {
            jdbcTemplate.update(sqlQuery, userId, filmId);
        } catch (DataAccessException e) {
            log.info("Like not found!");
            throw new NotFoundException("Like not found!");
        }
    }

    @Override
    public List<Film> getMostLikedFilms(int count) {
        String sqlQuery = "SELECT l.FILM_ID, COUNT(l.FILM_ID), f.*\n" +
                "FROM LIKED_FILMS AS l\n" +
                "LEFT JOIN FILMS AS f ON l.FILM_ID = f.ID\n" +
                "GROUP BY l.FILM_ID\n" +
                "ORDER BY COUNT(l.FILM_ID) DESC\n" +
                "LIMIT ?;\n";

        return jdbcTemplate.query(sqlQuery, new FilmRowMapper(), count);
    }
}
