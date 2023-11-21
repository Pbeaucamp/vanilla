package bpm.vanilla.repository.services.parsers;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.ReportsGroup;

import com.thoughtworks.xstream.XStream;

public class ReportsGroupParser implements IModelParser {
	
	private ReportsGroup model;
	protected List<Integer> dependanciesId = new ArrayList<Integer>();

	public ReportsGroupParser(String xmlModel) throws Exception {
		XStream xstream = new XStream();
		
		model = (ReportsGroup) xstream.fromXML(xmlModel);

		parseDependancies();
	}

	private void parseDependancies() {
		List<Integer> reports = model.getReports();
		if(reports != null) {
			for(Integer report : reports) {
				dependanciesId.add(report);
			}
		}
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
