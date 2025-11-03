package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
@Primary
@Slf4j
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
        User user = null;
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        try {
            user = jdbcTemplate.queryForObject(sqlQuery, new UserRowMapper(), id);
        } catch (DataAccessException e) {
            log.trace("User with ID = " + id + " wasn't found!");
        }
        return user;
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
