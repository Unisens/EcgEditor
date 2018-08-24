package de.fzi.ekgeditor.data.unisensAdapter;

public class Buffer {
	private double[][] data;
	private long startSample = 0;
	
	public Buffer(){
		
	}
	
	public Buffer(double[][] data, long startSample){
		this.data = data;
		this.startSample = startSample;
	}
	public double[][] getData() {
		return data;
	}
	public void setData(double[][] data) {
		this.data = data;
	}
	public long getStartSample() {
		return startSample;
	}
	public void setStartSample(long startSample) {
		this.startSample = startSample;
	}
	
}
