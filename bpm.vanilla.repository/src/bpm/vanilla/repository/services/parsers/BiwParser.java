package bpm.vanilla.repository.services.parsers;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.vanilla.platform.core.repository.Parameter;


public class BiwParser extends AbstractGeneralParser{
	public BiwParser(String xmlModel) throws Exception {
		super(xmlModel);
		
	}

	@Override
	protected void parseDependancies() throws Exception{
		for(Element e : (List<Element>)document.getRootElement().elements("BIWActivity")){
			if (e.element("BIWObject") != null){
				
				Element d = e.element("BIWObject").element("id");
				
				if (d != null){
					dependanciesId.add(Integer.parseInt(d.getStringValue()));
				}
				
			}
		}
		for(Element e : (List<Element>)document.getRootElement().elements("gatewayActivity")){
			if (e.element("gatewayObject") != null){
				
				Element d = e.element("gatewayObject").element("id");
				
				if (d != null){
					dependanciesId.add(Integer.parseInt(d.getStringValue()));
				}
				
			}
		}
		for(Element e : (List<Element>)document.getRootElement().elements("reportActivity")){
			if (e.element("birtObject") != null){
				
				Element d = e.element("birtObject").element("id");
				
				if (d != null){
					dependanciesId.add(Integer.parseInt(d.getStringValue()));
				}
				
			}
		}
		for(Element e : (List<Element>)document.getRootElement().elements("burstActivity")){
			if (e.element("birtObject") != null){
				
				Element d = e.element("birtObject").element("id");
				
				if (d != null){
					dependanciesId.add(Integer.parseInt(d.getStringValue()));
				}
				
			}
		}
		for(Element e : (List<Element>)document.getRootElement().elements("taskListActivity")){
			if (e.element("biRepositoryObject") != null){
				
				Element d = e.element("biRepositoryObject").element("id");
				
				if (d != null){
					dependanciesId.add(Integer.parseInt(d.getStringValue()));
				}
				
			}
		}
		for(Element e : (List<Element>)document.getRootElement().elements("interfaceActivity")){
			if (e.element("interfaceObject") != null){
				
				Element d = e.element("interfaceObject").element("id");
				
				if (d != null){
					dependanciesId.add(Integer.parseInt(d.getStringValue()));
				}
				
			}
		}
	}
	 
	@Override
	public List<Integer> getDataSourcesReferences() {
		return new ArrayList<Integer>();
	}

	@Override
	public String overrideXml(Object object) throws Exception {
		HashMap<String, String> p = (HashMap<String, String>)object;
		

		
		for(String k : p.keySet()){
			boolean found = false;
			for(Object o : document.selectNodes(k)){
				found = true;
				((Element)o).setText(p.get(k));
			}
			
			if (! found){
				int pos = k.lastIndexOf("/");
				Element n = (Element)document.selectSingleNode(k.substring(0, pos));
				n.addElement(k.substring(pos+1)).setText(p.get(k));
			}
		}
		
		try{
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			OutputFormat form = OutputFormat.createPrettyPrint();
			form.setTrimText(false);
			XMLWriter writer = new XMLWriter(os, form);
			writer.write(document.getRootElement());
			writer.close();
			
			return os.toString("UTF-8");
		}catch(Exception ex){
			
		}
		
		return document.getRootElement().asXML();
	}

	@Override
	protected void parseParameters() throws Exception {
		for(Element ep : (Collection<Element>)document.getRootElement().elements("workflowModelParameter")){
			String pName = ep.element("name").getStringValue();
			Parameter p  = new Parameter();
			p.setName(pName);
			p.setInstanceName(pName);
			p.setDefaultValue( ep.element("defaultValue").getStringValue());
			addParameters(p);
		}
		
	}
}
