package de.fzi.ekgeditor.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.unisens.Event;
import org.unisens.SignalEntry;

import de.fzi.ekgeditor.algorithm.AlgorithmEntry.ContentClass;
import de.fzi.ekgeditor.algorithm.jni.AlgorithmFactory;
import de.fzi.ekgeditor.algorithm.jni.IAlgorithmJNI;
import de.fzi.ekgeditor.data.Artefact;

public class Algorithm {
	private String name;
	private String className;
	private int inputEntriesCount;
	private List<AlgorithmEntry> inputEntries = new ArrayList<AlgorithmEntry>();
	private List<AlgorithmEntry> outputEntries = new ArrayList<AlgorithmEntry>(); 
	private List<Event> events = new ArrayList<Event>();
	private List<Artefact> artefacts = new ArrayList<Artefact>();
	private long currentSample = 0;
	private long maxSampleCount = 0;
	private int maxSampleRate = 0;
	private int delay;
	private int[] inputSignalSampleRates;
	private double[] sampleRateFaktors;
	private IAlgorithmJNI algorithmJNI;
	
	public Algorithm(String name, String className){
		this.name = name;
		this.className = className;
	}
	
	public void start() throws IOException{
		init();
		Thread analysisThread = new Thread(){
			public void run(){
				try {
					long lastArtefactStartSample = -1;
					for(currentSample = 0 ; currentSample < maxSampleCount; currentSample++){
						int[] sampleValues = readSampleValues(currentSample);
						int[] result = algorithmJNI.process(sampleValues);
						
						if(currentSample < delay)
							continue;
						for(int i = 0 ; i < result.length ; i++){
							AlgorithmEntry outputAlgorithmEntry = outputEntries.get(i);
							if(result[i] != 0){
								if(outputAlgorithmEntry.getEntryTypeEnum() == AlgorithmEntry.EntryTypeEnum.EVENT){
									delay = result[i] == 1 ? delay : result[i];
									Event event = new Event(currentSample - delay, "N", outputAlgorithmEntry.getName());
									events.add(event);
								}
								if(outputAlgorithmEntry.getEntryTypeEnum() == AlgorithmEntry.EntryTypeEnum.TIMERANGE){
									if(lastArtefactStartSample == -1){
										lastArtefactStartSample = currentSample;
									}else{
										if(currentSample == maxSampleCount - 1){
											artefacts.add(new Artefact(new Event(lastArtefactStartSample - delay, "(A", outputAlgorithmEntry.getName()), new Event(currentSample - delay - 1, ")A", outputAlgorithmEntry.getName())));
										}
									}
								}
							}else{
								if(outputAlgorithmEntry.getEntryTypeEnum() == AlgorithmEntry.EntryTypeEnum.TIMERANGE){
									if(lastArtefactStartSample != -1){
										artefacts.add(new Artefact(new Event(lastArtefactStartSample - delay, "(A", outputAlgorithmEntry.getName()), new Event(currentSample - delay - 1, ")A", outputAlgorithmEntry.getName())));
										lastArtefactStartSample = -1;
									}
								}
							}
						}	
					}
					for(AlgorithmEntry inputAlgorithmEntry : inputEntries){
						inputAlgorithmEntry.setInputSignal(null, -1);
					}
					
					this.interrupt();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		};
		analysisThread.start();
	}
	
	private void init(){
		currentSample = 0;
		inputEntriesCount = inputEntries.size();
		maxSampleCount = getInputSignalsMaxSampleCount();
		maxSampleRate = getInputSignalsMaxSampleRate();
		inputSignalSampleRates = getInputSignalsSampleRates();
		sampleRateFaktors = new double[inputSignalSampleRates.length];
		for (int i = 0; i < inputSignalSampleRates.length; i++) {
			sampleRateFaktors[i] = (double)inputSignalSampleRates[i]/ (double)maxSampleRate;
		}
		for(AlgorithmEntry algorithmInputEntry : inputEntries){
			if(algorithmInputEntry.getContentClass() == ContentClass.ECG){
				this.delay = algorithmInputEntry.getDelay();
			}
		}
		events.clear();
		artefacts.clear();
		algorithmJNI = AlgorithmFactory.createAlgorithmJNI(name, className);
		algorithmJNI.initJNI(inputSignalSampleRates);
	}
	
	public void setInputSignalToAlgorithmInputEntry(AlgorithmEntry algorithmEntry, SignalEntry signalEntry, int selectedChannel){
		for(AlgorithmEntry algorithmInputEntry : inputEntries){
			if(algorithmInputEntry.getInputSignal() != null && algorithmInputEntry.getInputSignal().getSignalEntry() == signalEntry){
				algorithmEntry.setInputSignal(algorithmInputEntry.getInputSignal(), selectedChannel);
				return;
			}
		}
		algorithmEntry.setInputSignal(new InputSignal(signalEntry), selectedChannel);
	}
	
	public void addInputEntry(AlgorithmEntry algorithmEntry){
		inputEntries.add(algorithmEntry);
	}
	
	public void addOutputEntry(AlgorithmEntry algorithmEntry){
		outputEntries.add(algorithmEntry);
	}
	
	public String getName(){
		return name;
	}
	
	private int[] readSampleValues(long sampleNumber) throws IOException{
		int[] sample = new int[inputEntriesCount];
		for(int i = 0 ; i < inputEntriesCount; i++){
			sample[i] = inputEntries.get(i).readSample((int) (sampleNumber * sampleRateFaktors[i]));
		}
		return sample;
	}
	
	private long getInputSignalsMaxSampleCount(){
		long sampleCount = -1;
		for(AlgorithmEntry algorithmInputEntry : inputEntries){
			sampleCount = algorithmInputEntry.getInputSignal().getSampleCount() > sampleCount ? algorithmInputEntry.getInputSignal().getSampleCount() : sampleCount;
		}
		return sampleCount;
	}
	
	private int getInputSignalsMaxSampleRate(){
		int sampleRate = -1;
		for(AlgorithmEntry algorithmInputEntry : inputEntries){
			sampleRate = algorithmInputEntry.getInputSignal().getSampleRate() > sampleRate ? algorithmInputEntry.getInputSignal().getSampleRate() : sampleRate;
		}
		return sampleRate;
	}
	
	private int[] getInputSignalsSampleRates(){
		int[] samplerates = new int[inputEntries.size()];
		for(int i = 0 ; i < samplerates.length ; i++){
			samplerates[i] = inputEntries.get(i).getInputSignal().getSampleRate();
		}
		return samplerates;
	}

	public List<AlgorithmEntry> getInputEntries() {
		return inputEntries;
	}

	public List<AlgorithmEntry> getOutputEntries() {
		return outputEntries;
	}
	
	public long getCurrentSample(){
		return currentSample;
	}
	
	public List<Event> getEvents(){
		return events;
	}
	
	public List<Artefact> getArtefacts(){
		return artefacts;
	}
	
	public long getSampleCount(){
		return maxSampleCount;
	}
}
