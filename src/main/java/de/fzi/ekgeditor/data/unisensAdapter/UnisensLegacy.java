package de.fzi.ekgeditor.data.unisensAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.unisens.Context;
import org.unisens.CustomEntry;
import org.unisens.DuplicateIdException;
import org.unisens.Entry;
import org.unisens.Event;
import org.unisens.EventEntry;
import org.unisens.Group;
import org.unisens.SignalEntry;
import org.unisens.Unisens;
import org.unisens.UnisensParseException;
import org.unisens.Value;
import org.unisens.ValuesEntry;
import org.unisens.ri.UnisensImpl;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Forms.Dialog_Wait;
import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.utils.Selection;
import de.fzi.ekgeditor.utils.SelectionList;

public class UnisensLegacy
{
	/**
	 * Copy one uniform file to some other and ommit all entries in cutListAll
	 * 
	 * @param unisens
	 *            The unisens-file that should be copied (Source)
	 * @param filename
	 *            The (new) unisens-file (destination)
	 * @param cutListAll
	 *            List of timed areas which should be cut out.
	 * @param cutListSampleRate
	 *            Sample-Rate of the timed areas of cutListAll
	 * @return true, if operation was successfull otherwise false.
	 */

	private static boolean copyCancel = false;

	private static boolean success = false;

	/** some debug constants */
	private static final boolean debug_writeSignal = true;
	private static final boolean debug_writeEvents = true;
	private static final boolean debug_writeValues = true;
	private static final boolean debug_writeGroups = true;

	public static void copyUnisens(final Unisens unisens, final String filename, final String comment,
			final SelectionList cutSelectionListInMs, final Shell parent, boolean isCut)
	{
		success = false;

		if (Constants.showSaveProgressWindow)
		{
			final Dialog_Wait w = new Dialog_Wait(parent, 0, 100, "Speichern von " + filename);
			final boolean FisCut = isCut;

			Thread t = new Thread()
			{
				public void run()
				{
					copyCancel = false;
					success = copyUnisensReal(unisens, filename, comment, cutSelectionListInMs, w, FisCut);
				}
			};

			t.start();

			Object result = w.open();
			if ((result == null) || ((Integer) result == SWT.CANCEL))
			{
				copyCancel = true;
			}

			try
			{
				t.join();
			}
			catch (InterruptedException ex)
			{
				System.err.println("Unerwartete Ausnahme in UnisensLegacy-copyUnisens:" + ex.getMessage());
			}

		}
		else
		{
			success = copyUnisensReal(unisens, filename, comment, cutSelectionListInMs, null, isCut);
		}

		if (!success)
		{
			Common.getInstance().ShowErrorBox(Constants.error, "Das Speichern konnte NICHT durchgeführt werden.");
		}
		else
		{
			Common.getInstance().ShowMessageBox("Operation erfolgreich",
					"Das Speichern konnte erfolgreich durchgeführt werden", SWT.OK);
		}
	}

	@SuppressWarnings("unchecked")
	private static boolean copyUnisensReal(Unisens unisens, String filename, String comment,
			SelectionList cutSelectionListInMs, Dialog_Wait waitDialog, boolean isCut)
	{
		boolean result = true;
		Unisens copyUnisens = null;
		try
		{
			copyUnisens = new UnisensImpl(filename);
		}
		catch (UnisensParseException e)
		{
			e.printStackTrace();
		}
		
		

		copyUnisens.setMeasurementId(unisens.getMeasurementId());

		if (unisens.getComment() == null && comment != null)
			copyUnisens.setComment(comment);
		else if (unisens.getComment() != null && comment != null)
			copyUnisens.setComment(comment + " (" + unisens.getComment() + ")");
		else if (unisens.getComment() != null && comment == null)
			copyUnisens.setComment(unisens.getComment());

		// aktuellen Timestamp als Startzeit hinzufügen
		copyUnisens.setTimestampStart(new Date());

		Context c = unisens.getContext();
		if (c != null)
		{
			copyUnisens.createContext(c.getSchemaUrl());
		}
		// TODO: How to write Context-Information
		copyUnisens.setDuration(unisens.getDuration());

		List<Entry> entries = unisens.getEntries();
		try
		{
			for (Entry o : entries)
			{
				if (o instanceof SignalEntry)
				{
					if (UnisensLegacy.debug_writeSignal)
					{
						SignalEntry signalEntry = (SignalEntry) o;

						SelectionList cutSelectionListInSignalSamples = null;
						if (cutSelectionListInMs != null)
						{
							cutSelectionListInSignalSamples = cutSelectionListInMs
									.TransformToSampleSelection(signalEntry.getSampleRate());
						}

						if (isCut)
						{
							copyAndCutSignalEntry(signalEntry, copyUnisens, cutSelectionListInSignalSamples, waitDialog);
						}
						else
						{
							SignalEntry copySignalEntry = (SignalEntry) copyUnisens.addEntry(signalEntry, false);

							Selection exportArea = cutSelectionListInSignalSamples.nextSelection(0);
							int numberOfSamples = (int) (exportArea.getSelectionEnd() - exportArea.getSelectionStart());

							UnisensLegacy.copySignalData(signalEntry, copySignalEntry, exportArea.getSelectionStart(),
									numberOfSamples);
						}
						if (copyCancel)
						{
							break;
						}
					}
				}
				else
				{
					if (o instanceof ValuesEntry)
					{
						if (UnisensLegacy.debug_writeValues)
						{
							ValuesEntry valuesEntry = (ValuesEntry) o;
							SelectionList cutSelectionListInValueSampleStamp = null;

							if (cutSelectionListInMs != null)
							{
								cutSelectionListInValueSampleStamp = cutSelectionListInMs.TransformToSampleSelection(
										valuesEntry.getSampleRate()).getConsolidatedList();
							}

							copyValuesEntry(valuesEntry, copyUnisens, cutSelectionListInValueSampleStamp, waitDialog,
									isCut);
							if (copyCancel)
							{
								break;
							}
						}
					}
					else
					{
						if (o instanceof EventEntry)
						{
							if (UnisensLegacy.debug_writeEvents)
							{
								EventEntry eventEntry = (EventEntry) o;
								SelectionList cutSelectionListInEventSampleStamp = null;
								if (cutSelectionListInMs != null)
								{
									cutSelectionListInEventSampleStamp = cutSelectionListInMs
											.TransformToSampleSelection(eventEntry.getSampleRate())
											.getConsolidatedList();
								}

								copyEventEntry(eventEntry, copyUnisens, cutSelectionListInEventSampleStamp, waitDialog,
										isCut);
								if (copyCancel)
								{
									break;
								}
							}
						}
						else
						{
							if (o instanceof CustomEntry)
							{
								copyUnisens.addEntry((CustomEntry) o, true);
							}
						}
					}
				}
			} // Entries copied

			if (UnisensLegacy.debug_writeGroups)
			{
				List<Group> groups = unisens.getGroups();
				for (Group group : groups)
				{
					copyUnisens.addGroup(group, false);
				}
			}

		}
		catch (Exception ex)
		{
			result = false;
			System.err.println(ex.getMessage());
		}

		try
		{
			copyUnisens.save();
			copyUnisens.closeAll();
		}
		catch (IOException ex)
		{
			result = false;
		}
		catch (Exception ex)
		{
			result = false;
		}

		if (waitDialog != null)
		{
			waitDialog.closeSynchronized();
		}

		if (Constants.isDebug)
		{
			System.out.println("copyUnisensReal beendet." + copyCancel);
			// Dirty hack to communicate result:
			System.out.println("CopyUnisens-Result:" + result);
		}

		return result;
		
	}

	/**
	 * Copy signal data from position pos
	 * 
	 * @param signalEntry
	 *            source-signal Entry
	 * @param copySignalEntry
	 *            destination signal Entry
	 * @param pos
	 *            Start postion
	 * @param lenght
	 *            Number of samples to copy
	 * 
	 * @return new pointer, or old pointer if sth. went wrong
	 */
	public static long copySignalData(SignalEntry signalEntry, SignalEntry copySignalEntry, long startSample, int lenght)
	{
		long pos = startSample;
		// normal read and write (copy)
		try
		{
			if (lenght > 0)
			{
				System.out.print("*");
				copySignalEntry.append(signalEntry.read(pos, lenght));
				System.out.print("#");

				if (Constants.isDebug)
				{
					System.out.println("Wrote:" + pos + " - " + (pos + lenght - 1) + "(" + lenght + ")");
				} // endif debug

				pos = pos + lenght;
			} // endif length>0
		}
		catch (IOException ex)
		{
			System.err.println("Fehler in copySignalData:" + ex.getMessage());
			return startSample;
		}

		return pos;
	}

	private static void setProgess(long pos, long samples, Dialog_Wait w)
	{
		// System.out.println(pos+" "+samples);
		if (w != null)
		{
			w.setProgressSynchronized((int) Math.round(((double) pos / ((double) samples / (double) 100))));
		}
	}

	/**
	 * Copy signal the full signal entry
	 * 
	 * @param signalEntry
	 *            signal Entry to copy
	 * @param copyDestination
	 *            the destination unisens-file
	 * @param cutSelectionListInSignalSamples
	 *            signal areas to cut (in samples)
	 */
	public static boolean copyAndCutSignalEntry(SignalEntry signalEntry, Unisens copyDestination,
			SelectionList cutSelectionListInSignalSamples, Dialog_Wait waitDialog)
	{
		boolean result = true;

		if (Constants.isDebug)
		{
			System.out.println(signalEntry.getId());
		}

		try
		{
			// set header for new signal-entry
			SignalEntry copySignalEntry = (SignalEntry) copyDestination.addEntry(signalEntry, false);

			// prepare to copy data
			long signalEntrySamplesCount = signalEntry.getCount();
			signalEntry.resetPos();
			long pos = 0;

			while (pos < signalEntrySamplesCount)
			{
				if (copyCancel)
				{
					break;
				}

				UnisensLegacy.setProgess(pos, signalEntrySamplesCount, waitDialog);
				// calculate std. length
				int length = Constants.stdSampleBlock;
				if (pos + length > signalEntrySamplesCount)
				{
					length = (int) (signalEntrySamplesCount - pos);
				}

				// if there is some samples we want to ommit, we handle it here:
				boolean cutThisIteration = true;
				boolean hadCut = false;
				Selection nextCutSelectionInSignalSamples = null;
				Selection tempSelectionInSignalSamples = null;

				if (cutSelectionListInSignalSamples != null)
				{
					if (nextCutSelectionInSignalSamples == null)
					{
						nextCutSelectionInSignalSamples = cutSelectionListInSignalSamples.nextSelection(pos);
					}
					if (nextCutSelectionInSignalSamples != null)
					{
						tempSelectionInSignalSamples = new Selection(pos, pos + length - 1);
						if (!tempSelectionInSignalSamples.isIntersectionAreaZero(nextCutSelectionInSignalSamples))
						{
							length = (int) (nextCutSelectionInSignalSamples.getSelectionStart() - pos);
							hadCut = true;
						}
					}
					else
					{
						cutThisIteration = false;
					}
				} // endif cutSelectionListInSignalSamples != null
				else
				{
					cutThisIteration = false;
				} // endif else cutSelectionListInSignalSamples != null

				long pos2 = copySignalData(signalEntry, copySignalEntry, pos, length);
				if (pos == pos2)
				{
					return false;
				}
				else
				{
					pos = pos2;
				}

				// if we have cut sth. we need to increase position-pointer
				// here.
				if (cutThisIteration)
				{
					if (hadCut)
					{
						long oldpos = pos;
						pos = pos + nextCutSelectionInSignalSamples.getLength() + 1;

						if (Constants.isDebug)
						{
							System.out.println("Ausgeschnitten:" + nextCutSelectionInSignalSamples);
							System.out.println("Positionzeiger (vorher)" + oldpos + ", (nachher)" + pos
									+ " readLength:" + length);
						} // endif isDebug

						nextCutSelectionInSignalSamples = null;

					} // endif hadCut
				} // end if cutThisIteration
			} // end while pos<samples

		}
		catch (DuplicateIdException ex)
		{
			System.out.println("Fehler:" + ex.getMessage());
			result = false;
		}


		return result;
	}

	public static boolean copyValuesEntry(ValuesEntry valuesEntry, Unisens copyUnisens,
			SelectionList cutSelectionListInValueSampleStamp, Dialog_Wait waitDialog, boolean isCut)
	{
		boolean result = true;

		try
		{
			if (Constants.isDebug)
			{
				System.out.println(valuesEntry.getId());
			}

			ValuesEntry copyValuesEntry = (ValuesEntry) copyUnisens.addEntry(valuesEntry, false);

			long samples = valuesEntry.getCount();
			long pos = 0;
			UnisensLegacy.setProgess(pos, samples, waitDialog);

			Value[] values = valuesEntry.read(pos, (int) samples);
			List<Value> valuesToAppendAsList = new ArrayList<Value>();
			for (Value value : values)
			{
				if (copyCancel)
				{
					break;
				}
				long sampleStamp = value.getSampleStamp();

				boolean doAppend = cutSelectionListInValueSampleStamp.inList(sampleStamp);
				long offset = 0;
				if (isCut)
				{
					doAppend = !doAppend;
					offset = cutSelectionListInValueSampleStamp.getAreaUntilPoint(sampleStamp);
				}
				else
				{
					offset = cutSelectionListInValueSampleStamp.nextSelection(0).getSelectionStart();
				}

				long newSampleStamp = sampleStamp - offset;

				if (doAppend)
				{
					value.setSampleStamp(newSampleStamp);
					valuesToAppendAsList.add(value);
				}
			}
			if (valuesToAppendAsList.size() != 0)
			{
				copyValuesEntry.append(valuesToAppendAsList.toArray(new Value[0]));
			}

		}
		catch (DuplicateIdException ex)
		{
			System.err.println("copyValuesEntry:" + ex.getMessage());
			return false;
		}
		catch (IOException ex)
		{
			System.err.println("copyValuesEntry:" + ex.getMessage());
			return false;
		}
		return result;
	}

	public static boolean copyEventEntry(EventEntry eventEntry, Unisens copyUnisens,
			SelectionList cutSelectionListInEventSampleStamp, Dialog_Wait waitDialog, boolean isCut)
	{
		boolean result = true;

		try
		{
			if (Constants.isDebug)
			{
				System.out.println(eventEntry.getId());
			}
			EventEntry copyEventEntry = (EventEntry) copyUnisens.addEntry(eventEntry, false);

			long eventsCount = eventEntry.getCount();
			long pos = 0;

			List<Event> events = eventEntry.read(pos, (int) eventsCount);
			List<Event> eventsToAppend = new ArrayList<Event>();
			for (Event event : events)
			{
				UnisensLegacy.setProgess(pos++, eventsCount, waitDialog);
				if (copyCancel)
				{
					break;
				}

				long sampleStamp = event.getSampleStamp();

				boolean doAppend = cutSelectionListInEventSampleStamp.inList(sampleStamp);
				long offset = 0;

				if (isCut)
				{
					doAppend = !doAppend;
					offset = cutSelectionListInEventSampleStamp.getAreaUntilPoint(sampleStamp);
				}
				else
				{
					offset = cutSelectionListInEventSampleStamp.nextSelection(0).getSelectionStart();
				}

				if (doAppend)
				{
					event.setSampleStamp(event.getSampleStamp() - offset);
					eventsToAppend.add(event);
				}
			}
			if (eventsToAppend.size() != 0)
			{
				copyEventEntry.append(eventsToAppend);
			}
			else
			{
				File tmpFile = new File(copyUnisens.getPath() + File.separator + copyEventEntry.getId());
				tmpFile.createNewFile();
			}

		} // end try

		catch (DuplicateIdException ex)
		{
			System.err.println("copyEventEntry:" + ex.getMessage());
			return false;
		}

		catch (IOException ex)
		{
			return false;
		}
		return result;
	}
}
