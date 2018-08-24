package de.fzi.ekgeditor.data.unisensAdapter;

import java.util.Comparator;

import de.fzi.ekgeditor.data.Artefact;

public class ArtefactComparator implements Comparator<Artefact>{
	private static ArtefactComparator artefactComparator = new ArtefactComparator();
	
	public static ArtefactComparator getInstance(){
		return artefactComparator;
	} 
	@Override
	public int compare(Artefact artefact1, Artefact artefact2) {
		if(artefact1.getStartEvent().getSampleStamp() < artefact2.getStartEvent().getSampleStamp())
			return -1;
		if(artefact1.getStartEvent().getSampleStamp() > artefact2.getStartEvent().getSampleStamp())
			return 1;
		return artefact1.getStartEvent().getType().compareToIgnoreCase(artefact2.getStartEvent().getType());
	}
}
