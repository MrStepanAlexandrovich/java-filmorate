package ru.yandex.practicum.filmorate.storage.friend;

public interface FriendStorage {
    public void addFriend(int userIdFrom, int userIdTo);

    public void deleteFriend(int userIdFrom, int userIdTo);
}
