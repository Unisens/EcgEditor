/**
 * This class manages all common resources for the project.
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.unisens.Unisens;

import de.fzi.ekgeditor.Gui.Forms.Form_Main;
import de.fzi.ekgeditor.Gui.Menu.MenuCommon;
import de.fzi.ekgeditor.Gui.Widgets.SignalViewer.SignalViewerColors;
import de.fzi.ekgeditor.algorithm.AlgorithmsConfig;
import de.fzi.ekgeditor.data.ArtefactModel;
import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.EventModel;
import de.fzi.ekgeditor.data.FontsManager;
import de.fzi.ekgeditor.data.ImageManager;
import de.fzi.ekgeditor.data.Registry;
import de.fzi.ekgeditor.data.SignalModel;
import de.fzi.ekgeditor.data.TriggerDataVisible;
import de.fzi.ekgeditor.data.TriggerModel;
import de.fzi.ekgeditor.data.TriggerTypes;
import de.fzi.ekgeditor.events.SettingsEvent;
import de.fzi.ekgeditor.events.SettingsListener;

public class Common {

	/** The Screen itself. */
	public Display display;
	
	/** Access to that object, which manages the main window. */
	public Form_Main mainForm;
	
	// Some common ressources
	
	/** All common images for the use in the program (ex. menu-icons, toolbar-icosn) are managed in class ImageManager */
	public ImageManager im;
	
	/** Menumethods that are called by different sources (mainmenu, contextmenu, toolbar are managed here.
	 */
	public MenuCommon mc = null;
	
	/** registry for the program */
	public Registry reg=new Registry();
	
	public TriggerTypes triggerClasses = new TriggerTypes();
	
	public AlgorithmsConfig algorithmsConfig = new AlgorithmsConfig();
	
	/** Private entry that manages all the listeners who want to be informed if some settings have been changed. */
	private EventListenerList settingsListeners = new EventListenerList(); 
	
	/** Pointer to Color Manager */
	public SignalViewerColors signalViewerColors=null;
	
	/** Pointer to Fonts Manager */
	public FontsManager fontsManager=null;
	
	/** The current file name */
	public String currentFile=Constants.noFile;
		
	/** instancevariable that implements singleton desgin pattern. */
	private static Common instance=null;
	
	public static int signalRound=Constants.StdRound;
	
	public SignalModel signalModel=null;
	
	public Unisens unisens;
	
	public TriggerModel triggerModel;
	
	public ArtefactModel artefactModel;
	public EventModel eventModel;
	
	// Singleton
	/** Private constructor that sets the instance-variable for implementing singleton-desgin-pattern.
	 * 
	 * 
	 * @author glose
	 * @version 0.2
	 */
	private Common()
	{
		display 		= new Display ();
		// Just create main_form class to get a shell, main_form has to be initizialsed later!
		mainForm		= new Form_Main(display);
		
		// init config-registry
		
		im = new ImageManager(display);
		mc = new MenuCommon(mainForm.mainWindow);
		
		// load images;
		im.add(ImageManager.ico_Program);
		
		// Menu Analysis
		im.add(ImageManager.ico_analysis);
		
		// Menu Edit
		im.add(ImageManager.ico_cut);
		im.add(ImageManager.ico_paste);
		im.add(ImageManager.ico_remove);
		
		// Menu File
		im.add(ImageManager.ico_openFile);
		im.add(ImageManager.ico_saveFile);
		im.add(ImageManager.ico_saveFileAs);
		im.add(ImageManager.ico_quit);

		// Menu Help
		im.add(ImageManager.ico_debug);
		im.add(ImageManager.ico_about);
		im.add(ImageManager.ico_about2);
		im.add(ImageManager.ico_unisens);
		im.add(ImageManager.ico_memory);
		im.add(ImageManager.ico_license);

		// Menu Settings
		im.add(ImageManager.ico_settings);

		// Menu View
		im.add(ImageManager.ico_view);
		im.add(ImageManager.ico_view_pole_change);
		im.add(ImageManager.ico_view_zoom_x);
		im.add(ImageManager.ico_view_zoom_y);
		im.add(ImageManager.ico_view_channels);
		im.add(ImageManager.ico_view_grid);
		im.add(ImageManager.ico_view_trigger);
		im.add(ImageManager.ico_view_goto);
		im.add(ImageManager.ico_view_separator);
		
		im.add(ImageManager.ico_ecg_trigger_add_mode);
		im.add(ImageManager.ico_ecg_trigger_delete_mode);

		signalViewerColors=new SignalViewerColors();
		// Registry interpretieren
		for (int currentChannel=0;currentChannel<SignalViewerColors.maxChannelColors;currentChannel++)
		{
			String RegistryKey=Registry.prop_channelcolor+Integer.toString(currentChannel);
			Color c = reg.loadColorFromRegistry(RegistryKey);
			if (c!=null)
			{
				signalViewerColors.setChannelColor(currentChannel, c);
			}
		}
		
		for (int currentSystemColor=0;currentSystemColor<SignalViewerColors.SignalViewerColorNames.length;currentSystemColor++)
		{
			String RegistryKey=Registry.prop_color+Integer.toString(currentSystemColor);
			Color c = reg.loadColorFromRegistry(RegistryKey);
			if (c!=null)
			{
				signalViewerColors.setSignalViewerColor(currentSystemColor,c);
			}
		}
		
		fontsManager=new FontsManager();
		for (int currentSystemFont=0;currentSystemFont<FontsManager.maxFonts;currentSystemFont++)
		{
			String key=Registry.prop_font+Integer.toString(currentSystemFont);
			fontsManager.setSystemFont(currentSystemFont, reg.loadFontFromRegistry(key));
		}
		
		String RegistryKey=Registry.prop_rounder;
		String result=reg.reg.getProperty(RegistryKey);
		if (result!=null)
		{
			signalRound=Integer.parseInt(result);
		}
		
		signalModel = new SignalModel();
		triggerModel = new TriggerModel();
		artefactModel = new ArtefactModel();
		eventModel = new EventModel();
		
	}
	
	/**
	 * This method return the instance-variable (singleton-design pattern)
	 * 
	 * @return instance
	 */
	public static Common getInstance()
	{
 		if (instance==null)
		{
			instance=new Common();
		}
		return instance;
	}
	
	/** Alias for getInstance 
	 * 
	 * @see Common#getInstance()
	 * @return instance
	 */
	public static Common start()
	{
		return getInstance();
	}
	
	/** Add a listener 
	 * @param listener The SettingsListener
	 * */
	public void addSettingsListener( SettingsListener listener ) { 
		settingsListeners.add( SettingsListener.class, listener ); 
	} 

	/** Remove a listener 
	 * @param listener The SettingsListener to be removed.
	 * */
	public void removeSettingsListener( SettingsListener listener ) { 
		settingsListeners.remove( SettingsListener.class, listener ); 
	} 

	/** Notify every SettingsListener
	 * 
	 * @param e The Settingsevent
	 * 
	 */
	public synchronized void notifySettingsChanged( SettingsEvent e ) 
	{ 
		for ( SettingsListener l : settingsListeners.getListeners(SettingsListener.class) ) 
			l.settingsChanged(e);
	} 
	
	/** This methods displays a message-box.
	 *  
	 * @param title Title of the messagebox.
	 * @param message Message of the messagebox.
	 * @param style Type of MessageBox (see SWT-Constants)
	 * @return Number of pressed button (see SWT-Constants)
	 */
	public int ShowMessageBox(String title,String message,int style)
	{
		// Warning: just use mainWindow only from mainForm. mainForm must not be initialized.
		MessageBox m = new MessageBox(mainForm.mainWindow,style);
		m.setMessage(message);
		m.setText(title);
		return m.open();
	}
	
	/** This methods displays a errorobox.
	 *  This is a sepcial MessageBox
	 *  @see ShowMessageBox
	 *  
	 * @param title Title of the messagebox.
	 * @param message Message of the messagebox.
	 * @return Number of pressed button (see SWT-Constants)
	 */
	
	public int ShowErrorBox(String title,String message)
	{
		if (title==null)
		{
			title="";
		}
		if (message==null)
		{
			message="";
		}
		return ShowMessageBox(title,message,SWT.ICON_ERROR);
	}
	
	/** This method displays an errorbox saying that the called method was not implemented yet.
	 *
	 */
	public void NotYetImplemented()
	{
		NotYetImplemented("");
	}
	
	/** This method display an errorline on the console.
	 * 
	 * @param text Text to output.
	 */
	public static void NotYetImplementedText(String text)
	{
		System.err.println(text);
	}
	
	/** This method displays an errorbox saying that the called method was not implemented yet.
	 * 
	 * @param CustomString Title of the errorbox.
	 */
	public void NotYetImplemented(String CustomString)
	{
		if (CustomString=="")
		{
			CustomString=Constants.error;
		}
		
		ShowErrorBox(CustomString,Constants.notImplemented);
	}
	
	/** This method calls the external program as definied in configuration file and blocks until termination of that external program.
	 * 
	 */
	public void CallExternProgram()
	{
		try
		{
		Process proc = Runtime.getRuntime().exec(reg.reg.getProperty(Registry.prop_externalProgram1));
		
		
		// Maybe we should wait for the termination of the external process
		try
		{
			proc.waitFor();
		}
		catch (InterruptedException e)
		{
			
		}

		/*
		InputStream stderr = proc.getErrorStream();
		OutputStream stdout = proc.getOutputStream();
		InputStream stdin = proc.getInputStream();
		*/
		
		BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String line;
		while ((line = input.readLine()) != null) 
		{
			System.out.println(line);
		}
		input.close();

		}
		catch (IOException ex)
		{
			
		}
	}
	/** This method shows some specialized FileSaveAs-Dialog.
	 * 
	 * @return FileName (incl. Path) or Null if Cancel was pressed.
	 *
	 */
	
	public String MyOwnFileSaveAsDialog()
	{
		DirectoryDialog d=new DirectoryDialog(mainForm.mainWindow);
		String lastDir=Common.getInstance().reg.reg.getProperty(Registry.prop_lastDir);
		
		//TODO Change sth here for issue LHEK_00014_EKG-Editor_005
		if (lastDir!=null)
		{
			if (lastDir.compareTo("")==0)
			{
				lastDir=null;
			}
		}
		d.setFilterPath(lastDir);
		d.setMessage("Datensatz speichern unter ...");

		String FileName = d.open();
		if (FileName != null)
		{
			// update registry
			Common.getInstance().reg.reg.setProperty(Registry.prop_lastDir, FileName);
			Common.getInstance().reg.save();


			File f=new File(FileName+System.getProperty("file.separator")+Constants.StdUnisensFileName);
			if (f.exists())
			{
				Common.getInstance().ShowErrorBox(Constants.error, "Unisens-Datei existiert schon!!!");
				return null;
			}
			else
			{
				return FileName;
			}
		}
		else
		{
			return null;
		}
	}
	
	
    public static String Double2String(Double d)
    {
    	
    	if (d==null)
    	{
    		return "--";
    	}
    	else
    	{
    		long d2=Math.round(d*signalRound);
    		return Double.toString((double)d2/(double)signalRound);
    	}
    }
    
    public static TriggerDataVisible getNextTrigger(int pixel,List<TriggerDataVisible> l)
    {
    	for (TriggerDataVisible t:l)
    	{
    		if (Math.abs(t.pixel-pixel)<Constants.minimum_range_tooltip)
    		{
    			return t;
    		}
    	}
    	
    	return null;
    }
    
    public int getAutomaticPlaySpeed(){
    	return Integer.parseInt(reg.reg.getProperty(Registry.prop_automaticPlaySpeedInMs));
    }
    
    public void finalize() throws Throwable
	{
		try
		{
			if (signalModel!=null)
			{
				signalModel.finalize();
				signalModel=null;
			}
			if (Constants.isDebug)
			{
				System.out.println("Common finalisiert.");
			}
		}
		catch (Exception e)
		{
			super.finalize();
		}
	}
}
