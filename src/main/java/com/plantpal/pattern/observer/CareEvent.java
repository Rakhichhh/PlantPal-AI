package com.plantpal.pattern.observer;
public record CareEvent(CareEventType type, Long plantId) {
 public CareEvent {
  if(type==null||plantId==null)throw new IllegalArgumentException("Event type and plant ID required");
 }
}
