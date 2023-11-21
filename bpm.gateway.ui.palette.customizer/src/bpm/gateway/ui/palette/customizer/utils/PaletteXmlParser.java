package bpm.gateway.ui.palette.customizer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.Transformation;

public class PaletteXmlParser {
	
	private LinkedHashMap<String , List<PaletteEntry>> groups = new LinkedHashMap<String , List<PaletteEntry>>();
	
	private Document doc;
	private String paletteName;
	
	public PaletteXmlParser(InputStream paletteOrganizationStream) throws DocumentException, IOException{
		
		doc =  DocumentHelper.parseText(IOUtils.toString(paletteOrganizationStream, "UTF-8"));
		parse();
	}
	public LinkedHashMap<String ,  List<PaletteEntry>> getGroupsOrganization(){
		return groups;
	}
	private void parse(){
		Element root = doc.getRootElement();
		paletteName = root.attributeValue("name");
		for(Element gr : (List<Element>)root.elements("groups")){
			String groupName = gr.attributeValue("name");
			
			String key = null;
			for(String s : groups.keySet()){
				if (s.equals(groupName)){
					key = s;
				}
			}
			
			if (key == null){
				key = groupName;
				groups.put(key, new ArrayList<PaletteEntry>());
			}
			
			for(Element e : (List<Element>)gr.elements("transformations")){
				String trClassName = e.attributeValue("class");
				String trName = e.attributeValue("label");
				String trDesc = e.getStringValue();
				try {
					Class c = Class.forName(trClassName);
					Class<? extends Transformation> cl = c;
					
					PaletteEntry entry = new PaletteEntry(c, trName, trDesc); 
					
					groups.get(key).add(entry);
					
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
			}
		}
	}
	public String getPaletteName() {
		return paletteName;
	}
}
