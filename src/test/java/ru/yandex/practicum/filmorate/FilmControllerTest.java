package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTest {

    @Autowired
    private FilmController filmController;

    @Autowired
    private UserDbStorage userDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void clearDb() {
        jdbcTemplate.update("DELETE FROM liked_films");
        jdbcTemplate.update("DELETE FROM films_genres");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM friendships");
        jdbcTemplate.update("DELETE FROM users");
    }

    private Film makeFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofMinutes(120));

        MPARating mpa = new MPARating();
        mpa.setId(1);
        film.setMpa(mpa);

        return film;
    }

    @Test
    public void addAndGetFilms() {
        Film film = makeFilm("TestFilmDB");

        Film saved = filmController.addFilm(film);
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);

        Film fetched = filmController.getFilm(saved.getId());
        assertEquals(saved.getId(), fetched.getId());
        assertEquals("TestFilmDB", fetched.getName());
        assertEquals(1, fetched.getMpa().getId());
    }

    @Test
    public void likesFlowAndGetMostLikedFilmsAndDeleteLike() {
        User u1 = new User();
        u1.setName("User1");
        u1.setLogin("u1db");
        u1.setEmail("u1db@example.com");
        u1.setBirthday(LocalDate.of(1990, 1, 1));
        User savedU1 = userDbStorage.add(u1);

        User u2 = new User();
        u2.setName("User2");
        u2.setLogin("u2db");
        u2.setEmail("u2db@example.com");
        u2.setBirthday(LocalDate.of(1991, 2, 2));
        User savedU2 = userDbStorage.add(u2);

        Film f1 = filmController.addFilm(makeFilm("F1-db"));
        Film f2 = filmController.addFilm(makeFilm("F2-db"));

        filmController.userLikesFilm(f1.getId(), savedU1.getId());
        filmController.userLikesFilm(f1.getId(), savedU2.getId());
        filmController.userLikesFilm(f2.getId(), savedU1.getId());

        List<Film> top1 = filmController.getMostLikedFilms(1);
        assertNotNull(top1);
        assertEquals(1, top1.size());
        assertEquals(f1.getId(), top1.get(0).getId());

        filmController.userDeletesLike(f2.getId(), savedU1.getId());
        Integer remaining = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM liked_films WHERE film_id = ?", Integer.class, f2.getId());
        assertEquals(0, remaining);
    }

    @Test
    public void getNonExistingFilmShouldThrow() {
        assertThrows(Exception.class, () -> filmController.getFilm(99999));
    }
}