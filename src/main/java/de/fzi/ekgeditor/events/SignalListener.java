/**
 * This class provides the interface for the signal listeners
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor.events;

import java.util.EventListener;

public interface SignalListener extends EventListener { 
	/** Method which is fired when the underlying signal changes
	 * 
	 * @param e The Settings Event
	 */
	public void signalChanged(SignalEvent e ); 
}

