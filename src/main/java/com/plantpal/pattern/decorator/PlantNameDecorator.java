package com.plantpal.pattern.decorator;
public class PlantNameDecorator extends NotificationDecorator {
 private final String plantName;
 public PlantNameDecorator(NotificationText wrapped,String plantName){super(wrapped);this.plantName=plantName==null?"Plant":plantName;}
 @Override public String render(){return plantName+": "+wrapped.render();}
}
