package com.plantpal.pattern.factory;

import com.plantpal.domain.*;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class FertilizeCreator extends CareTaskCreator {
    @Override protected CareTask createTask(Plant plant, LocalDate start) {
        return new CareTask(plant, CareTaskType.FERTILIZE, "Review fertilizer needs", start.plusDays(30));
    }
    @Override public CareTaskType supportedType() { return CareTaskType.FERTILIZE; }
}
