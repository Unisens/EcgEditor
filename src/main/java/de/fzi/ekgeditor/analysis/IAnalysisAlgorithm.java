package de.fzi.ekgeditor.analysis;

public interface IAnalysisAlgorithm {
	public int[] analyze(short[] ecg, int sampleRate); 
	public int getCurrentSample();
}
