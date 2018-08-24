/**
 * This class provides some util methods for layouting.
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.Gui;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

public class Layouts {
	
	/** Provides some specific gridLayout
	 * 
	 * @return my special super duper gridLayout
	 */
	public static GridLayout GetmyLayout()
	{
		GridLayout myLayout = new GridLayout();
		myLayout.verticalSpacing = 20;
		myLayout.marginHeight = 5;
		myLayout.marginWidth = 5;
		myLayout.numColumns = 4;
		myLayout.makeColumnsEqualWidth = true;
		
		return myLayout;
	}
	
	 /** Provides some specific LayoutData where one Row is completly filled 
	 * 
	 * @return my layoutdata for my special super duper gridLayout
	 */
	public static GridData GetLayoutFillOneRow(int fillStyle)
	{
		  GridData g = new GridData(fillStyle);
		  g.horizontalSpan = 4;
		  
		  return g;
	}
}
