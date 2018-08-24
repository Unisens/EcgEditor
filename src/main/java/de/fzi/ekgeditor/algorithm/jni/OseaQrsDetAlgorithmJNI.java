package de.fzi.ekgeditor.algorithm.jni;


public class OseaQrsDetAlgorithmJNI implements IAlgorithmJNI{
	public OseaQrsDetAlgorithmJNI(String name){
		System.loadLibrary(name);
	}
	public native void initJNI(int[] sampleRates);
	public native int[] process(int[] values);
}
