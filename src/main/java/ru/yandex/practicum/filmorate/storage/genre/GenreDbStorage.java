package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

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

    public Set<Genre> getByFilmId(int filmId) {
        String sqlQuery = "SELECT g.* " +
                "FROM FILMS_GENRES AS f " +
                "JOIN PUBLIC.GENRES g on g.ID = f.GENRE_ID " +
                "WHERE f.FILM_ID = ?";

        List<Genre> genres = jdbcTemplate.query(con -> {
            var ps = con.prepareStatement(sqlQuery);
            ps.setInt(1, filmId);
            return ps;
        }, new GenreRowMapper());

        return new HashSet<>(genres);
    }

    public List getAllGenresOfAllFilms() {
        String sqlQuery = "SELECT * " +
                "FROM films_genres AS f " +
                "LEFT JOIN genres AS g ON f.genre_id = g.id";

        List<Map<String, Object>> genresByFilmId = jdbcTemplate.queryForList(sqlQuery);

        List<Pair<Integer, Genre>> filmsByGenre = genresByFilmId.stream()
                .map(stringObjectMap -> new Pair<Integer, Genre>((Integer) stringObjectMap.get("FILM_ID"),
                        new Genre(
                                (Integer) stringObjectMap.get("GENRE_ID"),
                                (String) stringObjectMap.get("NAME")
                        )))
                .collect(Collectors.toList());

        return filmsByGenre;
    }
}
