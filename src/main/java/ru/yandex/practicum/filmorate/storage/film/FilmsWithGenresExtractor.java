package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FilmsWithGenresExtractor implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Film> films = new LinkedHashMap<>(); //сохраняем порядок
        while (rs.next()) {
            Integer filmId = rs.getInt("film_id");
            Film film = films.get(filmId);
            if (film == null) {
                film = new Film();
                film.setId(filmId);
                film.setName(rs.getString("film_name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(Duration.of(rs.getInt("duration"), ChronoUnit.MINUTES));
                film.setMpa(new MPARating(rs.getInt("mpa_id"), rs.getString("rating")));
                films.put(filmId, film);
            }

            Integer genreId = rs.getObject("genre_id") != null ? rs.getInt("genre_id") : null;
            if (genreId != null) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(rs.getString("genre_name"));
                film.getGenres().add(genre);
            }
        }

        return new ArrayList<>(films.values());
    }
}
