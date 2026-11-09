package com.plantpal.pattern.factory;

import com.plantpal.domain.*;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class SoilCheckCreator extends CareTaskCreator {
    @Override protected CareTask createTask(Plant plant, LocalDate start) {
        return new CareTask(plant, CareTaskType.SOIL_CHECK, "Check soil before watering",
                start.plusDays(plant.getWateringIntervalDays()));
    }
    @Override public CareTaskType supportedType() { return CareTaskType.SOIL_CHECK; }
}
