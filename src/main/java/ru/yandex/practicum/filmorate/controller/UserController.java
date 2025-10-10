package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private UserStorage userStorage;
    private UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Getting users...");
        return userStorage.getUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userStorage.add(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userStorage.update(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        userStorage.delete(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUsersFriends(@PathVariable int id) {
        User user = userStorage.findById(id);
        return user.getFriends();
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriendsWithUser(
            @PathVariable int id,
            @PathVariable int otherId
    ) {
        return userService.getCommonFriends();
    }


}
