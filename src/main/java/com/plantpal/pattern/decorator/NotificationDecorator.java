package com.plantpal.pattern.decorator;
/** Keeps component contract while delegating to wrapped message. */
public abstract class NotificationDecorator implements NotificationText {
 protected final NotificationText wrapped;
 protected NotificationDecorator(NotificationText wrapped){if(wrapped==null)throw new IllegalArgumentException("Wrapped notification required");this.wrapped=wrapped;}
}
