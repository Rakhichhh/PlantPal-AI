package com.plantpal.service;
import com.plantpal.domain.*;
import com.plantpal.pattern.observer.*;
import com.plantpal.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service
public class CarePlanningService {
 private final PlantRepository plants;
 private final CareSettingsRepository settings;
 private final CareNotificationRepository notifications;
 private final CareEventPublisher publisher;
 public CarePlanningService(PlantRepository plants,CareSettingsRepository settings,CareNotificationRepository notifications,CareEventPublisher publisher){
  this.plants=plants;this.settings=settings;this.notifications=notifications;this.publisher=publisher;
 }
 @Transactional(readOnly=true)
 public CareSettings getSettings(Long plantId){
  requirePlant(plantId);
  return settings.findById(plantId).orElse(new CareSettings(plantId,CareMode.STANDARD));
 }
 @Transactional
 public CareSettings changeMode(Long plantId,CareMode mode){
  requirePlant(plantId);
  if(mode==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Care mode required");
  CareSettings saved=settings.findById(plantId).orElse(new CareSettings(plantId,CareMode.STANDARD));
  saved.changeMode(mode);saved=settings.save(saved);
  publisher.publish(new CareEvent(CareEventType.CARE_MODE_CHANGED,plantId));
  return saved;
 }
 @Transactional(readOnly=true)
 public List<CareNotification> recentNotifications(){return notifications.findTop30ByOrderByCreatedAtDesc();}
 @Transactional
 public void markRead(Long id){
  CareNotification n=notifications.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Notification not found"));
  n.markRead();notifications.save(n);
 }
 private void requirePlant(Long plantId){if(!plants.existsById(plantId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Plant not found");}
}
