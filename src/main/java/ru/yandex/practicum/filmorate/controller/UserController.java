package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private int counter = 0;
    List<User> users = new ArrayList<>();

    @GetMapping
    public List<User> getUsers() {
        log.info("Getting users...");
        return users;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (!user.getBirthday().isAfter(LocalDate.now())) {
            if (user.getName() == null) {
                user.setName(user.getLogin());
            }
            user.setId(++counter);
            users.add(user);
            log.info("User was successfully added!");
            return user;
        } else {
            log.info("Incorrect birthday date! User wasn't added!");
            throw new ValidationException("Incorrect birthday date!");
        }
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        int id = user.getId();

        Optional<User> optionalUser = users.stream()
                .filter(user1 -> user1.getId() == id)
                .findFirst();
        if (optionalUser.isEmpty()) {
            log.info("User with such ID wasn't found!");
            throw new ValidationException("User with such ID wasn't found!");
        } else {
            if (!user.getBirthday().isAfter(LocalDate.now())) {
                User oldUser = optionalUser.get();
                users.set(users.indexOf(oldUser), user);
                log.info("User was successfully updated!");
                return user;
            } else {
                log.info("Incorrect birthday date!");
                throw new ValidationException("Incorrect birthday date!");
            }
        }
    }
}
