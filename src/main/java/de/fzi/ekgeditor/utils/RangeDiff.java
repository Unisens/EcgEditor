/** This class is used to calculate if the current mouse-cursor position is in the
 * area where we can move it (Start or End of that Area)
 * @author glose
 * @version 0.2
 *
 */
package de.fzi.ekgeditor.utils;

import de.fzi.ekgeditor.data.Constants;

public class RangeDiff {

	/** difference to selection start */
	public long diffStart;
	/** difference to selection end */
	public long diffEnd;
	/** is in range of startSelection */
	public boolean start;
	/** is in range of endSelection */
	public boolean end;
	
	public RangeDiff()
	{
		diffStart=Constants.notInRange;
		diffEnd=Constants.notInRange;
		start=false;
		end=false;
	}
}
