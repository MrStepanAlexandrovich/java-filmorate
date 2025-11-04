package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class MPARating {
    private int id;
    private String name;

    public boolean idIsValid() {
        return id <= 5 && id >= 1;
    }
}

