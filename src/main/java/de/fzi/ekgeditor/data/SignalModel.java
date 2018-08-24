package de.fzi.ekgeditor.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.apache.commons.io.FileUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.unisens.Group;
import org.unisens.SignalEntry;
import org.unisens.Unisens;
import org.unisens.UnisensFactory;
import org.unisens.UnisensFactoryBuilder;
import org.unisens.UnisensParseException;

import de.fzi.ekgeditor.Common;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.model.SignalViewerModel;
import de.fzi.ekgeditor.data.unisensAdapter.UnisensAdapter;
import de.fzi.ekgeditor.data.unisensAdapter.UnisensDataBuffer;
import de.fzi.ekgeditor.data.unisensAdapter.UnisensLegacy;
import de.fzi.ekgeditor.events.CacheListener;
import de.fzi.ekgeditor.events.SignalEvent;
import de.fzi.ekgeditor.events.SignalListener;
import de.fzi.ekgeditor.utils.Selection;
import de.fzi.ekgeditor.utils.SelectionList;
import de.fzi.ekgeditor.utils.TestData;

public class SignalModel {

	/** The signal */
	;
	private SignalEntry signal = null;
	private Unisens UnisensFile = null;
	private ArrayList<ChannelData> arrayChannelData = new ArrayList<ChannelData>();

	private UnisensDataBuffer signalBuffer = null;
	private boolean signalLoaded = false;
	private int numberOfChannels = 0;
	private double samplingFrequency = 200;
	private long samples = 0;
	private long msLength = 0;
	private SelectionList removedSelection = new SelectionList();
	private SelectionList testdataSelection = new SelectionList();

	private EventListenerList signalListeners = new EventListenerList();

	private List<SignalViewerModel> signalViewerModel = new ArrayList<SignalViewerModel>();

	public void addSignalListener(SignalListener listener) {
		signalListeners.add(SignalListener.class, listener);
	}

	public void removeSignalListener(SignalListener listener) {
		signalListeners.remove(SignalListener.class, listener);
	}

	protected synchronized void notifySignalChanged(SignalEvent e) {
		for (SignalListener l : signalListeners
				.getListeners(SignalListener.class))
			l.signalChanged(e);
	}

	private EventListenerList cacheListeners = new EventListenerList();

	public void addCacheListener(CacheListener listener) {
		cacheListeners.add(CacheListener.class, listener);
		if (signalBuffer != null) {
			signalBuffer.addCacheListener(listener);
		}
	}

	public void removeCacheListener(CacheListener listener) {
		cacheListeners.remove(CacheListener.class, listener);
		if (signalBuffer != null) {
			signalBuffer.removeCacheListener(listener);
		}
	}

	public void addSignalViewerModel(SignalViewerModel a) {
		this.signalViewerModel.add(a);
	}

	public void removeSignalViewerModel(SignalViewerModel a) {
		this.signalViewerModel.remove(a);
	}

	public long getMaxSamp() {
		if (signal != null) {
			return signal.getCount();
		} else {
			return 0;
		}
	}

	public boolean isSignalLoaded() {
		return signalLoaded;
	}

	private ArrayList<ChannelData> generateChannelData(SignalEntry s) {
		ArrayList<ChannelData> l = new ArrayList<ChannelData>();
		String[] names = s.getChannelNames();

		for (int i = 0; i < s.getChannelCount(); i++) {
			l.add(new ChannelData(names[i], i));
		}

		return l;
	}

	public ArrayList<ChannelData> getChannelList() {
		return arrayChannelData;
	}

	public String getChannelName(int channelNumber) {
		if ((channelNumber >= 0) & (channelNumber < numberOfChannels)) {
			return arrayChannelData.get(channelNumber).getName();
		} else {
			return null;
		}
	}

	public int getChannelIndex(String channelName) {
		for (int i = 0; i < arrayChannelData.size(); i++) {
			ChannelData channelData = arrayChannelData.get(i);
			if (channelData.getName().equalsIgnoreCase(channelName))
				return i;
		}
		return -1;
	}

	public Double getSample(long sampleNumber, int channel) {
		if (signalBuffer != null) {
			if ((sampleNumber >= 0) & (sampleNumber < signal.getCount())) {
				return signalBuffer.read(sampleNumber, channel);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public String getUnit() {
		return signal.getUnit();
	}

	// Data Create and Set
	public void setSignal(SignalEntry signal, Unisens unisens, Group group) {
		this.UnisensFile = unisens;
		if (signalBuffer != null) {
			try {
				signalBuffer.finalize();
			} catch (Throwable t) {

			}
		}
		this.signalBuffer = new UnisensDataBuffer(signal);
		this.msLength = (long) (((double) signal.getCount() / (double) signal
				.getSampleRate()) * 1000);
		this.samplingFrequency = signal.getSampleRate();
		this.samples = signal.getCount();
		if (samples > Integer.MAX_VALUE) {
			Common.getInstance()
					.ShowMessageBox(
							Constants.warning,
							"Dies ist ein sehr grosser Datensatz (mehr als maxint samples)!\n"
									+ "Dieses Programm ist nicht darauf ausgerichtet, so grosse Datensätze zu bearbeiten.\n"
									+ "Vermutlich wird es daher zu Problemen kommen.",
							SWT.ICON_WARNING);
		}
		this.setNumberOfChannels(signal.getChannelCount(),
				Constants.NO_NOTIFICATION);

		if (this.arrayChannelData != null) {
			arrayChannelData.clear();
		}
		arrayChannelData = this.generateChannelData(signal);
		this.signal = signal;

		this.removedSelection.RemoveAll();
		this.testdataSelection.RemoveAll();
		SelectionList t = UnisensAdapter.readTestDataInformation(unisens);
		if (t != null) {
			this.testdataSelection = t;
		}

		this.signalLoaded = true;

		// before we notify all the strange listener we should update data
		// structure
		SignalEvent sigEvent = new SignalEvent(this, Constants.NEWSIGNAL);

		for (SignalViewerModel sModel : this.signalViewerModel) {
			sModel.signalChanged(sigEvent);
		}

		this.notifySignalChanged(sigEvent);
		synchronized (cacheListeners) {
			for (CacheListener l : cacheListeners
					.getListeners(CacheListener.class))
				signalBuffer.addCacheListener(l);
		}
	}

	/**
	 * Saves the selected data as a test data set.
	 * 
	 * @param fileName
	 *            Path and filename of the parent data set.
	 * @param comment
	 *            Optional comment for test data set.
	 * @param selection
	 *            Selection in parent data set.
	 * @return True when successful.
	 */
	public boolean SaveTestData(String fileName, String comment,
			Selection selection, MedicalClass medicalClass) {
		if (selection != null) {
			// UnisensAdapter.copyUnisens(this.UnisensFile, FileName, s);
			SelectionList sList = new SelectionList(selection);
			UnisensLegacy.copyUnisens(this.UnisensFile, fileName, comment,
					sList, Common.getInstance().mainForm.mainWindow, false);

			// Save some additional information in the new exported data set:
			UnisensFactory unisensFactory = UnisensFactoryBuilder
					.createFactory();
			Unisens unisens;
			try {
				unisens = unisensFactory.createUnisens(fileName);

				// Some custom attributes.
				unisens.addCustomAttribute("parentDatasetPath",
						this.UnisensFile.getPath());
				unisens.addCustomAttribute("parentDatasetOffsetInMs",
						selection.getSelectionStart() + "");
				unisens.addCustomAttribute("testdataCreated",
						(new Date()).toString());

				// Change the timestampStart.
				long newTimestampStartMs = this.UnisensFile.getTimestampStart()
						.getTime() + selection.getSelectionStart();
				Date newTimestampStart = new Date(newTimestampStartMs);
				unisens.setTimestampStart(newTimestampStart);

				// Set the duration of the exported dataset.
				unisens.setDuration((double) selection.getLength());

				// Save and close the updated exported dataset
				unisens.save();
				unisens.closeAll();
				
				// Save medical classes
				TestData.saveMedicalClasses(fileName, medicalClass);
			} catch (UnisensParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (this.testdataSelection != null) {
				UnisensAdapter.saveTestDataInformation(this.UnisensFile,
						this.testdataSelection);
			}

			return true;
		}

		return false;
	}

	public void LoadTestData(Unisens unisensFile) {
		SelectionList t = UnisensAdapter.readTestDataInformation(unisensFile);

		if (t != null) {
			this.testdataSelection = t;
		} else {
			this.testdataSelection = new SelectionList();
		}
	}

	public void Save(String FileName, Shell parent) {
		UnisensLegacy.copyUnisens(this.UnisensFile, FileName, null,
				this.removedSelection, parent, true);

	}

	public int getNumberOfChannels() {
		return numberOfChannels;
	}

	private void setNumberOfChannels(int numberOfChannels, boolean notification) {
		this.numberOfChannels = numberOfChannels;
		if (notification) {
			this.notifySignalChanged(new SignalEvent(this, Constants.OLDSIGNAL));
		}
	}

	public double getSamplingFrequency() {
		return samplingFrequency;
	}

	public Double getSlope(long sampleNumber, int channelNumber) {
		Double valueA = getSample(sampleNumber, channelNumber);
		Double valueB = getSample(sampleNumber + 1, channelNumber);
		if ((valueA != null) & (valueB != null)) {
			return getSlope(valueA, valueB);
		} else {
			return null;
		}
	}

	public double getSlope(double a, double b) {
		return (b - a);
	}

	public void setSamplingFrequency(double samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	public long getSamples() {
		return samples;
	}

	public long getMsLength() {
		return msLength;
	}

	public long getSampleForTimeInMilliSeconds(long t) {
		return Math.round(Math.floor((double) t / (double) 1000
				* getSamplingFrequency()));
	}

	public long getTimeInMilliSecsForSample(long s) {
		return Math.round((double) s / getSamplingFrequency() * 1000);
	}

	private void printPointInfo(String text, long timeA, int channel) {
		long sampleA = this.getSampleForTimeInMilliSeconds(timeA);
		Double slopeA = this.getSlope(sampleA, channel);
		Double valueA = this.getSample(sampleA, channel);

		System.out.println(text + " Aktuelle Cursor-Position:" + timeA
				+ " sample:" + sampleA + " Steigung:" + slopeA + " Wert:"
				+ valueA);
	}

	public double bewertungCut(double slopeA, double slopeB, double newSlope,
			int i, int j) {
		double slopeDiff = Math.abs(slopeA - newSlope)
				+ Math.abs(slopeB - newSlope);
		return slopeDiff;
	}

	public long getSamplesForTimeInMilliSec(long timeInMilliSecs) {
		return Math.round((this.samplingFrequency / (double) 1000)
				* (double) timeInMilliSecs);
	}

	public Selection automaticCut(Selection orig, int channel) {
		Selection res = (Selection) orig.clone();

		long timeA = res.getSelectionStart();
		long timeB = res.getSelectionEnd();

		if (Constants.printAutomaticCutInformation) {
			printPointInfo("Ursprung A:", timeA, channel);
			printPointInfo("Ursprung B:", timeB, channel);
		}

		long sampleA = this.getSampleForTimeInMilliSeconds(timeA);
		long sampleB = this.getSampleForTimeInMilliSeconds(timeB);

		long bestSampleA = sampleA;
		long bestSampleB = sampleB;

		Double valueA = this.getSample(sampleA, channel);
		Double valueB = this.getSample(sampleB, channel);
		Double slopeA = this.getSlope(sampleA, channel);
		Double slopeB = this.getSlope(sampleB, channel);

		double dummy = 0;
		double directSlope = Double.POSITIVE_INFINITY;
		if ((valueA != null) & (valueB != null)) {
			directSlope = this.getSlope(valueA, valueB);
		}

		double diff = Double.POSITIVE_INFINITY;
		if ((slopeA != null) & (slopeB != null)) {
			diff = bewertungCut(slopeA, slopeB, directSlope, 0, 0);
		}

		long searchSpaceT = getSamplesForTimeInMilliSec(Constants.searchTimeInMilliSecsForAutomaticCut);
		if (searchSpaceT < Constants.searchMinimumSamples) {
			searchSpaceT = Constants.searchMinimumSamples;
		}

		final int searchSpace = (int) searchSpaceT;
		if (Constants.printAutomaticCutInformation) {
			System.out.println("Automatisches Schneiden, Suchraum:"
					+ searchSpace + " Samples.");
		}

		for (int i = -searchSpace; i < searchSpace; i++) {

			Double slopeAT = this.getSlope(sampleA + i, channel);
			Double valueAT = this.getSample(sampleA + i, channel);

			if ((slopeAT != null) & (valueAT != null)) {
				for (int j = -searchSpace; j < searchSpace; j++) {
					Double slopeBT = this.getSlope(sampleB + j, channel);

					if (slopeBT != null) {
						Double valueBT = this.getSample(sampleB + j, channel);
						double newSlope = this.getSlope(valueAT, valueBT);

						double bw = bewertungCut(slopeAT, slopeBT, newSlope, i,
								j);
						if (bw < diff) {
							diff = bw;
							bestSampleA = sampleA + i;
							bestSampleB = sampleB + j;
							dummy = newSlope;
						}
					}
				}
			}
		}
		res.setSelectionStart(this.getTimeInMilliSecsForSample(bestSampleA));
		res.setSelectionEnd(this.getTimeInMilliSecsForSample(bestSampleB));

		if (Constants.printAutomaticCutInformation) {
			printPointInfo("Optimiert (A):", res.getSelectionStart(), channel);
			printPointInfo("Optimiert (B):", res.getSelectionEnd(), channel);
		}

		valueA = this.getSample(bestSampleA, channel);
		valueB = this.getSample(bestSampleB, channel);

		if (Constants.printAutomaticCutInformation) {
			System.out.println("Kanal:" + channel);
			System.out.println("Sample A:" + bestSampleA + " Sample B:"
					+ bestSampleB);
			System.out.println("Neue Steigung:" + this.getSlope(valueA, valueB)
					+ " " + dummy);
			System.out.println("Bewertung:" + diff);
		}

		return res;
	}

	public Selection automaticTestDataExportHelper(Selection orig,
			int minimalLength) {
		Selection res = (Selection) orig.clone();
		boolean found = false;

		int searchSpaceT = (int) Math
				.round(this
						.getSamplesForTimeInMilliSec(Constants.searchTimeInMilliSecsForAutomaticExport));
		if (searchSpaceT < Constants.searchMinimumSamplesForAutomaticExport) {
			searchSpaceT = Constants.searchMinimumSamplesForAutomaticExport;
		}
		final int searchSpace = searchSpaceT;

		if (Constants.printAutomaticCutInformation) {
			System.out.println("Automatisches Testdatenexport, Suchraum:"
					+ searchSpace + " Samples.");
		}

		for (int i = 1; i < searchSpace; i++) {
			res.setSelection(orig.getSelectionStart() + i,
					orig.getSelectionStart() + i + minimalLength);
			if (!(testdataSelection.inList(res,
					Constants.maxIntersectionOfTestDataPercentage))) {
				if (!(this.removedSelection.inList(res))) {
					found = true;
					break;
				}
			}
		}

		if (Constants.printAutomaticCutInformation) {
			System.out
					.println("automaticTestDataExportHelper: Anpassung gefunden:"
							+ found);
		}
		if (found) {
			return res;
		} else {
			return null;
		}

	}

	public void finalize() throws Throwable {
		try {
			if (signalBuffer != null) {
				signalBuffer.finalize();
				signalBuffer = null;
			}
			if (Constants.isDebug) {
				System.out.println("SignalModel finalisiert.");
			}
		} catch (Exception e) {
			super.finalize();
		}
	}

	public double getCacheFill() {
		return this.signalBuffer.getFillStatus();
	}

	public void AddTestdataSelection(Selection currentSelection,
			MedicalClass m, String FileName) {
		this.testdataSelection.Add((Selection) currentSelection.clone(), m,
				FileName);
		this.notifySignalChanged(new SignalEvent(this, Constants.OLDSIGNAL));
	}

	public boolean DeleteTestData(String fileName) {
		boolean success = true;
		// delete from SelectionList
		if (!this.testdataSelection.Remove(fileName)) {
			success = false;
		}

		// save SelectionList
		success = UnisensAdapter.saveTestDataInformation(this.UnisensFile,
				this.testdataSelection);

		// delete from filesystem
		boolean deletedAll = true;
		File dirToDelete = new File(fileName);
		if(dirToDelete != null && dirToDelete.exists())
		{
			try {
				for (File file : dirToDelete.listFiles()) {
					try {
						if (!file.delete()) {
							deletedAll = false;
						}
					} catch (SecurityException ex) {
						deletedAll = false;
					}
				}
	
				if (deletedAll) {
					dirToDelete.delete();
				}
			} catch (SecurityException ex) {
				success = false;
			}
		}

		return success;
	}

	public void reclassifyTestData(String fileName, MedicalClass newClass) {
		// move data
		String subFolderPath = fileName
				.substring(0, fileName.lastIndexOf("\\"));
		String destPath = subFolderPath
				+ "\\"
				+ TestData.getNextTestdataDirectory(subFolderPath,
						newClass.abbrev);
		this.moveTestData(fileName, destPath);
		
		// save new medical class in TestdataList
		this.testdataSelection.updateMedicalClass(destPath, newClass);
		UnisensAdapter.saveTestDataInformation(this.UnisensFile,
				this.testdataSelection);
		
		//change new medical class in unisens.xml
		try {
			TestData.saveMedicalClasses(destPath, newClass);
		} catch (UnisensParseException e) 
		{
			System.err.println("Unable to save new medical class to unisens.xml in '" + destPath + "'");
		} catch (IOException e) {
			System.err.println("Unable to save new medical class to unisens.xml in '" + destPath + "'");
		}
	}

	public boolean moveTestData(String fromFileName, String toFileName) {
		boolean success = true;
		// alter SelectionList
		this.testdataSelection.updateFileName(fromFileName, toFileName);

		// save SelectioList
		success = UnisensAdapter.saveTestDataInformation(this.UnisensFile,
				this.testdataSelection);

		// move in filesystem
		File dirToMove = new File(fromFileName);
		File dirDest = new File(toFileName);
		try {
			FileUtils.moveDirectory(dirToMove, dirDest);
		} catch (IOException ex) {
			System.err.println("Unable to move '" + dirToMove + "' to '"
					+ dirDest + "': " + ex.getMessage() + ".");
			success = false;
		}

		return success;
	}

	public SelectionList getTestdataSelection() {
		return this.testdataSelection;
	}

	public SelectionList getRemovedSelection() {
		return removedSelection;
	}

	public void addRemovedSelection(Selection removedSelection) {
		this.removedSelection.Add(removedSelection);
		this.notifySignalChanged(new SignalEvent(this, Constants.OLDSIGNAL));
	}

	public SignalEntry getSignal() {
		return signal;
	}

	public UnisensDataBuffer getSignalBuffer() {
		return signalBuffer;
	}
}
