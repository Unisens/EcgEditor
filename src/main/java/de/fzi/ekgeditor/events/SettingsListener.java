/**
 * This class provides the interface for the settings listeners
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.events;

import java.util.EventListener;

public interface SettingsListener extends EventListener 
{ 
	  /** Method which is fired when the settings changes
	   * 
	   * @param e The Settings Event
	   */
  public void settingsChanged( SettingsEvent e ); 
}
