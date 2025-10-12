package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class User {
    @NonNull
    private int id;

    @NonNull
    @NotBlank
    @Email(message = "Incorrect email!")
    private String email;

    @NonNull
    @NotBlank(message = "Login field must not be empty!")
    @Pattern(regexp = "^\\S*$", message = "Login shouldn't contain any spaces!")
    private String login;

    @NonNull
    private String name;

    @NonNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private Set<Integer> friends = new HashSet<>();

    private Set<Integer> likedFilms = new HashSet<>();;

    public boolean birthdayIsValid() {
        return birthday.isAfter(LocalDate.now());
    }

    public void addFriend(User user) {
        friends.add(user.getId());
    }

    public void deleteFriend(int friendId) {
        friends.remove(friendId);
    }
}
