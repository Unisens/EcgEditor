package de.fzi.ekgeditor.events;

import java.util.EventObject;

import de.fzi.ekgeditor.data.TriggerModel;

public class TriggerModelEvent extends EventObject {
	private static final long serialVersionUID = 6759248879052757256L;
	
	public TriggerModelEvent(TriggerModel triggerModel) {
		super(triggerModel);
	}

}
