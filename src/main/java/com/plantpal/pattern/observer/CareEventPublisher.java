package com.plantpal.pattern.observer;
import java.util.*;
import org.springframework.stereotype.Component;
/** Explicit application-owned publisher rather than framework-only events. */
@Component
public class CareEventPublisher {
 private final List<CareObserver> observers;
 public CareEventPublisher(List<CareObserver> observers){
  this.observers=new ArrayList<>(observers);
  this.observers.sort(Comparator.comparingInt(CareObserver::priority));
 }
 public void publish(CareEvent event){for(var observer:observers)observer.onCareEvent(event);}
}
