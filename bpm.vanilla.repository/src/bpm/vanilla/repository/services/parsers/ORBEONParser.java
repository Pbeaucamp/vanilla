package bpm.vanilla.repository.services.parsers;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import bpm.vanilla.platform.core.repository.Parameter;

public class ORBEONParser extends AbstractGeneralParser{
	
	public ORBEONParser(String xmlModel) throws Exception {
		super(xmlModel);
	}
	
	
	
	@Override
	public List<Integer> getDataSourcesReferences() {
		return new ArrayList<Integer>();
	}

	@Override
	public List<Integer> getDependanciesDirectoryItemId() {
		return dependanciesId;
	}
	

	@Override
	public String overrideXml(Object object) throws Exception {
		return getXmlModelDefinition();
	}

	@Override
	public void parseDependancies() throws Exception {
		
	}
	
	@Override
	public void parseParameters() throws Exception {
		List<Element> lst = new ArrayList<Element>();
		Element xmlElement = document.getRootElement();
		  
		lst.addAll(xmlElement.selectNodes("//xforms:input[@ref]"));
		lst.addAll(xmlElement.selectNodes("//xforms:select1[@ref]"));
		lst.addAll(xmlElement.selectNodes("//xforms:select[@ref]"));
		lst.addAll(xmlElement.selectNodes("//xforms:secret[@ref]"));
		lst.addAll(xmlElement.selectNodes("//xforms:textarea[@ref]"));
		lst.addAll(xmlElement.selectNodes("//xforms:range[@ref]"));
		
		
		for(Element e : lst){
			String s = e.attribute("ref").getText();
			 
			Parameter param = new Parameter();
			param.setName(s);
			param.setInstanceName(e.attributeValue("ref"));
			addParameters(param);
		}
	}
}
