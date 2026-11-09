package com.plantpal.pattern;
import com.plantpal.domain.*;
import com.plantpal.pattern.strategy.*;
import com.plantpal.pattern.observer.*;
import com.plantpal.pattern.builder.PlantBuilder;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class ObserverStrategyPatternsTest {
 private final Plant plant=new PlantBuilder().nickname("Monstera").species("Monstera deliciosa")
  .room("Living Room").wateringIntervalDays(7).build();
 @Test void standardUsesConfiguredInterval(){
  assertEquals(LocalDate.of(2026,11,16),new StandardCareStrategy().nextSoilCheck(plant,LocalDate.of(2026,11,9)));
 }
 @Test void conservativeChecksSoilEarlier(){
  var date=LocalDate.of(2026,11,9);
  assertTrue(new ConservativeCareStrategy().nextSoilCheck(plant,date).isBefore(new StandardCareStrategy().nextSoilCheck(plant,date)));
 }
 @Test void strategiesNeverScheduleInPastOrSameDay(){
  Plant small=new PlantBuilder().nickname("A").species("B").room("C").wateringIntervalDays(1).build();
  assertEquals(LocalDate.of(2026,11,10),new ConservativeCareStrategy().nextSoilCheck(small,LocalDate.of(2026,11,9)));
 }
 @Test void registrySelectsCorrectAlgorithm(){
  var registry=new CareStrategyRegistry(List.of(new StandardCareStrategy(),new ConservativeCareStrategy()));
  assertEquals(CareMode.CONSERVATIVE,registry.forMode(CareMode.CONSERVATIVE).mode());
 }
 @Test void publisherNotifiesBothSubscribersInPriorityOrder(){
  var calls=new ArrayList<String>();
  CareObserver later=new CareObserver(){public int priority(){return 20;}public void onCareEvent(CareEvent e){calls.add("notification");}};
  CareObserver earlier=new CareObserver(){public int priority(){return 10;}public void onCareEvent(CareEvent e){calls.add("schedule");}};
  new CareEventPublisher(List.of(later,earlier)).publish(new CareEvent(CareEventType.PLANT_UPDATED,1L));
  assertEquals(List.of("schedule","notification"),calls);
 }
 @Test void eventRejectsNullPlant(){assertThrows(IllegalArgumentException.class,()->new CareEvent(CareEventType.PLANT_UPDATED,null));}
}
