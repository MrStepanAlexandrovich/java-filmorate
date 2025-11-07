package ru.yandex.practicum.filmorate.storage.like;

public interface LikeStorage {
    public void deleteLike(int userId, int filmId);

    public void addLike(int userId, int filmId);
}
