package de.fzi.ekgeditor.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.unisens.DuplicateIdException;
import org.unisens.Entry;
import org.unisens.Event;
import org.unisens.EventEntry;
import org.unisens.Group;
import org.unisens.Unisens;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.data.unisensAdapter.ArtefactBuffer;
import de.fzi.ekgeditor.data.unisensAdapter.UnisensAdapter;
import de.fzi.ekgeditor.events.ArtefactModelListener;
import de.fzi.ekgeditor.events.EventModelListener;

public class EventModel
{
	private List<Entry> entryList;
	private List<Event> eventList;
	private Unisens unisens;
	private EventListenerList eventListeners = new EventListenerList();
	private EventEntry entry;

	public void loadEventEntries(Unisens unisens, Group group)
	{
		this.unisens = unisens;
		this.entryList = unisens.getEntries();

		for (Entry e : entryList)
		{
			System.out.println(e.getId());
			if (e instanceof EventEntry)
			{
				// Do nothing
			}
			else
			{
				this.entryList.remove(e.getId());
			}
		}

		if (this.entryList.size() > 0)
		{
			notifyActiveEventEntryChanged();
		}
	}

	public void addEventModelListener(EventModelListener eventModelListener)
	{
		eventListeners.add(EventModelListener.class, eventModelListener);
	}

	protected synchronized void notifyActiveEventEntryChanged()
	{
		for (EventModelListener l : eventListeners.getListeners(EventModelListener.class))
		{
			l.activeEventEntryChanged();
		}
	}

	public Event getSelectedEvent()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void readData(String entryId)
	{
		this.entry = (EventEntry) this.unisens.getEntry(entryId);
		
		int nRead;
		
		if (entry.getCount() > 1000)
		{
			nRead = 1000;
		}
		else
		{
			nRead = (int) entry.getCount();
		}
		
		try
		{
			eventList = entry.read(0, nRead);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			eventList = new ArrayList<Event>();
		}
		
	}
	
	public EventEntry getEntry()
	{
		return this.entry;
	}

	public List<Event> getEventList()
	{
		return eventList;
	}

	public void setSelectedEvent(org.unisens.Event selectedEvent)
	{
		// TODO Auto-generated method stub
		
	}
}
