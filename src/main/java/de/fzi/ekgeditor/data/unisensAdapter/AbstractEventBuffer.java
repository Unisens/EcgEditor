package de.fzi.ekgeditor.data.unisensAdapter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.unisens.Event;
import org.unisens.EventEntry;

public abstract class AbstractEventBuffer {
	protected EventEntry eventEntry;
	protected List<Event> events;
	protected boolean changed = false;
	protected boolean tempEventEntry = false;
	
	public AbstractEventBuffer(EventEntry eventEntry){
		try {
			this.eventEntry = eventEntry;
			events = eventEntry.read(0, (int)eventEntry.getCount());
			Collections.sort(events, EventComparator.getInstance());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public AbstractEventBuffer(EventEntry eventEntry, List<Event> events){
		this.eventEntry = eventEntry;
		this.events = events;
		this.tempEventEntry = true;
		this.changed = true;
		Collections.sort(events, EventComparator.getInstance());
	}
	
	public List<Event> read(long startSample, long endSample){
		int startEventIndex = Collections.binarySearch(events, new Event(startSample, "", ""), EventComparator.getInstance());
		int endEventIndex = Collections.binarySearch(events, new Event(endSample, "", ""), EventComparator.getInstance());
		
		startEventIndex = (startEventIndex >= 0) ? startEventIndex : Math.abs(startEventIndex + 1);
		endEventIndex = (endEventIndex >= 0) ? endEventIndex + 1 : Math.abs(endEventIndex + 1);
		
		return events.subList(startEventIndex, endEventIndex);
	}
	
	public void add(Event event){
		changed = true;
		int eventIndex = Collections.binarySearch(events, event, EventComparator.getInstance());
		if(eventIndex < 0){
			eventIndex = Math.abs(eventIndex + 1);
			events.add(eventIndex, event);
		}
	}
	
	public void save() throws IOException{
		if(tempEventEntry){
			eventEntry.append(events);
			tempEventEntry = false;
			changed = false;
		}else{
			if(changed){
				eventEntry.empty();
				eventEntry.append(events);
				changed = false;
			}
		}
	}
	
	public void clear(){
		events.clear();
		changed = true;
	}
	
	public boolean isTempEventEntry() {
		return tempEventEntry;
	}
	
	public EventEntry getEventEntry() {
		return eventEntry;
	}
	
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	public boolean isChanged() {
		return changed;
	}
}
