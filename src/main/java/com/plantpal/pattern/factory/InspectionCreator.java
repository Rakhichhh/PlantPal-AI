package com.plantpal.pattern.factory;

import com.plantpal.domain.*;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class InspectionCreator extends CareTaskCreator {
    @Override protected CareTask createTask(Plant plant, LocalDate start) {
        return new CareTask(plant, CareTaskType.INSPECT, "Inspect leaves and growth", start.plusDays(14));
    }
    @Override public CareTaskType supportedType() { return CareTaskType.INSPECT; }
}
