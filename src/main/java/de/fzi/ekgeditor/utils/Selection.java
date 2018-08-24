/**
 * This class manages one selection
 *
 * @author glose
 * @version 0.2
 */
package de.fzi.ekgeditor.utils;

import java.io.Serializable;

import javax.swing.event.EventListenerList;

import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.MedicalClass;
import de.fzi.ekgeditor.events.MySelectionEvent;
import de.fzi.ekgeditor.events.MySelectionListener;


public class Selection implements Cloneable,Comparable<Selection>, Serializable
{
	static final long serialVersionUID = 1L;
	
	/** where does the selection start */
	private long SelectionStart=Constants.notSelected;
	/** where does the selection end */
	private long SelectionEnd=Constants.notSelected;
	/** is the selection actie */
	private boolean selected=false;
	
	public MedicalClass m=null;
	public String FileName="";
	
	
	/** all the listeners that want to get informed if sth. changes with the selection */
	private EventListenerList selectionListeners = new EventListenerList(); 

	/** registers listener to get notifications
	 * 
	 * @param listener Listener to add
	 */
	public void addSelectionListener( MySelectionListener listener ) { 
		selectionListeners.add( MySelectionListener.class, listener ); 
	} 

	/** removes listener from listener-List
	 * 
	 * @param listener listener to remove
	 */
	public void removeSelectionListener( MySelectionListener listener ) { 
		selectionListeners.remove( MySelectionListener.class, listener ); 
	} 
	
	/** notifies all listeners that some selectionEvent occured
	 * 
	 * @param e Event that has been occured.
	 */
	protected synchronized void notifySelectionChanged( MySelectionEvent e ) 
	{ 
		for ( MySelectionListener l : selectionListeners.getListeners(MySelectionListener.class) ) 
			l.MyselectionChanged(e);
	} 

	/** standard constructor */
	public Selection()
	{
		unSelect();
	}
	
	/** standard constructor that inits some values
	 * 
	 * @param SelectionStart start of the selection
	 * @param SelectionEnd end of the selection
	 */
	public Selection(long SelectionStart,long SelectionEnd)
	{
		this.SelectionStart=SelectionStart;
		this.SelectionEnd=SelectionEnd;
		
		checkSelected();
	}
	
	/** return if this selection is active
	 * 
	 * @return true, if this selection is active otherwise false
	 */
	public boolean isSelected() {
		return selected;
	}
		
	/** extend the selection in such a way that selectionPoint is inside the selection
	 * 
	 * @param selectionPoint this point should be inside the modified selection
	 */
	public void addSelectionPoint(long selectionPoint)
	{
		if (SelectionStart==Constants.notSelected)
		{
			SelectionStart=selectionPoint;
		}
		else if (SelectionEnd==Constants.notSelected)
		{
			SelectionEnd=selectionPoint;
		}
		else if (selectionPoint<SelectionStart)
		{
			SelectionStart=selectionPoint;
		}
		else if (selectionPoint>SelectionEnd)
		{
			SelectionEnd=selectionPoint;
		}
		checkSelected();
			
	}
	
	/** get starting point of this selection
	 * 
	 * @return starting point (in milliseconds) of this selection
	 */
	public long getSelectionStart() {
		return SelectionStart;
	}
	
	/** get starting point of this selection in samples
	 * 
	 * @return starting point (in samples) of this selection
	 */
	public long getSelectionStartInSamples(double SampleRate) 
	{
		return (long) Math.floor( ((double)SelectionStart / (double) 1000) * SampleRate );
	}
	
	/** sets the starting point of this selection
	 * 
	 * @param selectionStart starting point (in milliseconds) of selection
	 */
	public void setSelectionStart(long selectionStart) 
	{
		SelectionStart = selectionStart;
		checkSelected();
	}
		
	/** get ending point of this selection
	 * 
	 * @return ending point (in milliseconds) of this selection
	 */
	public long getSelectionEnd() 
	{
		return SelectionEnd;
	}
	
	public void setSelection(long selectionStart,long selectionEnd)
	{
		this.SelectionStart=selectionStart;
		this.SelectionEnd=selectionEnd;
		
		checkSelected();
	}
	
	/** get ending point of this selection in samples
	 * 
	 * @return ending point (in samples) of this selection
	 */
	public long getSelectionEndInSamples(double SampleRate) 
	{
		return (long) Math.floor( ((double)SelectionEnd / (double) 1000) * SampleRate );
	}
	
	/** sets the ending point (in milliseconds) of this selection
	 * 
	 * @param selectionEnd ending point of selection
	 */
	public void setSelectionEnd(long selectionEnd) {
		SelectionEnd = selectionEnd;
		checkSelected();
	}
	
	/** is this selection a cursor?
	 * 
	 * @return returns true if this selection represents a cursor otherwise false
	 */
	public boolean isCursor()
	{
		return ((SelectionStart!=Constants.notSelected) & (SelectionEnd==Constants.notSelected));
	}
	
	/** invalidate this selection */
	public void unSelect()
	{
		SelectionStart=Constants.notSelected;
		SelectionEnd=Constants.notSelected;
		selected=false;
		this.notifySelectionChanged(new MySelectionEvent(this,this));
	}
	
	/** check if this selection is active after a change and notify all listeners */
	private void checkSelected()
	{
		if ((SelectionEnd<SelectionStart) & (SelectionEnd!=Constants.notSelected))
		{
			long h=SelectionStart;
			SelectionStart=SelectionEnd;
			SelectionEnd=h;
		}
		selected = ((SelectionEnd-SelectionStart)>Constants.minimum_selected);
		this.notifySelectionChanged(new MySelectionEvent(this,this));
	}
	
	/** clone this selection
	 * 
	 * @return cloned selection
	 */
	public Object clone()
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
	
	/** is this selection equal to some other selection (by value)
	 * 
	 * @param t other selection to compare with
	 * @return true, if selections have same values otherwise false
	 */
	public boolean equals(Selection t)
	{
		return ((t.SelectionStart==this.SelectionStart) && (t.SelectionEnd==this.SelectionEnd));
	}
	
	/** compare this selection another selection t
	 * 
	 * @param t Selection to compare to
	 * @return 0, if selections are equal, 1 if this selection is bigger than t, otherwise return -1
	 */
	public int compareTo(Selection t)
	{
		if (this.equals(t))
		{
			return 0;
		}
		else if (this.SelectionStart==t.SelectionStart)
		{
			if (this.SelectionEnd>t.SelectionEnd)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
		else if (this.SelectionStart>t.SelectionStart)
		{
			return 1;
		}
		else return -1;
	}
	
	/** returns a printable string representing this selection 
	 * 
	 * @return printable string
	 * */
	public String toString()
	{
		return Long.toString(SelectionStart)+"-"+Long.toString(SelectionEnd);
	}
	
	/** calculate the difference from point point to start- and endpoint of this selection
	 * 
	 * @param point point to calculate the difference to
	 * @return RangeDiff-structure containing all the information
	 */
	public RangeDiff calcDiffToStartOrEndPoint(long point)
	{
		RangeDiff result=new RangeDiff();
		
		if (SelectionStart!=Constants.notSelected)
		{
			result.diffStart=Math.abs(SelectionStart-point);
		}
		if (SelectionEnd!=Constants.notSelected)
		{
			result.diffEnd=Math.abs(SelectionEnd-point);
		}
		result.start=result.diffStart<Constants.minimum_range;
		result.end=result.diffEnd<Constants.minimum_range;

		return result;
	}
	
	/** delete start or endpoint to extend this selection in such a way 
	 * that point is inside the selection 
	 * 
	 * @param point point that should be inside the selection
	 * @return modified selection
	 */
	public Selection RangeSelection(long point)
	{
		RangeDiff d=calcDiffToStartOrEndPoint(point);
		return RangeSelection(d);
	}
	
	/** delete start or endpoint to extend this selection depending on the
	 * set of opitions set by RangeDiff
	 * 
	 * @param d RangeDiff options-set
	 * @return modified selection
	 */
	public Selection RangeSelection(RangeDiff d)
	{
		Selection newSelection=(Selection) this.clone();
		
		if (atStartOrEndPoint(d))
		{
			
			if (d.start)
			{
				newSelection.SelectionStart=Constants.notSelected;
			}
			else if (d.end)
			{
				newSelection.SelectionEnd=Constants.notSelected;
			}
		}
		
		return newSelection;
	}
	
	/** is point at the start- or endpoint of this selection
	 * 
	 * @param point point to work with
	 * @return true, if the point is in the range of start- or endpoint of the selection,
	 * otherwise return false
	 */
	public boolean atStartOrEndPoint(long point)
	{
		RangeDiff d=calcDiffToStartOrEndPoint(point);
		return (d.start || d.end);
	}
	
	/** is point at the start- or endpoint of this selection
	 * 
	 * @param r RangeDiff containing the information to work with
	 * @return true, if it is in the range of start- or endpoint of the selection,
	 * otherwise return false
	 */
	public static boolean atStartOrEndPoint(RangeDiff r)
	{
		return (r.start||r.end);
	}
	
	/** is point point inside the selection
	 * 
	 * @param point point to work with
	 * @return true, if point lies inside the selection, otherwise false
	 */
	public boolean inSelection(long point)
	{
		if ((SelectionStart!=Constants.notSelected) & (SelectionEnd!=Constants.notSelected))
		{
			return ((point>=SelectionStart) & (point<=SelectionEnd));
		}
		else
		{
			return false;
		}
	}
	
	/** is this selection complety defined (meaning: does it represent an active selection or a cursor
	 * 
	 * @return true, if this selection is complety defined, otherwise false
	 */
	public boolean completlyDefined()
	{
		return ((SelectionStart!=Constants.notSelected) & (SelectionEnd!=Constants.notSelected));
	}
	
	/** checks if one selection is inside the other selection
	 * 
	 * @param A Selection A
	 * @param B Selection B
	 * @return true, if Selection A is inside Selection B or B is inside A, otherwise return false
	 */
	private static boolean isSelectionInside(Selection A,Selection B)
	{
		Selection[] t=SwapSort(A,B);
		A=t[0];B=t[1];
		
		if (A.completlyDefined() & B.completlyDefined())
		{
			if ((B.getSelectionStart()>=A.SelectionStart) & (B.getSelectionEnd()<=A.getSelectionEnd()))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	/** checks if there is some intersection between A and B
	 * 
	 * @param A Selection A
	 * @param B Selection B
	 * @return true, if there is some intersection between A and B, false otherwise.
	 */ 
	private static boolean hasSomeIntersecton(Selection A,Selection B)
	{				
		Selection[] t=SwapSort(A,B);
		A=t[0];B=t[1];; // prerequisite A<=B
		
		return ((B.getSelectionStart()>=A.getSelectionStart()) & (B.getSelectionStart()<=A.getSelectionEnd()));
	}
	
	/** Swap Selection A and Selection B, in such a way that A is smaller than B 
	 * 
	 * @param A Selection A
	 * @param B Selection B
	 */
	public static Selection[] SwapSort(Selection A,Selection B)
	{
		Selection H=null;
		Selection[] t = new Selection[2];
		
		if (B.getSelectionStart()<A.getSelectionStart())
		{
			H=A;
			A=B;
			B=H;
		}
		
		t[0]=A;t[1]=B;
		
		return t;
	}
	
	/** checks if intersectionArea between this Selection and Selection s is zero
	 * 
	 * @param s Selection s
	 * @return true, if intersection area between this selection and selection s is zero.
	 * otherwise return false.
	 */
	public boolean isIntersectionAreaZero(Selection s)
	{		
		Selection A=(Selection) this.clone();
		Selection B=(Selection) s.clone();
		
		Selection[] t=SwapSort(A,B);
		A=t[0];B=t[1];
		
		if (isSelectionInside(A,B))
		{
			return false;
		}
		else
		if (hasSomeIntersecton(A,B))
		{
			return false;
		}
		
		return true;
	}
	
	/** gets in percentage how much of one selection is intersected by selection b
	 * 
	 * @param s Selection s
	 * @return percentage of intersection between this selection and selection s
	 */
	public double getIntersectionArrayPercentage(Selection s)
	{
		Selection A=(Selection) this.clone();
		Selection B=(Selection) s.clone();
		
		Selection[] t=SwapSort(A,B);
		A=t[0];B=t[1];

		if (isSelectionInside(A,B))
		{
			// completly inside
			return 100;
		}
		else
		{
			if (hasSomeIntersecton(A,B))
			{
				// o.B.d.A: A starts before B
				long intersectionPoints=A.getSelectionEnd()-B.getSelectionStart();
				long value=Math.min(A.getLength(), B.getLength());
				return ((double) 100/ (double) value)* (double) intersectionPoints;
			}
			else
			{
				return 0;
			}
		}
	}
	
	/** returns the length of this intersection
	 * 
	 * @return length (in milliseconds) 
	 */
	public long getLength()
	{
		if (this.completlyDefined())
		{
			return SelectionEnd-SelectionStart;
		}
		else
		{
			return 0;
		}
	}
	
	/** get one consolidated selection of Selection A and Selection B, if
	 * A and B have some intersection.
	 * 
	 * @param A Selection A
	 * @param B Selection B
	 * @return consolidated Selection if A and B have some intersection; otherwise return null.
	 */
	public static Selection getConsolidated(Selection A,Selection B)
	{
		if (A.completlyDefined() & B.completlyDefined())
		{
			Selection[] t=SwapSort(A,B);
			A=t[0];B=t[1];
			
			if (hasSomeIntersecton(A,B))
			{
				long start=A.getSelectionStart();
				if (B.getSelectionStart()<start)
				{
					start=B.getSelectionStart();
				}
				
				long end=A.getSelectionEnd();
				if (B.getSelectionEnd()>end)
				{
					end=B.getSelectionEnd();
				}
				
				return new Selection(start,end);
				
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	
	/** transform this selection to another sampleRate
	 * 
	 * @param thisSampleRate sampleRate of this Selection
	 * @param destinationSampleRate new sampleRate
	 * @return transformed selection 
	 */
	public Selection TransformToSampleSelection(double destinationSampleRate)
	{
		Selection result=(Selection) this.clone();
		
		if (result.SelectionStart!=Constants.notSelected)
		{
			result.SelectionStart=(long) ((double)SelectionStart/((double) 1000) * (double) destinationSampleRate);
		}
		if (result.SelectionEnd!=Constants.notSelected)
		{
			result.SelectionEnd  =(long) ((double)SelectionEnd/((double) 1000)* (double) destinationSampleRate);
		}
			
		return result;
	}
	
}
