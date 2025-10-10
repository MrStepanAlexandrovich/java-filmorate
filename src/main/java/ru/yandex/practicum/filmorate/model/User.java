package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    private int id;

    @NotBlank
    @Email(message = "Incorrect email!")
    private String email;

    @NotBlank(message = "Login field must not be empty!")
    @Pattern(regexp = "^\\S*$", message = "Login shouldn't contain any spaces!")
    private String login;

    private String name;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private Set<User> friends;

    private Set<Film> likedFilms;

    public boolean birthdayIsValid() {
        return birthday.isAfter(LocalDate.now());
    }

    public void addFriend(User user) {
        friends.add(user);
    }

    public void deleteFriend(User user) {
        friends.remove(user);
    }
}
