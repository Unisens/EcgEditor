package de.fzi.ekgeditor.events;

import java.util.EventListener;

public interface EventModelListener extends EventListener
{
	public void activeEventEntryChanged();

	public void eventSelected();

//	public void selectedEventModified();
}
