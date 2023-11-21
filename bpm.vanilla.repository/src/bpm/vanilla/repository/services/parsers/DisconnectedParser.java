package bpm.vanilla.repository.services.parsers;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

public class DisconnectedParser extends AbstractGeneralParser{
	
	public DisconnectedParser(String xmlModel) throws Exception {
		super(xmlModel);
		
	}

	@Override
	protected void parseDependancies() throws Exception{
		for(Element e : (List<Element>)document.getRootElement().elements("id_repo")){
			if (e.element("id_directory") != null){
				
				Element d = e.element("id_directory").element("id_directory_item");
				
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
		return getXmlModelDefinition();
	}
	
	@Override
	protected void parseParameters() throws Exception {
		
	}
}
