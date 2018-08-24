package de.fzi.ekgeditor.data;

import org.unisens.Event;

public class ArtefactUtils {
	
	public static boolean isStartArtefactEvent(Event event){
		return event.getType().startsWith("(");
	}
	
	public static String getEndArtefactEventType(String startArtefactEventType){
		return startArtefactEventType.replace('(', ')');
	}
}
