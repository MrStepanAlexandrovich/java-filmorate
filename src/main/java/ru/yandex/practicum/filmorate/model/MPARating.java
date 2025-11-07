package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MPARating {
    private int id;
    private String name;

    public boolean idIsValid() {
        return id <= 5 && id >= 1;
    }
}

