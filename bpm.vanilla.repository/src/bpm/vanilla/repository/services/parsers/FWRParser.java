package bpm.vanilla.repository.services.parsers;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.components.BirtCommentComponents;
import bpm.fwr.api.beans.components.IReportComponent;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.DataSource;
import bpm.vanilla.platform.core.repository.Parameter;

import com.thoughtworks.xstream.XStream;

public class FWRParser implements IModelParser {
	
	private FWRReport report;

	private List<Parameter> parameters = new ArrayList<Parameter>();

//	private List<ReportCommentDefinition> commentsDef = new ArrayList<ReportCommentDefinition>();

	private int repId;

	protected List<Integer> dependanciesId = new ArrayList<Integer>();

	public FWRParser(String xmlModel) throws Exception {
		XStream xstream = new XStream();
		
		report = (FWRReport) xstream.fromXML(xmlModel);

		parseDocument();
		parseDependancies();
	}

	private void parseDocument() {
		if(report.getComponents() != null) {
			for(IReportComponent comp : report.getComponents()) {
				if(comp instanceof BirtCommentComponents) {
					BirtCommentComponents comment = (BirtCommentComponents)comp;
					
//					ReportCommentDefinition def = new ReportCommentDefinition();
//					def.setCommentItemId(comment.getName());
//					def.setName(comment.getTitle());
//					commentsDef.add(def);
				}
			}
		}

	}

	private void parseDependancies() {
		List<DataSet> datasets = report.getAllDatasets();
		if(datasets != null) {
			for(DataSet ds : datasets) {
				DataSource datasource = ds.getDatasource();
				if(datasource != null) {
					dependanciesId.add(datasource.getItemId());
				}
			}
		}
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
	public String overrideXml(Object object) {
		return "";
	}

//	public List<ReportCommentDefinition> getCommentDefinitions() {
//		return commentsDef;
//	}

	public void setRepId(int repId) {
		this.repId = repId;
	}

	public int getRepId() {
		return repId;
	}

	@Override
	public List<Integer> getDataSourcesReferences() {
		return new ArrayList<Integer>();
	}
}
