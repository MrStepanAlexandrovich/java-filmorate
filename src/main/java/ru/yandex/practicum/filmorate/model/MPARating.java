package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class MPARating {
    private int id;
    private String rating;

    public boolean idIsValid() {
        return id <= 5 && id >= 1;
    }
}

