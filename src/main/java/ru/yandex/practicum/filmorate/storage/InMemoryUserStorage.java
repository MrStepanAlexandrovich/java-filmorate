package ru.yandex.practicum.filmorate.storage;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage{
    private int counter = 0;
    private List<User> users = new ArrayList<>();

    @Override
    public void delete(int id) {
        log.info("Deleting user...");
        Optional<User> optionalUser = users.stream()
                .filter(user -> user.getId() == id)
                .findFirst();
        if (optionalUser.isPresent()) {
            users.remove(optionalUser.get());
        }
    }

    @Override
    public User add(User user) {
        if (!user.birthdayIsValid()) {
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

    @Override
    public User update(User user) {
        int id = user.getId();

        Optional<User> optionalUser = users.stream()
                .filter(user1 -> user1.getId() == id)
                .findFirst();
        if (optionalUser.isEmpty()) {
            log.info("User with such ID wasn't found!");
            throw new ValidationException("User with such ID wasn't found!");
        } else {
            if (!user.birthdayIsValid()) {
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

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public Optional<User> findById(int id) {
        Optional<User> optionalUser = users.stream()
                .filter(user -> user.getId() == id)
                .findFirst();

        return optionalUser;
    }
}
