package com.plantpal.api;
import com.plantpal.domain.*;
import com.plantpal.service.CarePlanningService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
@RestController @RequestMapping("/api")
public class CarePlanningController {
 private final CarePlanningService service;
 public CarePlanningController(CarePlanningService service){this.service=service;}
 @GetMapping("/plants/{id}/care-settings") public CareSettings settings(@PathVariable Long id){return service.getSettings(id);}
 @PutMapping("/plants/{id}/care-settings") public CareSettings change(@PathVariable Long id,@RequestBody ModeRequest r){return service.changeMode(id,r.mode());}
 @GetMapping("/notifications") public List<CareNotification> notifications(){return service.recentNotifications();}
 @PatchMapping("/notifications/{id}/read") @ResponseStatus(HttpStatus.NO_CONTENT) public void read(@PathVariable Long id){service.markRead(id);}
 public record ModeRequest(CareMode mode){}
}
