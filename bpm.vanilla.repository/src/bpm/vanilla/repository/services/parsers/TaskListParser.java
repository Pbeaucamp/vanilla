package bpm.vanilla.repository.services.parsers;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

public class TaskListParser extends AbstractGeneralParser{
	public TaskListParser(String xmlModel) throws Exception {
		super(xmlModel);
	}
	
	@Override
	public List<Integer> getDataSourcesReferences() {
		return  new ArrayList<Integer>();
	}

	@Override
	public void parseDependancies() throws Exception{
		for(Element e : (List<Element>)document.getRootElement().elements("task")){
			for(Element d : (List<Element>)e.elements("property")){				
				if (d != null && d.attributeValue("name").equals("directoryItemId")){
					dependanciesId.add(Integer.parseInt(d.getStringValue()));
				}
			}
		}
	}

	@Override
	public String overrideXml(Object object) throws Exception {
		return getXmlModelDefinition();
	}
	@Override
	public void parseParameters() throws Exception {
		
	}

}
