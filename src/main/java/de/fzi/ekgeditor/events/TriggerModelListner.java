package de.fzi.ekgeditor.events;

import java.util.EventListener;

public interface TriggerModelListner extends EventListener{
	public void activeTriggerEntryChanged(TriggerModelEvent triggerModelEvent);
}
