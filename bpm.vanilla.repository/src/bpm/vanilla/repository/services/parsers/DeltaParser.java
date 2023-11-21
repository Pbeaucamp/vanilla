package bpm.vanilla.repository.services.parsers;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.repository.Parameter;

public class DeltaParser extends AbstractGeneralParser{
	
	public DeltaParser(String xmlModel) throws Exception {
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
	protected void parseDependancies() throws Exception {
		
	}
	@Override
	protected void parseParameters() throws Exception {
		
	}

}
