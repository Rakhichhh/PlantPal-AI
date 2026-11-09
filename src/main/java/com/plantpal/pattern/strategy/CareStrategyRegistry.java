package com.plantpal.pattern.strategy;
import com.plantpal.domain.CareMode;
import org.springframework.stereotype.Component;
import java.util.*;
@Component
public class CareStrategyRegistry {
 private final Map<CareMode,CareSchedulingStrategy> strategies=new EnumMap<>(CareMode.class);
 public CareStrategyRegistry(List<CareSchedulingStrategy> available){
  for(var s:available)if(strategies.putIfAbsent(s.mode(),s)!=null)throw new IllegalStateException("Duplicate strategy: "+s.mode());
  for(var mode:CareMode.values())if(!strategies.containsKey(mode))throw new IllegalStateException("Missing strategy: "+mode);
 }
 public CareSchedulingStrategy forMode(CareMode mode){
  if(mode==null || !strategies.containsKey(mode))throw new IllegalArgumentException("Unsupported care mode");
  return strategies.get(mode);
 }
}
