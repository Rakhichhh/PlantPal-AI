package com.plantpal.pattern.observer;
import com.plantpal.domain.CareNotification;
import com.plantpal.pattern.decorator.*;
import com.plantpal.repository.CareNotificationRepository;
import com.plantpal.repository.PlantRepository;
import org.springframework.stereotype.Component;
import java.time.Instant;
/** Student 3: existing Observer creates composable Decorator notifications. */
@Component
public class CareNotificationObserver implements CareObserver {
 private final CareNotificationRepository notifications;
 private final PlantRepository plants;
 public CareNotificationObserver(CareNotificationRepository notifications,PlantRepository plants){this.notifications=notifications;this.plants=plants;}
 public int priority(){return 20;}
 public void onCareEvent(CareEvent event){
  String message=switch(event.type()){
   case PLANT_CREATED -> "Care schedule created";
   case PLANT_UPDATED -> "Care schedule recalculated";
   case CARE_MODE_CHANGED -> "Care strategy changed";
   case SOIL_CHECK_COMPLETED -> "Next soil check scheduled";
  };
  NotificationText decorated=new BasicNotification(message);
  var plant=plants.findById(event.plantId());
  if(plant.isPresent())decorated=new PlantNameDecorator(decorated,plant.get().getNickname());
  if(event.type()==CareEventType.SOIL_CHECK_COMPLETED)
    decorated=new CareTipDecorator(decorated,"Check soil moisture before watering");
  notifications.save(new CareNotification(event.plantId(),decorated.render(),Instant.now()));
 }
}
