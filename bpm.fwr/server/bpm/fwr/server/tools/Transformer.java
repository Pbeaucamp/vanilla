package bpm.fwr.server.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.PageOptions;
import bpm.fwr.api.beans.Constants.Orientation;
import bpm.fwr.api.beans.components.GridComponent;
import bpm.fwr.api.beans.components.IReportComponent;
import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.JoinDataSet;
import bpm.fwr.api.beans.template.DefaultTemplate;
import bpm.fwr.server.security.FwrSession;

public class Transformer {

	public static FWRReport getPreviewDatasetReport(DataSet fwrDs, FwrSession session) {
		if (fwrDs.getName().equals("")) {
			fwrDs.setName("noName");
		}

		// create the report
		GridComponent rep = new GridComponent();

		// set styles to the report
		DefaultTemplate temp = new DefaultTemplate();
		rep.setDataCellsStyle(temp.getDataStyle());
		rep.setHeaderCellsStyle(temp.getHeaderStyle());
		rep.setOddRowsBackgroundColor(temp.getOddRowsBackgroundColor());

		// set dataset and coordonates for the report
		rep.setDataset(fwrDs);
		rep.setX(0);
		rep.setY(0);

		// add columns
		rep.setColumns(fwrDs.getColumns());

		FWRReport report = new FWRReport();
		report.addComponent(rep);
		report.setHeader(new PageOptions());
		report.setFooter(new PageOptions());
		report.getHeader().setText("");
		report.getFooter().setText("");

		report.setOrientation(Orientation.PORTRAIT);
		report.setOutput("html");

		HashMap<String, String> res = new HashMap<String, String>();
		res.put("left", "10");
		res.put("right", "10");
		res.put("top", "10");
		res.put("bottom", "10");
		report.setMargins(res);

		report.setPageSize(740 + "]" + (rep.getColumns().size() * 85));

		// set language and titles
		String selectedLanguage = "";
		for (String lang : fwrDs.getColumns().get(0).getTitles().keySet()) {
			selectedLanguage = lang;
			break;
		}
		report.setSelectedLanguage(selectedLanguage);
		report.getTitles().put(selectedLanguage, "Preview");
		report.getSubtitles().put(selectedLanguage, "Dataset : " + fwrDs.getName());

		return report;
	}

	public static FWRReport getPreviewJoinDatasetReport(JoinDataSet dataset, FwrSession session) {
		if (dataset.getName().equals("")) {
			dataset.setName("noName");
		}
		
		GridComponent rep = new GridComponent();

		// set styles to the report
		DefaultTemplate temp = new DefaultTemplate();
		rep.setDataCellsStyle(temp.getDataStyle());
		rep.setHeaderCellsStyle(temp.getHeaderStyle());
		rep.setOddRowsBackgroundColor(temp.getOddRowsBackgroundColor());

		// set dataset and coordonates for the report
		rep.setDataset(dataset);
		rep.setX(0);
		rep.setY(0);

		// add columns
		rep.setColumns(findPreviewJoinDsColumns(dataset));

		FWRReport report = new FWRReport();
		report.addComponent(rep);
		report.setHeader(new PageOptions());
		report.setFooter(new PageOptions());
		report.getHeader().setText("");
		report.getFooter().setText("");

		report.setOrientation(Orientation.PORTRAIT);
		report.setOutput("html");

		HashMap<String, String> res = new HashMap<String, String>();
		res.put("left", "10");
		res.put("right", "10");
		res.put("top", "10");
		res.put("bottom", "10");
		report.setMargins(res);

		report.setPageSize(740 + "]" + (rep.getColumns().size() * 85));
		
		String selectedLanguage = "";
		for(DataSet ds : dataset.getChilds()){
			for (String lang : ds.getColumns().get(0).getTitles().keySet()) {
				selectedLanguage = lang;
				break;
			}
		}

		// set language and titles
		report.setSelectedLanguage(selectedLanguage);
		report.getTitles().put(selectedLanguage, "Preview");
		report.getSubtitles().put(selectedLanguage, "Dataset : " + dataset.getName());

		return report;
	}

	private static List<Column> findPreviewJoinDsColumns(JoinDataSet dataset) {
		List<Column> cols = new ArrayList<Column>();

		for (DataSet ds : dataset.getChilds()) {
			cols.addAll(ds.getColumns());
		}

		return cols;
	}

	public static FWRReport getReportForComponentPreview(IReportComponent comp, String type, FwrSession session, String customSize) {
		DataSet dataset = comp.getDataset();
		String selectedLanguage = "";
		if(dataset instanceof JoinDataSet){
			for(DataSet childDataset : ((JoinDataSet) dataset).getChilds()){
				for (String lang : childDataset.getColumns().get(0).getTitles().keySet()) {
					selectedLanguage = lang;
					break;
				}
			}
		}
		else {
			for (String lang : dataset.getColumns().get(0).getTitles().keySet()) {
				selectedLanguage = lang;
				break;
			}
		}
		
		FWRReport rep = new FWRReport();
		rep.setSelectedLanguage(selectedLanguage);
		rep.setPreview(true);
		rep.addComponent(comp);

		// set page and template options
		rep.setHeader(new PageOptions());
		rep.setFooter(new PageOptions());
		rep.getHeader().setText("");
		rep.getFooter().setText("");

		rep.setOrientation(Orientation.PORTRAIT);
		rep.setOutput(type);

		HashMap<String, String> res = new HashMap<String, String>();
		res.put("left", "5");
		res.put("right", "5");
		res.put("top", "5");
		res.put("bottom", "5");
		rep.setMargins(res);

		if (customSize != null && !customSize.isEmpty()) {
			rep.setPageSize(customSize);
		}
		else {
			rep.setPageSize("A4");
		}

		return rep;
	}
}
