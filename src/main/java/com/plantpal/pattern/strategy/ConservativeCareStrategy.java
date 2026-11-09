package com.plantpal.pattern.strategy;
import com.plantpal.domain.*;
import java.time.LocalDate;
import org.springframework.stereotype.Component;
/** More frequent soil checks; does NOT automatically demand more watering. */
@Component
public class ConservativeCareStrategy implements CareSchedulingStrategy {
 public CareMode mode(){return CareMode.CONSERVATIVE;}
 public LocalDate nextSoilCheck(Plant plant,LocalDate from){
  return from.plusDays(Math.max(1,plant.getWateringIntervalDays()-2));
 }
}
