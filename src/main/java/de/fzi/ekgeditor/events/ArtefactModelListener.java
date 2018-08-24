package de.fzi.ekgeditor.events;

import java.util.EventListener;

public interface ArtefactModelListener extends EventListener {
	public void activeArtefactEntryChanged();
	public void artefactSelected();
	public void selectedArtefactModified();
}
