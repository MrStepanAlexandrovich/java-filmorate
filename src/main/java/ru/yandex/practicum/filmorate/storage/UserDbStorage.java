package ru.yandex.practicum.filmorate.storage;

import jakarta.validation.ValidationException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Primary
public class UserDbStorage implements UserStorage {
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
        if (!user.birthdayIsValid()) {
            throw new ValidationException("Incorrect birthday date!");
        } else {
            String sqlQuery = "INSERT INTO users(name, login, email, birthday) VALUES(?, ?, ?, ?)";
            jdbcTemplate.update(sqlQuery, user.getName(), user.getLogin(), user.getEmail(), user.getBirthday());
            return user;
        }
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
        String sqlQuery = "SELECT * FROM users WHERE id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, new UserRowMapper(), id);
    }

    @Override
    public List<User> getUsers() {
        String sqlQuery1 = "SELECT * FROM users";
        String sqlQuery2 = "SELECT * FROM friendships WHERE user1_id = ?";
        List<User> users = jdbcTemplate.query(sqlQuery1, new UserRowMapper());
        return users;
    }

    public UserDbStorage() {
        super();
    }

    @Override
    public void addFriend(int id, int friendId) {

    }

    @Override
    public void deleteFriend(int id, int friendId) {

    }
}
