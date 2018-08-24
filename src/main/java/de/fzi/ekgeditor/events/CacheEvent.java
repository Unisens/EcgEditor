package de.fzi.ekgeditor.events;

import java.util.EventObject;

public class CacheEvent extends EventObject 
{
	/** constant for serialization */
	static final long serialVersionUID = 1L;
	public int pageNumber;

	public CacheEvent(Object source,int pageNumber)
	{
		super(source);
		this.pageNumber=pageNumber;
	}
}