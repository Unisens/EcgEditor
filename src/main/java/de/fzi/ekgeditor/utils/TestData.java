package de.fzi.ekgeditor.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.io.comparator.NameFileComparator;
import org.unisens.Unisens;
import org.unisens.UnisensFactory;
import org.unisens.UnisensFactoryBuilder;
import org.unisens.UnisensParseException;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.data.MedicalClass;
import de.fzi.ekgeditor.data.Registry;

public class TestData
{
	private static final String TESTDATA_PREFIX = "Testdata";
	private static final String TESTDATA_SEPERATOR = "_";
	private static final int NUMBER_OF_DIGITS = 4;

	/**
	 * Searches for the next available testdata name in path
	 * @param path Parent directory for testdata sets
	 * @param abbreviation short form in testdata name
	 * @return new testdata name
	 */
	public static String getNextTestdataDirectory(String path, final String abbreviation)
	{
		String numberOfDigits = Common.getInstance().reg.reg.getProperty(Registry.prop_numberOfDigits);
		if (numberOfDigits == null)
		{
			numberOfDigits = Integer.toString(NUMBER_OF_DIGITS);
		}
		final int iNumberOfDigits = Integer.parseInt(numberOfDigits);
		
		// The filename filter is for getting all testdata with matchin abbrevation.
		FilenameFilter testdataFilter = new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				if (name.startsWith(TESTDATA_PREFIX + TESTDATA_SEPERATOR + abbreviation + TESTDATA_SEPERATOR)  &&  name.length() == (TESTDATA_PREFIX + TESTDATA_SEPERATOR + abbreviation + TESTDATA_SEPERATOR).length() + iNumberOfDigits)
				{
					return true;
				}
				
				return false;
			}
		};
		
		// The files[] are NOT sorted, so use the apache tools for sorting.
		File directory = new File(path);
		File[] files = directory.listFiles(testdataFilter);
		Arrays.sort(files, NameFileComparator.NAME_INSENSITIVE_REVERSE);
		
		// The last characters are the number - try to read them as integer
		int currentNumber = 0;
		try 
		{
			currentNumber = Integer.parseInt(files[0].getName().substring(files[0].getName().length() - iNumberOfDigits));
		}
		catch (Exception e)
		{
		}
		
		// Add the leading-zero-trick and increment the counter
		currentNumber += (int) Math.pow(10, iNumberOfDigits);
		currentNumber++;
		
		// führende Nullen einfügen mit Trick :-)
		String currentNumberAsString = Integer.toString(currentNumber);
		currentNumberAsString = currentNumberAsString.substring(1);
		
		return TESTDATA_PREFIX + TESTDATA_SEPERATOR + abbreviation + TESTDATA_SEPERATOR + currentNumberAsString;
	}
	
	/**
	 * Saves the medical class as second opinion to customAttributes in Unisens file
	 * @param path Path to Unisens dataset
	 * @param medicalClass Medical class
	 * @throws UnisensParseException
	 * @throws IOException
	 */
	public static void saveSecondOpinion(String path, MedicalClass medicalClass) throws UnisensParseException, IOException
	{
		UnisensFactory unisensFactory = UnisensFactoryBuilder.createFactory();
		Unisens unisens = unisensFactory.createUnisens(path);
		unisens.addCustomAttribute("medicalClassesTitleSecondOpinion", medicalClass.title);
		unisens.addCustomAttribute("medicalClassesAbbreviationSecondOpinion", medicalClass.abbrev);
		unisens.save();
		unisens.closeAll();
	}

	/**
	 * Saves the medical class to customAttributes in Unisens file
	 * @param path Path to Unisens dataset
	 * @param medicalClass Medical class
	 * @throws UnisensParseException
	 * @throws IOException
	 */
	public static void saveMedicalClasses(String path, MedicalClass medicalClass) throws UnisensParseException, IOException
	{
		UnisensFactory unisensFactory = UnisensFactoryBuilder.createFactory();
		Unisens unisens = unisensFactory.createUnisens(path);
		unisens.addCustomAttribute("medicalClassesTitle", medicalClass.title);
		unisens.addCustomAttribute("medicalClassesAbbreviation", medicalClass.abbrev);
		unisens.save();
		unisens.closeAll();
	}
	
	/**
	 * Returns "medicalClassesTitleSecondOpinion" of given Unisens dataset.
	 * @param path Path to Unisens dataset
	 * @return medical class title if set or null
	 * @throws UnisensParseException
	 */
	public static String getSecondOpinion(String path) throws UnisensParseException
	{
		UnisensFactory unisensFactory = UnisensFactoryBuilder.createFactory();
		Unisens unisens = unisensFactory.createUnisens(path);
		HashMap<String, String> attributes = unisens.getCustomAttributes();
		unisens.closeAll();
		if(attributes.containsKey("medicalClassesTitleSecondOpinion"))
		{
			return attributes.get("medicalClassesTitleSecondOpinion");
		}
		else
		{
			return null;
		}
	}
}
