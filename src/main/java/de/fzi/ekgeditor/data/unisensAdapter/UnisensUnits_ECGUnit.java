/**
 * This class manages one ECG-Unit
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor.data.unisensAdapter;

public class UnisensUnits_ECGUnit {

	/** Names of the unit */
	public final String[] name;
	/** conversion rate to base unit */
	public final double conversionToBase;
	/** link to base unit */
	public final UnisensUnits_ECGUnit base;
	
	/** constant to show that this instance does not have any id */
	public final int NOID=-1;
	/** id of this instance */
	public int id=NOID;
	
	/** Standard constructor
	 * 
	 *  Initializes one instance of this class
	 * 
	 * @param name Names of the unit
	 * @param conversionToBase conversion rate to base unit
	 * @param base Base unit (or null if this is the base unit)
	 */
	public UnisensUnits_ECGUnit(String[] name,double conversionToBase,UnisensUnits_ECGUnit base)
	{
		this.name=name;
		this.conversionToBase=conversionToBase;
		this.base=base;
	}
	
	/** Standard constructor
	 * 
	 *  Initializes one instance of this class
	 * 
	 * @param name Name of the unit
	 * @param conversionToBase conversion rate to base unit
	 * @param base Base unit (or null if this is the base unit)
	 */
	public UnisensUnits_ECGUnit(String name,double conversionToBase,UnisensUnits_ECGUnit base)
	{
		String[] t={name};
		this.name=t;
		this.conversionToBase=conversionToBase;
		this.base=base;
	}
	
	/** Standard constructor
	 * 
	 *  Initializes one instance of this class
	 * 
	 * @param name Names of the unit
	 * @param conversionToBase conversion rate to base unit
	 * @param base Base unit (or null if this is the base unit)
	 * @param id ID of this unit
	 */
	public UnisensUnits_ECGUnit(String[] name,double conversionToBase,UnisensUnits_ECGUnit base,int id)
	{
		this(name,conversionToBase,base);
		this.id=id;
	}
	
	/** Standard constructor
	 * 
	 *  Initializes one instance of this class
	 * 
	 * @param name Name of the unit
	 * @param conversionToBase conversion rate to base unit
	 * @param base Base unit (or null if this is the base unit)
	 * @param id ID of this unit
	 */
	public UnisensUnits_ECGUnit(String name,double conversionToBase,UnisensUnits_ECGUnit base,int id)
	{
		String[] t={name};
		this.name=t;
		this.conversionToBase=conversionToBase;
		this.base=base;
		this.id=id;
	}
}
