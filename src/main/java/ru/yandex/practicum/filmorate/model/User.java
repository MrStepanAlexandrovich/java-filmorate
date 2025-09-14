package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class User {
    private int id;
    private String email;
    private String login;
    private String name;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private Date birthday;
}
