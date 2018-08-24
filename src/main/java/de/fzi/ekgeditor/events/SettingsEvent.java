/**
 * This class provides the interface for the settings event
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.events;

import java.util.EventObject;

public class SettingsEvent extends EventObject 
{
	/** constant for serialization */
	static final long serialVersionUID = 1L;

	/** Standard constructor for a new SettingsEvent
	 * 
	 * @param source The source who fires the SettingsEvent
	 */
	public SettingsEvent(Object source)
	{
		super(source);
	}
}
