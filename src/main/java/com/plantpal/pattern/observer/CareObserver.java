package com.plantpal.pattern.observer;
/** Observer contract implemented by independent subscribers. */
public interface CareObserver {
 int priority();
 void onCareEvent(CareEvent event);
}
