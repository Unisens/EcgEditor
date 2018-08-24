/**
 * This class manages the data for some medical classes (Vorhoffflimmern etc.)
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor.data;

import java.io.Serializable;

public class MedicalClass implements Cloneable, Serializable {

	public static final long serialVersionUID=1L;
	
	/** Name of the Medical Class */
	public String title="";
	/** Minimum Length (in ms) that have to be saved to be some test-data */
	public int minimalLength=0;
	public String abbrev="";
	
	/** Standard constructor
	 * 
	 * @param title Name of the Medical Class (e.g. Vorhoffflimmern)
	 * @param minimalLength Minimum Length (in ms) to be considered as testdata 
	 */
	public MedicalClass(String title,int minimalLength)
	{
		this.title=title;
		this.minimalLength=minimalLength;
	}
	
	/** Anonymous constructor */
	public MedicalClass()
	{
	}
	
	/** Convert the class-data to some string (e.g. for printouts usefull) */ 
	public String toString()
	{
		return title+";"+abbrev+" MinimalLength:"+minimalLength;
	}
	
	/** Check if this instance was really initialized 
	 *  Can be false, if using anonymous constructor
	 * @return true, if this instance was initialized, otherwise return false
	 */
	public Boolean isInitialized()
	{
		return ((title.compareTo("")!=0) && (minimalLength!=0) && abbrev.compareTo("")!=0);
	}
	
	/** Clone this object 
	 * @return a copy of this instance or null if this instance can not be cloned. */
	protected Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}
	public MedicalClass Clone()
	{
		return (MedicalClass) this.clone();
	}
}
