/**
 * This class manages one AnnotationCode (for Triggers)
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.data;

public class AnnotationCode {

	/** Long text of that annotation code */
	public String name;
	/** Short text (code). eg. "(" */
	public String code;
	/** Some comment associated with that annotation */
	public String comment;
	/** defines if this code marks a real beat */
	public boolean isBeatCode;
	
	/** Standard constructor that inits this class.
	 * 
	 * @param name Name of the annotation code
	 * @param comment Some comment associated with that annotation
	 * @param code Short text (code). eg. "("
	 * @param isBeatCode defines if this code marks a real beat
	 */
	public AnnotationCode(String name,String code,String comment,boolean isBeatCode)
	{
		this.name=name;
		this.code=code;
		this.comment=comment;
		this.isBeatCode=isBeatCode;
	}
	
	public String toString()
	{
		return name+"("+code+"), "+comment+", isBeatCode:"+isBeatCode;
	}
}
