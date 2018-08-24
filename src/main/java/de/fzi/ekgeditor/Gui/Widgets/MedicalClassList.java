/**
 * This class manages medical classes
 *
 * @author glose
 * @version 0.2
 */

package de.fzi.ekgeditor.Gui.Widgets;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.fzi.ekgeditor.data.Constants;
import de.fzi.ekgeditor.data.MedicalClass;
import de.fzi.ekgeditor.data.Registry;

public class MedicalClassList {
	
	/** array of all medical classes */
	public ArrayList<MedicalClass> mClasses = new ArrayList<MedicalClass>();
	
	/** current selected medical class */
	private MedicalClass currentMedicalClass;
	
	/** Standard constructor */
	public MedicalClassList()
	{
		// Fill with data
		try
		{
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
		    DocumentBuilder builder = factory.newDocumentBuilder(); 
		    if (Constants.printMedicalClassesLoad)
		    {
		    	System.out.println("Laden der Konfigurationsdatei MedicalClasses.xml");
		    	System.out.println("Datei:"+Constants.file_MedicalClassesConfig);
		    }
		    Document document = builder.parse(Registry.class.getClassLoader().getResourceAsStream(Constants.file_MedicalClassesConfig));
		    document.normalizeDocument();
		    
		    currentMedicalClass=null;
		    parseXMLDoc(document.getDocumentElement(),"");
		}
		catch (Exception e)
		{
			System.err.println("Fehler beim Laden der Konfigurationsdatei MedicalClasses.xml");
			System.err.println(e.getMessage());
		}
	}
	
	/** find one medical class by string name
	 * 
	 * @param title name of the medical class (display name)
	 * @return medical class that first matches or null if no class was found
	 */
	public MedicalClass FindMedicalClass(String title)
	{
		for (MedicalClass m:mClasses)
		{
			if (m.title.compareTo(title)==0)
			{
				return m.Clone();
			}
		}
		
		return null;
	}
	
	/** parse one xml-node
	 * The node can also be the root type which means that the whole document is parsed
	 * 
	 * @param node node to parse
	 * @param name current node structure
	 */
	private void parseXMLDoc(Node node,String name)
    {
		if (currentMedicalClass==null)
		{
			currentMedicalClass = new MedicalClass();
		}
		
        short type = node.getNodeType();
        switch (type)
        {
            case Node.ELEMENT_NODE:
            {
                name = name+"-"+node.getNodeName();
                if (Constants.printMedicalClassesLoad)
                {
                	System.out.println("ELEMENT:"+name);
                }
                NodeList children = node.getChildNodes();
                if (children != null)
                {
                    int length = children.getLength();
                    for (int i = 0; i < length; i++)
                        parseXMLDoc(children.item(i),name);
                }
                break;
            }
            case Node.TEXT_NODE:
            {
            	if (Constants.printMedicalClassesLoad)
            	{
            		System.out.println("TEXT:"+name+";"+node.getNodeValue());
            	}
            	if (name.compareTo("-MedicalClasses-entry-title")==0)
            	{
            		if (Constants.printMedicalClassesLoad)
            		{
            			System.out.println(name+"-Text: "+node.getNodeValue());
            		}
            		currentMedicalClass.title=node.getNodeValue();
            		
            		if (currentMedicalClass.isInitialized())
            		{
            			mClasses.add(currentMedicalClass.Clone());
            			if (Constants.printMedicalClassesLoad)
            			{
            				System.out.println("Klasse geladen:"+currentMedicalClass);
            			}
            			currentMedicalClass=null;
            		}
            	}
            	if (name.compareTo("-MedicalClasses-entry-minimalLength")==0)
            	{
            		currentMedicalClass.minimalLength=Integer.parseInt(node.getNodeValue());
            		//System.out.println(m.toString());
            		//listOfClasses.add(m.title);
            		
            		if (currentMedicalClass.isInitialized())
            		{
            			mClasses.add(currentMedicalClass.Clone());
            			if (Constants.printMedicalClassesLoad)
            			{
            				System.out.println("Klasse geladen:"+currentMedicalClass);
            			}
            			currentMedicalClass=null;
            		}
            	}
            	if (name.compareTo("-MedicalClasses-entry-abbreviation")==0)
            	{
            		currentMedicalClass.abbrev=node.getNodeValue();
            		//System.out.println(m.toString());
            		//listOfClasses.add(m.title);
            		
            		if (currentMedicalClass.isInitialized())
            		{
            			mClasses.add(currentMedicalClass.Clone());
            			if (Constants.printMedicalClassesLoad)
            			{
            				System.out.println("Klasse geladen:"+currentMedicalClass);
            			}
            			currentMedicalClass=null;
            		}
            	}
            	
                break;
            }
        }
    }
}
