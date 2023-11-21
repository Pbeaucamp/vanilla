package bpm.vanilla.repository.services.parsers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dom4j.Element;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.repository.Parameter;

public class MarkdownTypeParser extends AbstractGeneralParser {
	
	protected List<Integer> dependanciesId = new ArrayList<Integer>();

	public MarkdownTypeParser(String xmlModel) throws Exception {
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
	public List<Parameter> getParameters() {
		return parameters;
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
		Element e = document.getRootElement().element("parameters");
		
		if (e == null){
			return;
		}
		
		for(Element ep : (Collection<Element>)e.elements("parameter")){

			XStream xstream = new XStream();
			xstream.alias("parameter", Parameter.class);
			Parameter p = (Parameter)xstream.fromXML(ep.asXML());


			addParameters(p);
		}
	}

}
