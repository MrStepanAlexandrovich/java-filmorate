package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    void delete(int id);

    User add(User user);

    User update(User user);

    List<User> getUsers();

    User findById(int id);

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);
}
