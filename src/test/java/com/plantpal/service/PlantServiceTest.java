package com.plantpal.service;

import com.plantpal.api.PlantRequest;
import com.plantpal.domain.*;
import com.plantpal.pattern.factory.*;
import com.plantpal.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PlantServiceTest {
    @Mock PlantRepository plants;
    @Mock CareTaskRepository tasks;
    PlantService service;
    @BeforeEach void setup() {
        MockitoAnnotations.openMocks(this);
        when(plants.save(any(Plant.class))).thenAnswer(inv -> inv.getArgument(0));
        service = new PlantService(plants,tasks,List.of(new SoilCheckCreator(),new FertilizeCreator(),new InspectionCreator()));
    }
    @Test void addingPlantPersistsPlantAndThreeTasks() {
        var created = service.add(new PlantRequest("Pothos","Epipremnum aureum","Office",LightLevel.INDIRECT,8,"",""));
        assertEquals("Pothos",created.getNickname());
        verify(plants).save(any(Plant.class));
        verify(tasks,times(3)).save(any(CareTask.class));
    }
    @Test void deletingPlantCleansTasksFirst() {
        Plant p = new com.plantpal.pattern.builder.PlantBuilder().nickname("A").species("B").room("C").build();
        when(plants.findById(5L)).thenReturn(Optional.of(p));
        service.delete(5L);
        var order = inOrder(tasks, plants);
        order.verify(tasks).deleteByPlantId(5L);
        order.verify(plants).deleteById(5L);
    }
}
