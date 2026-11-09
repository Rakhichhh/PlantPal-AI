package com.plantpal.pattern.decorator;
public class CareTipDecorator extends NotificationDecorator {
 private final String tip;
 public CareTipDecorator(NotificationText wrapped,String tip){super(wrapped);this.tip=tip==null?"":tip;}
 @Override public String render(){return wrapped.render()+(tip.isBlank()?"":" · Tip: "+tip);}
}
