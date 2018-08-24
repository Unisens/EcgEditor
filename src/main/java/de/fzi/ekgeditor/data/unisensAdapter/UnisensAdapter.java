/**
 * This class offers some utility methods needed for Unisens 2.0 interface
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.data.unisensAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.UnknownTypeException;

import org.unisens.Entry;
import org.unisens.EventEntry;
import org.unisens.Group;
import org.unisens.SignalEntry;
import org.unisens.Unisens;

import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.utils.SelectionList;

public class UnisensAdapter
{

	/** Constant to indicate that a method should read as much data as it can */
	public static final int READALL = -1;;

	/** Constant to indicate that a method should read data from the beginning */
	public static final long READFROMBEGINNING = 0;

	/** Standard ECG-Group-Constant */
	public static final String GROUP_default_ecg = "default_ecg";

	/** ContentClass constants */
	public static final String ECG_CONTENT_CLASS = "ECG";
	public static final String TRIGGER_CONTENT_CLASS = "TRIGGER";
	public static final String PACER_CONTENT_CLASS = "PACER";
	public static final String ARTEFACT_CONTENT_CLASS = "ARTIFACT";

	/**
	 * private method to get the conversion rate for known units
	 * 
	 * @param s
	 *            SignalEntry for which the conversion rate should be
	 *            calculated.
	 * @return conversion rate.
	 */
	private static double getFudgeFactor(SignalEntry s)
	{
		UnisensUnits_ECG tmp = new UnisensUnits_ECG(s.getUnit());
		return tmp.getConversionRate(new UnisensUnits_ECG(UnisensUnits_ECG.ECGunits.mV));
	}


	/**
	 * get some factor for the calculation of the physical value
	 * 
	 * @param s
	 *            Source-SignalEntry
	 * @return LsbValue used for SignalEntry
	 */
	public static double getLsbValue(SignalEntry s)
	{
		double result = s.getLsbValue();
		if (result == 0)
		{
			// TODO: Nutzer nach dem LsbValue fragen
			result = Constants.StandardLsbValue;
		}

		return result;
	}

	/**
	 * Conversion method
	 * 
	 * Internally all our values we are dealing with are doubles. Unisens 2.0
	 * provides a generic interface for many different kinds of data types. To
	 * get some uniform view, the methods calculateValue convert this things to
	 * a standard uniform format (mV)
	 * 
	 * @param value
	 *            byte,short,int,long (all integer value-types) value, that
	 *            should be converted.
	 * @param s
	 *            SignalEntry (defines the conversion rate)
	 * @return uniform standard double value (unit: mV)
	 */
	public static double calculateValue(long value, double fudgeFactor, int baseline, double lsbValue)
	{
		return (double) ((value - baseline) * lsbValue * fudgeFactor);
	}

	/**
	 * Conversion method
	 * 
	 * Internally all our values we are dealing with are doubles. Unisens 2.0
	 * provides a generic interface for many different kinds of data types. To
	 * get some uniform view, the methods calculateValue convert this things to
	 * a standard uniform format (mV)
	 * 
	 * @param value
	 *            float, double (all floating-point number value types) value,
	 *            that should be converted.
	 * @param s
	 *            SignalEntry (defines the conversion rate)
	 * @return uniform standard double value (unit: mV)
	 */
	public static double calculateValue(double value, double fudgeFactor, int baseline, double lsbValue)
	{
		return (double) ((value - baseline) * lsbValue * fudgeFactor);
	}

	/**
	 * Unisens uniform read method
	 * 
	 * Internally all our values we are dealing with are doubles. Unisens 2.0
	 * provides a generic interface for many different kinds of data types. This
	 * method reads some value (or values) from the Unisens-file and implicitly
	 * converts the values to a standard uniform format (mV).
	 * 
	 * @param s
	 *            SignalEntry where we read from
	 * @param start
	 *            Starting frame for reading.
	 * @param length
	 *            Number of frames we should read.
	 * 
	 *            throws UnknownTypeException if the readed data type is
	 *            unknown.
	 * */
	public static double[][] readUniform(SignalEntry s, long start, int length) throws UnknownTypeException
	{
		try
		{
			double fudgeFactor = getFudgeFactor(s);
			int baseline = s.getBaseline();
			double lsbValue = s.getLsbValue();

			// Anzahl der Samples
			if (length == READALL)
			{
				if (s.getCount() > Integer.MAX_VALUE)
				{
					length = Integer.MAX_VALUE;
				}
				else
				{
					length = (int) s.getCount();
				}
			}

			Object o = s.read(start, length);

			if (o == null)
				return null;

			double[][] result = new double[length][s.getChannelCount()];

			if (o instanceof byte[][])
			{
				byte[][] temp = (byte[][]) o;

				for (int i = 0; i < temp.length; i++)
					for (int j = 0; j < temp[i].length; j++)
						result[i][j] = calculateValue((long) temp[i][j], fudgeFactor, baseline, lsbValue);
			} // endif instanceof byte
			else
			{
				if (o instanceof short[][])
				{
					short[][] temp = (short[][]) o;

					for (int i = 0; i < temp.length; i++)
						for (int j = 0; j < temp[i].length; j++)
							result[i][j] = calculateValue((long) temp[i][j], fudgeFactor, baseline, lsbValue);
				} // endif instanceof short
				else
				{
					if (o instanceof int[][])
					{
						int[][] temp = (int[][]) o;

						for (int i = 0; i < temp.length; i++)
							for (int j = 0; j < temp[i].length; j++)
								result[i][j] = calculateValue((long) temp[i][j], fudgeFactor, baseline, lsbValue);

					} // endif instanceof int
					else
					{
						if (o instanceof long[][])
						{
							long[][] temp = (long[][]) o;

							for (int i = 0; i < temp.length; i++)
								for (int j = 0; j < temp[i].length; j++)
									result[i][j] = calculateValue(temp[i][j], fudgeFactor, baseline, lsbValue);
						} // endif instanceof long
						else
						{
							if (o instanceof float[][])
							{
								float[][] temp = (float[][]) o;

								for (int i = 0; i < temp.length; i++)
									for (int j = 0; j < temp[i].length; j++)
										result[i][j] = calculateValue(temp[i][j], fudgeFactor, baseline, lsbValue);
							} // endif instanceof float
							else
							{
								if (o instanceof double[][])
								{
									double[][] temp = (double[][]) o;

									for (int i = 0; i < temp.length; i++)
										for (int j = 0; j < temp[i].length; j++)
											result[i][j] = calculateValue(temp[i][j], fudgeFactor, baseline, lsbValue);
								} // endif instanceof double
								else
								{
									throw new UnknownTypeException(null, o);
								}
							} // endif else instanceof float
						} // endif else instanceof long
					} // endif else instanceof int
				} // endif else instancof short
			} // endif else instanceof byte

			return result;
		}
		catch (IOException e)
		{
			System.err.println("Fehler beim Lesen der Datan." + e.getLocalizedMessage());
			return null;
		}
	}

	/**
	 * Get all the SignalEntries with the specific ID
	 * 
	 * @param entries
	 *            List of SignalEntries
	 * @param ID
	 *            ID of some SignalEntry
	 * @return List of SignalEntries with the specific ID
	 */
	public static List<SignalEntry> getSignalEntries(List<SignalEntry> entries, String ID)
	{
		ArrayList<SignalEntry> result = new ArrayList<SignalEntry>();
		for (SignalEntry s : entries)
		{
			if (s.getId().compareTo(ID) == 0)
			{
				result.add(s);
			}
		}

		return result;
	}

	private static List<SignalEntry> filterEntriesbyContentClass(String contentClass, List<Entry> entries)
	{
		ArrayList<SignalEntry> result = new ArrayList<SignalEntry>();

		for (Entry e : entries)
		{
			if (e.getContentClass() != null)
			{
				if (e.getContentClass().compareTo(contentClass) == 0)
				{
					result.add((SignalEntry) e);
				}
			}
			else
			{
				System.err.println("Merkwürdige SignalEntry-Definition");
			}
		}
		return result;
	}

	/**
	 * Get all (Signal) entries that contain ECG-Data
	 * 
	 * @param unisens
	 *            Unisens-file where we read from
	 * @return List of SignalEntries that contain ECG-Data
	 */
	public static List<SignalEntry> getECGEntries(Unisens unisens)
	{
		List<Entry> entries = unisens.getEntries();
		return filterEntriesbyContentClass(UnisensAdapter.ECG_CONTENT_CLASS, entries);
	}


	@SuppressWarnings("unchecked")
	public static List<SignalEntry> getECGGroup(Unisens unisens)
	{
		Group g = unisens.getGroup(UnisensAdapter.GROUP_default_ecg);
		if (g == null)
		{
			return getECGEntries(unisens);
		}
		else
		{
			List entries = g.getEntries();
			return filterEntriesbyContentClass(UnisensAdapter.ECG_CONTENT_CLASS, entries);
		}
	}

	public static Group findGroup(Unisens unisens, SignalEntry s)
	{
		List<Group> lg = unisens.getGroups();
		for (Group g : lg)
		{
			if (GroupHasEntry(g, s))
			{
				return g;
			}
		}

		return null;
	}

	public static boolean GroupHasEntry(Group g, Entry s)
	{
		List<Entry> l = g.getEntries();
		for (Entry e : l)
		{
			if (e.getId().compareTo(s.getId()) == 0)
			{
				return true;
			}
		}

		return false;
	}

	public static List<EventEntry> getEventEntriesFromGroup(Unisens unisens, Group g, String contentClass,
			boolean findMore)
	{
		List<EventEntry> eventEntries = new ArrayList<EventEntry>();
		if (g != null)
		{
			for (Entry entry : g.getEntries())
			{
				if (entry.getContentClass() != null)
				{
					if (entry.getContentClass().equalsIgnoreCase(contentClass))
					{
						eventEntries.add((EventEntry) entry);
					}
				}
				else
				{
					System.err.println("UnisensAdapter-getTriggerEntriesFromGroup : Content-Class is null");
				}
			}
		}

		if (findMore)
		{ // find all other triggers
			List<Entry> allEntries = unisens.getEntries();
			for (Entry entry : allEntries)
			{
				if (entry.getContentClass() != null)
				{
					if (entry.getContentClass().equalsIgnoreCase(contentClass) && !eventEntries.contains(entry))
					{
						if (entry instanceof EventEntry)
						{
							eventEntries.add((EventEntry) entry);
						}
						else
						{
							System.err.println("UnisensAdapter-getTriggerEntriesFromGroup : no EventEntry");
						}
					}
				}
				else
				{
					System.err.println("UnisensAdapter-getTriggerEntriesFromGroup : Content-Class is null");
				}
			}
		}

		return eventEntries;
	}

	public static List<EventEntry> getTriggerEntries(Unisens unisens)
	{
		Group g = unisens.getGroup(UnisensAdapter.GROUP_default_ecg);
		ArrayList<EventEntry> result = new ArrayList<EventEntry>();
		List<Entry> l = unisens.getEntries();

		if (g != null)
		{
			List<Entry> entries = g.getEntries();
			for (Entry e : entries)
			{
				if (e.getContentClass() != null)
				{
					if (e.getContentClass().equalsIgnoreCase(UnisensAdapter.TRIGGER_CONTENT_CLASS))
					{
						result.add((EventEntry) e);
						return result;
					}
				}
				else
				{
					System.err.println("Merkwürdige EventEntry-Definition");
				}
			}
		}

		for (Entry e : l)
		{
			if (e.getContentClass().compareTo(UnisensAdapter.TRIGGER_CONTENT_CLASS) == 0)
			{
				result.add((EventEntry) e);
			}
		}

		return result;
	}

	public static List<SignalEntry> getSignalEntries(Unisens unisens)
	{
		List<SignalEntry> signalEntries = new ArrayList<SignalEntry>();
		for (Entry entry : unisens.getEntries())
		{
			if (entry instanceof SignalEntry)
				signalEntries.add((SignalEntry) entry);
		}
		return signalEntries;
	}

	private static String generateTestDataFileName(Unisens unisensFile)
	{
		return unisensFile.getPath() + "testdata.list";
	}

	public static boolean saveTestDataInformation(Unisens unisensFile, SelectionList testdataSelection)
	{
		boolean result = true;

		String fileName = generateTestDataFileName(unisensFile);
		try
		{
			FileOutputStream fs = new FileOutputStream(new File(fileName));
			ObjectOutputStream os = new ObjectOutputStream(fs);
			if (Constants.isDebug)
			{
				System.out.println("saveTestDataInformation, exportierte Bereiche Liste:");
				testdataSelection.printOut();
			}
			os.writeObject(testdataSelection);
			os.close();
			fs.close();
		}
		catch (FileNotFoundException ex)
		{
			result = false;
			System.err.println("Serialisierung testdata fehlgeschlagen." + ex.getMessage());
		}
		catch (IOException io)
		{
			result = false;
			System.err.println("Serialisierung testdata fehlgeschlagen." + io.getMessage());
		}

		return result;
	}

	public static SelectionList readTestDataInformation(Unisens unisensFile)
	{
		boolean result = true;

		String fileName = generateTestDataFileName(unisensFile);
		SelectionList res = null;
		try
		{
			FileInputStream fs = new FileInputStream(fileName);
			ObjectInputStream os = new ObjectInputStream(fs);
			res = (SelectionList) os.readObject();
			os.close();
			fs.close();
		}
		catch (ClassNotFoundException ex)
		{
			result = false;
			System.err.println("Serialisierung testdata fehlgeschlagen." + ex.getMessage());
		}
		catch (FileNotFoundException ex)
		{
			result = false;
			if (Constants.isDebug)
			{
				System.out.println("Serialisierung testdata fehlgeschlagen." + ex.getMessage());
			}
		}
		catch (IOException io)
		{
			result = false;
			System.err.println("Serialisierung testdata fehlgeschlagen." + io.getMessage());
		}

		if (result)
		{
			return res;
		}
		else
		{
			return null;
		}
	}
}
