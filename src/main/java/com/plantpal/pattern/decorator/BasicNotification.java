package com.plantpal.pattern.decorator;
public record BasicNotification(String message) implements NotificationText {
 public BasicNotification {if(message==null||message.isBlank())throw new IllegalArgumentException("Message is required");}
 public String render(){return message;}
}
