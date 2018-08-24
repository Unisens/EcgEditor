package de.fzi.ekgeditor.events;

import java.util.EventObject;

public class EventEntryEvent extends EventObject {
	static final long serialVersionUID = 83728737273464L;
	
	public EventEntryEvent(Object source){
		super(source);
	}
}
