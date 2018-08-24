/**
 * This class provides the interface for the viewEvent listeners
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.events;

import java.util.EventListener;

public interface ViewListener extends EventListener 
{ 
	/** Method which is fired when the zoom changes
	 * 
	 * @param e The Settings Event
	 */
	public void zoomChanged( ViewEvent e ); 

	/** Method which is fired when the showed section of the signal changes.
	 * 
	 * @param e The Settings Event
	 */
	public void viewSectionChanged(ViewEvent e);
	
	/** Method which is fired when some channel gets visible/hides
	 * 
	 * @param e The Settings Event
	 */
	public void channelViewChanged(ViewEvent e);
	
}


