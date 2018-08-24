package de.fzi.ekgeditor.data.unisensAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.unisens.Event;
import org.unisens.EventEntry;

import de.fzi.ekgeditor.data.Artefact;
import de.fzi.ekgeditor.data.ArtefactUtils;
import de.fzi.ekgeditor.data.Colors;

public class ArtefactBuffer extends AbstractEventBuffer{
	private List<Artefact> artefacts = new ArrayList<Artefact>();
	private List<Color> availableColors = new ArrayList<Color>();
	private HashMap<String, Color> artefactColors = new HashMap<String, Color>();

	public ArtefactBuffer(EventEntry eventEntry){
		super(eventEntry);
		initColors();
		for(int i = 0 ; i < events.size() ; i++){
			Event startArtefactEvent = events.get(i);
			if(ArtefactUtils.isStartArtefactEvent(startArtefactEvent)){
				String endArtefactEventType = ArtefactUtils.getEndArtefactEventType(startArtefactEvent.getType());
				for(int j = i + 1; j < events.size() ; j++){
					Event endArtefactEvent = events.get(j);
					if(endArtefactEvent.getType().equalsIgnoreCase(endArtefactEventType)){
						Artefact artefact = new Artefact(startArtefactEvent, endArtefactEvent);
						String artefactType = artefact.getType();
						Color artefactColor = artefactColors.get(artefactType);
						if(artefactColor == null){
							if(availableColors.size() == 0)
								initColors();
							artefactColor = availableColors.remove(0);
							artefactColors.put(artefactType, artefactColor);
						}
						artefact.setColor(artefactColor);
						artefact.setTypeIndex(Colors.artefactColors.indexOf(artefactColor));
						artefacts.add(artefact);
						break;
					}
				}
			}
		}
	}
	
	public ArtefactBuffer(EventEntry eventEntry, List<Event> events){
		super(eventEntry, events);
	}
	
	public List<Artefact> readAsArtefacts(long startSample, long endSample){
		List<Artefact> resultArtefacts = new ArrayList<Artefact>();
		for(Artefact artefact : artefacts){
			long artefactStartSamplestamp = artefact.getStartEvent().getSampleStamp();
			long artefactEndSamplestamp = artefact.getEndEvent().getSampleStamp();
			if((artefactEndSamplestamp >= startSample && artefactEndSamplestamp <= endSample) || (artefactStartSamplestamp >= startSample && artefactStartSamplestamp <= endSample) || (artefactStartSamplestamp < startSample && artefactEndSamplestamp > endSample)){
				resultArtefacts.add(artefact);
			}
		}
		return resultArtefacts;
	}
	
	public void add(Artefact artefact){
		int artefactIndex = Collections.binarySearch(artefacts, artefact, ArtefactComparator.getInstance());
		if(artefactIndex < 0){
			if(artefact.getColor() == null){
				String artefactType = artefact.getType();
				Color artefactColor = artefactColors.get(artefactType);
				if(artefactColor == null){
					if(availableColors.size() == 0)
						initColors();
					artefactColor = availableColors.remove(0);
					artefactColors.put(artefactType, artefactColor);
				}
				artefact.setColor(artefactColor);
				artefact.setTypeIndex(Colors.artefactColors.indexOf(artefactColor));
			}
			add(artefact.getStartEvent());
			add(artefact.getEndEvent());
			artefactIndex = Math.abs(artefactIndex + 1);
			artefacts.add(artefactIndex, artefact);
		}
	}
	
	public void delete(Artefact artefact){
		events.remove(artefact.getStartEvent());
		events.remove(artefact.getEndEvent());
		artefacts.remove(artefact);
		changed = artefacts.remove(artefact);
	}
	
	private void initColors(){
		availableColors.addAll(Colors.artefactColors);
	}
}



