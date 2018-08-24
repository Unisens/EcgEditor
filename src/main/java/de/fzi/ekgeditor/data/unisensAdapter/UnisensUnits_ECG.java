/**
 * This class manages all ECG-Units
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.data.unisensAdapter;

public class UnisensUnits_ECG {
	
	/*
 	Unit conversion-rate
  	V	1
	mV	1E-3
	uV	1E-6
	µV	1E-6
	nV	1E-9
 */
 
	/** units we support */
	public static enum ECGunits {V,mV,µV,nV};
	/** which unit did we have selected now */
	private Integer index=null;
	
	/** Base unit. This is the unit where all conversion go through */
	private static final UnisensUnits_ECGUnit BaseUnit = new UnisensUnits_ECGUnit("mV",1,null,ECGunits.mV.ordinal());
	
	/** just a field for initialization purposes */
	private static final String[] micro={"µV","uV"};

	/** all the units we manage */
	public static final UnisensUnits_ECGUnit[] units=
	{
			new UnisensUnits_ECGUnit("V",1000,BaseUnit,ECGunits.V.ordinal()),
			BaseUnit,
			new UnisensUnits_ECGUnit(micro,0.001,BaseUnit,ECGunits.µV.ordinal()),
			new UnisensUnits_ECGUnit("nV",0.000001,BaseUnit,ECGunits.nV.ordinal())
	};
	
	/** Standard constructor
	 * 
	 *  Initializes one instance of this class
	 * 
	 * @param unit The unit we have currently selected
	 */
	public UnisensUnits_ECG(ECGunits unit)
	{
		index=unit.ordinal();
	}
	
	/** Constructor
	 * 
	 *  Initializes one instance of this class
	 *  We can also do a contruction by unit name, case is ignored
	 * 
	 * @param name Name of the unit we should select.
	 */
	public UnisensUnits_ECG(String name)
	{
		index=null;
		
		setToUnit(name);
		
		if (index==null)
		{
			throw new IllegalArgumentException("UnisensUnits_ECG: Unit does not exists!");
		}
	}
	
	/** 
	 *  Gets the name of the currently selected unit
	 * 
	 *  @return Name of the currently selected unit.
	 */
	public String getName()
	{
		return units[index].name[0];
	}
	
	/**
	 * Selects another unit by name
	 * @param name Name of the unit to select
	 * @return true, if operation succeeds, otherwise false (e.g. unit does not exists)
	 */
	public boolean setToUnit(String name)
	{
		for (UnisensUnits_ECGUnit u:units)
		{
			for (String s:u.name)
			{
				if (s.equalsIgnoreCase(name))
				{
					index=u.id;
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * Converts the value value to another unit (in this case: base unit)
	 * @param value value to convert
	 * @return Value (Unit is the baseUnit)
	 */
	public double convertValueToUnit(double value)
	{
		return value*units[index].conversionToBase;
	}
	
	/**
	 * Converts the value value to another unit 
	 * @param value value to convert
	 * @param destinationUnit The unit to convert that to.
	 * @return Value (Unit is the destinationUnit)
	 */
	public double convertValueToUnit(double value,UnisensUnits_ECG destinationUnit)
	{
		return this.convertValueToUnit(value)*(1/units[destinationUnit.index].conversionToBase);
	}
	
	public double getConversionRate(UnisensUnits_ECG destinationUnit)
	{
		return units[index].conversionToBase*(1/units[destinationUnit.index].conversionToBase);
	}
}
