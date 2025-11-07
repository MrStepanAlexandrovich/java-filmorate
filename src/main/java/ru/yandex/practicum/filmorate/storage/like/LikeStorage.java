package ru.yandex.practicum.filmorate.storage.like;

public interface LikeStorage {
    void deleteLike(int userId, int filmId);

    void addLike(int userId, int filmId);
}
