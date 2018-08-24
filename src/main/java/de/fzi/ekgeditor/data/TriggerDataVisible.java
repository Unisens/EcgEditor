package de.fzi.ekgeditor.data;

import org.unisens.Event;

public class TriggerDataVisible {

	public Event event;
	public int pixel;
	public long time;
	public long sample;
	public String triggerChannelName;
	
	public TriggerDataVisible(Event e, int pixel, long time, long sample, String triggerChannelName)
	{
		this.event = e;
		this.pixel = pixel;
		this.time = time;
		this.sample = sample;
		this.triggerChannelName = triggerChannelName;
	}
}
