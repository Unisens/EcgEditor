/**
 * This class offers access to the application registry where many settings are saved.
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class Registry {
	
	// Properties
	/** Property used in our registry (settings file) */
	public static final String prop_progname="programname";
	public static final String prop_version="version";
	public static final String prop_lastUsed="lastUsed";
	public static final String prop_externalProgram1="ExternalProgram1";
	public static final String prop_channelcolor="colorOfChannel";
	public static final String prop_color="color";
	public static final String prop_font="font";
	public static final String prop_lastDir="lastDirectory";
	public static final String prop_saveDir="saveDirectory";
	public static final String prop_dynamicGrid="dynamicGrid";
	public static final String prop_gridSize="gridSize";
	public static final String prop_rounder="Rounder";
	public static final String prop_numberOfDigits="numberOfDigits";
	public static final String prop_automaticPlaySpeedInMs = "automaticPlaySpeedInMs";
	public static final String prop_tachogramMaxinalGapInSeconds = "tachogramMaxinalGapInSeconds";
	public static final String prop_secondOpinionMode = "secondOpinionMode";
	private static final String ColorBlue="Blue";
	private static final String ColorRed="Red";
	private static final String ColorGreen="Green";
	
	private static final String FontHeight="Height";
	private static final String FontName="Name";
	private static final String FontStyle="Style";

	
	/** General config file name */
	public static final String file_registry="config.xml";
	
	/** Some variable to get/set settings-entries */
	public Properties reg = null;
	
	/** Was the last read/write sucessfull? */
	public Boolean StatusOK=false;
	
	/** Load all the settings from filesystem to memory 
	 * @return true, if operation was sucessfull otherwise returns false
	 * */
	public Boolean load()
	{
		StatusOK=true;
		reg = new Properties();
		FileInputStream regFile = null;
		try
		{
			regFile=new FileInputStream(file_registry);
			//reg.load(regFile);
			reg.loadFromXML(regFile);
		}
		catch (Exception e)
		{
			System.err.println("Fehler beim Laden der Konfigurationsdatei.\n");
			System.err.println(e.getMessage());
			StatusOK=false;
		}
		finally
		{
			if (regFile!=null)
			{
				try
				{
					regFile.close();
				}
				catch (IOException e)
				{
					StatusOK=false;
				}
			}
		}
		
		return StatusOK;
	}
	
	/** Save all the settings from memory to registry on the local filesystem 
	 * 
	 * @return true, if operation was sucessfull, otherwise returns false
	 */
	public Boolean save()
	{
		StatusOK=true;
		FileOutputStream out=null;
		try
		{
			out=new FileOutputStream(new File(file_registry));
			reg.storeToXML(out,null);
		}
		catch (Exception e)
		{
			StatusOK=false;
			if (out!=null)
			{
				try
				{
					out.close();
				}
				catch (IOException ex)
				{
					StatusOK=false;
				}
			}
		}
		return StatusOK;
	}
	
	/** Convert-method to map (save) one color-object to registry (memory) settings 
	 * @param c The color to be saved
	 * @param RegistryKey the Registry-Key where this color should be saved.
	 * */ 
	public void saveColorToRegistry(Color c,String RegistryKey)
	{
		String RegKey;
		
		RegKey=RegistryKey+ColorBlue;
		reg.setProperty(RegKey, Integer.toString(c.getBlue()));
		RegKey=RegistryKey+ColorRed;
		reg.setProperty(RegKey, Integer.toString(c.getRed()));
		RegKey=RegistryKey+ColorGreen;
		reg.setProperty(RegKey, Integer.toString(c.getGreen()));
	}
	
	/** Convert-method to map (load) one color-object from registry (memory) settings and save it to local filesystem-registry 
	 * @param RegistryKey the Registry-Key where this color should be loaded from.
	 * @return loaded color, if it was found in registry otherwise return null
	 * */ 
	public Color loadColorFromRegistry(String RegistryKey)
	{
		Color result=null;
		try
		{
			String RegKey;

			RegKey=RegistryKey+ColorBlue;
			String blue=reg.getProperty(RegKey);
			int b=Integer.parseInt(blue);
			
			RegKey=RegistryKey+ColorRed;
			String red=reg.getProperty(RegKey);
			int r=Integer.parseInt(red);
			
			RegKey=RegistryKey+ColorGreen;
			String green=reg.getProperty(RegKey);
			int g=Integer.parseInt(green);
			
			result=new Color(null,r,g,b);
		}
		catch (NumberFormatException e)
		{
			result=null;
		}

		return result;
	}
	
	public Font loadFontFromRegistry(String RegistryKey)
	{
		Font result=null;

		try
		{
			String RegKey;
			RegKey=RegistryKey+FontHeight;
			String h=reg.getProperty(RegKey);
			int height=Integer.parseInt(h);
			RegKey=RegistryKey+FontName;
			String name=reg.getProperty(RegKey);
			RegKey=RegistryKey+FontStyle;
			String s=reg.getProperty(RegKey);
			int style=Integer.parseInt(s);
			result= new Font(null,name,height,style);
		}
		catch (NumberFormatException e)
		{
			result=null;
		}

		return result;
	}
	public void saveFontToRegistry(Font f,String RegistryKey)
	{
		FontData f2=f.getFontData()[0];
		String RegKey;
		
		RegKey=RegistryKey+FontHeight;
		reg.setProperty(RegKey, Integer.toString(f2.getHeight()));
		RegKey=RegistryKey+FontName;
		reg.setProperty(RegKey, f2.getName());
		RegKey=RegistryKey+FontStyle;
		reg.setProperty(RegKey, Integer.toString(f2.getStyle()));
		
		
	}
	
	/** Standard constructor for Registry (loads directly settings from filesystem to memory) */
	public Registry()
	{
		StatusOK=load();
		if (!StatusOK)
		{
			//Date now = new Date();
			reg.setProperty(prop_progname,Constants.Programname);
			reg.setProperty(prop_version,Constants.Programversion);
			//reg.reg.setProperty(Constants.prop_lastUsed,now.toString());
			reg.setProperty(prop_externalProgram1,Constants.StdexternalProgram);
			StatusOK=save();
		}
	}
}
