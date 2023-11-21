package bpm.vanilla.repository.services.parsers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dom4j.Element;

import bpm.vanilla.platform.core.repository.Parameter;

public class GTWParser extends AbstractGeneralParser{

	public GTWParser(String xmlModel) throws Exception {
		super(xmlModel);
	}

	@Override
	protected void parseDependancies() {
		Element root  = document.getRootElement();
		for(Object o : root.elements("fmdtInput")){
			
			if (((Element)o).element("repositoryItemId") != null){
				Integer i = Integer.parseInt(((Element)o).element("repositoryItemId").getStringValue());
				dependanciesId.add(i);
			}
		}
		
		for(Object o : root.elements("subTransformation")){
			
			if (((Element)o).element("definition") != null){
				try{
					Integer i = Integer.parseInt(((Element)o).element("repositoryItemId").getStringValue());
					dependanciesId.add(i);
				}catch(Exception ex){
					
				}
				
			}
		}
		
		for(Object o : root.elements("olapDimensionInput")){
			
			if (((Element)o).element("definition") != null){
				try{
					Integer i = Integer.parseInt(((Element)o).element("repositoryItemId").getStringValue());
					dependanciesId.add(i);
				}catch(Exception ex){
					
				}
				
			}
		}
		
		for(Object o : root.elements("olapInput")){
			
			if (((Element)o).element("definition") != null){
				try{
					Integer i = Integer.parseInt(((Element)o).element("repositoryItemId").getStringValue());
					dependanciesId.add(i);
				}catch(Exception ex){
					
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
		return getXmlModelDefinition();
	}

	@Override
	protected void parseParameters() throws Exception {
		Element e = document.getRootElement().element("parameters");
		
		if (e == null){
			return;
		}
		
		for(Element ep : (Collection<Element>)e.elements("parameter")){
			String pName = ep.element("name").getStringValue();
			String defaultValue = ep.element("defaultValue").getStringValue();
			
			Parameter p  = new Parameter();
			p.setName(pName);
			p.setInstanceName(pName);
			p.setDefaultValue(defaultValue);
			addParameters(p);
		}
		
	}
}
