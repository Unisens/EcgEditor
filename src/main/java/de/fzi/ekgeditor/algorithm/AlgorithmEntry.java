package de.fzi.ekgeditor.algorithm;

import java.io.IOException;
import java.util.Arrays;

public class AlgorithmEntry{
	private EntryTypeEnum entryTypeEnum;
	private String name;
	private ContentClass contentClass;
	private InputSignal inputSignal;
	private int selectedChannel;
	private int delay;
	private Integer[] supportedSampleRates;
	private double[] lsbs;
	private int[] delays;
	private String[] units;
	
	public AlgorithmEntry(String name, EntryTypeEnum entryTypeEnum, ContentClass contentClass, Integer[] supportedSampleRates, int[] delays, double[] lsbs, String[] units){
		this.entryTypeEnum = entryTypeEnum;
		this.name = name;
		this.contentClass = contentClass;
		this.supportedSampleRates = supportedSampleRates;
		this.delays = delays;
		this.lsbs = lsbs;
		this.units = units;
		
	}
	
	public AlgorithmEntry(String name, EntryTypeEnum entryTypeEnum, ContentClass contentClass){
		this.entryTypeEnum = entryTypeEnum;
		this.name = name;
		this.contentClass = contentClass;
	}

	public EntryTypeEnum getEntryTypeEnum() {
		return entryTypeEnum;
	}

	public void setEntryTypeEnum(EntryTypeEnum entryTypeEnum) {
		this.entryTypeEnum = entryTypeEnum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ContentClass getContentClass() {
		return contentClass;
	}

	public void setContentClass(ContentClass contentClass) {
		this.contentClass = contentClass;
	}
	
	public int getDelay(){
		return delay;
	}
	
	public boolean checkSampleRateSupported(int sampleRate) {
		if (supportedSampleRates == null || Arrays.asList(supportedSampleRates).contains(sampleRate)) {
			return true;
		}
		return false;
	}
	
	public void setInputSignal(InputSignal inputSignal, int selectedChannel){
		this.inputSignal = inputSignal;
		this.selectedChannel = selectedChannel;
		if(inputSignal != null){
			for(int i = 0 ; i < supportedSampleRates.length ; i++){
				if(supportedSampleRates[i] == inputSignal.getSampleRate()){
					delay = delays[i];
				}
			}
		}
	}
	
	public InputSignal getInputSignal(){
		return inputSignal;
	}
	
	public int readSample(long sampleNumber) throws IOException{
		return inputSignal.readSample(sampleNumber, selectedChannel);
	}

	public enum EntryTypeEnum {
		SIGNAL,
		EVENT,
		VALUES,
		HEARTRATE,
		NUMERIC,
		TIMERANGE,
		NOVALUE;
		
		public static EntryTypeEnum toEntryTypeEnum(String str) {
			try {
				return valueOf(str.toUpperCase());
			}
			catch (Exception e) {
				return NOVALUE;
			}
		}
	}
	
	public enum ContentClass {
		ECG		("EKG-Daten"),
		TRIGGER	("Trigger-Liste"),
		ACC		("Beschleunigungssignal"),
		IMP		("Impedanzsignal"),
		RAW		("Rohdaten"),
		RR		("Blutdruck"),
		PLETH	("Pleth-Signal"),
		RESP	("Atmung"),
		MARKER	("Patienten-Marker"),
		NOVALUE	("Andere");
		
		private String longName;
		
		ContentClass(String longName) {
			this.longName = longName;
		}
		
		@Override
		public String toString() {
			return longName;
		}
	
		public static ContentClass toContentClass(String str) {
			try {
				return valueOf(str.toUpperCase());
			}
			catch (Exception e) {
				return NOVALUE;
			}
		}
	}
}
