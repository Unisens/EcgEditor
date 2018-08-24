#include "osea4msp_jni.h"
#include "osea4msp.h"
#include "jni.h"
#include <stdlib.h>

/**
 * Runs a QRS detection on an ECG signal
 * @param env
 * @param clazz
 * @param j_ecg array with ECG samples
 * @param j_sampleRate sample rate of ECG signal
 * @return trigger list sampled in j_sampleRate
 */
int i = 0;
JNIEXPORT jintArray JNICALL Java_de_fzi_ekgeditor_analysis_Osea4Msp_analyze(
                            JNIEnv *env, jclass clazz, jshortArray j_ecg, jint j_sampleRate)
{
	int position;
	int nTrigger = 0;
	int* pTrigger;
	jsize len = (*env)->GetArrayLength(env, j_ecg);
	jshort *body = (*env)->GetShortArrayElements(env, j_ecg, 0);
	jintArray j_returnArray;

	pTrigger = (int*)malloc(len * sizeof(int));

	// initialize QRS detection
	qrsDetection(0, 1);

	// process QRS detection
	for (i = 0; i < len; i++)
	{
		position = qrsDetection(body[i], 0);

		if (position > 0)
		{
			*(pTrigger + nTrigger) = i - position;
			nTrigger++;
		}
	}

	// store results in j_returnArray
	j_returnArray = (*env)->NewIntArray(env, nTrigger);
	(*env)->ReleaseIntArrayElements(env, j_returnArray, pTrigger, 0);

	return j_returnArray;
}

JNIEXPORT jint JNICALL Java_de_fzi_ekgeditor_analysis_Osea4Msp_getCurrentSample
  (JNIEnv *env, jclass clazz){
	return i;
}
