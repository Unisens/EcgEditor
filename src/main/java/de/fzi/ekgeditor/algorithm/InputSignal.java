package de.fzi.ekgeditor.algorithm;

import java.io.IOException;

import org.unisens.SignalEntry;

public class InputSignal {
	private SignalEntry signalEntry;
	private long sampleCount;
	private double[][] lastReadSample;
	private long lastReadSampleNumber = -1;
	
	public InputSignal(SignalEntry signalEntry){
		this.signalEntry = signalEntry;
		signalEntry.resetPos();
		lastReadSampleNumber = -1;
		sampleCount = signalEntry.getCount();
	}
	
	public int readSample(long sampleNumber, int channel) throws IOException{
		if(lastReadSampleNumber != sampleNumber){
			lastReadSampleNumber = sampleNumber;
			lastReadSample = signalEntry.readScaled(1);
		}
		return (int)lastReadSample[0][channel];
	}
	
	public long getSampleCount(){
		return sampleCount;
	}
	
	public int getSampleRate(){
		return (int)signalEntry.getSampleRate();
	}
	
	public SignalEntry getSignalEntry(){
		return signalEntry;
	}
}
