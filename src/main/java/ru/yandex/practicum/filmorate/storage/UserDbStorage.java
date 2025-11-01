package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Primary
public class UserDbStorage implements UserStorage{
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void delete(int id) {
        String sqlQuery = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public User add(User user) {
        String sqlQuery = "INSERT INTO users(name, login, email, birthday) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, user.getName(), user.getLogin(), user.getEmail(), user.getBirthday());
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public User findById(int id) {
        String sqlQuery1 = "SELECT * FROM users WHERE id = ?";
        User user = jdbcTemplate.queryForObject(sqlQuery1, new UserRowMapper(), id);
        String sqlQuery2 = "SELECT user2_id FROM friendships WHERE user1_id = ?";
        user.setFriends(new HashSet<>(jdbcTemplate.queryForList(sqlQuery2, Integer.class, user.getId())));
        String sqlQuery3 = "SELECT film_id FROM liked_films WHERE user_id = ?";
        user.setLikedFilms(new HashSet<>(jdbcTemplate.queryForList(sqlQuery3, Integer.class,
                user.getId())));

        return user;
    }

    @Override
    public List<User> getUsers() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, new UserRowMapper());
    }
}
