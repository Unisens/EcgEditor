package de.fzi.ekgeditor.analysis;


public class AnalysisAlgorithmThread extends Thread {
	private IAnalysisAlgorithm analysisAlgorithm;
	private short[] ecg;
	private int sampleRate;
	private int[] triggers;
	
	public AnalysisAlgorithmThread(IAnalysisAlgorithm analysisAlgorithm, short[] ecg, int sampleRate){
		this.analysisAlgorithm = analysisAlgorithm;
		this.ecg = ecg;
		this.sampleRate = sampleRate;
	}
	
	public void run(){
		triggers = analysisAlgorithm.analyze(ecg, sampleRate);
		this.interrupt();
	}
	
	public int getCurrentSample(){
		return analysisAlgorithm.getCurrentSample();
	}
	
	public int[] getAnalysisTriggers(){
		return triggers;
	}
}
