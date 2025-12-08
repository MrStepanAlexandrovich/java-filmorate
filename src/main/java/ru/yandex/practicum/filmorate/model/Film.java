package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Film {
    private Integer id;

    @NotBlank(message = "Name field must not be empty!")
    private String name;

    @Size(max = 200)
    private String description;

    @NonNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @NonNull
    @NotNull(message = "Duration must not be null!")
    @JsonDeserialize(using = DurationDeserializer.class)
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;

    @JsonIgnore
    private List<User> likedUsers = new ArrayList<>();

    @NotNull
    private MPARating mpa = new MPARating();

    private Set<Genre> genres = new HashSet<>();

    @JsonIgnore
    private int likesAmount;

    public boolean releaseDateIsValid() {
        return releaseDate.isAfter(LocalDate.of(1895, 1, 28))
                || releaseDate.equals(LocalDate.of(1895, 1, 28));
    }

    public boolean durationIsValid() {
        return duration.isPositive();
    }

    public int getLikesAmount() {
        return likedUsers.size();
    }

    private static class DurationDeserializer extends JsonDeserializer<Duration> {
        @Override
        public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException, JacksonException {
            int minutes = jsonParser.getIntValue();

            return Duration.of(minutes, ChronoUnit.MINUTES);
        }
    }

    private static class DurationSerializer extends JsonSerializer<Duration> {
        @Override
        public void serialize(Duration duration, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
                throws IOException {
            jsonGenerator.writeNumber(duration.toMinutes());
        }
    }
}
