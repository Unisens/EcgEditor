/**
 * This class provides the interface for the selection listeners
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.events;

import java.util.EventListener;

public interface MySelectionListener extends EventListener 
{
	  /** Method which is fired when the selection changes
	   * 
	   * @param e The Selection Event
	   */
	  public void MyselectionChanged( MySelectionEvent e ); 
}

