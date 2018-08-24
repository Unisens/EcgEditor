/**
 * This class provides the Signal Event Object
 * This class is used to give additional information if some signalEvent occurs.
 * A signal event occurs when the data model changes the signal where it is bound to.
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor.events;

import java.util.EventObject;

public class SignalEvent extends EventObject 
{
	/** constant for serialization */
	static final long serialVersionUID = 1L;
	
	public boolean newSignal=false;

	/** Standard constructor for a new SignalEvent 
	 * 
	 * @param source The sources who fired the SignalEvent
	 */
	public SignalEvent( Object source,boolean newSignal)
	{
		super(source);
		this.newSignal=newSignal;
	}
}