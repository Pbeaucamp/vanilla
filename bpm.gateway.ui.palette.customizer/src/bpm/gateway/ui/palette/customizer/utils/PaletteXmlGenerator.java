package bpm.gateway.ui.palette.customizer.utils;

import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class PaletteXmlGenerator {

	public static Document generateXmlDocument(String paletteName, HashMap<String, List<PaletteEntry>> content){
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("gatewayPalette");
		root.addAttribute("name", paletteName);
		for(String s :content.keySet()){
			Element gr = root.addElement("groups");
			gr.addAttribute("name", s);
			
			for(PaletteEntry e : content.get(s)){
				Element entry = gr.addElement("transformations");
				entry.addAttribute("label", e.getEntryName());
				entry.addAttribute("class", e.getTransformationClass().getName());
				entry.setText(e.getEntryDescription());;
			}
		}
		
		return doc;
	}
}
