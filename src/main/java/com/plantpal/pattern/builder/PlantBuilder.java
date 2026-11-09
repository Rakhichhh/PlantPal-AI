package com.plantpal.pattern.builder;

import com.plantpal.domain.LightLevel;
import com.plantpal.domain.Plant;
import java.time.Instant;

/** Builder pattern: a validated, named-step construction process for Plant. */
public final class PlantBuilder {
    private String nickname, species, room, notes = "", photoUrl = "";
    private LightLevel lightLevel = LightLevel.INDIRECT;
    private int wateringIntervalDays = 7;
    private Instant createdAt = Instant.now();

    public PlantBuilder nickname(String value) { nickname = value; return this; }
    public PlantBuilder species(String value) { species = value; return this; }
    public PlantBuilder room(String value) { room = value; return this; }
    public PlantBuilder lightLevel(LightLevel value) { lightLevel = value; return this; }
    public PlantBuilder wateringIntervalDays(int value) { wateringIntervalDays = value; return this; }
    public PlantBuilder notes(String value) { notes = value; return this; }
    public PlantBuilder photoUrl(String value) { photoUrl = value; return this; }
    public PlantBuilder createdAt(Instant value) { createdAt = value; return this; }

    public Plant build() {
        if (blank(nickname) || blank(species) || blank(room))
            throw new IllegalArgumentException("Name, species and room are required");
        if (nickname.length() > 80 || species.length() > 120 || room.length() > 80)
            throw new IllegalArgumentException("Plant text is too long");
        if (wateringIntervalDays < 1 || wateringIntervalDays > 90)
            throw new IllegalArgumentException("Check interval must be between 1 and 90 days");
        if (lightLevel == null || createdAt == null)
            throw new IllegalArgumentException("Light level and creation date are required");
        if (notes != null && notes.length() > 500)
            throw new IllegalArgumentException("Notes cannot exceed 500 characters");
        if (photoUrl != null && photoUrl.length() > 500)
            throw new IllegalArgumentException("Photo URL is too long");
        return new Plant(nickname.trim(), species.trim(), room.trim(), lightLevel,
                wateringIntervalDays, notes == null ? "" : notes.trim(),
                photoUrl == null ? "" : photoUrl.trim(), createdAt);
    }
    private boolean blank(String s) { return s == null || s.isBlank(); }
}
