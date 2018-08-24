package de.fzi.ekgeditor.algorithm.jni;


public class OseaQrsDet2AlgorithmJNI implements IAlgorithmJNI{
	public OseaQrsDet2AlgorithmJNI(String name){
		System.loadLibrary(name);
	}
	public native void initJNI(int[] sampleRates);
	public native int[] process(int[] values);
}
