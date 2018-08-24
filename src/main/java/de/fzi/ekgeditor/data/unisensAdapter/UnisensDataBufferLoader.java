package de.fzi.ekgeditor.data.unisensAdapter;

import org.unisens.SignalEntry;

public class UnisensDataBufferLoader extends Thread {
	private UnisensDataBuffer buffer;
	private Boolean nextPageLoaded;
	private Boolean previousPageLoaded;
	private Boolean currentPageLoaded;
	private SignalEntry signal;
	private long sampleCount;
	
	public UnisensDataBufferLoader(UnisensDataBuffer ub, SignalEntry s) {
		signal = s;
		buffer = ub;
		sampleCount = s.getCount();
		previousPageLoaded = true;
		currentPageLoaded = false;
		nextPageLoaded = true;
		if(sampleCount > buffer.getBufferSize())
			nextPageLoaded = false;
		this.setName("UnisensBufferLoader");
		this.setPriority(Thread.MAX_PRIORITY);
		this.setDaemon(true);
	}
	
	public void run(){
		while(!isInterrupted()){
			if(!currentPageLoaded){
				synchronized (buffer.getCurrent()) {
					long startSample = buffer.getStartSampleInBuffer();
					double[][] data = UnisensAdapter.readUniform(signal, startSample, buffer.getBufferSize());
					buffer.setCurrent(new Buffer(data, startSample));
				}
				currentPageLoaded = true;
			}
			if(!previousPageLoaded){
				synchronized (buffer.getPrevious()) {
					long startSample = buffer.getStartSampleInBuffer() - buffer.getBufferSize();
					double[][] data = UnisensAdapter.readUniform(signal, startSample, buffer.getBufferSize());
					buffer.setPrevious(new Buffer(data, startSample));
				}
				previousPageLoaded = true;
			}
			if(!nextPageLoaded){
				synchronized (buffer.getNext()){
					long startSample = buffer.getStartSampleInBuffer() + buffer.getBufferSize();
					double[][] data = UnisensAdapter.readUniform(signal, startSample, buffer.getBufferSize());
					buffer.setNext(new Buffer(data, startSample));
				}
				nextPageLoaded = true;
			}
			try {
				sleep(50);
			} catch (Exception e) {
			}
		}
		
	}
	public boolean isNextPageLoaded() {
		return nextPageLoaded;
	}
	public void setNextPageLoaded(boolean nextPageLoaded) {
		this.nextPageLoaded = nextPageLoaded;
	}
	public boolean isPreviousPageLoaded() {
		return previousPageLoaded;
	}
	public void setPreviousPageLoaded(boolean previousPageLoaded) {
		this.previousPageLoaded = previousPageLoaded;
	}
	public boolean isCurrentPageLoaded() {
		return currentPageLoaded;
	}
	public void setCurrentPageLoaded(boolean currentPageLoaded) {
		this.currentPageLoaded = currentPageLoaded;
	}
}
