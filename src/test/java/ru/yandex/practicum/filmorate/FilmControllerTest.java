package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

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
        filmController.addFilm(film);
        assertEquals(0, filmController.getFilms().size());
        film.setReleaseDate(LocalDate.of(1895, 1, 28));
        filmController.addFilm(film);

        assertEquals(1, filmController.getFilms().size());

        filmController.getFilms().clear();

        //Проверка валидации длительности
        film.setDuration(Duration.ofMinutes(-90));
        filmController.addFilm(film);
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
        filmController.updateFilm(newFilm);
        assertEquals(film, filmController.getFilms().get(0));
        newFilm.setReleaseDate(LocalDate.of(1895, 1, 28));
        filmController.updateFilm(newFilm);

        assertEquals(newFilm, filmController.getFilms().get(0));

        filmController.getFilms().clear();

        //Проверка валидации длительности
        filmController.addFilm(film);

        newFilm.setDuration(Duration.ofMinutes(-90));
        filmController.updateFilm(newFilm);
        assertEquals(film, filmController.getFilms().get(0));
    }
}
