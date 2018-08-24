package de.fzi.ekgeditor.utils;

import java.io.File;
import java.io.IOException;

import org.unisens.ri.io.BufferedFileWriter;

public class TriggerListUtils {
	private static String NEWLINE = System.getProperty("line.separator");
	private static String separator = ";";
	
	public static void createTriggerListFile(String fileName, int[] samplestamps, String[] values) throws IOException{
		File file = new File(fileName);
		if(file.exists())
			file.delete();
		BufferedFileWriter fileWriter = new BufferedFileWriter(file);
		for (int i = 0; i < Math.min(samplestamps.length, values.length); i++)
			fileWriter.writeString(samplestamps[i] + separator + values[i] + NEWLINE);
		fileWriter.flush();
		fileWriter.close();
	}
	
}
