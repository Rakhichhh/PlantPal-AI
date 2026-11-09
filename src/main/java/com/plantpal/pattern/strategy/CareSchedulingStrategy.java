package com.plantpal.pattern.strategy;
import com.plantpal.domain.Plant;
import com.plantpal.domain.CareMode;
import java.time.LocalDate;
/** Strategy: alternative algorithms produce different dates using the same input. */
public interface CareSchedulingStrategy {
 CareMode mode();
 LocalDate nextSoilCheck(Plant plant, LocalDate from);
}
