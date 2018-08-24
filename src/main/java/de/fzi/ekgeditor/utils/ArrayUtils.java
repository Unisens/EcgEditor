package de.fzi.ekgeditor.utils;

public class ArrayUtils {
	public static short[] getEcgChannelAsShortArray(Object ecg, int channel){
		if(ecg instanceof short[][]){
			short[][] ecgShort = (short[][])ecg;
			short[] result = new short[ecgShort.length];
			if(ecgShort[0].length < channel || channel < 0)
				return null;
			else
				for (int i = 0; i < result.length; i++) {
					result[i] = ecgShort[i][channel];
				}
			return result;
		}
		if(ecg instanceof int[][]){
			int[][] ecgInt = (int[][])ecg;
			short[] result = new short[ecgInt.length];
			if(ecgInt[0].length < channel || channel < 0)
				return null;
			else
				for (int i = 0; i < result.length; i++) {
					result[i] = (short)ecgInt[i][channel];
				}
			return result;
		}
		
		if(ecg instanceof long[][]){
			long[][] ecgLong = (long[][])ecg;
			short[] result = new short[ecgLong.length];
			if(ecgLong[0].length < channel || channel < 0)
				return null;
			else
				for (int i = 0; i < result.length; i++) {
					result[i] = (short)ecgLong[i][channel];
				}
			return result;
		}
		
		if(ecg instanceof float[][]){
			float[][] ecgFloat = (float[][])ecg;
			short[] result = new short[ecgFloat.length];
			if(ecgFloat[0].length < channel || channel < 0)
				return null;
			else
				for (int i = 0; i < result.length; i++) {
					result[i] = (short)ecgFloat[i][channel];
				}
			return result;
		}
		
		if(ecg instanceof double[][]){
			double[][] ecgDouble = (double[][])ecg;
			short[] result = new short[ecgDouble.length];
			if(ecgDouble[0].length < channel || channel < 0)
				return null;
			else
				for (int i = 0; i < result.length; i++) {
					result[i] = (short)ecgDouble[i][channel];
				}
			return result;
		}
		
		return null;
	}
}
