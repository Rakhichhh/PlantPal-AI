package com.plantpal.pattern;

import com.plantpal.domain.*;
import com.plantpal.pattern.builder.PlantBuilder;
import com.plantpal.pattern.factory.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class CareTaskFactoryTest {
    private final Plant plant = new PlantBuilder().nickname("Snake Plant")
        .species("Dracaena trifasciata").room("Bedroom").wateringIntervalDays(14).build();

    @Test void soilCreatorRespectsPlantInterval() {
        var start = LocalDate.of(2026, 11, 9);
        var task = new SoilCheckCreator().schedule(plant, start);
        assertEquals(CareTaskType.SOIL_CHECK, task.getType());
        assertEquals(start.plusDays(14), task.getDueDate());
    }
    @Test void eachCreatorCreatesItsOwnType() {
        var start = LocalDate.of(2026, 11, 9);
        assertEquals(CareTaskType.FERTILIZE, new FertilizeCreator().schedule(plant,start).getType());
        assertEquals(CareTaskType.INSPECT, new InspectionCreator().schedule(plant,start).getType());
    }
    @Test void rejectsMissingPlant() {
        assertThrows(IllegalArgumentException.class, () -> new SoilCheckCreator().schedule(null,LocalDate.now()));
    }
}
