/**
 * This class manages all the metadata for one channel (name, number)
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.data;


public class ChannelData {

	/** Name of the channel (displaytext) */
	private String name;
	/** Number associated with that channel */
	private int number;

	/** Standard constructor that inits all except the optional MenuItem-Component.
	 * 
	 * @param name Name of the channel (ex. channel A)
	 * @param number Number of the Channel (ex. channel 1)
	 */
	public ChannelData(String name,int number)
	{
		this.name=name;
		this.number=number;
	}

	/** Get the name of the channel
	 * 
	 * @return Name of the channel
	 */
	public String getName() {
		return name;
	}

	/** Set the name of the channel
	 * 
	 * @param name Name of the channel
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** Get the number (id) of the channel
	 * 
	 * @return number of the channel
	 */
	public int getNumber() {
		return number;
	}

	/** Sets the number (id) of the channel
	 * 
	 * @param number Number (id) of the channel
	 */
	public void setNumber(int number) {
		this.number = number;
	}
}
