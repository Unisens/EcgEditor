package de.fzi.ekgeditor.data;

import java.util.List;

import org.unisens.EventEntry;
import org.unisens.Group;
import org.unisens.Unisens;

public interface IEventEntryModel {
	void loadEventEntries(Unisens unisens, Group group);
	void setActiveEventEntry(EventEntry eventEntry);
	public void saveActiveEventEntryAs(EntryData eventEntryData, boolean addToDefaultEcgGroup);
	EventEntry getActiveEventEntry();
	boolean isActiveEventEntryChanged();
	List<EventEntry> getEventEntries();
	Unisens getUnisens();
	EventEntry getEventEntry(String eventEntryId);
	boolean isActiveEventEntryTemp();
	public void removeActiveEventEntry();
}
