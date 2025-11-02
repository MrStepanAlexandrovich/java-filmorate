package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.MPARating;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class MPARowMapper implements RowMapper<MPARating> {

    @Override
    public MPARating mapRow(ResultSet rs, int rowNum) throws SQLException {
        MPARating mpaRating = new MPARating();
        mpaRating.setId(rs.getInt("id"));
        mpaRating.setRating("mpa");

        return mpaRating;
    }
}
