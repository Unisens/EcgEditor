/**
 * This class is manages all the annotation codes (for the triggers)
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor.data;

public class Constants_AnnotationCodes {

	// source: http://physionet.fri.uni-lj.si/physiotools/wpg/wpg_31.htm#SEC132
	
	/** Constant defining that this is a beat annotation */
	public static final boolean isBeatAnnotation=true;
	/** Constant defining that this is NOT a beat annotation */
	public static final boolean isNOTBeatAnnotation=false;
	
	/** Array of all the annotation codes */
	public static final AnnotationCode[] annotationcodes=
	{
		new AnnotationCode("NORMAL",	"N",	"Normal beat",						isBeatAnnotation),
		new AnnotationCode("LBBB",		"L",	"Left bundle branch block beat",	isBeatAnnotation),
		new AnnotationCode("RBBB",		"R",	"Right bundle branch block beat",	isBeatAnnotation),
		new AnnotationCode("BBB",		"B",  	"Bundle branch block beat (unspecified)",isBeatAnnotation),
		new AnnotationCode("APC",		"A",   	"Atrial premature beat",			isBeatAnnotation),
		new AnnotationCode("APC",      	"A",   	"Atrial premature beat",			isBeatAnnotation),
		new AnnotationCode("ABERR",    	"a",   	"Aberrated atrial premature beat",	isBeatAnnotation),
		new AnnotationCode("NPC",      	"J",   	"Nodal (junctional) premature beat",isBeatAnnotation),
		new AnnotationCode("SVPB",     	"S",   	"Supraventricular premature or ectopic beat (atrial or nodal)",isBeatAnnotation),
		new AnnotationCode("PVC",      	"V",   	"Premature ventricular contraction",isBeatAnnotation),
		new AnnotationCode("RONT",     	"r",   	"R-on-T premature ventricular contraction",isBeatAnnotation),
		new AnnotationCode("FUSION",   	"F",   	"Fusion of ventricular and normal beat",isBeatAnnotation),
		new AnnotationCode("AESC",     	"e",   	"Atrial escape beat",				isBeatAnnotation),
		new AnnotationCode("NESC",     	"j",   	"Nodal (junctional) escape beat",	isBeatAnnotation),
		new AnnotationCode("SVESC",    	"n",   	"Supraventricular escape beat (atrial or nodal)",isBeatAnnotation),
		new AnnotationCode("VESC",     	"E",   	"Ventricular escape beat",			isBeatAnnotation),
		new AnnotationCode("PACE",     	"/",   	"Paced beat",						isBeatAnnotation),
		new AnnotationCode("PFUS",     	"f",   	"Fusion of paced and normal beat",	isBeatAnnotation),
		new AnnotationCode("UNKNOWN",  	"Q",   	"Unclassifiable beat",				isBeatAnnotation),
		new AnnotationCode("LEARN",    	"?",   	"Beat not classified during learning",isBeatAnnotation),
		
		new AnnotationCode("VFON",     	"[",   	"Start of ventricular flutter/fibrillation",isNOTBeatAnnotation),
		new AnnotationCode("FLWAV",    	"!",   	"Ventricular flutter wave",			isNOTBeatAnnotation),
		new AnnotationCode("VFOFF",    	"]",   	"End of ventricular flutter/fibrillation",isNOTBeatAnnotation),
		new AnnotationCode("NAPC",     	"x",   	"Non-conducted P-wave (blocked APC)",isNOTBeatAnnotation),
		new AnnotationCode("WFON",     	"(",   	"Waveform onset",					isNOTBeatAnnotation),
		new AnnotationCode("WFOFF",    	")",   	"Waveform end",						isNOTBeatAnnotation),
		new AnnotationCode("PWAVE",    	"p",   	"Peak of P-wave",					isNOTBeatAnnotation),
		new AnnotationCode("TWAVE",    	"t",   	"Peak of T-wave",					isNOTBeatAnnotation),
		new AnnotationCode("UWAVE",    	"u",   	"Peak of U-wave",					isNOTBeatAnnotation),
		new AnnotationCode("PQ",       	"`",   	"PQ junction",						isNOTBeatAnnotation),
		new AnnotationCode("JPT",      	"'",   	"J-point",							isNOTBeatAnnotation),
		new AnnotationCode("PACESP",   	"^",   	"(Non-captured) pacemaker artifact",isNOTBeatAnnotation),
		new AnnotationCode("ARFCT",    	"|",   	"Isolated QRS-like artifact",		isNOTBeatAnnotation),
		new AnnotationCode("NOISE",    	"~",   	"Change in signal quality",			isNOTBeatAnnotation),
		new AnnotationCode("RHYTHM",   	"+",   	"Rhythm change",					isNOTBeatAnnotation),
		new AnnotationCode("STCH",     	"s",   	"ST segment change",				isNOTBeatAnnotation),
		new AnnotationCode("TCH",      	"T",   	"T-wave change",					isNOTBeatAnnotation),
		new AnnotationCode("SYSTOLE",  	"*",   	"Systole",							isNOTBeatAnnotation),
		new AnnotationCode("DIASTOLE", 	"D",   	"Diastole",							isNOTBeatAnnotation),
		new AnnotationCode("MEASURE",  	"=",   	"Measurement annotation",			isNOTBeatAnnotation),
		new AnnotationCode("NOTE",     	"\"",  	"Comment annotation",				isNOTBeatAnnotation),
		new AnnotationCode("LINK",     	"@",   	"Link to external data",			isNOTBeatAnnotation),
	};
	
	/** Method to get a annotion code object by name 
	 * 
	 * @param code short code of the annotation
	 * @return AnnotationCode object representing this annotation type or null if it can not be found.
	 */
	public static final AnnotationCode getAnnotationToCode(String code)
	{
		for (AnnotationCode a:annotationcodes)
		{
			if (a.code.compareTo(code)==0)
			{
				return a;
			}
		}
		
		return null;
	}
}
