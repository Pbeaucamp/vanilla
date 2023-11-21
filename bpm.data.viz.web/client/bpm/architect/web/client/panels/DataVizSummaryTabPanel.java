package bpm.architect.web.client.panels;

import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DataVizSummaryTabPanel extends Composite {
	
	private static final int RIGHT_OPEN = 0;
	private static final int RIGHT_CLOSE = -826;

	private static DataVizSummaryTabPanelUiBinder uiBinder = GWT.create(DataVizSummaryTabPanelUiBinder.class);

	interface DataVizSummaryTabPanelUiBinder extends UiBinder<Widget, DataVizSummaryTabPanel> {}

	interface MyStyle extends CssResource {
		String btnSelected();
	}
	
	@UiField
	MyStyle style;

	@UiField
	Label lblScripts;
	
	@UiField
	ListBoxWithButton<DataColumn> lstColumn;
	
	@UiField
	FocusPanel btnValue, btnChart;
	
	@UiField
	HTMLPanel mainPanel;

	@UiField
	SimplePanel panelContent;

	private DataVizChartPanel chartPanel;
	private DataVizValueInfoPanel valueInfoPanel;
	
	private boolean isOpen = false;
	private int rightClose;
	private DataVizDesignPanel parent;
	
	private Object lastClicked;

	public DataVizSummaryTabPanel(DataVizDesignPanel parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rightClose = RIGHT_CLOSE - 20;
		this.parent = parent;
		mainPanel.getElement().getStyle().setRight(rightClose, Unit.PX);
		
		if (parent.getDataPreparation().getDataset() != null && parent.getDataPreparation().getDataset().getMetacolumns() != null) {
			lstColumn.setList(parent.getDataPreparation().getDataset().getMetacolumns(), false);
		}
		
//		this.chartPanel = new DataVizChartPanel();
//		this.valueInfoPanel = new DataVizValueInfoPanel();
		
		panelContent.setWidget(chartPanel);
	}
	
	@UiHandler("lstColumn")
	public void onColumnSelection(ChangeEvent event) {
		if(lastClicked == chartPanel) {
			onChartClick(null);
		}
		else {
			onValueClick(null);
		}
	}

	@UiHandler("lblScripts")
	public void onScriptsClick(ClickEvent event) {
		updateUi();
	}

	@UiHandler("btnChart")
	public void onChartClick(ClickEvent event) {
		if(chartPanel == null) {
			chartPanel = new DataVizChartPanel(parent.getDataPanel().getLastResult(), lstColumn.getSelectedObject());
		}
		else {
			chartPanel.generateChart(lstColumn.getSelectedObject());
		}
		panelContent.setWidget(chartPanel);
		btnChart.addStyleName(style.btnSelected());
		btnValue.removeStyleName(style.btnSelected());
		
		lastClicked = chartPanel;
	}

	@UiHandler("btnValue")
	public void onValueClick(ClickEvent event) {
		if(valueInfoPanel == null) {
			valueInfoPanel = new DataVizValueInfoPanel(parent.getDataPanel().getLastResult(), lstColumn.getSelectedObject());
		}
		else {
			valueInfoPanel.refreshValueInfo(lstColumn.getSelectedObject());
		}
		panelContent.setWidget(valueInfoPanel);
		btnChart.removeStyleName(style.btnSelected());
		btnValue.addStyleName(style.btnSelected());
		
		lastClicked = valueInfoPanel;
	}

	private void updateUi() {
		if (isOpen) {
			lblScripts.setVisible(true);
			mainPanel.getElement().getStyle().setRight(rightClose, Unit.PX);
		}
		else {
			lblScripts.setVisible(false);
			mainPanel.getElement().getStyle().setRight(RIGHT_OPEN, Unit.PX);
		}
		this.isOpen = !isOpen;
	}

	@UiHandler("btnHide")
	public void onCancelClick(ClickEvent event) {
		updateUi();
	}
	
	public DataVizChartPanel getChartPanel() {
		return chartPanel;
	}
	
	public DataVizValueInfoPanel getValueInfoPanel() {
		return valueInfoPanel;
	}

}
