/**
 * This class provides the selection Event Object
 * This class is used to give additional information if some selectionEvent occurs.
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.events;

import java.util.EventObject;

import de.fzi.ekgeditor.utils.Selection;

public class MySelectionEvent extends EventObject 
{
	/** constant for serialization */
	static final long serialVersionUID = 1L;
	public Selection sel;

	/** Standard constructor for a new selection Event
	 * 
	 * @param source The source who fired the selection event
	 * @param s The new selection
	 */
	public MySelectionEvent(Object source, Selection s)
	{
		super(source);
		sel=s;
	}
}
