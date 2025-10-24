package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;

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

        user.deleteFriend(friendId);
        friend.deleteFriend(id);
    }

    public List<User> getFriends(int userId) {
        Set<Integer> friendsID = userStorage.findById(userId).getFriends();

        return friendsID.stream()
                .map(userStorage::findById)
                .toList();
    }

    public List<User> getCommonFriends(int id, int friendId) {
        Set<Integer> user1FriendsId = userStorage.findById(id).getFriends();
        Set<Integer> user2FriendsId = userStorage.findById(friendId).getFriends();

        return user1FriendsId.stream()
                .filter(user2FriendsId::contains)
                .map(userStorage::findById)
                .toList();
    }
}
