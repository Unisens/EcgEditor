package de.fzi.ekgeditor.events;

import java.util.EventObject;

import de.fzi.ekgeditor.data.ArtefactModel;

public class ArtefactModelEvent extends EventObject {
	private static final long serialVersionUID = 1759252879052757256L;
	
	public ArtefactModelEvent(ArtefactModel artefactModel) {
		super(artefactModel);
	}

}