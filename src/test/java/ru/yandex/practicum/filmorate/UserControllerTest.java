package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void beforeEach() {
        jdbcTemplate.update("DELETE FROM friendships");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    public void addAndGetUsers() {
        User user = new User();
        user.setName("TestUserDB");
        user.setLogin("testdb");
        user.setEmail("testdb@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User saved = userController.addUser(user);
        assertNotNull(saved);
        assertTrue(saved.getId() > 0);

        assertTrue(userController.getUsers().stream().anyMatch(u -> u.getLogin().equals("testdb")));
    }

    @Test
    public void friendsFlow() {
        User u1 = new User();
        u1.setName("A");
        u1.setLogin("a_db");
        u1.setEmail("a_db@example.com");
        u1.setBirthday(LocalDate.of(1990, 1, 1));

        User u2 = new User();
        u2.setName("B");
        u2.setLogin("b_db");
        u2.setEmail("b_db@example.com");
        u2.setBirthday(LocalDate.of(1991, 2, 2));

        User s1 = userController.addUser(u1);
        User s2 = userController.addUser(u2);

        userController.addFriend(s1.getId(), s2.getId());
        Set<User> friends = userController.getUsersFriends(s1.getId());
        assertTrue(friends.stream().anyMatch(u -> u.getId() == s2.getId()));

        userController.deleteFriend(s1.getId(), s2.getId());
        Set<User> friendsAfter = userController.getUsersFriends(s1.getId());
        assertTrue(friendsAfter.isEmpty());
    }
}