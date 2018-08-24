/**
 * This class manages standard signalViewerColors
 *
 * @author glose
 * @version 0.2
 */



package de.fzi.ekgeditor.Gui.Widgets.SignalViewer;

//TODO NOCH NICHT FERTIG DOKUMENTIERT !!!!

import org.eclipse.swt.graphics.Color;

import de.fzi.ekgeditor.data.Colors;

public class SignalViewerColors {

	/** color used for the trigger Background */
	public static final int colorTriggerBackground	= 0;
	/** color used for the trigger Foreground of 1st trigger list*/
	public static final int colorTriggerForeground	= 1;
	/** color to paint the ZeroLine */
	public static final int colorZeroLine			= 2;
	/** color to paint the legend */
	public static final int colorLegend				= 3;
	/** color to paint the dash-line */
	public static final int colorDashLine			= 4;
	/** color to paint a selection */
	public static final int colorSelectionLine		= 5;
	/** color to paint the beginning of one selection */
	public static final int colorSelectionStart		= 6;
	/** color to paint the cursor */
	public static final int colorCursor				= 7;
	/** color for error messages */
	public static final int colorError				= 8;
	/** color to paint the signal background */
	public static final int colorBackground			= 9;
	/** color to paint one deleted section */
	public static final int colorDeletedSection		=10;
	/** color to paint one exported section */
	public static final int colorExportedSection	=11;
	
	public static final int colorPreviewLine		=12;
	
	/** color used for the grid Foreground */
	public static final int colorGridForeground1	=13;
	public static final int colorGridForeground2	=14;
	public static final int colorGridForeground3	=15;
	
	/** color used for the trigger Foreground of 2nd trigger list */
	public static final int colorSecondaryTriggerForeground	=16;
	
	/** our private color table with standard values initialized */
	private Color[] SignalViewerColorTable =
	{
			Colors.Black,
			Colors.DarkBlue,
			Colors.Black,
			Colors.Gray,
			Colors.Black,
			Colors.DarkBlue,
			Colors.Magenta,
			Colors.Yellow,
			Colors.Red,
			Colors.White,
			Colors.Red,
			Colors.DarkBlue,
			Colors.Blue,
			Colors.Gray,
			Colors.Gray,
			Colors.Gray,
			Colors.Gray
	};
	
	/** our human-readable names for the colors */
	public static final String[] SignalViewerColorNames =
	{
			"Triggerhintergrund",
			"1. Triggervordergrund",
			"Nulllinie",
			"Einheitssignal",
			"Signaltrennlinie",
			"Markierung",
			"Markierungsbeginn",
			"Cursor",
			"Fehler",
			"Hintergrund",
			"Entferntes Teilstück",
			"Exportiertes Teilstück",
			"10Sek-Linie Preview",
			"Millimeterpapier 1mm",
			"Millimeterpapier 5mm",
			"Millimeterpapier 10mm",
			"2. Triggervordergrund"
	};

	/** our color table for all the channels */
	private Color[] colorChannel		= { Colors.Blue, Colors.Red, Colors.Green, Colors.Cyan, Colors.Magenta, Colors.Yellow, Colors.DarkBlue, Colors.DarkRed };
	/** and how many channels we have */
	public static final int maxChannelColors 		= 8;
	
	/** returns the count of system colors 
	 * 
	 * @return count of system colors
	 */
	public int getSignalViewerColorCount()
	{
		return SignalViewerColorTable.length;
	}
	
    public Color getChannelColor(int ChannelNumber)
    {
    	ChannelNumber=ChannelNumber % maxChannelColors;
    	return colorChannel[ChannelNumber];
    }
    public Color getGridColor(int gridWidth)
    {
    	switch (gridWidth)
    	{
    		case 1:
    			return getSignalViewerColor(colorGridForeground2);
    		case 2:
    			return getSignalViewerColor(colorGridForeground3);
    		default:
    			return getSignalViewerColor(colorGridForeground1);
    	}
    }
    public void setChannelColor(int ChannelNumber,Color c)
    {
    	if (c!=null)
    	{
    		colorChannel[ChannelNumber]=null;
    		colorChannel[ChannelNumber]=c;
    	}
    }
    
    public Color getSignalViewerColor(int colortyp)
    {
    	Color result=null;
    	
    	if ((colortyp<SignalViewerColorTable.length) & (colortyp>=0))
    	{
    		result=SignalViewerColorTable[colortyp];
    	}
    	return result;
    }
    
    public void setSignalViewerColor(int colortyp,Color c) 
    {
    	if ((colortyp<SignalViewerColorTable.length) & (colortyp>=0))
    	{
    		SignalViewerColorTable[colortyp]=null;
    		SignalViewerColorTable[colortyp]=c;
    	}
    	else
    	{
    		System.err.println("Fehler! setSignalViewerColor, Farbe nicht gefunden.");
    	}
    }
}
