package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage{
    private final JdbcTemplate jdbcTemplate;

    public List<MPARating> getMpas() {
        String sqlQuery = "SELECT * FROM MPA";
        return jdbcTemplate.query(con -> con.prepareStatement(sqlQuery), new MPARowMapper());
    }

    public MPARating getMpa(int id) {
        String sqlQuery = "SELECT * FROM MPA WHERE id = ?";
        List<MPARating> list = jdbcTemplate.query(con -> {
            var ps = con.prepareStatement(sqlQuery);
            ps.setInt(1, id);
            return ps;
        }, new MPARowMapper());
        MPARating mpaRating = list.stream().findFirst().orElse(null);
        if (mpaRating == null) {
            log.info("MPA rating with ID = " + id + " wasn't found");
        }
        return mpaRating;
    }
}
