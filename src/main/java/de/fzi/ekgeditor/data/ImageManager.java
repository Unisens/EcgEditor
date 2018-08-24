/**
 * This class manages all images (mostly icons) in the program
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.data;

import java.util.ArrayList;

import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class ImageManager {

	// Icons
	
	/** Iconfilename for Open File */
	public static final String ico_openFile = "document-open.png";
	/** Iconfilename for Save File */
	public static final String ico_saveFile = "document-save.png";
	/** Iconfilename for Save File As*/
	public static final String ico_saveFileAs = "document-save-as.png";
	/** Iconfilename for program exit */
	public static final String ico_quit="system-log-out.png";
	/** Iconfilename for cut */
	public static final String ico_cut="edit-cut.png";
	/** Iconfilename for paste */
	public static final String ico_paste="edit-paste.png";
	/** Iconfilename for delete (selection) */
	public static final String ico_remove="edit-delete.png";
	/** Iconfilename for transparent image */
	public static final String ico_none="no-image.png";
	/** Iconfilename for Preview/Zoom */
	public static final String ico_view="no-image.png";
	public static final String ico_view_channels="view-ecg-channels.png";
	public static final String ico_view_zoom_x="view-ecg-scale-x.png";
	public static final String ico_view_zoom_y="view-ecg-scale-y.png";
	public static final String ico_view_pole_change="view-ecg-pole-change.png";
	public static final String ico_view_grid="view-ecg-grid.png";
	public static final String ico_view_trigger="view-ecg-trigger.png";
	public static final String ico_view_goto="view-ecg-goto.png";
	public static final String ico_view_separator="view-ecg-separator.png";
	/** Iconfilename for license */
	public static final String ico_license="help-license.png";
	/** Iconfilename for memory monitor */
	public static final String ico_memory="help-memory.png";
	/** Iconfilename for Settings */
	public static final String ico_settings="preferences-system.png";
	/** Iconfilename for run analysis */
	public static final String ico_analysis="system-search.png";
	/** Iconfilename for menu-entries used to test the program. */
	public static final String ico_debug="internet-web-browser.png";
	/** Iconfilename for Programinfo*/
	public static final String ico_about="help-browser.png";
	public static final String ico_unisens="help-unisens.png";
	/** Iconfilename for Programinfo2*/
	public static final String ico_about2="help-browser.png";
	/** Iconfilename for our organization */
	public static final String ico_Program="fzi.bmp";
	/** Iconfilename for our organization */
	public static final String ico_ecg_trigger_add_mode="ecg-trigger-add-mode.png";
	/** Iconfilename for our organization */
	public static final String ico_ecg_trigger_delete_mode="ecg-trigger-delete-mode.png";
	
	/** ArrayList to save all the images */
	private ArrayList<ImageKVP> images = new ArrayList<ImageKVP>();
	/** images for that display */
	private Display display;
	
	/** standard constructor
	 * 
	 * @param display Display to use
	 */
	public ImageManager(Display display)
	{
		this.display=display;
	}
	
	/** load the image with the name name from standard filesystem path
	 * 
	 * @param name Name of the image to load
	 */
	public boolean add(String name)
	{
		boolean result=true;
		try
		{
			images.add(new ImageKVP(name, new Image(display,Constants.iconPath+name)));
		}
		catch (IllegalArgumentException e)
		{
			System.err.println(e.getMessage());
			result=false;
		}
		catch (SWTException e)
		{
			System.err.println(e.getMessage());
			result=false;
		}
		catch (SWTError e)
		{
			System.err.println(e.getMessage());
			result=false;
		}

		return result;
	}
	
	/** Return the image with key name from memory (if loaded) or otherwise return null
	 * 
	 * @param name Name of the image (Key)
	 * @return null, if image is not in memory otherwise return the found image
	 */
	public Image get(String name)
	{
		for (ImageKVP k:images)
		{
			if (k.key.compareTo(name)==0)
			{
				return k.image;
			}
		}
		
		return null;
	}
}
