package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GenresWithFilmIdExtractor implements ResultSetExtractor<Map<Integer, List<Genre>>> {
    @Override
    public Map<Integer, List<Genre>> extractData(ResultSet rs) throws SQLException, DataAccessException {
       Map<Integer, List<Genre>> filmIdToGenresMap = new HashMap<>();

        int id;
        while (rs.next()) {
            id = rs.getInt("film_id");
            filmIdToGenresMap.computeIfAbsent(id, k -> new ArrayList<>());
            filmIdToGenresMap.get(id).add(new Genre(
                    rs.getInt("id"),
                    rs.getString("name")
            ));
        }

        return filmIdToGenresMap;
    }
}
