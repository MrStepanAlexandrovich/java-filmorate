package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int id, int friendId) {
        User user = userStorage.findById(id);
        User friend = userStorage.findById(friendId);

        user.addFriend(friend);
        friend.addFriend(user);
    }

    public void deleteFriend(int id, int friendId) {
        User user = userStorage.findById(id);
        User friend = userStorage.findById(friendId);

        user.deleteFriend(friend);
        friend.deleteFriend(user);
    }

    public List<User> getCommonFriends(int id, int friendId) {
        User user1 = userStorage.findById(id);
        User user2 = userStorage.findById(friendId);

        return user1.getFriends().stream()
                .filter(user2.getFriends()::contains)
                .toList();
    }
}
