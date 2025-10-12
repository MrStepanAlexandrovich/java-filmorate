package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {

    private static UserController userController;

    @BeforeEach
    public void beforeEach() {
        UserStorage userStorage = new InMemoryUserStorage();
        userController = new UserController(userStorage, new UserService(userStorage));
    }

    @Test
    public void ifNameIsBlankLoginBecomeNameToo() {
        User user = new User(1, "stepan@gmail.com", "name", "",
                LocalDate.of(2000, 11, 21));
        userController.addUser(user);
        assertEquals(user.getName(), user.getLogin());
    }
}
