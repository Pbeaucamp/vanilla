package bpm.faweb.client.panels;

import java.util.HashMap;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.shared.OptionsExport;
import bpm.faweb.shared.OptionsExport.ExportType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SaveOptionsDialogBoxPanel extends Composite {
	private static final String A4 = "A4";
	private static final String LEGAL = "Legal";
	private static final String LETTER = "Letter";
	
	private static final String PDF = "PDF";
	private static final String HTML = "HTML";
	private static final String EXCEL = "Excel";
	private static final String EXCEL_DRILL_THROUGH = "ExcelDrillThrough";
	

	private static SaveOptionsDialogBoxPanelUiBinder uiBinder = GWT
			.create(SaveOptionsDialogBoxPanelUiBinder.class);

	interface SaveOptionsDialogBoxPanelUiBinder extends
			UiBinder<Widget, SaveOptionsDialogBoxPanel> {
	}

	@UiField
	Label lblReportName, lblSelectOutputType, lblWidth, lblHeight, lblLeft, lblTop, lblRight, lblBottom, lblReportTitle;
	
	@UiField
	Label lblReportDescription;
	
	@UiField
	TextBox txtReportName, txtWidth, txtHeight, txtLeft, txtTop, txtRight, txtBottom, txtReportTitle;
	
	@UiField
	CaptionPanel captionReportName, captionOutput, captionOrientation, captionPageSize, captionMargins, captionReportTitleDescription;
	
	@UiField
	ListBox listBoxOutputType, listBoxPageSize;
	
	@UiField
	RadioButton radioBtnPortrait, radioBtnLandscape, radioBtnStandard, radioBtnCustom;
	
	@UiField
	CheckBox checkBoxChart;
	
	@UiField
	TextArea txtAreaDescription;

	public SaveOptionsDialogBoxPanel(boolean isDrillThroughExport) {
		initWidget(uiBinder.createAndBindUi(this));
		
		//ReportName Part
		captionReportName.setCaptionText(FreeAnalysisWeb.LBL.Name());
		lblReportName.setText(FreeAnalysisWeb.LBL.ReportName());
		
		captionReportTitleDescription.setCaptionText(FreeAnalysisWeb.LBL.ReportTitleDescription());
		lblReportTitle.setText(FreeAnalysisWeb.LBL.ReportTitle());
		lblReportDescription.setText(FreeAnalysisWeb.LBL.ReportDescription());
		
		//Output Type Part
		captionOutput.setCaptionText(FreeAnalysisWeb.LBL.OutputType());
		lblSelectOutputType.setText(FreeAnalysisWeb.LBL.OutputType());
		
		if(!isDrillThroughExport){
			captionOutput.setVisible(true);
			
			listBoxOutputType.addItem(PDF, PDF);
			listBoxOutputType.addItem(HTML, HTML);
			listBoxOutputType.addItem(EXCEL, EXCEL);
			listBoxOutputType.addChangeHandler(outputChangeHandler);
		}
		else {
			listBoxOutputType.addItem(EXCEL_DRILL_THROUGH, EXCEL_DRILL_THROUGH);
			
			captionOutput.setVisible(false);
		}
		
		checkBoxChart.setText(FreeAnalysisWeb.LBL.ExportChart());
		checkBoxChart.setValue(false);
		
		//Orientation Part
		captionOrientation.setCaptionText("Orientation");
		
		radioBtnPortrait.setText(FreeAnalysisWeb.LBL.Portrait());
		radioBtnLandscape.setText(FreeAnalysisWeb.LBL.Landscape());
		
		//PageSize Part
		captionPageSize.setCaptionText(FreeAnalysisWeb.LBL.Pagesize());
		
		radioBtnStandard.setText(FreeAnalysisWeb.LBL.Standard());
		radioBtnStandard.addClickHandler(btnClickHandler);
		listBoxPageSize.addItem(A4, A4);
		listBoxPageSize.addItem(LEGAL, LEGAL);
		listBoxPageSize.addItem(LETTER, LETTER);
		
		radioBtnCustom.setText(FreeAnalysisWeb.LBL.Custom());
		radioBtnCustom.addClickHandler(btnClickHandler);
		lblWidth.setText(FreeAnalysisWeb.LBL.Width());
		lblHeight.setText(FreeAnalysisWeb.LBL.Height());
		
		//Margins Part
		captionMargins.setCaptionText(FreeAnalysisWeb.LBL.Margins());
		
		lblLeft.setText(FreeAnalysisWeb.LBL.LEFT());
		lblTop.setText(FreeAnalysisWeb.LBL.Top());
		lblRight.setText(FreeAnalysisWeb.LBL.RIGHT());
		lblBottom.setText(FreeAnalysisWeb.LBL.Bottom());
		
		updatePageSizePart();
	}
	
	private void updatePageSizePart(){
		if(radioBtnStandard.getValue()){
			listBoxPageSize.setEnabled(true);
			
			txtWidth.setEnabled(false);
			txtHeight.setEnabled(false);
		}
		else {
			listBoxPageSize.setEnabled(false);
			
			txtWidth.setEnabled(true);
			txtHeight.setEnabled(true);
		}
	}
	
	public OptionsExport buildOptionsExport(){
		String reportName = txtReportName.getText();
		String exportType = listBoxOutputType.getValue(listBoxOutputType.getSelectedIndex());
		String reportTitle = txtReportTitle.getText();
		String reportDescription = txtAreaDescription.getText();
		
		ExportType type = ExportType.PDF;
		if(exportType.equals(HTML)){
			type = ExportType.HTML;
		}
		else if(exportType.equals(EXCEL)){
			type = ExportType.XLS;
		}
		else if(exportType.equals(EXCEL_DRILL_THROUGH)){
			type = ExportType.XLS_DRILL_THROUGH;
		}
		
		String pageSize = "";
		if(radioBtnCustom.getValue()){
			String width = txtWidth.getText();
			String height = txtHeight.getText();
			
			pageSize = height + "]" + width;
		}
		else {
			pageSize = listBoxPageSize.getValue(listBoxPageSize.getSelectedIndex());
		}
		
		String orientation = "portrait";
		if(radioBtnLandscape.getValue()){
			orientation = "landscape";
		}

		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put("pagesize", pageSize);
		options.put("orientation", orientation);
		options.put("margins", getMargins());
		options.put("title", reportTitle);
		options.put("description", reportDescription);
		
		OptionsExport opt = new OptionsExport();
		opt.setReportName(reportName);
		opt.setExportType(type);
		opt.setOptions(options);
		opt.setExportChart(checkBoxChart.getValue());
		
		return opt;
	}

	private HashMap<String, String> getMargins(){
		String marginLeft = txtLeft.getText();
		String marginRight = txtRight.getText();
		String marginTop = txtTop.getText();
		String marginBottom = txtBottom.getText();
		
		HashMap<String, String>  res = new HashMap<String, String> ();
		res.put("left", marginLeft.equals("") ? "0" : marginLeft);
		res.put("right", marginRight.equals("") ? "0" : marginRight);
		res.put("top", marginTop.equals("") ? "0" : marginTop);
		res.put("bottom", marginBottom.equals("") ? "0" : marginBottom);
		 
		return res; 
	}
	
	private void updateOutputTypePart(String exportType){
		if(exportType.equals(PDF) || exportType.equals(HTML)){
			captionReportTitleDescription.setVisible(true);
			checkBoxChart.setVisible(true);
		}
		else {
			captionReportTitleDescription.setVisible(false);
			checkBoxChart.setVisible(false);
		}
	}
	
	private ChangeHandler outputChangeHandler = new ChangeHandler() {
		
		@Override
		public void onChange(ChangeEvent event) {
			String exportType = listBoxOutputType.getValue(listBoxOutputType.getSelectedIndex());
			updateOutputTypePart(exportType);
		}
	};

	private ClickHandler btnClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event){
			if(event.getSource().equals(radioBtnStandard) || event.getSource().equals(radioBtnCustom)){
				updatePageSizePart();
			}
		}
	};
}
