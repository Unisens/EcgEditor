package de.fzi.ekgeditor.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.fzi.ekgeditor.algorithm.AlgorithmEntry.ContentClass;
import de.fzi.ekgeditor.algorithm.AlgorithmEntry.EntryTypeEnum;


public class AlgorithmsConfig {
	private List<Algorithm> availableAlgorithms = new ArrayList<Algorithm>();
	private Algorithm activeAlgorithm;
	
	
	public AlgorithmsConfig(){
		loadAlgorithmConfig();
	}
	
	protected void loadAlgorithmConfig() {
		System.out.println("Loading algorithm-configuration file");
		
		try {
			readAlgorithmConfig("algorithms.xml");
		}catch(XMLParseException xmlpe) {
			System.out.println("Could not read conifiguration file. Reason: "+ xmlpe.getLocalizedMessage());
			System.out.println("Using default configuration.");
		}
	}

	private void readAlgorithmConfig(String configFile) throws XMLParseException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			//parse using builder to get DOM representation of the XML file
			Document dom = db.parse(configFile);
			Element docEle = dom.getDocumentElement();
			
			// find algorithm nodes
			NodeList nl = docEle.getElementsByTagName("algorithm");
			if(nl == null || nl.getLength() <= 0) { throw new XMLParseException("No algorithms found"); }
			
			for(int i = 0 ; i < nl.getLength();i++) {
				Element el = (Element)nl.item(i);
				
				// make basic algorithm
				Algorithm algorithm = new Algorithm(el.getAttribute("name"), el.getAttribute("class"));
				
				// add input signals
				readInputConfig(algorithm, el.getElementsByTagName("input"));
				
				// add output
				readOutputConfig(algorithm, el.getElementsByTagName("output"));
				
				// add to list of algorithms
				availableAlgorithms.add(algorithm);
			}
		// convert all exceptions to XMLParseException
		} catch (ParserConfigurationException e) {
			throw new XMLParseException(e.getMessage());
		} catch (SAXException e) {
			throw new XMLParseException(e.getMessage());
		} catch (IOException e) {
			throw new XMLParseException(e.getMessage());
		}
	}
	
	/**
	 * Reads input configuration from XML-file and adds it to algorithm
	 * @param algorithm
	 * @param input		list of input-config-nodes for algorithm
	 */
	private void readInputConfig(Algorithm algorithm, NodeList input) 
	{
		if (input == null || input.getLength() <= 0) 
		{ 
			return; 
		}
		
		NodeList inputs = ((Element)input.item(0)).getChildNodes();
		
		
		// abort, if no inputs available
		if (inputs == null || inputs.getLength() <= 0) 
		{ 
			return; 
		}
		
		for (int j = 0; j < inputs.getLength(); j++)
		{
			if (inputs.item(j).getNodeType() != Node.ELEMENT_NODE) 
			{ 
				continue; 
			}
			
			Element in = (Element)inputs.item(j);
			
			// find supported samplerates
			NodeList configSets = in.getElementsByTagName("configset");
			if (configSets == null || configSets.getLength() <= 0) 
			{ 
				continue; 
			}
						
			// Read the different configuration sets from XML. First the variables
			// for the configuration attributes are initialized, ...
			Integer[] supportedSampleRates = new Integer[configSets.getLength()];
			int[] delays = new int[configSets.getLength()];
			double[] lsbs = new double[configSets.getLength()];
			String[] units = new String[configSets.getLength()];
			
			// then those variables are filled with data from the XML file.
			for(int k = 0; k < configSets.getLength(); k++) 
			{
				Element configSet = (Element)configSets.item(k);
				supportedSampleRates[k] = Integer.parseInt(configSet.getAttribute("samplerate"));
				delays[k] = Integer.parseInt(configSet.getAttribute("delay"));
				lsbs[k] = Double.parseDouble(configSet.getAttribute("lsb"));
				units[k] = configSet.getAttribute("delay");
			}

			// find channels
			NodeList channels = in.getElementsByTagName("channel");
			if (channels == null || channels.getLength() <= 0) { 
				continue; 
			}
			
			for(int k = 0; k < channels.getLength(); k++) 
			{
				Element channel = (Element)channels.item(k);
				
				String name = in.getAttribute("name") +" - "+ channel.getAttribute("name");
				EntryTypeEnum type = AlgorithmEntry.EntryTypeEnum.toEntryTypeEnum(in.getNodeName());
				ContentClass contentClass = AlgorithmEntry.ContentClass.toContentClass(in.getAttribute("contentClass"));
				algorithm.addInputEntry(new AlgorithmEntry(name, type, contentClass, supportedSampleRates, delays, lsbs, units));
			}
		}
	}
	
	private void readOutputConfig(Algorithm algorithm, NodeList output){
		// some outputs can only be used once:
		boolean heartrateExists = false;
		boolean timerangeExists = false;
		
		if (output == null || output.getLength() <= 0){ 
			return; 
		}
		
		NodeList outputs = ((Element)output.item(0)).getChildNodes();
		if (outputs == null || outputs.getLength() <= 0){ 
			return; 
		}
		
		for (int j = 0; j < outputs.getLength(); j++){
			if (outputs.item(j).getNodeType() != Node.ELEMENT_NODE) { 
				continue; 
			}
			
			Element outputNode = (Element)outputs.item(j);
			
			// check duplicate output types
			if (outputNode.getNodeName().equalsIgnoreCase("heartrate"))
			{
				if (heartrateExists)
					System.err.println("Output of type HEARTRATE already exists!");
				else
					heartrateExists = true;
			}
			if (outputNode.getNodeName().equalsIgnoreCase("timerange"))
			{
				if (timerangeExists)
					System.err.println("Output of type TIMERANGE already exists!");
				else
					timerangeExists = true;
			}
			
			
			algorithm.addOutputEntry(new AlgorithmEntry( outputNode.getAttribute("name"), AlgorithmEntry.EntryTypeEnum.toEntryTypeEnum(outputNode.getNodeName()), AlgorithmEntry.ContentClass.toContentClass(outputNode.getAttribute("contentClass"))));
		}
	}
	

	public List<Algorithm> getAvailableAlgorithms(){
		return availableAlgorithms;
	}
	
	public void activateAlgorithm(String name) {
		for(int i = 0; i < availableAlgorithms.size(); i++) {
			if (availableAlgorithms.get(i).getName().equals(name)) {
				activateAlgorithm(i);
			}
		}
	}
	public void activateAlgorithm(int index) {
		activeAlgorithm = availableAlgorithms.get(index);
	}
	
	public Algorithm getActiveAlgorithm() {
		return activeAlgorithm;
	}
}
