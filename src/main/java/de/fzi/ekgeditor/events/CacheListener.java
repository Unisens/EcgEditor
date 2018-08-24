package de.fzi.ekgeditor.events;

import java.util.EventListener;

public interface CacheListener extends EventListener 
{ 
	public void CacheFlush( CacheEvent e ); 
	public void PageLoad( CacheEvent e);
}

