package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    public void addLike(int userId, int filmId) {
        String sqlQuery = "INSERT INTO liked_films(user_id, film_id) VALUES(?, ?)";

        jdbcTemplate.update(con -> {
            var ps = con.prepareStatement(sqlQuery);
            ps.setInt(1, userId);
            ps.setInt(2, filmId);
            return ps;
        });
    }

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

}
