package de.fzi.ekgeditor.analysis;


public class Osea4Msp implements IAnalysisAlgorithm
{
	static 
	{ 
		System.loadLibrary("osea4msp_jni"); 
	} 

	/* Aufruf um die Header-Datei zu erstellen: 
	 * $ cd /Projekte/Unisens/Eclipse-Workspace/EKGEditor/bin
	 * $ javah -jni -o osea4msp_jni.h -classpath /Projekte/Unisens/Eclipse-Workspace/EKGEditor/bin/ de.fzi.ekgeditor.analysis.Osea4Msp
	 * 
	 * create dll library
	 * $cl -IC:\Programme\Java\jdk1.6.0_04\include -IC:\Programme\Java\jdk1.6.0_04\include\win32 -LD *.c -Feosea4msp_jni.dll
	 */
	
	/**
	 * QRS detector for ECG signal
	 * @param ecg ECG signal
	 * @param sampleRate sample rate of ECG signal
	 * @return trigger list with N-triggers, sampled with sampleRate
	 */
	public native int[] analyze(short[] ecg, int sampleRate); 
	
	public native int getCurrentSample();
}
