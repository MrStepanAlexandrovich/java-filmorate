package ru.yandex.practicum.filmorate.storage.friend;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    public void addFriend(int userIdFrom, int userIdTo) {
        String sqlQuery = "INSERT INTO friendships(user_id_from, user_id_to) VALUES (?, ?)";
        jdbcTemplate.update(con -> {
            var ps = con.prepareStatement(sqlQuery);
            ps.setInt(1, userIdFrom);
            ps.setInt(2, userIdTo);
            return ps;
        });
    }
    public void deleteFriend(int userIdFrom, int userIdTo) {
        String sqlQuery = "DELETE FROM friendships WHERE user_id_from = ? AND user_id_to = ?";
        jdbcTemplate.update(con -> {
            var ps = con.prepareStatement(sqlQuery);
            ps.setInt(1, userIdFrom);
            ps.setInt(2, userIdTo);
            return ps;
        });
    }
}
