package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.List;

public interface MpaStorage {
    public List<MPARating> getMpas();

    public MPARating getMpa(int id);
}
