/**
 * This class manages all the metadata for a trigger list
 *
 * @author kirst
 * @version 0.1
 */

package de.fzi.ekgeditor.data;


public class EntryData
{
	private double sampleRate;
	private String contentClass;
	private String source;
	private String sourceId;
	private String id;
	private String comment;
	private int typeLength;
	private int commentLength;
	
	
	public EntryData(){
	}
	
	public EntryData(String id, double sampleRate, String contentClass, String source, String sourceId, String comment){
		this.id = id;
		this.sampleRate = sampleRate;
		this.contentClass = contentClass;
		this.source = source;
		this.sourceId = sourceId;
		this.comment = comment;
	}

	public void setSampleRate(double sampleRate)
	{
		this.sampleRate = sampleRate;
	}
	
	public double getSampleRate()
	{
		return sampleRate;
	}
	
	public void setComment(String comment)
	{
		this.comment = comment;
	}
	
	public String getComment()
	{
		return comment;
	}
	
	public void setContentClass(String contentClass)
	{
		this.contentClass = contentClass;
	}
	
	public String getContentClass()
	{
		return contentClass;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public String getId()
	{
		return id;
	}
	
	public void setSource(String source)
	{
		this.source = source;
	}
	
	public String getSource()
	{
		return source;
	}
	
	public void setSourceId(String sourceId)
	{
		this.sourceId = sourceId;
	}
	
	public String getSourceId()
	{
		return sourceId;
	}
	
	public void setCommentLength(int commentLength)
	{
		this.commentLength = commentLength;
	}
	
	public int getCommentLength()
	{
		return commentLength;
	}
	
	public void setTypeLength(int typeLength)
	{
		this.typeLength = typeLength;
	}
	
	public int getTypeLength()
	{
		return typeLength;
	}
}
