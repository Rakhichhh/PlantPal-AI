package com.plantpal.pattern.factory;

import com.plantpal.domain.CareTask;
import com.plantpal.domain.CareTaskType;
import com.plantpal.domain.Plant;
import java.time.LocalDate;

/** Factory Method: subclasses choose the concrete type of task to create. */
public abstract class CareTaskCreator {
    public final CareTask schedule(Plant plant, LocalDate start) {
        if (plant == null || start == null) throw new IllegalArgumentException("Plant and date required");
        return createTask(plant, start);
    }
    protected abstract CareTask createTask(Plant plant, LocalDate start);
    public abstract CareTaskType supportedType();
}
