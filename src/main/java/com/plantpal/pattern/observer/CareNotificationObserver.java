package com.plantpal.pattern.observer;
import com.plantpal.domain.CareNotification;
import com.plantpal.repository.CareNotificationRepository;
import org.springframework.stereotype.Component;
import java.time.Instant;
@Component
public class CareNotificationObserver implements CareObserver {
 private final CareNotificationRepository notifications;
 public CareNotificationObserver(CareNotificationRepository notifications){this.notifications=notifications;}
 public int priority(){return 20;}
 public void onCareEvent(CareEvent event){
  String message=switch(event.type()){
   case PLANT_CREATED -> "Plant added: care schedule created";
   case PLANT_UPDATED -> "Plant updated: care schedule recalculated";
   case CARE_MODE_CHANGED -> "Care strategy changed: schedule recalculated";
   case SOIL_CHECK_COMPLETED -> "Soil check completed: next check scheduled";
  };
  notifications.save(new CareNotification(event.plantId(),message,Instant.now()));
 }
}
