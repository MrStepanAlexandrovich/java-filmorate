package ru.yandex.practicum.filmorate.storage.friend;

public interface FriendStorage {
    void addFriend(int userIdFrom, int userIdTo);

    void deleteFriend(int userIdFrom, int userIdTo);
}
