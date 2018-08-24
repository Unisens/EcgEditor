/**
 * This class provides the view Event Class
 * This class is used to signal if some view parameter (zoom etc.) are changing.
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.events;

import java.util.EventObject;

public class ViewEvent extends EventObject 
{
	/** constant for serialization */
	static final long serialVersionUID = 1L;

	/** Standard constructor for a new view Event
	 * 
	 * @param source The source who fired the view event
	 */
	public ViewEvent(Object source)
	{
		super(source);
	}
}
