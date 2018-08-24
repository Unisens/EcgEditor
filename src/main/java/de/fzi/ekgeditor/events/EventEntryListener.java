package de.fzi.ekgeditor.events;

import java.util.EventListener;

public interface EventEntryListener extends EventListener{
	
	public void eventEntryChanged(EventEntryEvent eventEntryEvent);
}
