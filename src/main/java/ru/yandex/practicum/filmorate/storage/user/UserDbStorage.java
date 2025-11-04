package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private JdbcTemplate jdbcTemplate;

    public UserDbStorage() {
        super();
    }

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
            String sqlQuery2 = "SELECT * FROM users WHERE id = (SELECT MAX(id) FROM users)";

            return jdbcTemplate.queryForObject(sqlQuery2, new UserRowMapper());
        }
    }

    @Override
    public User update(User user) {
        findById(user.getId());
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
            throw new NotFoundException("User with ID = " + id + " wasn't found!");
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


    @Override
    public void addFriend(int userIdFrom, int userIdTo) {
        findById(userIdFrom);
        findById(userIdTo);
        String sqlQuery = "INSERT INTO friendships(user_id_from, user_id_to) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userIdFrom, userIdTo);
    }

    @Override
    public void deleteFriend(int userIdFrom, int userIdTo) {
        findById(userIdFrom);
        findById(userIdTo);

        String sqlQuery = "DELETE FROM friendships WHERE user_id_from = ? AND user_id_to = ?";
        jdbcTemplate.update(sqlQuery, userIdFrom, userIdTo);
    }

    @Override
    public Set<User> getFriends(int id) {
        findById(id);
        String sqlQuery = "SELECT u.*\n" +
                "FROM FRIENDSHIPS AS f\n" +
                "RIGHT JOIN USERS AS u ON f.USER_ID_TO = u.ID\n" +
                "WHERE f.USER_ID_FROM = ?";

        return new HashSet<>(jdbcTemplate.query(sqlQuery, new UserRowMapper(), id));
    }

    @Override
    public Set<User> getCommonFriends(int user1Id, int user2Id) {
        Set<User> friendsSet1 = getFriends(user1Id);
        Set<User> friendsSet2 = getFriends(user2Id);

        return friendsSet1.stream()
                .filter(friendsSet2::contains)
                .collect(Collectors.toSet());
    }
}
