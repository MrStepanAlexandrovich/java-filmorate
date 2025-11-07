package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public void addFriend(int id, int friendId) {
        userStorage.findById(id);
        userStorage.findById(friendId);

        friendStorage.addFriend(id, friendId);
    }

    public void deleteFriend(int id, int friendId) {
        userStorage.findById(id);
        userStorage.findById(friendId); //Проверка существования пользователей

        friendStorage.deleteFriend(id, friendId);
    }

    public Set<User> getFriends(int userId) {
        Set<User> friends = userStorage.getFriends(userId);
        return friends;
    }

    public Set<User> getCommonFriends(int user1Id, int user2Id) {
        return userStorage.getCommonFriends(user1Id, user2Id);
    }

    public User add(User user) {
        return userStorage.add(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void delete(int id) {
        userStorage.delete(id);
    }
}
