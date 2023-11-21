package bpm.fwr.api;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.model.api.AutoTextHandle;
import org.eclipse.birt.report.model.api.CellHandle;
import org.eclipse.birt.report.model.api.DesignConfig;
import org.eclipse.birt.report.model.api.DesignEngine;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.GridHandle;
import org.eclipse.birt.report.model.api.JointDataSetHandle;
import org.eclipse.birt.report.model.api.OdaDataSetHandle;
import org.eclipse.birt.report.model.api.OdaDataSourceHandle;
import org.eclipse.birt.report.model.api.PropertyHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.RowHandle;
import org.eclipse.birt.report.model.api.SessionHandle;
import org.eclipse.birt.report.model.api.SimpleMasterPageHandle;
import org.eclipse.birt.report.model.api.StructureFactory;
import org.eclipse.birt.report.model.api.TextDataHandle;
import org.eclipse.birt.report.model.api.TextItemHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.elements.structures.JoinCondition;
import org.eclipse.birt.report.model.elements.interfaces.IJointDataSetModel;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.WysiwygReportHeader;
import bpm.fwr.api.beans.Constants.Colors;
import bpm.fwr.api.beans.Constants.Orientation;
import bpm.fwr.api.beans.Constants.Variables;
import bpm.fwr.api.beans.components.IReportComponent;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.DataSource;
import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.fwr.api.beans.dataset.IResource;
import bpm.fwr.api.beans.dataset.JoinDataSet;
import bpm.fwr.api.beans.template.Style;
import bpm.fwr.api.birt.ComponentHelper;
import bpm.fwr.api.birt.DatasHelper;
import bpm.vanilla.platform.core.IRepositoryApi;

import com.ibm.icu.util.ULocale;

public class FwrReportManager {

	private IReportEngine reportEngine;
	private FWRReport report;
	private String selectedLanguage;

	public FwrReportManager(IReportEngine reportEngine, FWRReport report) throws Exception {
		this.reportEngine = reportEngine;
		this.report = report;
		this.selectedLanguage = report.getSelectedLanguage();
	}
	
	public IReportRunnable createReport(IRepositoryApi sock, HashMap<String, String> alternateConnections, String outputFormat) throws Exception {
		ReportDesignHandle designHandle = buildReport(sock, alternateConnections, outputFormat);
//		designHandle.saveAs("C:/BPM/test/test.rptdesign");
		return reportEngine.openReportDesign(designHandle);
	}

	public InputStream buildRPTDesignFromFWR(IRepositoryApi sock) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ReportDesignHandle reportDesignHandle = buildReport(sock, new HashMap<String, String>(), "html");
		reportDesignHandle.serialize(outputStream);

		String reportXml = outputStream.toString("UTF-8");
		return IOUtils.toInputStream(reportXml, "UTF-8");
	}

	private ReportDesignHandle buildReport(IRepositoryApi sock, HashMap<String, String> alternateConnections, String outputFormat) throws Exception {
		DesignConfig config = new DesignConfig();
		SessionHandle session = new DesignEngine(config).newSessionHandle((ULocale) null);

		ReportDesignHandle designHandle = null;
		if (report.getTemplatePath() != null && !report.getTemplatePath().equals("")) {
			designHandle = session.createDesignFromTemplate(report.getTemplatePath());
		}
		else {
			designHandle = (ReportDesignHandle) session.createDesign();
		}
		
		ElementFactory designFactory = designHandle.getElementFactory();

		buildDatasetsAndDatasources(designHandle, designFactory, report.getAllDatasets(), sock, alternateConnections, report.isPreview());

		// Apply common properties (size, orientation...), Manage Header and Footer and Title/Subtitles
		SimpleMasterPageHandle properties = applyProperties(designFactory);
		manageHeaderFooter(designHandle, designFactory, properties, sock.getContext().getGroup().getName());
		manageTitleSubtitle(designHandle, designFactory);
		designHandle.getMasterPages().add(properties);

		if (report.getComponents() != null) {
			HashMap<Integer, Integer> nbColsPerRow = new HashMap<Integer, Integer>();
			for (IReportComponent comp : report.getComponents()) {
				if (nbColsPerRow.get(comp.getY() + 1) == null || nbColsPerRow.get(comp.getY() + 1) < comp.getX() + 1) {
					nbColsPerRow.put(comp.getY() + 1, comp.getX() + 1);
				}
			}

			HashMap<Integer, GridHandle> grids = new HashMap<Integer, GridHandle>();
			
			for (Integer key : nbColsPerRow.keySet()) {
				GridHandle gr = designFactory.newGridItem("GridContainer", nbColsPerRow.get(key), 1);
				grids.put(key, gr);
			}
			
			for (IReportComponent comp : report.getComponents()) {
				CellHandle cell = (CellHandle) ((RowHandle) grids.get(comp.getY() + 1).getRows().get(0)).getCells().get(comp.getX());
				cell.getContent().add(ComponentHelper.createComponent(designHandle, designFactory, selectedLanguage, comp, outputFormat));
			}
			
			for (Integer key : grids.keySet()) {
				designHandle.getBody().add(grids.get(key));
			}
		}

		return designHandle;
	}

	private void buildDatasetsAndDatasources(ReportDesignHandle designHandle, ElementFactory designFactory, List<DataSet> datasets, IRepositoryApi sock, HashMap<String, String> alternateConnections, boolean isPreview) {
		List<DataSet> simpleDs = new ArrayList<DataSet>();
		List<DataSet> joinDs = new ArrayList<DataSet>();
		if (datasets != null) {
			for (DataSet ds : datasets) {
				if (ds instanceof JoinDataSet) {
					joinDs.add(ds);
				}
				else {
					simpleDs.add(ds);
				}
			}
		}
		for (DataSet ds : simpleDs) {
			buildDsDatasource(ds, designFactory, designHandle, sock.getContext().getGroup().getName(), alternateConnections);
			buildDataSet(ds, designFactory, designHandle, sock, isPreview);
		}
		for (DataSet ds : joinDs) {
			buildDataSet(ds, designFactory, designHandle, sock, isPreview);
		}
	}
	
	private void buildDsDatasource(DataSet ds, ElementFactory designFactory, ReportDesignHandle designHandle, String groupName, HashMap<String, String> alternateConnections) {
		DataSource datasource = ds.getDatasource();

		OdaDataSourceHandle dsHandle = null;

		if (datasource.isOnOlap()) {
			dsHandle = designFactory.newOdaDataSource(datasource.getName(), "bpm.metadata.birt.oda.runtime.olap");
		}
		else {
			dsHandle = designFactory.newOdaDataSource(datasource.getName(), "bpm.metadata.birt.oda.runtime");
		}
		try {
			dsHandle.setProperty("URL", datasource.getUrl());
			dsHandle.setProperty("USER", datasource.getUser());
			dsHandle.setProperty("PASSWORD", datasource.getPassword());
			dsHandle.setProperty("GROUP_NAME", groupName);
			dsHandle.setProperty("IS_ENCRYPTED", datasource.isEncrypted() + "");

			String connectionName = alternateConnections.get(ds.getDatasource().getName());
			if(connectionName == null) {
				connectionName = datasource.getConnectionName();
			}
			
			dsHandle.setPrivateDriverProperty("CONNECTION_NAME", connectionName != null ? connectionName : "Default");
			dsHandle.setProperty("DIRECTORY_ITEM_ID", datasource.getItemId() + "");
			dsHandle.setProperty("BUSINESS_MODEL", datasource.getBusinessModel());
			dsHandle.setProperty("BUSINESS_PACKAGE", datasource.getBusinessPackage());
			dsHandle.setProperty("REPOSITORY_ID", datasource.getRepositoryId() + "");
			designHandle.getDataSources().add(dsHandle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void buildDataSet(DataSet ds, ElementFactory designFactory, ReportDesignHandle designHandle, IRepositoryApi sock, boolean isPreview) {
		if (ds instanceof JoinDataSet) {
			
			JoinDataSet joinDs = (JoinDataSet)ds;
			
			// create the dataset
			JointDataSetHandle joinDsHandle = designFactory.newJointDataSet(ds.getName());

			try {
				joinDsHandle.addDataSet(joinDs.getLeftDatasetName());
				joinDsHandle.addDataSet(joinDs.getRightDatasetName());
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			// create the joincondition
			JoinCondition jc = StructureFactory.createJoinCondition();
			jc.setJoinType(joinDs.getType());
			jc.setLeftDataSet(joinDs.getLeftDatasetName());
			jc.setRightDataSet(joinDs.getRightDatasetName());
			jc.setOperator(joinDs.getOperator());
			jc.setLeftExpression(joinDs.getLeftExpression());
			jc.setRightExpression(joinDs.getRightExpression());
			// add the dataset to the report
			PropertyHandle prop = joinDsHandle.getPropertyHandle(IJointDataSetModel.JOIN_CONDITONS_PROP);
			try {
				prop.addItem(jc);

				if (joinDs.isPreview()) {
					joinDsHandle.setRowFetchLimit(100);
				}

				designHandle.getDataSets().add(joinDsHandle);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			// create the dataset

			OdaDataSetHandle datasetHandle = null;

			if (ds.getDatasource().isOnOlap()) {
				datasetHandle = designFactory.newOdaDataSet(ds.getName(), "bpm.metadata.birt.oda.runtime.olap.dataset");
			}
			else {
				datasetHandle = designFactory.newOdaDataSet(ds.getName(), "bpm.metadata.birt.oda.runtime.dataSet");
			}
			
			try {
				datasetHandle.setDataSource(ds.getDatasource().getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// create the query
			try {
				String query = DatasHelper.buildQuery(ds, sock, designHandle, datasetHandle, selectedLanguage, isPreview);
				datasetHandle.setQueryText(query);
				if (ds.isPreview()) {
					datasetHandle.setRowFetchLimit(100);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			// add the dataset to the report
			try {
				designHandle.getDataSets().add(datasetHandle);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private SimpleMasterPageHandle applyProperties(ElementFactory designFactory) throws SemanticException {
		SimpleMasterPageHandle properties = designFactory.newSimpleMasterPage("page" + new Object().hashCode());

		LinkedHashMap<String, String> property = new LinkedHashMap<String, String>();
		property.put("orientation", report.getOrientation() == Orientation.PORTRAIT ? "portrait" : "landscape");
		property.put("topMargin", report.getMarginTop("px") + "pt");
		property.put("leftMargin", report.getMarginLeft("px") + "pt");
		property.put("bottomMargin", report.getMarginBottom("px") + "pt");
		property.put("rightMargin", report.getMarginRight("px") + "pt");

		if (Character.isDigit(report.getPageSize().charAt(0))) {
			String[] v = report.getPageSize().split("]");
			property.put("type", "custom");
			property.put("height", v[0] + "pt");
			property.put("width", v[1] + "pt");
		}
		else {
			property.put("type", report.getPageSize());
		}
		properties.setProperties(property);
		return properties;
	}

	private void manageHeaderFooter(ReportDesignHandle designHandle, ElementFactory designFactory, SimpleMasterPageHandle properties, String groupName) throws Exception {
		if (report.getHeader() != null) {
			// add header text or/and page numbers
			if (report.getHeader().getText() != null || report.getHeader().isNumber() || report.getHeader().getTextRight() != null || report.getHeader().isNumberRight()) {

				GridHandle gr = designFactory.newGridItem("gridHead", 2, 1);
				RowHandle row = (RowHandle) gr.getRows().get(0);

				if (report.getHeader().isNumber()) {
					AutoTextHandle headtext = designFactory.newAutoText("headtextLeft");
					headtext.setAutoTextType(DesignChoiceConstants.AUTO_TEXT_PAGE_NUMBER);
					CellHandle cell = (CellHandle) row.getCells().get(0);
					cell.getContent().add(headtext);
				}
				if (report.getHeader().getText() != null && !report.getHeader().getText().equals("") && !report.getHeader().getText().equals("null")) {
					TextDataHandle headtext = designFactory.newTextData("headtextdataLeft");
					LinkedHashMap<String, String> propheader = new LinkedHashMap<String, String>();
					propheader.put("contentType", "html");
					String h = createHeaderFooterText(report.getHeader().getText(), groupName);
					propheader.put("valueExpr", "new String(\"" + h + "\")");
					headtext.setProperties(propheader);
					CellHandle cell = (CellHandle) row.getCells().get(0);
					cell.getContent().add(headtext);
				}

				if (report.getHeader().isNumberRight()) {
					AutoTextHandle headtext = designFactory.newAutoText("headtextNumberPageRight");
					headtext.setAutoTextType(DesignChoiceConstants.AUTO_TEXT_PAGE_NUMBER);
					CellHandle cell = (CellHandle) row.getCells().get(1);
					cell.getContent().add(headtext);
					cell.setProperty("textAlign", DesignChoiceConstants.TEXT_ALIGN_RIGHT);
				}
				if (report.getHeader().getTextRight() != null && !report.getHeader().getTextRight().equals("") && !report.getHeader().getTextRight().equals("null")) {
					TextDataHandle headtext = designFactory.newTextData("headtextdataRight");
					LinkedHashMap<String, String> propheader = new LinkedHashMap<String, String>();
					propheader.put("contentType", "html");
					String h = createHeaderFooterText(report.getHeader().getTextRight(), groupName);
					propheader.put("valueExpr", "new String(\"" + h + "\")");
					headtext.setProperties(propheader);
					CellHandle cell = (CellHandle) row.getCells().get(1);
					cell.getContent().add(headtext);
				}

				properties.getPageHeader().add(gr);
			}
		}

		if (report.getFooter() != null) {
			// add footer text or/and page numbers
			if (report.getFooter().getText() != null || report.getFooter().isNumber()) {

				GridHandle gr = designFactory.newGridItem("gridFoot", 2, 1);
				RowHandle row = (RowHandle) gr.getRows().get(0);

				if (report.getFooter().isNumber()) {
					AutoTextHandle foottext = designFactory.newAutoText("foottext");
					foottext.setAutoTextType(DesignChoiceConstants.AUTO_TEXT_PAGE_NUMBER);
					CellHandle cell = (CellHandle) row.getCells().get(0);
					cell.getContent().add(foottext);
				}
				if (report.getFooter().getText() != null && !report.getFooter().getText().equals("") && !report.getFooter().getText().equals("null")) {
					TextDataHandle foottext = designFactory.newTextData("foottextdata");
					LinkedHashMap<String, String> propfoot = new LinkedHashMap<String, String>();
					propfoot.put("contentType", "html");
					String f = createHeaderFooterText(report.getFooter().getText(), groupName);
					propfoot.put("valueExpr", "new String(\"" + f + "\")");
					foottext.setProperties(propfoot);
					CellHandle cell = (CellHandle) row.getCells().get(0);
					cell.getContent().add(foottext);
				}

				if (report.getFooter().isNumberRight()) {
					AutoTextHandle foottext = designFactory.newAutoText("foottextRight");
					foottext.setAutoTextType(DesignChoiceConstants.AUTO_TEXT_PAGE_NUMBER);
					CellHandle cell = (CellHandle) row.getCells().get(1);
					cell.getContent().add(foottext);
					cell.setProperty("textAlign", DesignChoiceConstants.TEXT_ALIGN_RIGHT);
				}
				if (report.getFooter().getTextRight() != null && !report.getFooter().getTextRight().equals("") && !report.getFooter().getTextRight().equals("null")) {
					TextDataHandle foottext = designFactory.newTextData("foottextdataRight");
					LinkedHashMap<String, String> propfoot = new LinkedHashMap<String, String>();
					propfoot.put("contentType", "html");
					String f = createHeaderFooterText(report.getFooter().getTextRight(), groupName);
					propfoot.put("valueExpr", "new String(\"" + f + "\")");
					foottext.setProperties(propfoot);
					CellHandle cell = (CellHandle) row.getCells().get(1);
					cell.getContent().add(foottext);
				}

				properties.getPageFooter().add(gr);
			}
		}

		// Add the report header with potentials images, and texts
		WysiwygReportHeader reportHeader = report.getWysiwygReportHeader();
		if (reportHeader != null) {
			GridHandle gr = null;
			RowHandle row = null;
			if (reportHeader.getImagesTopLeft() != null && reportHeader.getImagesTopRight() != null) {
				gr = designFactory.newGridItem("gridWysiwygHeader", 2, 1);
				row = (RowHandle) gr.getRows().get(0);

				CellHandle cellLeft = (CellHandle) row.getCells().get(0);
				CellHandle cellRight = (CellHandle) row.getCells().get(1);

				cellLeft.getContent().add(ComponentHelper.createComponent(designHandle, designFactory, selectedLanguage, reportHeader.getImagesTopLeft(), null));
				cellRight.getContent().add(ComponentHelper.createComponent(designHandle, designFactory, selectedLanguage, reportHeader.getImagesTopRight(), null));
				cellRight.setProperty("textAlign", DesignChoiceConstants.TEXT_ALIGN_RIGHT);
			}
			else if (reportHeader.getImagesTopLeft() == null && reportHeader.getImagesTopRight() == null) {
				// Do nothing because there is no image set to the header
			}
			else {
				gr = designFactory.newGridItem("gridWysiwygHeader", 1, 1);
				row = (RowHandle) gr.getRows().get(0);
				CellHandle cell = (CellHandle) row.getCells().get(0);

				if (reportHeader.getImagesTopLeft() != null) {
					cell.getContent().add(ComponentHelper.createComponent(designHandle, designFactory, selectedLanguage, reportHeader.getImagesTopLeft(), null));
				}
				else {
					cell.getContent().add(ComponentHelper.createComponent(designHandle, designFactory, selectedLanguage, reportHeader.getImagesTopRight(), null));
					cell.setProperty("textAlign", DesignChoiceConstants.TEXT_ALIGN_RIGHT);
				}
			}
			designHandle.getBody().add(gr);

			// We set the texts
			if (reportHeader.getLblTopLeft() != null && reportHeader.getLblTopRight() != null) {
				gr = designFactory.newGridItem("gridWysiwygHeaderText", 2, 1);
				row = (RowHandle) gr.getRows().get(0);

				CellHandle cellLeft = (CellHandle) row.getCells().get(0);
				CellHandle cellRight = (CellHandle) row.getCells().get(1);

				TextItemHandle leftText = designFactory.newTextItem("lefttextdata");
				leftText.setContentType("html");
				leftText.setContent(reportHeader.getLblTopLeft().getTextContent());

				TextItemHandle rightText = designFactory.newTextItem("righttextdata");
				rightText.setContentType("html");
				rightText.setContent(reportHeader.getLblTopRight().getTextContent());

				cellLeft.getContent().add(leftText);
				cellRight.getContent().add(rightText);
				cellRight.setProperty("textAlign", DesignChoiceConstants.TEXT_ALIGN_RIGHT);
			}
			else if (reportHeader.getLblTopLeft() == null && reportHeader.getLblTopRight() == null) {
				// Do nothing because there is no image set to the header
			}
			else {
				gr = designFactory.newGridItem("gridWysiwygHeaderText", 1, 1);
				row = (RowHandle) gr.getRows().get(0);
				CellHandle cell = (CellHandle) row.getCells().get(0);

				if (reportHeader.getLblTopLeft() != null) {
					LinkedHashMap<String, String> propfoot = new LinkedHashMap<String, String>();
					propfoot.put("contentType", "html");
					propfoot.put("valueExpr", "new String(\"" + reportHeader.getLblTopLeft() + "\")");

					TextDataHandle rightText = designFactory.newTextData("righttextdata");
					rightText.setProperties(propfoot);

					cell.getContent().add(rightText);
				}
				else {
					LinkedHashMap<String, String> propfoot = new LinkedHashMap<String, String>();
					propfoot.put("contentType", "html");
					propfoot.put("valueExpr", "new String(\"" + reportHeader.getLblTopRight() + "\")");

					TextDataHandle rightText = designFactory.newTextData("righttextdata");
					rightText.setProperties(propfoot);

					cell.getContent().add(rightText);
					cell.setProperty("textAlign", DesignChoiceConstants.TEXT_ALIGN_RIGHT);
				}
			}

			designHandle.getBody().add(gr);
		}
	}

	private void manageTitleSubtitle(ReportDesignHandle designHandle, ElementFactory designFactory) throws Exception {
		TextItemHandle title = null;
		if (!report.getTitles().isEmpty()) {
			String titleString = report.getTitle(selectedLanguage);
			if(titleString == null || titleString.isEmpty()) {
				for(String key : report.getTitles().keySet()) {
					titleString = report.getTitle(key);
				}
			}
			
			title = designFactory.newTextItem("title");
			title.setContentType("html");
			title.setContent(titleString);
			title.setWidth("100%");
		}

		// add the report subTitle with his style
		TextItemHandle subtitle = null;
		if (!report.getSubtitles().isEmpty()) {
			String subtitleString = report.getSubtitle(selectedLanguage);
			if(subtitleString == null || subtitleString.isEmpty()) {
				for(String key : report.getSubtitles().keySet()) {
					subtitleString = report.getSubtitle(key);
				}
			}
			
			subtitle = designFactory.newTextItem("subtitle");
			subtitle.setContentType("html");
			subtitle.setContent(subtitleString);
			subtitle.setWidth("100%");
		}

		designHandle.getBody().add(title);
		designHandle.getBody().add(subtitle);
	}

	private String createHeaderFooterText(String text, String groupName) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

		Properties properties = new Properties();

		// put common variables properties
		properties.put(Variables.VAR_DATE_VALUE, sdf.format(new Date()));
		properties.put(Variables.VAR_GROUP_VALUE, groupName);

		// put prompts properties
		List<DataSet> datasets = report.getAllDatasets();
		if (datasets != null) {
			for (DataSet dataset : datasets) {
				for (IResource resource : dataset.getPrompts()) {
					if(resource instanceof FwrPrompt) {
						FwrPrompt prompt = (FwrPrompt) resource;
						properties.put("${" + prompt.getName() + "}", prompt.getSelectedValuesToString(true));
					}
				}
			}
		}

		return Variables.getHeaderFooterText(text, properties);
	}

	public static HashMap<String, String> addBirtStyle(Style ts) {
		HashMap<String, String> prop = new HashMap<String, String>();
		prop.put("fontFamily", ts.getFontType());
		prop.put("fontSize", ts.getFontSize());
		if (ts.getTextColor() != null)
			prop.put("color", birtcolor(ts.getTextColor()));
		if (ts.getBackgroundColor() != null)
			prop.put("backgroundColor", birtcolor(ts.getBackgroundColor()));

		return prop;
	}

	public static String birtcolor(String color) {
		if (color.charAt(0) != '#') {
			String titlebg = Colors.colors.get(color);
			String[] rgb = titlebg.split(",");
			color = "#" + convertTOhex(Integer.parseInt(rgb[0])) + "" + convertTOhex(Integer.parseInt(rgb[1])) + "" + convertTOhex(Integer.parseInt(rgb[2]));
		}
		return color;
	}

	private static String convertTOhex(int V) {
		String col = Integer.toHexString(V);
		if (col.length() == 1) {
			col = "0" + col;
		}
		return col;
	}

	public static String birtExpression(String expression) {
		HashMap<String, String> expr = new HashMap<String, String>();
		expr.put("Average", "AVE");
		expr.put("Count", "COUNT");
		expr.put("Distinct Count", "COUNTDISTINCT");
		expr.put("First", "FIRST");
		expr.put("Max", "MAX");
		expr.put("Min", "MIN");
		expr.put("Standard Deviation", "STDDEV");
		expr.put("Average", "AVE");
		expr.put("Sum", "SUM");
		expr.put("Variance", "VARIANCE");

		return expr.get(expression);
	}

	public static String birtoperator(String entree) {
		HashMap<String, String> operation = new HashMap<String, String>();
		operation.put("=", "in");
		operation.put("<", "lt");
		operation.put("<=", "le");
		operation.put(">", "gt");
		operation.put(">=", "ge");
		operation.put("!=", "ne");
		operation.put("<>", "ne");
		return operation.get(entree);
	}
}
