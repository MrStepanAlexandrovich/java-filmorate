package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class UserDbStorageIntegrationTest {

    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void clearDb() {
        jdbcTemplate.update("DELETE FROM friendships");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    public void addAndFindUser() {
        User u = new User();
        u.setName("John Doe");
        u.setLogin("johnd");
        u.setEmail("john@example.com");
        u.setBirthday(LocalDate.of(1990, 1, 1));

        User saved = userDbStorage.add(u);
        assertNotNull(saved);
        assertTrue(saved.getId() > 0);

        User fetched = userDbStorage.findById(saved.getId());
        assertEquals(saved.getId(), fetched.getId());
        assertEquals("John Doe", fetched.getName());
        assertEquals("johnd", fetched.getLogin());
        assertEquals("john@example.com", fetched.getEmail());
        assertEquals(LocalDate.of(1990, 1, 1), fetched.getBirthday());
    }

    @Test
    public void updateUser() {
        User u = new User();
        u.setName("Alice");
        u.setLogin("alice");
        u.setEmail("alice@example.com");
        u.setBirthday(LocalDate.of(1985, 5, 5));

        User saved = userDbStorage.add(u);

        saved.setName("Alice Updated");
        saved.setEmail("alice.updated@example.com");

        User updated = userDbStorage.update(saved);
        assertEquals(saved.getId(), updated.getId());

        User fetched = userDbStorage.findById(saved.getId());
        assertEquals("Alice Updated", fetched.getName());
        assertEquals("alice.updated@example.com", fetched.getEmail());
    }

    @Test
    public void addAndDeleteFriendAndGetFriends() {
        User u1 = new User();
        u1.setName("U1");
        u1.setLogin("u1");
        u1.setEmail("u1@example.com");
        u1.setBirthday(LocalDate.of(1990, 1, 1));

        User u2 = new User();
        u2.setName("U2");
        u2.setLogin("u2");
        u2.setEmail("u2@example.com");
        u2.setBirthday(LocalDate.of(1991, 2, 2));

        User s1 = userDbStorage.add(u1);
        User s2 = userDbStorage.add(u2);

        userDbStorage.addFriend(s1.getId(), s2.getId());

        Set<User> friendsOf1 = userDbStorage.getFriends(s1.getId());
        assertNotNull(friendsOf1);
        assertTrue(friendsOf1.stream().anyMatch(u -> u.getId() == s2.getId()));

        userDbStorage.deleteFriend(s1.getId(), s2.getId());
        Set<User> friendsAfterDelete = userDbStorage.getFriends(s1.getId());
        assertTrue(friendsAfterDelete.isEmpty());
    }

    @Test
    public void getCommonFriends() {
        User u1 = new User();
        u1.setName("User1");
        u1.setLogin("user1");
        u1.setEmail("user1@example.com");
        u1.setBirthday(LocalDate.of(1990, 1, 1));

        User u2 = new User();
        u2.setName("User2");
        u2.setLogin("user2");
        u2.setEmail("user2@example.com");
        u2.setBirthday(LocalDate.of(1991, 2, 2));

        User m = new User();
        m.setName("Mutual");
        m.setLogin("mutual");
        m.setEmail("mutual@example.com");
        m.setBirthday(LocalDate.of(1992, 3, 3));

        User s1 = userDbStorage.add(u1);
        User s2 = userDbStorage.add(u2);
        User sm = userDbStorage.add(m);

        userDbStorage.addFriend(s1.getId(), sm.getId());
        userDbStorage.addFriend(s2.getId(), sm.getId());

        Set<User> common = userDbStorage.getCommonFriends(s1.getId(), s2.getId());
        assertNotNull(common);
        assertEquals(1, common.size());
        assertTrue(common.stream().anyMatch(u -> u.getId() == sm.getId()));
    }

    @Test
    public void deleteUserShouldThrowNotFound() {
        User u = new User();
        u.setName("ToDelete");
        u.setLogin("todel");
        u.setEmail("todel@example.com");
        u.setBirthday(LocalDate.of(1990, 4, 4));

        User saved = userDbStorage.add(u);
        assertNotNull(userDbStorage.findById(saved.getId()));

        userDbStorage.delete(saved.getId());
        assertThrows(NotFoundException.class, () -> userDbStorage.findById(saved.getId()));
    }
}

