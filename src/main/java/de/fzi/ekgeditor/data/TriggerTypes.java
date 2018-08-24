package de.fzi.ekgeditor.data;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TriggerTypes{
	private List<TriggerType> triggerTypes = new ArrayList<TriggerType>();
	public TriggerTypes(){
		try {
			Properties properties = new Properties();
			properties.loadFromXML(new FileInputStream(new File("triggers.xml")));
			for(Map.Entry<Object, Object> entry : properties.entrySet()){
				triggerTypes.add(new TriggerType((String)entry.getKey(), (String)entry.getValue()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public List<TriggerType> getTriggerTypes() {
		return triggerTypes;
	}
}
