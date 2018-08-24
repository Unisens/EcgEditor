/**
 * This class manages some key-value-pairs for the image-manager
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.data;

import org.eclipse.swt.graphics.Image;

public class ImageKVP {

	/** the key to point to the image */
	public String key;
	/** the image itself */
	public Image image;
	
	/** standard constructor
	 * 
	 * @param key Key to point to the image
	 * @param image the image to save
	 */
	public ImageKVP(String key,Image image)
	{
		this.key=key;
		this.image=image;
	}
}
