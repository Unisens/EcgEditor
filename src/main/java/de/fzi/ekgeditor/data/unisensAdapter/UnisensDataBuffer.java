/**
 * This class provides some data buffer
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.data.unisensAdapter;

import org.unisens.SignalEntry;

import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.events.CacheListener;


public class UnisensDataBuffer {
	private int bufferSize = 300000;
	private int channelCount;
	private Buffer previous;
	private Buffer current;
	private Buffer next;
	private long sampleCount;
	private UnisensDataBufferLoader loader;
	
	public UnisensDataBuffer(SignalEntry s){
		this.bufferSize = (int)s.getSampleRate() * 5 * 60;
		channelCount = s.getChannelCount();
		sampleCount = s.getCount();
		current = new Buffer();
		next = new Buffer();
		previous = new Buffer();
		loader = new UnisensDataBufferLoader(this, s);
		loader.start();
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
	}
	
	public double read(long sample, int channel){
		try {
				if(sample >= current.getStartSample() && sample < (current.getStartSample() + bufferSize))
					return current.getData()[(int)(sample - current.getStartSample())][channel];
				if(sample > current.getStartSample() + 3*bufferSize - 1){
					current.setStartSample(sample - sample%bufferSize);
					loader.setCurrentPageLoaded(false);
					if(sampleCount > current.getStartSample() + bufferSize)
						loader.setNextPageLoaded(false);
					loader.setPreviousPageLoaded(false);
					while(!loader.isCurrentPageLoaded())
						Thread.sleep(100);
					synchronized (current) {
						return current.getData()[(int)(sample - current.getStartSample())][channel];
					}
				}
				if(sample > current.getStartSample() + 2*bufferSize - 1){
					previous = next;
					current.setStartSample(sample - sample%bufferSize);
					loader.setCurrentPageLoaded(false);
					if(sampleCount > current.getStartSample() + bufferSize)
						loader.setNextPageLoaded(false);
					while(!loader.isCurrentPageLoaded())
						Thread.sleep(100);
					synchronized (current) {
						return current.getData()[(int)(sample - current.getStartSample())][channel];
					}
				}
				if((sample > current.getStartSample() + bufferSize - 1) && (sample < current.getStartSample() + bufferSize + bufferSize/6)){
					synchronized (next) {
						return next.getData()[(int)(sample - current.getStartSample() - bufferSize)][channel];
					}
				}
				if(sample > current.getStartSample() + bufferSize - 1){
					previous = current;
					if(loader.isNextPageLoaded()){
						current = next;
					}else{
						current.setStartSample(current.getStartSample() + bufferSize);
						loader.setCurrentPageLoaded(false);
					}
					
					if(sampleCount > current.getStartSample() + bufferSize)
						loader.setNextPageLoaded(false);
					
					while(!loader.isCurrentPageLoaded())
						Thread.sleep(100);
					synchronized (current) {
						return current.getData()[(int)(sample - current.getStartSample())][channel];
					}
				}
				if((bufferSize/6 + current.getStartSample() - bufferSize) < sample && sample < current.getStartSample()){
					synchronized (previous) {
						return previous.getData()[(int)(sample - current.getStartSample() + bufferSize)][channel];
					}
				}
				if((current.getStartSample() - bufferSize) < sample && sample < current.getStartSample()){
					next = current;
					if(loader.isPreviousPageLoaded()){
						current = previous;
					}else{
						current.setStartSample(current.getStartSample() - bufferSize);
						loader.setCurrentPageLoaded(false);
					}
					if(current.getStartSample() != 0)
						loader.setPreviousPageLoaded(false);
					
					while(!loader.isCurrentPageLoaded())
						Thread.sleep(100);
					
					synchronized (current) {
						return current.getData()[(int)(sample - current.getStartSample())][channel];
					}
				}
				if( sample < current.getStartSample() - 2*bufferSize){
					current.setStartSample(sample - sample%bufferSize);
					loader.setCurrentPageLoaded(false);
					loader.setNextPageLoaded(false);
					if(current.getStartSample() != 0)
						loader.setPreviousPageLoaded(false);
					while(!loader.isCurrentPageLoaded())
						Thread.sleep(100);
					synchronized (current) {
						return current.getData()[(int)(sample - current.getStartSample())][channel];
					}
				}
			
				if( sample < current.getStartSample() - bufferSize ){
					next = previous;
					current.setStartSample(current.getStartSample() - 2 * bufferSize);
					loader.setCurrentPageLoaded(false);
					if(current.getStartSample() != 0)
						loader.setPreviousPageLoaded(false);
					while(!loader.isCurrentPageLoaded())
						Thread.sleep(100);
					synchronized (current) {
						return current.getData()[(int)(sample - current.getStartSample())][channel];
					}
				}

				return -1;
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			return -2;
		} catch (ArrayIndexOutOfBoundsException e) {
			return read(sample, channel);
		}

	}
	
	public void addCacheListener( CacheListener listener ) { 
		
	} 

	public void removeCacheListener( CacheListener listener ) { 

	} 
	
	public void finalize() throws Throwable
	{
		try
		{
			
			if (loader !=null)
			{
				loader.interrupt();
				loader=null;
				this.current = null;
				this.next = null;
				this.previous = null;
			}
			if (Constants.isDebug)
			{
				System.out.println("UnisensDataBuffer finalisiert.");
			}
		}
		catch (Exception e)
		{
			super.finalize();
		}
	}
	
	public double getFillStatus()
	{
		return 100;
	}

	public Buffer getPrevious() {
		return previous;
	}

	public  void setPrevious(Buffer previous) {
		this.previous = previous;
	}

	public Buffer getNext() {
		return next;
	}

	public  void setNext(Buffer next) {
		this.next = next;
	}

	public int getBufferSize() {
		return bufferSize;
	}
	
	public int getChannelCount() {
		return channelCount;
	}

	public long getStartSampleInBuffer() {
		return current.getStartSample();
	}
	public  Buffer getCurrent() {
		return current;
	}
	public  void setCurrent(Buffer current) {
		this.current = current;
	}
}
