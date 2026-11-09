package com.plantpal.api;
import com.plantpal.domain.LightLevel;
import jakarta.validation.constraints.*;
public record PlantRequest(
    @NotBlank @Size(max=80) String nickname,
    @NotBlank @Size(max=120) String species,
    @NotBlank @Size(max=80) String room,
    @NotNull LightLevel lightLevel,
    @Min(1) @Max(90) int wateringIntervalDays,
    @Size(max=500) String notes,
    @Size(max=500) String photoUrl
) { }
