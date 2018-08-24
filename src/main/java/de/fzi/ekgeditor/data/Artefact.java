package de.fzi.ekgeditor.data;

import org.eclipse.swt.graphics.Color;
import org.unisens.Event;

public class Artefact {
	private Event startEvent;
	private Event endEvent;
	private String type;
	private int channel;
	private int typeIndex;
	private Color color;
	private boolean selected;
	private int selectedChannel;
	private boolean startSelected;
	private boolean endSelected;
	
	public Artefact (Event startEvent, Event endEvent){
		this.startEvent = startEvent;
		this.endEvent = endEvent;
		char channelChar = startEvent.getType().charAt(startEvent.getType().length() - 1);
		if(Character.isDigit(channelChar))
			channel = Integer.parseInt(""+channelChar) - 1;
		else
			channel = -1;
		if(channel == -1)
			type = startEvent.getType().substring(1, startEvent.getType().length()).toUpperCase();
		else
			type = startEvent.getType().substring(1, startEvent.getType().length() - 3).toUpperCase();
	}
	
	public Event getStartEvent() {
		return startEvent;
	}

	public void setStartEvent(Event startEvent) {
		this.startEvent = startEvent;
	}

	public Event getEndEvent() {
		return endEvent;
	}

	public void setEndEvent(Event endEvent) {
		this.endEvent = endEvent;
	}
	
	public String getType(){
		return type;
	}
	
	public void setType(String type){
		startEvent.getType().replace(this.type, type);
		endEvent.getType().replace(this.type, type);
		this.type = type;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public int getTypeIndex() {
		return typeIndex;
	}

	public void setTypeIndex(int typeIndex) {
		this.typeIndex = typeIndex;
	}

	public int getChannel(){
		return channel;
	}
	
	public void setChannel(int channel){
		if(channel != this.channel){
			if(channel == -1){
				startEvent.getType().replace("00" + this.channel, "");
				endEvent.getType().replace("00" + this.channel, "");
			}else{
				startEvent.getType().replace("00" + this.channel, "00"+channel);
				endEvent.getType().replace("00" + this.channel, "00"+channel);
			}
			this.channel = channel;
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int getSelectedChannel() {
		return selectedChannel;
	}

	public void setSelectedChannel(int selectedChannel) {
		this.selectedChannel = selectedChannel;
	}

	public boolean isStartSelected() {
		return startSelected;
	}

	public void setStartSelected(boolean startSelected) {
		this.startSelected = startSelected;
	}

	public boolean isEndSelected() {
		return endSelected;
	}

	public void setEndSelected(boolean endSelected) {
		this.endSelected = endSelected;
	}
	
	
}
