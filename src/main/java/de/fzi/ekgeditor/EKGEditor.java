/**
 * This class is the main class of project EKGEditor
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.fzi.ekgeditor.data.Constants;

public class EKGEditor {	
	
	/** This constructor does a general bootstrapping
	 * 
	 */
	
	private static final String firstline = Constants.Programname + " comes with ABSOLUTELY NO WARRANTY.";
	
	public EKGEditor()
	{		
		if (Constants.showIntro)
		{
//			System.out.println(Constants.Programname + " " + Constants.Programversion + "\n" + 
//					"Copyright (C) 2007-2010, FZI Forschungszentrum Informatik" + "\n" + 
//					firstline);
			
			System.out.println(Constants.aboutInfo);
		}
		
		System.out.println();
		String osname=System.getProperty("os.name");
		System.out.println("Betriebssystem: " + osname);
		System.out.println("Architektur: " + System.getProperty("os.arch"));
		System.out.println("Betriebssystemversion: " + System.getProperty("os.version"));

		if (!(osname.toUpperCase().startsWith("WINDOWS")))
		{
			System.err.println("Dieses Programm benötigt eine installierte Version von Microsoft Windows!");
			System.exit(Constants.error_wrongOsVersion);
		}
		
		Runtime r = Runtime.getRuntime();
		
		System.out.println("Speicher: total "+r.totalMemory()+" Byte, frei:"+r.freeMemory()+" Byte, maximal:"+r.maxMemory()+" Byte.");
		
		int result = SWT.YES;
		
		
		if (r.maxMemory() < 256*1000*1000)
		{
			String error="Es ist zu wenig Speicher vorhanden, um das Programm auszuführen. Es werden mindestens 512 MB benötigt.\n";
			error = error + "Weisen Sie daher der virtuellen Maschine mit dem Parameter -Xmx512m mehr Speicher zu.\n";
			
			System.err.println(error);
			
			MessageBox m = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
			m.setMessage(error);
			m.setText(Constants.Programname);
			result = m.open();
		}
			
		// Invalid Thread access cause of new Shell(), so there is no questian message here.
		if (result == SWT.YES)
		{
			// init all common ressources
			Common.start();

			// Init main window
			Common.getInstance().mainForm.Init();

			// Start main-window and call message loop
			Common.getInstance().mainForm.Run();

			try
			{
				Common.getInstance().finalize();
			}
			catch(Throwable ex){}
		}
		System.out.println();
		System.out.println("Bis zum nächsten Mal!");
	}
	
	/** This main method just creates a new instance of EKGEditor.
	 * 
	 */
	public static void main(String[] args) {
		new EKGEditor();
	} // end main
} // end class EKGEditor
