/**
 * This static class manages STATIC constants and resources that don't have to be initialized. 
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.data;

public class Constants
{

	/** Program name */
	public static final String Programname = "EKG-Editor";

	/** current version of this program */
	public static final String Programversion = "Version %VERSION%";

	/**
	 * Test of equality with floating-point variables is always not exact. This
	 * constants defines which minimal difference between two double values are
	 * considered as equal.
	 */
	public static final Double Epsiolon = 0.0001;

	/** Some common message string */
	public static final String yes = "JA";

	/** Some common message string */
	public static final String no = "Nein";

	/** Some common message string */
	public static final String error = "Fehler";

	/** Some common message string */
	public static final String warning = "Achtung";

	/** Some common message string */
	public static final String question = "Frage";

	/** Some common message string */
	public static final String notImplemented = "Diese Funktion ist in der derzeitigen Version noch nicht fertiggestellt.";

	/** Some common message string used many times */
	public static final String undefined = "undefiniert";

	/** Title of the about-box */
	public static final String aboutTitle = "Über " + Programname;
	
	/** License information */
	public static final String licenseInfo = "%LICENSE%"; 

	/** Message string of the about box */
	public static final String aboutInfo = Constants.Programname + "\n"
			+ Constants.Programversion + "\tbuild %BUILD% \n\n"
			+ licenseInfo.trim() + (licenseInfo.trim().length() > 0 ? "\n\n" : "")
			+ "(c) 2007-2012 \tFZI Forschungszentrum Informatik \n" 
			+ "\t\tEmbedded Systems and Sensors Engineering\n"
			+ "\t\thttp://www.fzi.de/ess\n"
			+ "\t\tmit@fzi.de\n\n"
			+ "Autoren:" 
			+ "\t\tMalte Kirst\n"
			+ "\t\tCarsten Glose\n"
			+ "\t\tRadoslav Nedkov\n";

	/** Message string of the aboutUnisens box */
	public static final String unisensTitle = "Über Unisens";
	public static final String unisensInfo = 
			  "UNISENS \n" 
			+ "Unisens ist ein universelles Datenformat für\n"
			+ "Multi-Sensor-Daten. Mehr Informationen über \n"
			+ "Unisens erfahren Sie auf der Unisens-Homepage:\n"
			+ "\n"
			+ "http://www.unisens.org\n";

	/** message to print of in case of some real severe error. */
	public static final String internError = "Interner Fehler. Bitte wenden Sie sich an den Hersteller.";

	/** listbox_medicalClasses uses this (also for comparison) */
	public static final String pleaseSelect = "- Signalklasse auswählen -";
	
	/** medical classes comment */
	public static final String comment = "Kommentar";

	/** String used, if no file is currently loaded */
	public static final String noFile = "";

	// Filenames:

	/** Configuration Filename for the list of medical classes */
	public static final String file_MedicalClassesConfig = "MedicalClasses.xml";

	/** icon/image path */
	public static final String iconPath = "icons/";

	/** Standard unisens filename to check whether a file already exists */
	public static final String StdUnisensFileName = "unisens.xml";

	// External programs
	/** Standard external program */
	public static final String StdexternalProgram = "testit.bat";

	// Mouse
	/** STD. SWT Mouse Event Types */
	public static final int mouse_left = 1;

	/** STD. SWT Mouse Event Types */
	public static final int mouse_middle = 2;

	/** STD. SWT Mouse Event Types */
	public static final int mouse_right = 3;

	// Selection
	/**
	 * The minimum samples that have to be selected before Selection.isSelected
	 * returns true
	 */
	public static final long minimum_selected = 2;

	/**
	 * maximum distance until you can change the selection (make wider or
	 * smaller)
	 */
	public static final long minimum_range = 20;
	
	/** Some constant to indicate that there is no selection */
	public static final long notSelected = Long.MIN_VALUE;

	/** constant indicating that a point is not in range */
	public static final long notInRange = Long.MAX_VALUE;

	/** The maximum percentage of intersection between two testdata-sets */
	public static final double maxIntersectionOfTestDataPercentage = 50;

	// Tooltip
	public static final long minimum_range_tooltip = 7;

	// conditional compiling flags:
	/** this version is or is not a debug version */
	public static final boolean isDebug = false;

	public static final boolean showCacheAccess = true & isDebug;

	public static final boolean showThreads = true & isDebug;

	public static final boolean activateThreads = true;

	/** should we show the intro (licence and others ? */
	public static final boolean showIntro = true;

	/** flag to enable/disable preview feature */
	public static final boolean showPreview = true;

	public static final boolean showSaveProgressWindow = true;

	/** flag to enable/disable cut/paste features */
	public static final boolean cutPaste = false;

	public static final boolean previewLoadsData = Constants.LOAD;

	public static final boolean useAutomaticCut = true;

	public static final boolean printHeartFrequence = false & isDebug;

	public static final boolean printAutomaticCutInformation = true & isDebug;
	
	public static final boolean printMedicalClassesLoad = true & isDebug;

	/** standard lsb-value if there no lsb-value was set in the unisens-file */
	public static final double StandardLsbValue = 1;

	public static final int preSignalTime = -10000;

	public static final int StdRound = 1000;

	// Export-Statements
	public static final String exportedFrom = "Exported testdata from ";

	/** Automatic Cut tweaks */
	public static final long searchTimeInMilliSecsForAutomaticCut = 200;

	public static final int searchMinimumSamples = 10;

	public static final long searchTimeInMilliSecsForAutomaticExport = 20000;

	public static final int searchMinimumSamplesForAutomaticExport = 100;

	// Unisens-Adapter
	/** Standard sample block to read */
	public static final int stdSampleBlock = 65536;

	public static final boolean NO_NOTIFICATION = false;

	public static final boolean NOTIFICATION = true;

	public static final boolean LOAD = true;

	public static final boolean NO_LOAD = false;
	
	public static final boolean NEWSIGNAL = true;
	
	public static final boolean OLDSIGNAL = false;

	public static final boolean ArgumentNotSet = false;

	public static final boolean NoMilliSecs = false;

	public static final boolean withMilliSecs = true;

	public static final double outOfRangeValue = 0;

	// return values
	public static final int error_wrongOsVersion = 1;

	public static final long paintProgressTime = 300;

	public static final boolean NoSampleSelect = false;

	public static final boolean SampleSelect = true;

	public static final double pageIncrementFactor = 0.7;
}
