package de.fzi.ekgeditor.algorithm.jni;

public interface IAlgorithmJNI {
	void initJNI(int[] sampleRates);
	int[] process(int[] values);
}
