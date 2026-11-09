package com.plantpal.pattern.strategy;
import com.plantpal.domain.*;
import java.time.LocalDate;
import org.springframework.stereotype.Component;
@Component
public class StandardCareStrategy implements CareSchedulingStrategy {
 public CareMode mode(){return CareMode.STANDARD;}
 public LocalDate nextSoilCheck(Plant plant, LocalDate from){
  return from.plusDays(Math.max(1,plant.getWateringIntervalDays()));
 }
}
