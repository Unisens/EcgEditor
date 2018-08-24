/**
 * This static class returns some general system colors;
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class Colors {

	public static Color Black 		= Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	public static Color White 		= Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	public static Color Gray  		= Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
	public static Color Blue  		= Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
	public static Color Red   		= Display.getDefault().getSystemColor(SWT.COLOR_RED);
	public static Color Green 		= Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
	public static Color Yellow 		= Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
	public static Color Cyan  	    = Display.getDefault().getSystemColor(SWT.COLOR_CYAN);
	public static Color DarkBlue   = Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE);
	public static Color DarkGreen  = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
	public static Color Magenta    = Display.getDefault().getSystemColor(SWT.COLOR_MAGENTA);
	public static Color DarkRed    = Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
	public static Color DarkYellow = Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW);
	public static Color DarkGray = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
	
	
	public  static List<Color> artefactColors = new ArrayList<Color>();
	static{ 
		artefactColors.add(Colors.Red);
		artefactColors.add(Colors.Green);
		artefactColors.add(Colors.Cyan);
		artefactColors.add(Colors.Magenta);
		artefactColors.add(Colors.DarkRed);
		artefactColors.add(Colors.DarkGreen);
		artefactColors.add(Colors.DarkBlue);
		artefactColors.add(Colors.DarkYellow);
		artefactColors.add(Colors.Yellow);
		artefactColors.add(Colors.Gray);
	};

}
