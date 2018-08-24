package de.fzi.ekgeditor.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

public class FontsManager {

	public static final Font Font_Std_SignalNotLoaded = new Font(null,"Times New Roman",20,SWT.BOLD);
	public static final Font Font_Std_ChannelName = new Font(null , "Arial",8,SWT.BOLD);
	public static final Font Font_Std_Legend = new Font(null , "Arial",12,SWT.NONE);
	public static final Font Font_Std_Trigger= new Font(null , "Arial" ,6 ,SWT.NONE);
	
	public static final int fontSignalNotLoaded			= 0;
	public static final int fontChannelName				= 1;
	public static final int fontLegend					= 2;
	public static final int fontTrigger					= 3;
	
	private Font[] FontsTable = 
	{
			Font_Std_SignalNotLoaded,
			Font_Std_ChannelName,
			Font_Std_Legend,
			Font_Std_Trigger
	};
	
	public static final String[] FontNames =
	{
			"Fehlermeldung (Signal)",
			"Kanalname",
			"Legende",
			"Trigger"
	};
	
	public static final int maxFonts=4;

	public Font getSystemFont(int FontNumber)
	{
		FontNumber=FontNumber % maxFonts;
		return FontsTable[FontNumber];
	}
	public void setSystemFont(int FontNumber,Font f)
	{
		if (f!=null)
		{
			FontNumber=FontNumber % maxFonts;
			FontsTable[FontNumber].dispose();
			FontsTable[FontNumber]=null;
			FontsTable[FontNumber]=f;
		}
	}
}
