package com.plantpal.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "plants")
public class Plant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 80) private String nickname;
    @Column(nullable = false, length = 120) private String species;
    @Column(nullable = false, length = 80) private String room;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private LightLevel lightLevel;
    @Column(nullable = false) private int wateringIntervalDays;
    @Column(length = 500) private String notes;
    @Column(length = 500) private String photoUrl;
    @Column(nullable = false) private Instant createdAt;

    protected Plant() { } // JPA
    public Plant(String nickname, String species, String room, LightLevel lightLevel,
                 int wateringIntervalDays, String notes, String photoUrl, Instant createdAt) {
        this.nickname = nickname; this.species = species; this.room = room;
        this.lightLevel = lightLevel; this.wateringIntervalDays = wateringIntervalDays;
        this.notes = notes; this.photoUrl = photoUrl; this.createdAt = createdAt;
    }
    public Long getId() { return id; }
    public String getNickname() { return nickname; }
    public String getSpecies() { return species; }
    public String getRoom() { return room; }
    public LightLevel getLightLevel() { return lightLevel; }
    public int getWateringIntervalDays() { return wateringIntervalDays; }
    public String getNotes() { return notes; }
    public String getPhotoUrl() { return photoUrl; }
    public Instant getCreatedAt() { return createdAt; }
    public void update(String nickname, String species, String room, LightLevel lightLevel,
                       int wateringIntervalDays, String notes, String photoUrl) {
        this.nickname = nickname; this.species = species; this.room = room;
        this.lightLevel = lightLevel; this.wateringIntervalDays = wateringIntervalDays;
        this.notes = notes; this.photoUrl = photoUrl;
    }
}
