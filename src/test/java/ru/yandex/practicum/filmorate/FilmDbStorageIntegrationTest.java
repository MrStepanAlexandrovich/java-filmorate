package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageIntegrationTest {

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private UserDbStorage userDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void clearDb() {
        jdbcTemplate.update("DELETE FROM liked_films");
        jdbcTemplate.update("DELETE FROM films_genres");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");
    }

    private Film makeFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Some description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofMinutes(120));

        MPARating mpa = new MPARating();
        mpa.setId(1);
        film.setMpa(mpa);

        Genre g1 = new Genre();
        g1.setId(1);
        Genre g2 = new Genre();
        g2.setId(2);

        Set<Genre> genres = new HashSet<>();
        genres.add(g1);
        genres.add(g2);

        film.setGenres(genres);
        return film;
    }

    @Test
    public void addAndGetFilm() {
        Film f = makeFilm("MyFilm");
        Film saved = filmDbStorage.add(f);
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);

        Film fetched = filmDbStorage.getFilm(saved.getId());
        assertEquals(saved.getId(), fetched.getId());
        assertEquals("MyFilm", fetched.getName());
        assertNotNull(fetched.getGenres());
        assertTrue(fetched.getGenres().size() >= 1);
        assertEquals(1, fetched.getMpa().getId());
    }

    @Test
    public void updateFilm() {
        Film f = makeFilm("OldName");
        Film saved = filmDbStorage.add(f);

        saved.setName("NewName");
        Film updated = filmDbStorage.update(saved);
        assertEquals("NewName", updated.getName());

        Film fromDb = filmDbStorage.getFilm(saved.getId());
        assertEquals("NewName", fromDb.getName());
    }

    @Test
    public void likes_and_getMostLikedFilms_and_deleteLike() {
        User u1 = new User();
        u1.setName("User1");
        u1.setLogin("u1");
        u1.setEmail("u1@example.com");
        u1.setBirthday(LocalDate.of(1990, 1, 1));
        User savedU1 = userDbStorage.add(u1);

        User u2 = new User();
        u2.setName("User2");
        u2.setLogin("u2");
        u2.setEmail("u2@example.com");
        u2.setBirthday(LocalDate.of(1991, 2, 2));
        User savedU2 = userDbStorage.add(u2);

        Film f1 = filmDbStorage.add(makeFilm("F1"));
        Film f2 = filmDbStorage.add(makeFilm("F2"));

        jdbcTemplate.update("INSERT INTO liked_films(user_id, film_id) VALUES (?, ?)", savedU1.getId(), f1.getId());
        jdbcTemplate.update("INSERT INTO liked_films(user_id, film_id) VALUES (?, ?)", savedU2.getId(), f1.getId());
        jdbcTemplate.update("INSERT INTO liked_films(user_id, film_id) VALUES (?, ?)", savedU1.getId(), f2.getId());

        List<Film> top1 = filmDbStorage.getMostLikedFilms(1);
        assertNotNull(top1);
        assertEquals(1, top1.size());
        assertEquals(f1.getId(), top1.get(0).getId());

        filmDbStorage.deleteLike(savedU1.getId(), f2.getId());
        int remaining = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM liked_films WHERE film_id = ?",
                Integer.class, f2.getId());
        assertEquals(0, remaining);
    }
}