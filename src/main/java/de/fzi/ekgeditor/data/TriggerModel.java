package de.fzi.ekgeditor.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.unisens.CsvFileFormat;
import org.unisens.DuplicateIdException;
import org.unisens.Entry;
import org.unisens.Event;
import org.unisens.EventEntry;
import org.unisens.Group;
import org.unisens.Unisens;
import org.unisens.ri.CsvFileFormatImpl;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.data.unisensAdapter.EventBuffer;
import de.fzi.ekgeditor.data.unisensAdapter.UnisensAdapter;
import de.fzi.ekgeditor.events.TriggerModelEvent;
import de.fzi.ekgeditor.events.TriggerModelListner;

public class TriggerModel implements IEventEntryModel{
	protected EventBuffer eventBuffer;
	protected EventBuffer secondaryEventBuffer;
	protected List<EventEntry> triggerEntries = new ArrayList<EventEntry>();
	protected Unisens unisens;
	private boolean addTriggerMode = false;
	private boolean deleteTriggerMode = false;
	
	protected EventListenerList eventEntryListeners = new EventListenerList(); 
	
	public List<Event> readEvents(long startEventSample, long endEventSample){
    	if(eventBuffer == null)
    		return null;
    	return eventBuffer.read(startEventSample, endEventSample);
    }
	
	public List<Event> readSecondaryTriggerlistEvents(long startEventSample, long endEventSample){
    	if(secondaryEventBuffer == null)
    		return null;
    	return secondaryEventBuffer.read(startEventSample, endEventSample);
    }
	
	public void deleteEvents(long startEventSample, long endEventSample){
		eventBuffer.delete(startEventSample, endEventSample);
	}
	
	public void addEvent(Event event){
		eventBuffer.add(event);
	}
	
	public void loadEventEntries(Unisens unisens, Group group){
		if(eventBuffer != null){
			eventBuffer.clear();
			eventBuffer = null;
		}
		if(secondaryEventBuffer != null){
			secondaryEventBuffer.clear();
			secondaryEventBuffer = null;
		}
		this.unisens = unisens;
		this.triggerEntries = UnisensAdapter.getEventEntriesFromGroup(unisens, group, UnisensAdapter.TRIGGER_CONTENT_CLASS, true);
		this.triggerEntries.addAll(UnisensAdapter.getEventEntriesFromGroup(unisens, group, UnisensAdapter.PACER_CONTENT_CLASS, true));
		if(triggerEntries.size() > 0){
			this.eventBuffer = new EventBuffer(triggerEntries.get(0));
		}
		notifyActiveTriggerEntryChanged(new TriggerModelEvent(this));
	}
	
	
	public void saveActiveEventEntry(){
		try {
			eventBuffer.save();
			unisens.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	public void saveActiveEventEntryAs(EntryData eventEntryData, boolean addToDefaultEcgGroup){
		try {
			if(!eventEntryData.getId().endsWith(".csv"))
				eventEntryData.setId(eventEntryData.getId() + ".csv");
			EventEntry eventEntry = (EventEntry)unisens.getEntry(eventEntryData.getId());
			if(eventBuffer.isTempEventEntry()){
				if(eventEntry != null){
					unisens.deleteEntry(eventEntry);
					triggerEntries.remove(eventEntry);
				}
				
				eventEntry = eventBuffer.getEventEntry();
				eventEntry.rename(eventEntryData.getId());
				eventEntry.setComment(eventEntryData.getComment());
				eventEntry.setSampleRate(eventEntryData.getSampleRate());
				eventEntry.setSource(eventEntryData.getSource());
				eventEntry.setSourceId(eventEntryData.getSourceId());
				eventEntry.setContentClass(eventEntryData.getContentClass());

				eventBuffer.save();
				triggerEntries.add(eventEntry);
			}else{
				if(eventEntry == null){
					List<Event> events = eventBuffer.read(0, Integer.MAX_VALUE);
					eventEntry = unisens.createEventEntry(eventEntryData.getId(), eventEntryData.getSampleRate());
					eventEntry.setComment(eventEntryData.getComment());
					eventEntry.setSource(eventEntryData.getSource());
					eventEntry.setSourceId(eventEntryData.getSourceId());
					eventEntry.setContentClass(eventEntryData.getContentClass());
					eventEntry.append(events);
					triggerEntries.add(eventEntry);
					setActiveEventEntry(eventEntry);
				}else{
					if(eventEntry == eventBuffer.getEventEntry()){
						eventEntry.setComment(eventEntryData.getComment());
						eventEntry.setSampleRate(eventEntryData.getSampleRate());
						eventEntry.setSource(eventEntryData.getSource());
						eventEntry.setSourceId(eventEntryData.getSourceId());
						eventEntry.setContentClass(eventEntryData.getContentClass());
						eventBuffer.save();
					}else{
						List<Event> events = eventBuffer.read(0, Integer.MAX_VALUE);
						eventEntry.setComment(eventEntryData.getComment());
						eventEntry.setSampleRate(eventEntryData.getSampleRate());
						eventEntry.setSource(eventEntryData.getSource());
						eventEntry.setSourceId(eventEntryData.getSourceId());
						eventEntry.setContentClass(eventEntryData.getContentClass());
						eventEntry.empty();
						eventEntry.append(events);
						setActiveEventEntry(eventEntry);
					}
				}
			}
			if (addToDefaultEcgGroup){
				Group defaultEcg = this.unisens.getGroup("default_ecg");
				if (defaultEcg == null){
					defaultEcg = this.unisens.createGroup("default_ecg");
					defaultEcg.addEntry(Common.getInstance().signalModel.getSignal());
					defaultEcg.setComment("EKG und Referenztrigger");
				}
			
				boolean addEntryToGroup = true;
				
				for(Entry entry : defaultEcg.getEntries())
					if(entry.getId() == eventEntry.getId())
						addEntryToGroup = false;
				
				if(addEntryToGroup)
					defaultEcg.addEntry(eventEntry);
			}
			eventBuffer.setChanged(false);
			unisens.save();
			notifyActiveTriggerEntryChanged(new TriggerModelEvent(this));
		} catch (DuplicateIdException e2) {
			Common.getInstance().ShowErrorBox("Duplicate Group-Id exception", "Invalid id");
			e2.printStackTrace();
		} catch (IOException e2) {
			Common.getInstance().ShowErrorBox("IO Exception", "Unerwartete IO Exception bei Speichern der Triggerliste");
			e2.printStackTrace();
		}
	}
	
	public List<EventEntry> getEventEntries() {
		return triggerEntries;
	}
	
	public EventEntry getEventEntry(String triggerEntryId){
		for(EventEntry triggerEntry : triggerEntries)
			if(triggerEntry.getId().equalsIgnoreCase(triggerEntryId))
				return triggerEntry;
		return null;
	}
	
	public void addTriggerEntry(EventEntry triggerEntry){
		this.triggerEntries.add(triggerEntry);
	}
	
	public void addTempTriggerEntry(EventEntry triggerEntry, List<Event> events){
		if(eventBuffer != null){
			eventBuffer.clear();
		}
		eventBuffer = new EventBuffer(triggerEntry, events);
		notifyActiveTriggerEntryChanged(new TriggerModelEvent(this));
	}
	
	public void setTempTriggerEntryAsActive(){
		try {
			if(eventBuffer != null){
				eventBuffer.clear();
			}
				EventEntry tempTriggerEntry = unisens.createEventEntry("temp_" + System.currentTimeMillis() + ".csv", Common.getInstance().signalModel.getSignal().getSampleRate());
				tempTriggerEntry.setContentClass(UnisensAdapter.TRIGGER_CONTENT_CLASS);
				eventBuffer = new EventBuffer(tempTriggerEntry, new ArrayList<Event>());
				notifyActiveTriggerEntryChanged(new TriggerModelEvent(this));
		} catch (DuplicateIdException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void removeActiveEventEntry(){
		triggerEntries.remove(eventBuffer.getEventEntry());
		unisens.deleteEntry(eventBuffer.getEventEntry());
		if(triggerEntries.size() > 0)
			setActiveEventEntry(triggerEntries.get(0));
		else
			setActiveEventEntry(null);
	}
	
	public void removeTriggerEntry(EventEntry triggerEntry){
		if(triggerEntry == eventBuffer.getEventEntry())
			removeActiveEventEntry();
		else{
			triggerEntries.remove(triggerEntry);
			unisens.deleteEntry(triggerEntry);
		}
	}
	
	public void setActiveEventEntry(EventEntry triggerEntry){
		if(triggerEntry == null){
			if(eventBuffer != null)
				this.eventBuffer.clear();
			this.eventBuffer = null;
			return;
		}
		if(eventBuffer != null && triggerEntry == eventBuffer.getEventEntry()){
			return;
		}
		if(secondaryEventBuffer != null && triggerEntry == secondaryEventBuffer.getEventEntry()){
			secondaryEventBuffer.clear();
			secondaryEventBuffer = null;
		}
		this.eventBuffer = new EventBuffer(triggerEntry);
		notifyActiveTriggerEntryChanged(new TriggerModelEvent(this));	
	}
	
	public EventEntry getActiveEventEntry(){
		if(eventBuffer != null){
			return eventBuffer.getEventEntry();
		}else{
			return null;
		}
	}
	
	public void setSecondaryEventEntry(EventEntry secondaryTriggerEntry){
		if(secondaryTriggerEntry == null){
			if(secondaryEventBuffer != null){
				this.secondaryEventBuffer.clear();
				this.secondaryEventBuffer = null;
			}
			return;
		}
		if(secondaryEventBuffer != null && secondaryTriggerEntry == secondaryEventBuffer.getEventEntry()){
			return;
		}
		this.secondaryEventBuffer = new EventBuffer(secondaryTriggerEntry);
		notifyActiveTriggerEntryChanged(new TriggerModelEvent(this));
	}
	
	public EventEntry getSecondaryEventEntry(){
		if(secondaryEventBuffer != null){
			return secondaryEventBuffer.getEventEntry();
		}else{
			return null;
		}
	}
	
	public boolean isActiveEventEntryChanged() {
		if(eventBuffer != null)
			return eventBuffer.isChanged();
		else 
			return false;
	}
	
	public boolean isActiveEventEntryTemp(){
		if(eventBuffer != null)
			return eventBuffer.isTempEventEntry();
		else 
			return false;
	}
	
	public int getCountOfVisibleTriggerlist(){
		int count = 0;
		if(eventBuffer != null){
			count++;
		}
		if(secondaryEventBuffer != null){
			count++;
		}
		return count;
	}

	
	/**
	 * adds a new (but existing) csv trigger list to unisens file
	 * 
	 * @param eventEntryData
	 *            contains trigger list information
	 * @param seperator
	 *            delimiter for CSV files
	 * @param fileName
	 *            copy from file
	 * @param filePath
	 *            copy from file
	 * @param setDefaultEcg
	 *            add to default ECG group
	 * @author kirst
	 */
	public void addEventEntry(EntryData eventEntryData, String separator, String fileName, String filePath, boolean setDefaultEcg){
		try{
			// set required data
			if(!fileName.endsWith(".csv"))
				fileName += ".csv";
			EventEntry newTriggerEntry = this.unisens.createEventEntry(fileName, eventEntryData.getSampleRate());
			CsvFileFormat csvFileFormat = new CsvFileFormatImpl();
			csvFileFormat.setSeparator(separator);
			newTriggerEntry.setFileFormat(csvFileFormat);
			newTriggerEntry.setTypeLength(eventEntryData.getTypeLength());
			newTriggerEntry.setCommentLength(eventEntryData.getCommentLength());
			newTriggerEntry.setContentClass(eventEntryData.getContentClass());
			
			// set optional data
			if (eventEntryData.getComment().length() > 0)
				newTriggerEntry.setComment(eventEntryData.getComment());
			
			if (eventEntryData.getSource().length() > 0)
				newTriggerEntry.setSource(eventEntryData.getSource());
			
			if (eventEntryData.getSourceId().length() > 0)
				newTriggerEntry.setSourceId(eventEntryData.getSourceId());
			
			// copy file
			String sep = System.getProperty("file.separator");
			RandomAccessFile oldFile = new RandomAccessFile(filePath + sep + fileName,"r");
			RandomAccessFile newFile = new RandomAccessFile(this.unisens.getPath() + sep + eventEntryData.getId(), "rw");
			while (newFile.length() < oldFile.length()) 
			{
				newFile.write(oldFile.read());
			}
			oldFile.close();
			newFile.close();

			// set default ecg group (and create, if it doesn't exist)
			if (setDefaultEcg)
			{
				Group defaultEcg = this.unisens.getGroup("default_ecg");
				if (defaultEcg == null)
				{
					defaultEcg = this.unisens.createGroup("default_ecg");
					defaultEcg.addEntry(Common.getInstance().signalModel.getSignal());
				}
				defaultEcg.setComment("EKG und Referenztrigger");
				defaultEcg.addEntry(newTriggerEntry);
			}
			
			// save changes
			this.unisens.save();
		} 
		catch (DuplicateIdException e){
			e.printStackTrace();
		} 
		catch (FileNotFoundException e){
			e.printStackTrace();
		} 
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void addTriggerModelListener(TriggerModelListner triggerModelListener ) { 
		eventEntryListeners.add( TriggerModelListner.class, triggerModelListener ); 
	} 

	public void removeTriggerModelListener(TriggerModelListner triggerModelListener ) { 
		eventEntryListeners.remove( TriggerModelListner.class, triggerModelListener ); 
	} 

	protected synchronized void notifyActiveTriggerEntryChanged(TriggerModelEvent triggerModelEvent){ 
		for ( TriggerModelListner l : eventEntryListeners.getListeners(TriggerModelListner.class) ) 
			l.activeTriggerEntryChanged(triggerModelEvent);
	} 

	public boolean isAddTriggerMode() {
		return addTriggerMode;
	}

	public void setAddTriggerMode(boolean addTriggerMode) {
		this.addTriggerMode = addTriggerMode;
	}

	public boolean isDeleteTriggerMode() {
		return deleteTriggerMode;
	}

	public void setDeleteTriggerMode(boolean deleteTriggerMode) {
		this.deleteTriggerMode = deleteTriggerMode;
	}

	public Unisens getUnisens() {
		return unisens;
	}
}
