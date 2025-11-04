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
        jdbcTemplate.update(con -> {
            var ps = con.prepareStatement(sqlQuery);
            ps.setInt(1, id);
            return ps;
        });
    }

    @Override
    public Film add(Film film) {
        List<Integer> genresId = film.getGenres()
                .stream()
                .map(Genre::getId)
                .toList();

        String addUserQuery = "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES(?, ?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
            var ps = con.prepareStatement(addUserQuery);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration().toMinutes());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        });

        String sqlQuery4 = "SELECT * FROM films WHERE id IN (SELECT MAX(id) FROM films)";
        List<Film> filmList = jdbcTemplate.query(con -> con.prepareStatement(sqlQuery4), new FilmRowMapper());
        Film filmFromDb = filmList.stream().findFirst().orElse(null);
        if (filmFromDb == null) {
            throw new NotFoundException("Failed to retrieve freshly inserted film");
        }

        String addGenresOfFilmQuery = "INSERT INTO films_genres(genre_id, film_id) VALUES(?, ?)";

        for (Integer id : genresId) {
            int filmId = filmFromDb.getId();
            jdbcTemplate.update(con -> {
                var ps = con.prepareStatement(addGenresOfFilmQuery);
                ps.setInt(1, id);
                ps.setInt(2, filmId);
                return ps;
            });
        }

        List<Integer> genreIds = film.getGenres()
                .stream()
                .map(Genre::getId)
                .toList();


        Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
        genres.addAll(filmFromDb.getGenres());

        String getGenreByIdQuery = "SELECT * FROM genres WHERE id = ?";

        for (Integer id : genreIds) {
            List<Genre> result = jdbcTemplate.query(con -> {
                var ps = con.prepareStatement(getGenreByIdQuery);
                ps.setInt(1, id);
                return ps;
            }, new GenreRowMapper());
            Genre g = result.stream().findFirst().orElse(null);
            if (g != null) {
                genres.add(g);
            }
        }

        filmFromDb.setGenres(genres);

        return filmFromDb;
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
        String sqlQuery = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(con -> con.prepareStatement(sqlQuery), new FilmRowMapper());

        return films.stream()
                .peek(film -> {
                    String getGenresIdQuery = "SELECT genre_id FROM films_genres WHERE film_id = ?";
                    List<Integer> genreIds = jdbcTemplate.query(con -> {
                        var ps = con.prepareStatement(getGenresIdQuery);
                        ps.setInt(1, film.getId());
                        return ps;
                    }, (rs, rowNum) -> rs.getInt("genre_id"));
                    Set<Genre> genres = new HashSet<>();
                    String getGenresByIdQuery = "SELECT * FROM genres WHERE id = ?";
                    for (Integer id : genreIds) {
                        List<Genre> res = jdbcTemplate.query(con -> {
                            var ps = con.prepareStatement(getGenresByIdQuery);
                            ps.setInt(1, id);
                            return ps;
                        }, new GenreRowMapper());
                        res.stream().findFirst().ifPresent(genres::add);
                    }

                    film.setGenres(genres);
                })
                .collect(Collectors.toList()).reversed();
    }

    public List<MPARating> getMpas() {
        String sqlQuery = "SELECT * FROM MPA";
        return jdbcTemplate.query(con -> con.prepareStatement(sqlQuery), new MPARowMapper());
    }

    public MPARating getMpa(int id) {
        String sqlQuery = "SELECT * FROM MPA WHERE id = ?";
        List<MPARating> list = jdbcTemplate.query(con -> {
            var ps = con.prepareStatement(sqlQuery);
            ps.setInt(1, id);
            return ps;
        }, new MPARowMapper());
        MPARating mpaRating = list.stream().findFirst().orElse(null);
        if (mpaRating == null) {
            log.info("MPA rating with ID = " + id + " wasn't found");
        }
        return mpaRating;
    }

    public Genre getGenre(int id) {
        String sqlQuery = "SELECT * FROM genres WHERE id = ?";
        try {
            List<Genre> list = jdbcTemplate.query(con -> {
                var ps = con.prepareStatement(sqlQuery);
                ps.setInt(1, id);
                return ps;
            }, new GenreRowMapper());
            return list.stream().findFirst().orElseThrow(() -> new NotFoundException("Genre with ID = " + id + " wasn't found"));
        } catch (DataAccessException e) {
            log.info("Genre with ID = " + id + " wasn't found");
            throw new NotFoundException("Genre with ID = " + id + " wasn't found");
        }
    }

    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(con -> con.prepareStatement(sqlQuery), new GenreRowMapper());
    }

    @Override
    public Film getFilm(int id) {
        String sqlQuery = "SELECT * FROM films WHERE id = ?";

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

        String sqlQuery2 = "SELECT g.id, g.name\n" +
                "FROM FILMS_GENRES AS f\n" +
                "LEFT JOIN GENRES AS g on g.ID = f.GENRE_ID\n" +
                "WHERE f.FILM_ID = ?";

        Set<Genre> genres = null;
        try {
            genres = new HashSet<>(jdbcTemplate.query(con -> {
                        var ps = con.prepareStatement(sqlQuery2);
                        ps.setInt(1, id);
                        return ps;
                    }, new GenreRowMapper())
                    .stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .toList());
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
            jdbcTemplate.update(con -> {
                var ps = con.prepareStatement(sqlQuery);
                ps.setInt(1, userId);
                ps.setInt(2, filmId);
                return ps;
            });
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

        return jdbcTemplate.query(con -> {
            var ps = con.prepareStatement(sqlQuery);
            ps.setInt(1, count);
            return ps;
        }, new FilmRowMapper());
    }
}