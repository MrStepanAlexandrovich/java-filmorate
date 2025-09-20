package ru.yandex.practicum.filmorate;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    public void validationOnAddMethodTest() {
        Film film = new Film(1, "name", "desc", LocalDate.of(1895, 1, 27),
                Duration.ofMinutes(90));

        //Проверка валидации даты релиза
        ValidationException validationException = assertThrows(ValidationException.class, ()
                -> filmController.addFilm(film));

        assertTrue(validationException.getMessage().contains("Film wasn't added because it's too old!"));
        assertEquals(0, filmController.getFilms().size());

        film.setReleaseDate(LocalDate.of(1895, 1, 28));
        filmController.addFilm(film);

        assertEquals(1, filmController.getFilms().size());

        filmController.getFilms().clear();

        //Проверка валидации длительности
        film.setDuration(Duration.ofMinutes(-90));
        ValidationException validationException1 = assertThrows(ValidationException.class, ()
                -> filmController.addFilm(film));

        assertTrue(validationException1.getMessage().contains("Film wasn't added because duration is negative!"));
        assertEquals(0, filmController.getFilms().size());
    }

    @Test
    public void validationOnUpdateMethod() {
        Film film = new Film(1, "name", "desc", LocalDate.of(1895, 1, 29),
                Duration.ofMinutes(90));

        filmController.addFilm(film);

        assertEquals(1, filmController.getFilms().size());

        Film newFilm = new Film(1, "name3", "descadsfad", LocalDate.of(1895, 1,
                27), Duration.ofMinutes(90));

        //Проверка валидации даты релиза
        ValidationException validationException = assertThrows(ValidationException.class, ()
                -> filmController.updateFilm(newFilm));

        assertTrue(validationException.getMessage().contains("Film wasn't updated because it's too old!"));

        assertEquals(film, filmController.getFilms().get(0));
        assertEquals(1, filmController.getFilms().size());

        newFilm.setReleaseDate(LocalDate.of(1895, 1, 28));
        filmController.updateFilm(newFilm);

        assertEquals(newFilm, filmController.getFilms().get(0));

        filmController.getFilms().clear();

        //Проверка валидации длительности
        filmController.addFilm(film);

        newFilm.setDuration(Duration.ofMinutes(-90));
        ValidationException validationException1 = assertThrows(ValidationException.class, ()
                -> filmController.updateFilm(newFilm));

        assertTrue(validationException1.getMessage().contains("Film with such ID wasn't found!"));
        assertNotEquals(newFilm, filmController.getFilms().get(0));
    }
}
