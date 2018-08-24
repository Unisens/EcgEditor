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

public class ArtefactModel implements IEventEntryModel{
	private ArtefactBuffer artefactBuffer;
	private List<EventEntry> artefactEntries = new ArrayList<EventEntry>();
	private EventListenerList artefactListeners = new EventListenerList();
	private Unisens unisens;
	private Artefact selectedArtefact;
	private boolean modifyingSelectedArtefact;
	private int selectArtefactDistance = 20;
	
	public void loadEventEntries(Unisens unisens, Group group){
		if(artefactBuffer != null){
			artefactBuffer.clear();
			artefactBuffer = null;
		}
		selectedArtefact = null;
		this.unisens = unisens;
		this.artefactEntries = UnisensAdapter.getEventEntriesFromGroup(unisens, group, UnisensAdapter.ARTEFACT_CONTENT_CLASS, true);
		if(artefactEntries.size() > 0){
			this.artefactBuffer = new ArtefactBuffer(artefactEntries.get(0));
			notifyActiveArtefactEntryChanged();
		}
	}
	
	public List<Artefact> read(long startSample, long endSample){
		return artefactBuffer.readAsArtefacts(startSample, endSample);
	}
	
	public void add(Artefact artefact){
		artefactBuffer.add(artefact);
		notifyActiveArtefactEntryChanged();
	}
	
	public void add(List<Artefact> artefacts){
		for(Artefact artefact : artefacts){
			artefactBuffer.add(artefact);
		}
		notifyActiveArtefactEntryChanged();
	}
	
	public void delete(Artefact artefact){
		if(selectedArtefact.getChannel() == -1){
			for(int i = 0; i < Common.getInstance().signalModel.getNumberOfChannels(); i++){
				if(i != selectedArtefact.getSelectedChannel()){
					Event startEvent = new Event(selectedArtefact.getStartEvent().getSampleStamp(), String.format("(%s00%s", selectedArtefact.getType(), Integer.toString(i + 1)), selectedArtefact.getStartEvent().getComment());
					Event endEvent = new Event(selectedArtefact.getEndEvent().getSampleStamp(), String.format(")%s00%s", selectedArtefact.getType(), Integer.toString(i + 1)), selectedArtefact.getEndEvent().getComment());
					Artefact newArtefact = new Artefact(startEvent, endEvent);
					newArtefact.setColor(selectedArtefact.getColor());
					newArtefact.setTypeIndex(selectedArtefact.getTypeIndex());
					newArtefact.setSelected(false);
					this.add(newArtefact);
				}
			}
		}
		artefactBuffer.delete(artefact);
		notifyActiveArtefactEntryChanged();
	}
	
	public Artefact getArtefactForPoint(int xPixel, int yPixel){
		SignalViewerModel signalViewerModel = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel;
		long samplestamp = signalViewerModel.getSampleNumber(xPixel, Common.getInstance().artefactModel.getActiveEventEntry().getSampleRate());
		int visibleChannel = signalViewerModel.getVisibleChannelForPixel(yPixel);
		int realChannel = signalViewerModel.getRealChannelNumber(visibleChannel);
		int artefactTypeIndex = getArtefactTypeIndex(yPixel);
		
		ArtefactModel artefactModel = Common.getInstance().artefactModel;
		List<Artefact> artefacts = artefactModel.read(samplestamp, samplestamp + 1);
		
		for(Artefact artefact : artefacts){
			if((artefact.getChannel()  == realChannel || artefact.getChannel() == -1) && artefact.getTypeIndex() == artefactTypeIndex){
				if(artefact.getChannel() == -1){
					if(artefact.isSelected() && Common.getInstance().mainForm.signalViewerComposite.signalViewerModel.isKeyCtrlPressed() && artefact.getSelectedChannel() != realChannel )
						artefact.setSelectedChannel(-1);
					else
						artefact.setSelectedChannel(realChannel);
				}
				return artefact;
			}
		}
		return null;
	}
	
	public boolean isAtStartOrEndOfSelectedArtefact(int xPixel){
		if(selectedArtefact == null){
			return false;
		}else{
			SignalViewerModel signalViewerModel = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel;
			long samplestamp = signalViewerModel.getSampleNumber(xPixel, Common.getInstance().artefactModel.getActiveEventEntry().getSampleRate());
			if((samplestamp < selectedArtefact.getStartEvent().getSampleStamp()&& samplestamp > selectedArtefact.getStartEvent().getSampleStamp() - selectArtefactDistance) || (samplestamp > selectedArtefact.getEndEvent().getSampleStamp() && samplestamp < selectedArtefact.getEndEvent().getSampleStamp() + selectArtefactDistance)){
				return true;
			}else{
				return false;
			}
		}
	}
	
	public void selectStartOrEndOfSelectedArtefact(int xPixel){
		if(selectedArtefact != null){
			SignalViewerModel signalViewerModel = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel;
			long samplestamp = signalViewerModel.getSampleNumber(xPixel, Common.getInstance().artefactModel.getActiveEventEntry().getSampleRate());
			if(samplestamp < selectedArtefact.getStartEvent().getSampleStamp() && samplestamp > selectedArtefact.getStartEvent().getSampleStamp() - selectArtefactDistance){
				selectedArtefact.setStartSelected(true);
				selectedArtefact.setEndSelected(false);
				return;
			}
			if(samplestamp > selectedArtefact.getEndEvent().getSampleStamp() && samplestamp < selectedArtefact.getEndEvent().getSampleStamp() + selectArtefactDistance){
				selectedArtefact.setEndSelected(true);
				selectedArtefact.setStartSelected(false);
				return;
			}
		}
	}
	
	public void changeSelectedArtefactStartOrEnd(long samplestamp){
		if(selectedArtefact.getChannel() == -1 && selectedArtefact.getSelectedChannel() != -1){
			artefactBuffer.delete(selectedArtefact);
			for(int i = 0; i < Common.getInstance().signalModel.getNumberOfChannels(); i++){
				Event startEvent = new Event(selectedArtefact.getStartEvent().getSampleStamp(), String.format("(%s00%s", selectedArtefact.getType(), Integer.toString(i + 1)), selectedArtefact.getStartEvent().getComment());
				Event endEvent = new Event(selectedArtefact.getEndEvent().getSampleStamp(), String.format(")%s00%s", selectedArtefact.getType(), Integer.toString(i + 1)), selectedArtefact.getEndEvent().getComment());
				Artefact newArtefact = new Artefact(startEvent, endEvent);
				newArtefact.setColor(selectedArtefact.getColor());
				newArtefact.setTypeIndex(selectedArtefact.getTypeIndex());
				if(i == selectedArtefact.getSelectedChannel()){
					newArtefact.setSelected(true);
					newArtefact.setStartSelected(selectedArtefact.isStartSelected());
					newArtefact.setEndSelected(selectedArtefact.isEndSelected());
					selectedArtefact = newArtefact;
				}
				this.add(newArtefact);
				notifyActiveArtefactEntryChanged();
				notifyArtefactSelected();
			}
		}
		if(selectedArtefact.isStartSelected() && samplestamp > selectedArtefact.getEndEvent().getSampleStamp()){
			selectedArtefact.getStartEvent().setSampleStamp(selectedArtefact.getEndEvent().getSampleStamp());
			selectedArtefact.getEndEvent().setSampleStamp(samplestamp);
			selectedArtefact.setStartSelected(false);
			selectedArtefact.setEndSelected(true);
			notifySelectedArtefactModified();
			return;
		}
		if(selectedArtefact.isEndSelected() && samplestamp < selectedArtefact.getStartEvent().getSampleStamp()){
			selectedArtefact.getEndEvent().setSampleStamp(selectedArtefact.getStartEvent().getSampleStamp());
			selectedArtefact.getStartEvent().setSampleStamp(samplestamp);
			selectedArtefact.setStartSelected(true);
			selectedArtefact.setEndSelected(false);
			notifySelectedArtefactModified();
			return;
		}
		if(selectedArtefact.isStartSelected()){
			selectedArtefact.getStartEvent().setSampleStamp(samplestamp);
			notifySelectedArtefactModified();
			return;
		}
		if(selectedArtefact.isEndSelected()){
			selectedArtefact.getEndEvent().setSampleStamp(samplestamp);
			notifySelectedArtefactModified();
			return;
		}	
	}
	
	public int getArtefactTypeIndex(int yPixel){
		SignalViewerModel signalViewerModel = Common.getInstance().mainForm.signalViewerComposite.signalViewerModel;
		int visibleChannel = signalViewerModel.getVisibleChannelForPixel(yPixel);
		int channelZeroLine = signalViewerModel.getChannelZeroLine(visibleChannel) - signalViewerModel.getSignalGraphPositionY();
		int artefactTypeIndex = 0;
		if(yPixel < channelZeroLine)
			artefactTypeIndex = (50 - (channelZeroLine - yPixel))/5;
		else
			artefactTypeIndex = (yPixel - channelZeroLine)/5;
		return artefactTypeIndex;
	}
	
	public void addArtefactModelListener(ArtefactModelListener artefactModelListener ) { 
		artefactListeners.add( ArtefactModelListener.class, artefactModelListener ); 
	} 

	public void removeArtefactModelListener(ArtefactModelListener artefactModelListener ) { 
		artefactListeners.remove( ArtefactModelListener.class, artefactModelListener ); 
	}
	

	protected synchronized void notifyActiveArtefactEntryChanged(){ 
		for ( ArtefactModelListener l : artefactListeners.getListeners(ArtefactModelListener.class) ) 
			l.activeArtefactEntryChanged();
	}
	
	protected synchronized void notifyArtefactSelected(){ 
		for ( ArtefactModelListener l : artefactListeners.getListeners(ArtefactModelListener.class) ) 
			l.artefactSelected();
	} 
	
	protected synchronized void notifySelectedArtefactModified(){ 
		for ( ArtefactModelListener l : artefactListeners.getListeners(ArtefactModelListener.class) ) 
			l.selectedArtefactModified();
	}
	
	public void setSelectedArtefact(Artefact selectedArtefact){
		this.selectedArtefact = selectedArtefact;
		notifyArtefactSelected();
	}
	
	public Artefact getSelectedArtefact(){
		return selectedArtefact;
	}
	
	public void setActiveEventEntry(EventEntry artefactEntry){
		if(artefactEntry == null){
			if(artefactBuffer != null){
				this.artefactBuffer.clear();
			}
			this.artefactBuffer = null;
			return;
		}
		if(artefactBuffer != null && artefactEntry == artefactBuffer.getEventEntry()){
			return;
		}
		this.artefactBuffer = new ArtefactBuffer(artefactEntry);
		this.selectedArtefact = null;
		notifyActiveArtefactEntryChanged();
		return;
	}
	
	public void saveActiveEventEntryAs(EntryData eventEntryData, boolean addToDefaultEcgGroup){
		try {
			if(!eventEntryData.getId().endsWith(".csv"))
				eventEntryData.setId(eventEntryData.getId() + ".csv");
			EventEntry eventEntry = (EventEntry)unisens.getEntry(eventEntryData.getId());
			if(artefactBuffer.isTempEventEntry()){
				if(eventEntry != null){
					unisens.deleteEntry(eventEntry);
					artefactEntries.remove(eventEntry);
				}
				
				eventEntry = artefactBuffer.getEventEntry();
				eventEntry.rename(eventEntryData.getId());
				eventEntry.setComment(eventEntryData.getComment());
				eventEntry.setSampleRate(eventEntryData.getSampleRate());
				eventEntry.setSource(eventEntryData.getSource());
				eventEntry.setSourceId(eventEntryData.getSourceId());
				eventEntry.setContentClass(eventEntryData.getContentClass());

				artefactBuffer.save();
				artefactEntries.add(eventEntry);
			}else{
				if(eventEntry == null){
					List<Event> events = artefactBuffer.read(0, Integer.MAX_VALUE);
					eventEntry = unisens.createEventEntry(eventEntryData.getId(), eventEntryData.getSampleRate());
					eventEntry.setComment(eventEntryData.getComment());
					eventEntry.setSource(eventEntryData.getSource());
					eventEntry.setSourceId(eventEntryData.getSourceId());
					eventEntry.setContentClass(eventEntryData.getContentClass());
					eventEntry.append(events);
					artefactEntries.add(eventEntry);
					setActiveEventEntry(eventEntry);
				}else{
					if(eventEntry == artefactBuffer.getEventEntry()){
						eventEntry.setComment(eventEntryData.getComment());
						eventEntry.setSampleRate(eventEntryData.getSampleRate());
						eventEntry.setSource(eventEntryData.getSource());
						eventEntry.setSourceId(eventEntryData.getSourceId());
						eventEntry.setContentClass(eventEntryData.getContentClass());
						artefactBuffer.save();
					}else{
						List<Event> events = artefactBuffer.read(0, Integer.MAX_VALUE);
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
			artefactBuffer.setChanged(false);
			unisens.save();
			notifyActiveArtefactEntryChanged();
		} catch (DuplicateIdException e2) {
			Common.getInstance().ShowErrorBox("Duplicate Group-Id exception", "Invalid id");
			e2.printStackTrace();
		} catch (IOException e2) {
			Common.getInstance().ShowErrorBox("IO Exception", "Unerwartete IO Exception bei Speichern des ArtefactEntry");
			e2.printStackTrace();
		}
	}
	
	public void removeActiveEventEntry(){
		artefactEntries.remove(artefactBuffer.getEventEntry());
		unisens.deleteEntry(artefactBuffer.getEventEntry());
		if(artefactEntries.size() > 0)
			setActiveEventEntry(artefactEntries.get(0));
		else
			setActiveEventEntry(null);
	}
	
	public EventEntry getActiveEventEntry(){
		if(artefactBuffer != null)
			return artefactBuffer.getEventEntry();
		return null;
			
	}
	
	public EventEntry getEventEntry(String artefactEntryId){
		for(EventEntry triggerEntry : artefactEntries)
			if(triggerEntry.getId().equalsIgnoreCase(artefactEntryId))
				return triggerEntry;
		return null;
	}
	
	public boolean isActiveEventEntryChanged() {
		if(artefactBuffer != null)
			return artefactBuffer.isChanged();
		else 
			return false;
	}
	
	public void setActiveEventEntryChanged(boolean isChanged){
		artefactBuffer.setChanged(true);
	}
	
	public void setTempArtefactEntryAsActive(){
		try {
			EventEntry tempArtefactEntry = unisens.createEventEntry("temp_artefacts_" + System.currentTimeMillis() + ".csv", Common.getInstance().signalModel.getSignal().getSampleRate());
			tempArtefactEntry.setContentClass(UnisensAdapter.ARTEFACT_CONTENT_CLASS);
			artefactBuffer = new ArtefactBuffer(tempArtefactEntry, new ArrayList<Event>());
			notifyActiveArtefactEntryChanged();
		} catch (DuplicateIdException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public boolean isActiveEventEntryTemp(){
		if(artefactBuffer != null)
			return artefactBuffer.isTempEventEntry();
		else 
			return false;
	}
	
	public boolean isModifyingSelectedArtefact() {
		return modifyingSelectedArtefact;
	}

	public void setModifyingSelectedArtefact(boolean modifyingSelectedArtefact) {
		this.modifyingSelectedArtefact = modifyingSelectedArtefact;
	}

	public List<EventEntry> getEventEntries() {
		return artefactEntries;
	}

	public Unisens getUnisens() {
		return unisens;
	}
}
