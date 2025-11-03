package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
        userStorage.addFriend(id, friendId);
    }

    public void deleteFriend(int id, int friendId) {
        userStorage.deleteFriend(id, friendId);
    }

    public Set<User> getFriends(int userId) {
        Set<User> friends = userStorage.getFriends(userId);
        return friends;
    }

    public List<User> getCommonFriends(int id, int friendId) {
        Set<Integer> user1FriendsId = userStorage.findById(id).getFriends();
        Set<Integer> user2FriendsId = userStorage.findById(friendId).getFriends();

        return user1FriendsId.stream()
                .filter(user2FriendsId::contains)
                .map(userStorage::findById)
                .toList();
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
