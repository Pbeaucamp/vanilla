package bpm.vanilla.repository.services.parsers;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import bpm.vanilla.platform.core.repository.Parameter;

public class FdParser extends AbstractGeneralParser{

	private int dictionnaryId;
	
	public FdParser(String xmlModel) throws Exception {
		super(xmlModel);
		
	}

	@Override
	protected void parseDependancies() {
		Element root  = document.getRootElement();
		Element dependancies = root.element("dependancies");
		
		if (dependancies != null){
			
			for(Object o : dependancies.elements("dependantDirectoryItemId")){
				try{
					Integer i = Integer.parseInt(((Element)o).getStringValue());
					dependanciesId.add(i);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		
		//XXX ugly but don't know how to do it
		dictionnaryId = dependanciesId.get(0);
		
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
		//only FdForms have an output
		Element formOutputs = document.getRootElement().element("formsOutputs");
		if ( formOutputs == null){
			return;
		}
		
		for(Element e : (List<Element>)formOutputs.elements("output")){
			Parameter p  = new Parameter();
			p.setName(e.getText());
			p.setInstanceName(e.getText());
			
			addParameters(p);
		}
	
	}
	
	public int getDictionaryId() {
		return dictionnaryId;
	}
}
