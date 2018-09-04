#include "OseaQrsDetAlgorithmJNI.h"
#include <stdlib.h>


extern int SAMPLE_RATE;
extern int QRSDet(int ecgSample, int init) ;

JNIEXPORT void JNICALL Java_de_fzi_ekgeditor_algorithm_jni_OseaQrsDetAlgorithmJNI_initJNI (JNIEnv *env, jobject jobject, jintArray initSampleRates){
	SAMPLE_RATE = (*env).GetIntArrayElements(initSampleRates, 0)[0];
	QRSDet(0, 1);
}

JNIEXPORT jintArray JNICALL Java_de_fzi_ekgeditor_algorithm_jni_OseaQrsDetAlgorithmJNI_process (JNIEnv *env, jobject jobject, jintArray inValues){
	jintArray resultArray = (*env).NewIntArray(2);
	// convert JNI-arrays to c-arrays
	jint *valuesArray = (*env).GetIntArrayElements(inValues, 0);
	jint *result = (*env).GetIntArrayElements(resultArray, NULL);
	int trig_pos = QRSDet((int)(valuesArray[0]), 0);
	result[0] = (int)valuesArray[0];
	if (trig_pos > 0){
		result[1] = trig_pos;
	}else{
		result[1] = 0;
	}
	(*env).ReleaseIntArrayElements(resultArray, result, 0);
	return resultArray;
}
