package de.fzi.ekgeditor.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.MedicalClass;

public class SelectionList implements Cloneable,Serializable,Iterable<Selection> {
	
	static final long serialVersionUID = 1L;

	private ArrayList<Selection> selectionList=new ArrayList<Selection>();
	
	public SelectionList()
	{
	}
	public SelectionList(Selection s)
	{
		Add(s);
	}
	
	public Iterator<Selection> iterator()
	{
		return selectionList.listIterator();
	}
	
	public void Add(Selection s, MedicalClass m, String fileName)
	{
		Selection t=(Selection) s.clone();
		t.m=m;
		t.FileName=fileName;
		selectionList.add(t);
	}
	public void Add(Selection s)
	{
		selectionList.add(s);
	}
	public void Remove(Selection s)
	{
		Selection temp=null;
		for(Selection ls:selectionList)
		{
			if (ls.equals(s))
			{
				temp=ls;
			}
		}

		if (temp!=null)
		{
			selectionList.remove((Object)temp);
		}
	}
	public boolean Remove(String fileName)
	{
		Selection temp = null;
		for(Selection sel : this.selectionList)
		{
			if(sel.FileName.equalsIgnoreCase(fileName))
			{
				temp = sel;
			}
		}
		
		if(temp != null)
		{
			return this.selectionList.remove((Object)temp);
		}
		else
		{
			return false;
		}
	}
	public boolean updateFileName(String fromFileName, String toFileName)
	{
		Selection temp = null;
		for(Selection sel : this.selectionList)
		{
			if(sel.FileName.equalsIgnoreCase(fromFileName))
			{
				temp = sel;
			}
		}
		if(temp != null)
		{
			temp.FileName = toFileName;
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean updateMedicalClass(String fileName, MedicalClass newClass)
	{
		Selection temp = null;
		for(Selection sel : this.selectionList)
		{
			if(sel.FileName == fileName)
			{
				temp = sel;
			}
		}
		if(temp != null)
		{
			temp.m = newClass;
			return true;
		}
		else
		{
			return false;
		}
	}
	public void RemoveAll()
	{
		selectionList.clear();
	}
	
	public boolean inList(long point)
	{
		for(Selection s:selectionList)
		{
			if (s.inSelection(point))
			{
				return true;
			}
		}
		
		return false;
	}
	public boolean inList(Selection s)
	{
		for (Selection ls:selectionList)
		{
			if (!ls.isIntersectionAreaZero(s))
			{
				return true;
			}
		}
		
		return false;
	}
	public boolean inList(Selection s,double percent)
	{
		for (Selection ls:selectionList)
		{
			if (ls.getIntersectionArrayPercentage(s)>=percent)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public long getAreaUntilPoint(long point)
	{
		// Voraussetzung: Consolidated List
		long offset=0;

		for(Selection s:selectionList)
		{
			if (s.getSelectionEnd()!=Constants.notSelected)
			{
				if (s.inSelection(point))
				{
					if (s.getSelectionStart()<point) // Nur den Bereich bis point hinzufügen
					{
						offset=offset+(point-s.getSelectionStart());
					}
				} // Vollkommen ausserhalb
				else
				{
					if (s.getSelectionEnd()<=point) // Den gesamten Bereich hinzufügenb
					{
						offset=offset+s.getLength();
					}
				}
			}
		}
		
		return offset;
	}
	
	public void printOut()
	{
		System.out.println("---(Start) RemovedSelection List---");
		for (Selection s:selectionList)
		{
			System.out.println(s);
		}
		System.out.println("---(End) RemovedSelection List---");
	}
	
	public SelectionList getConsolidatedList()
	{
		SelectionList temp = (SelectionList) this.clone();
		boolean thereIsChange=true;
		
			while (thereIsChange)
			{
				thereIsChange=false;
				
				intersectionLoopBreak:
				for (int i=0;i<temp.selectionList.size();i++)
				{
					for (int j=0;j<temp.selectionList.size();j++)
					{
						if (i!=j)
						{
							Selection a=temp.selectionList.get(i);
							Selection b=temp.selectionList.get(j);
							
							if (!(a.isIntersectionAreaZero(b)))
							{
								temp.selectionList.remove(a);
								temp.selectionList.remove(b);
								Selection merged=Selection.getConsolidated(a, b);
								temp.selectionList.add(0, merged);
								thereIsChange=true;
								
								// And loop again the whole loop
								break intersectionLoopBreak;
							} // end if intersection
						}
					}
				} // end if for
			}

		temp.sort();
		return temp;
	}
	
	public Object clone()
	{
		try
		{
			SelectionList myclone= (SelectionList) super.clone();
			myclone.selectionList=new ArrayList<Selection>();
			
			for (Selection s:this.selectionList)
			{
				myclone.Add( (Selection) s.clone());
			}
			
			return myclone;
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}
	
	public Selection nextSelection(long start){
		this.sort();
		
		for (Selection selection : selectionList){
			if (selection.getSelectionStart() >= start){
				return ((Selection) selection.clone());
			}
		}
		
		return null;
	}
	
	public void sort()
	{
		Collections.sort(selectionList);
	}
	
	public SelectionList TransformToSampleSelection(double destinationSampleRate){
		SelectionList result=new SelectionList();
		
		for (Selection s:selectionList)
		{
			result.Add(s.TransformToSampleSelection(destinationSampleRate));
		}
		
		return result;
	}
}
