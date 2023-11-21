package bpm.vanilla.repository.services.parsers;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.repository.Parameter;

public class KpiThemeParser implements IModelParser {
	
	protected List<Integer> dependanciesId = new ArrayList<Integer>();

	public KpiThemeParser(String xmlModel) throws Exception {
//		XStream xstream = new XStream();
//		model = (KpiTheme) xstream.fromXML(xmlModel);
	}

	@Override
	public List<Integer> getDependanciesDirectoryItemId() {
		return dependanciesId;
	}

	@Override
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>();
	}

	@Override
	public String overrideXml(Object object) {
		return "";
	}

	@Override
	public List<Integer> getDataSourcesReferences() {
		return new ArrayList<Integer>();
	}
}
