package de.fzi.ekgeditor.data.unisensAdapter;

import java.util.Collections;
import java.util.List;

import org.unisens.Event;
import org.unisens.EventEntry;

public class EventBuffer extends AbstractEventBuffer{
	public EventBuffer(EventEntry eventEntry){
		super(eventEntry);
	} 
	
	public EventBuffer(EventEntry eventEntry, List<Event> events){
		super(eventEntry, events);
	}
	
	public void delete(long startSample, long endSample){
		int startEventIndex = Collections.binarySearch(events, new Event(startSample, "", ""), EventComparator.getInstance());
		int endEventIndex = Collections.binarySearch(events, new Event(endSample, "", ""), EventComparator.getInstance());
		
		startEventIndex = (startEventIndex >= 0) ? startEventIndex : Math.abs(startEventIndex + 1);
		endEventIndex = (endEventIndex >= 0) ? endEventIndex + 1 : Math.abs(endEventIndex + 1);
		
		if(startEventIndex < endEventIndex)
			changed = true;
		for(int i = startEventIndex; i < endEventIndex; i++)
			events.remove(startEventIndex);
	}
}


