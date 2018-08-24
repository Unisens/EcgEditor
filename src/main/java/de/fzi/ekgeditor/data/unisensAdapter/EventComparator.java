package de.fzi.ekgeditor.data.unisensAdapter;

import java.util.Comparator;

import org.unisens.Event;

public class EventComparator implements Comparator<Event>{
	private static EventComparator eventComparator = new EventComparator();
	
	public static EventComparator getInstance(){
		return eventComparator;
	} 
	@Override
	public int compare(Event o1, Event o2) {
		if(o1.getSampleStamp() < o2.getSampleStamp())
			return -1;
		if(o1.getSampleStamp() > o2.getSampleStamp())
			return 1;
		return o1.getType().compareToIgnoreCase(o2.getType());
	}
}
