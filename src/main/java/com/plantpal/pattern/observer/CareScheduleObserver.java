package com.plantpal.pattern.observer;
import com.plantpal.domain.*;
import com.plantpal.repository.*;
import com.plantpal.pattern.strategy.CareStrategyRegistry;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
@Component
public class CareScheduleObserver implements CareObserver {
 private final PlantRepository plants;
 private final CareTaskRepository tasks;
 private final CareSettingsRepository settings;
 private final CareStrategyRegistry registry;
 public CareScheduleObserver(PlantRepository plants,CareTaskRepository tasks,CareSettingsRepository settings,CareStrategyRegistry registry){
  this.plants=plants;this.tasks=tasks;this.settings=settings;this.registry=registry;
 }
 public int priority(){return 10;}
 public void onCareEvent(CareEvent event){
  Plant plant=plants.findById(event.plantId()).orElseThrow();
  CareSettings config=settings.findById(event.plantId()).orElseGet(()->settings.save(new CareSettings(event.plantId(),CareMode.STANDARD)));
  LocalDate next=registry.forMode(config.getMode()).nextSoilCheck(plant,LocalDate.now());
  // Only pending SOIL_CHECK tasks change. Historical completed records stay intact.
  var pending=tasks.findByPlantIdOrderByDueDateAsc(event.plantId()).stream()
   .filter(t->t.getType()==CareTaskType.SOIL_CHECK&&!t.isCompleted()).toList();
  if(pending.isEmpty()) {
   if(event.type()==CareEventType.SOIL_CHECK_COMPLETED)tasks.save(new CareTask(plant,CareTaskType.SOIL_CHECK,"Check soil moisture",next));
  } else {
   CareTask first=pending.get(0);first.setDueDate(next);tasks.save(first);
  }
 }
}
