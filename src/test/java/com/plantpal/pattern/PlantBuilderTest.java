package com.plantpal.pattern;

import com.plantpal.domain.LightLevel;
import com.plantpal.pattern.builder.PlantBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlantBuilderTest {
    @Test void buildsPlantWithOptionalData() {
        var p = new PlantBuilder().nickname("Monstera").species("Monstera deliciosa")
            .room("Living room").lightLevel(LightLevel.INDIRECT)
            .wateringIntervalDays(7).notes("Check soil").build();
        assertEquals("Monstera", p.getNickname());
        assertEquals(7, p.getWateringIntervalDays());
        assertEquals("Check soil", p.getNotes());
    }
    @Test void rejectsMissingName() {
        assertThrows(IllegalArgumentException.class, () -> new PlantBuilder()
            .nickname(" ").species("Pothos").room("Office").build());
    }
    @Test void rejectsOutOfRangeInterval() {
        assertThrows(IllegalArgumentException.class, () -> new PlantBuilder()
            .nickname("Pothos").species("Epipremnum aureum")
            .room("Office").wateringIntervalDays(0).build());
    }
}
