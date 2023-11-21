package bpm.faweb.client.panels.center.chart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dnd.BinDragController;
import bpm.faweb.client.dnd.BinDropController;
import bpm.faweb.client.dnd.ChartListDropController;
import bpm.faweb.client.dnd.DraggableLabel;
import bpm.faweb.client.dnd.DraggableTreeItem;
import bpm.faweb.client.dnd.FaWebDragController;
import bpm.faweb.client.dnd.FilterDropController;
import bpm.faweb.client.panels.center.HasFilter;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.tree.FaWebTreeItem;
import bpm.faweb.client.utils.FaWebFilterHTML;
import bpm.faweb.shared.ChartParameters;
import bpm.faweb.shared.GroupChart;
import bpm.faweb.shared.SerieChart;
import bpm.faweb.shared.infoscube.GridCube;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.charts.ChartObject;
import bpm.gwt.commons.client.charts.ChartRenderPanel;
import bpm.gwt.commons.client.charts.ChartValue;
import bpm.gwt.commons.client.charts.ValueMultiSerie;
import bpm.gwt.commons.client.charts.ValueSimpleSerie;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.platform.core.beans.chart.ChartType;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ChartPanel extends CompositeWaitPanel implements HasFilter {

	public enum ListType {
		GROUPS, MEASURES, SERIES
	}
	
	private List<String> colors = Arrays.asList("0075c2", "1aaf5d", "f2c500", "f45b00", "8e0000", "0e948c", "8cbb2c", "f3de00", "c02d00", "5b0101");
	private static final int SIZE_FILTER_PROMPT_PANEL = 70;

	private static GraphPanelUiBinder uiBinder = GWT.create(GraphPanelUiBinder.class);

	interface GraphPanelUiBinder extends UiBinder<Widget, ChartPanel> {
	}
	
	interface MyStyle extends CssResource {
		String resourceHtml();
		String dropPanelHeight();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	AbsolutePanel rootPanel, rootFilterPanel, dropFilterPanel;
	
	@UiField
	HTMLPanel mainDropFilterPanel;

	@UiField
	LabelTextBox txtTitle;

	@UiField
	ListBoxWithButton<ChartType> lstChartTypes;

	@UiField
	AbsolutePanel panelMeasures, panelGroups, panelSeries;

	@UiField
	ChartRenderPanel panelChart;
	
	@UiField
	Label lblFilter;
	
	@UiField
	Image trash, trashFilter;

	private MainPanel mainPanel;

//	private FaWebDragController dragController;
	private BinDragController filterDragController;
	private BinDragController allBinDragController;

	private FilterDropController filterDC;
	private ChartListDropController measuresDC;
	private ChartListDropController groupsDC;
	private ChartListDropController seriesDC;

	private List<ChartType> simpleTypes;
	private List<ChartType> multiTypes;

	private List<FaWebFilterHTML> filters = new ArrayList<FaWebFilterHTML>();
	private List<String> groups = new ArrayList<String>();
	private List<String> series = new ArrayList<String>();
	private String measure;

	private boolean isOpenFilter;
	private boolean isMultiSeries;
	
	private ChartObject currentChart;
	private ChartType currentType;

	public ChartPanel(MainPanel mainPanel, FaWebDragController dragController) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
//		this.dragController = dragController;
		buildChartTypes();

		filterDC = new FilterDropController(mainPanel, this, dropFilterPanel, false);
		dragController.registerDropController(filterDC);

		measuresDC = new ChartListDropController(mainPanel, panelMeasures, this, ListType.MEASURES);
		dragController.registerDropController(measuresDC);

		groupsDC = new ChartListDropController(mainPanel, panelGroups, this, ListType.GROUPS);
		dragController.registerDropController(groupsDC);

		seriesDC = new ChartListDropController(mainPanel, panelSeries, this, ListType.SERIES);
		dragController.registerDropController(seriesDC);
		

		BinDropController binDC = new BinDropController(this, trashFilter, false);
		filterDragController = new BinDragController(rootFilterPanel, false);
		filterDragController.registerDropController(binDC);

		BinDropController allBinDC = new BinDropController(this, trash, true);
		allBinDragController = new BinDragController(rootPanel, false);
		allBinDragController.registerDropController(allBinDC);
		

		DOM.setStyleAttribute(rootFilterPanel.getElement(), "marginTop", -(SIZE_FILTER_PROMPT_PANEL + 1) + "px");
		DOM.setStyleAttribute(rootFilterPanel.getElement(), "marginBottom", "15px");
	}

	private void buildChartTypes() {
		this.simpleTypes = new ArrayList<ChartType>();
		String simpleType = ChartObject.TYPE_SIMPLE;
		for (String type : ChartObject.SIMPLE_RENDERERS) {
			ChartType chartType = null;

			if (type.equals(ChartObject.RENDERER_PIE)) {
				chartType = new ChartType(LabelsConstants.lblCnst.pieChart(), type, simpleType);
			}
			else if (type.equals(ChartObject.RENDERER_DONUT)) {
				chartType = new ChartType(LabelsConstants.lblCnst.donutChart(), type, simpleType);
			}
			else if (type.equals(ChartObject.RENDERER_BAR)) {
				chartType = new ChartType(LabelsConstants.lblCnst.barChart(), type, simpleType);
			}
			else if (type.equals(ChartObject.RENDERER_COLUMN)) {
				chartType = new ChartType(LabelsConstants.lblCnst.columnChart(), type, simpleType);
			}
			else if (type.equals(ChartObject.RENDERER_LINE)) {
				chartType = new ChartType(LabelsConstants.lblCnst.lineChart(), type, simpleType);
			}
			else if (type.equals(ChartObject.RENDERER_AREA)) {
				chartType = new ChartType(LabelsConstants.lblCnst.areaChart(), type, simpleType);
			}
			else if (type.equals(ChartObject.RENDERER_SPLINE)) {
				chartType = new ChartType(LabelsConstants.lblCnst.spline(), type, simpleType);
			}
			else if (type.equals(ChartObject.RENDERER_PARETO)) {
				chartType = new ChartType(LabelsConstants.lblCnst.pareto(), type, simpleType);
			}
			else if (type.equals(ChartObject.RENDERER_RADAR)) {
				chartType = new ChartType(LabelsConstants.lblCnst.radar(), type, simpleType);
			}

			if (chartType != null) {
				simpleTypes.add(chartType);
			}
		}

		this.multiTypes = new ArrayList<ChartType>();
		String multiType = ChartObject.TYPE_MULTI;
		for (String type : ChartObject.MULTI_RENDERERS) {
			ChartType chartType = null;

			if (type.equals(ChartObject.RENDERER_MULTI_BAR)) {
				chartType = new ChartType(LabelsConstants.lblCnst.barChart(), type, multiType);
			}
			else if (type.equals(ChartObject.RENDERER_MULTI_COLUMN)) {
				chartType = new ChartType(LabelsConstants.lblCnst.columnChart(), type, multiType);
			}
			else if (type.equals(ChartObject.RENDERER_MULTI_LINE)) {
				chartType = new ChartType(LabelsConstants.lblCnst.lineChart(), type, multiType);
			}
			else if (type.equals(ChartObject.RENDERER_MULTI_AREA)) {
				chartType = new ChartType(LabelsConstants.lblCnst.areaChart(), type, multiType);
			}
			else if (type.equals(ChartObject.RENDERER_MULTI_STACKEDCOLUMN)) {
				chartType = new ChartType(LabelsConstants.lblCnst.stackedColumns(), type, multiType);
			}
			else if (type.equals(ChartObject.RENDERER_MULTI_STACKEDAREA)) {
				chartType = new ChartType(LabelsConstants.lblCnst.stackedArea(), type, multiType);
			}
			else if (type.equals(ChartObject.RENDERER_MULTI_STEPLINE)) {
				chartType = new ChartType(LabelsConstants.lblCnst.steppedLine(), type, multiType);
			}
			else if (type.equals(ChartObject.RENDERER_RADAR)) {
				chartType = new ChartType(LabelsConstants.lblCnst.radar(), type, multiType);
			}

			if (chartType != null) {
				multiTypes.add(chartType);
			}
		}

		lstChartTypes.setList(simpleTypes);
	}

	private void updateTypes(boolean isMultiSeries) {
		if (this.isMultiSeries == isMultiSeries) {
			return;
		}

		this.isMultiSeries = isMultiSeries;
		if (isMultiSeries) {
			lstChartTypes.setList(multiTypes);
		}
		else {
			lstChartTypes.setList(simpleTypes);
		}
	}

	@UiHandler("lstChartTypes")
	public void onChartTypeChange(ChangeEvent event) {
		refresh();
	}
	
	@UiHandler("btnFilter")
	public void onFilter(ClickEvent event) {
		Animation slideAnimation = new Animation() {

			@Override
			protected void onUpdate(double progress) {
				if (!isOpenFilter) {
					int filterPanelHeight = mainDropFilterPanel.getOffsetHeight();

					int progressValue = (int) (-(filterPanelHeight) * (1 - progress)) - 2;
					DOM.setStyleAttribute(rootFilterPanel.getElement(), "marginTop", progressValue + "px");
				}
				else {
					int filterPanelHeight = mainDropFilterPanel.getOffsetHeight();

					int progressValue = (int) (-(filterPanelHeight + 3) * (progress));
					DOM.setStyleAttribute(rootFilterPanel.getElement(), "marginTop", progressValue + "px");
				}
			}

			@Override
			protected void onComplete() {
				isOpenFilter = !isOpenFilter;
				if (isOpenFilter) {
					lblFilter.setText(FreeAnalysisWeb.LBL.HideFilters());
				}
				else {
					lblFilter.setText(FreeAnalysisWeb.LBL.ShowFilters());
				}
			};
		};
		slideAnimation.run(1000);
	}

	public void openChart(String title, String type, String graphe, List<String> series, List<String> groups, List<String> filters, String measure) {
		for (String item : series) {
			addSerie(item);
		}

		for (String item : groups) {
			addGroup(item);
		}

		dropFilterPanel.removeStyleName(style.dropPanelHeight());
		for (String filter : filters) {
			FaWebFilterHTML filterItem = addFilter(filter);
			addFilter(filterItem);
		}

		setMeasure(measure);

		this.txtTitle.setText(title);
		boolean isMultiSeries = series != null && series.size() > 1;
		updateTypes(isMultiSeries);
		
		if (isMultiSeries) {
			for (ChartType chartType : multiTypes) {
				if (chartType.getValue().equalsIgnoreCase(graphe)) {
					lstChartTypes.setSelectedObject(chartType);
					break;
				}
			}
		}
		else {
			for (ChartType chartType : simpleTypes) {
				if (chartType.getValue().equalsIgnoreCase(graphe)) {
					lstChartTypes.setSelectedObject(chartType);
					break;
				}
			}
		}
		
		refresh();
	}
	
	private void addGroup(String groupe) {
		DraggableLabel lblGroup = new DraggableLabel(groupe, ListType.GROUPS);
		lblGroup.addStyleName("dropGroup");

		allBinDragController.makeDraggable(lblGroup);
		panelGroups.add(lblGroup);
		groups.add(groupe);
	}
	
	private void addSerie(String serie) {
		DraggableLabel lblSeries = new DraggableLabel(serie, ListType.SERIES);
		lblSeries.addStyleName("dropMeasure");

		allBinDragController.makeDraggable(lblSeries);
		panelSeries.add(lblSeries);
		series.add(serie);
	}
	
	private void setMeasure(String measure) {
		panelMeasures.clear();
		DraggableLabel lblMeasure = new DraggableLabel(measure, ListType.MEASURES);
		lblMeasure.addStyleName("dropDimension");

		allBinDragController.makeDraggable(lblMeasure);
		panelMeasures.add(lblMeasure);
		this.measure = measure;
	}

	@Override
	public void addFilterItem(String filter) {
		dropFilterPanel.removeStyleName(style.dropPanelHeight());

		FaWebFilterHTML filterItem = addFilter(filter);
		addFilter(filterItem);
		
		refresh();
	}
	
	private FaWebFilterHTML addFilter(String filter) {
		FaWebFilterHTML filterItem = new FaWebFilterHTML(filter);
		filterItem.addStyleName(style.resourceHtml());
		
		dropFilterPanel.add(filterItem);
		filterDragController.makeDraggable(filterItem);
		return filterItem;
	}

	public void removeFilter(FaWebFilterHTML filter) {
		this.filters.remove(filter);
		
		refresh();
	}

	public void manageWidget(DraggableTreeItem item, ListType type) {
		if (type == ListType.GROUPS) {
			if (!item.getUname().contains("[Measures]")) {
				boolean exist = false;
				for (String gr : groups) {
					if (item.getUname().equals(gr)) {
						exist = true;
						break;
					}
				}

				if (!exist) {
					addGroup(item.getUname());
					
					refresh();
				}
			}
		}
		else if (type == ListType.MEASURES) {
			if (item.getUname().contains("[Measures]")) {
				setMeasure(item.getUname());
				
				refresh();
			}
		}
		else if (type == ListType.SERIES) {
			if (!item.getUname().contains("[Measures]")) {
				boolean exist = false;
				for (String serie : series) {
					if (item.getUname().equals(serie)) {
						exist = true;
						break;
					}
				}

				if (!exist) {
					addSerie(item.getUname());

					refresh();
				}
			}
		}
	}

	private void removeDraggableLabel(DraggableLabel label) {
		if (label.getType() == ListType.GROUPS) {
			for (String gr : groups) {
				if (gr.equals(label.getLabel())) {
					groups.remove(gr);
					
					refresh();
					break;
				}
			}
		}
		else if (label.getType() == ListType.MEASURES) {
			measure = "";
			
			refresh();
		}
		else if (label.getType() == ListType.SERIES) {
			for (String serie : series) {
				if (serie.equals(label.getLabel())) {
					series.remove(serie);
					
					refresh();
					break;
				}
			}
		}
		label.removeFromParent();
	}

	public void clearChartData() {
		this.txtTitle.setText("");

		this.measure = "";
		this.panelMeasures.clear();

		this.groups.clear();
		this.panelGroups.clear();

		this.series.clear();
		this.panelSeries.clear();
	}

	public boolean isComplete() {
		return measure != null && !measure.isEmpty() && ((series != null && !series.isEmpty()) || (groups != null && !groups.isEmpty()));
	}

	// RENDER
	public void refresh() {
		if (isComplete()) {
			updateTypes(series != null && series.size() > 1);

			ChartType type = lstChartTypes.getSelectedObject();
			refreshChart(type);
		}
	}

	public ChartParameters getChartParameters() {
		String title = txtTitle.getText();
		ChartType type = lstChartTypes.getSelectedObject();

		List<String> filters = new ArrayList<String>();
		for (FaWebFilterHTML filterHTML : getFilters()) {
			filters.add(filterHTML.getFilter());
		}

		ChartParameters chartParam = new ChartParameters();
		chartParam.setChartTitle(title);
		chartParam.setChartType(type.getValue());
		chartParam.setDefaultRenderer(type.getSubType());
		chartParam.setMeasure(measure);
		chartParam.setSelectedChartFilters(filters);
		chartParam.setSelectedData(series);
		chartParam.setSelectedGroup(groups);

		return chartParam;
	}
	
	private void refreshChart(final ChartType type) {
		List<String> filters = new ArrayList<String>();
		for (FaWebFilterHTML filterHTML : getFilters()) {
			filters.add(filterHTML.getFilter());
		}
		FaWebService.Connect.getInstance().executeQuery(mainPanel.getKeySession(), this.groups, this.series, filters, this.measure, new GwtCallbackWrapper<List<GroupChart>>(this, true, true) {

			@Override
			public void onSuccess(List<GroupChart> groupsChart) {
				if (groupsChart != null && !groupsChart.isEmpty()) {
					buildChart(type, groupsChart);
				}
			}
		}.getAsyncCallback());
	}
	
	private void buildChart(ChartType type, List<GroupChart> groupsChart) {
		ChartObject chart = new ChartObject();
		chart.setTitle(txtTitle.getText());
		chart.setyAxisName(ChartFunctions.getMeasureName(groupsChart.get(0).getMeasure()));

		List<ChartValue> multiseries = new ArrayList<ChartValue>();
		
		int seriesSize = groupsChart.get(0).getSeries().size();
		if (seriesSize > 1) {
			int index = 0;
			for (GroupChart groupChart : groupsChart) {
				String color = colors.get(0);
				if (colors.size() > index) {
					color = colors.get(index);
				}
				index++;
				
				chart.addColor(color);
				
				ValueMultiSerie multiSerie = new ValueMultiSerie();
				multiSerie.setCategory(showCaption(groupChart.getUname()));
				multiSerie.setSerieName(showCaption(groupChart.getUname()));
				multiSerie.setSerieValues(getSeries(groupChart));
				multiseries.add(multiSerie);
			}
		}
		else {
			String color = colors.get(0);
			chart.addColor(color);
			
			for (GroupChart groupChart : groupsChart) {
				ValueSimpleSerie value = new ValueSimpleSerie();
				value.setCategory(showCaption(groupChart.getUname()));
				try {
					value.setValue(Double.parseDouble(groupChart.getSeries().get(0).getValue()));
				} catch (Exception e) {
				}
				multiseries.add(value);
			}

			chart.setxAxisName(ChartFunctions.getMemberName(groupsChart.get(0).getSeries().get(0).getName()));
		}

		chart.setValues(multiseries);

		// If PIE or DONUT, we set a palette of colors
		if (type.getValue().equals(ChartObject.RENDERER_PIE) || type.getValue().equals(ChartObject.RENDERER_DONUT)) {
			chart.setColors(colors);
		}

		// For now we set the YAxis to ""
		if (!type.getValue().equals(ChartObject.RENDERER_BAR)) {
			// chart.setxAxisName(axeXName);
			chart.setyAxisName("");
		}
		else {
			chart.setxAxisName("");
			// chart.setyAxisName(axeXName);
		}

		
		this.currentChart = chart;
		this.currentType = type;
		panelChart.loadChart(chart, type);
	}

	private List<ChartValue> getSeries(GroupChart groupChart) {
		List<ChartValue> values = new ArrayList<ChartValue>();
		for (SerieChart serie : groupChart.getSeries()) {
			ValueSimpleSerie value = new ValueSimpleSerie();
			value.setCategory(serie.getName());
			try {
				value.setValue(Double.parseDouble(serie.getValue()));
			} catch (Exception e) {
			}
			values.add(value);
		}
		return values;
	}

	private String showCaption(String text) {
		try {
			FaWebTreeItem it = MainPanel.getInstance().getNavigationPanel().getDimensionPanel().findRootItem2(text);
			return findCaption(it, text);
		} catch (Exception e) {
			return text;
		}
	}

	private String findCaption(FaWebTreeItem it, String text) {
		for (int i = 0; i < it.getChildCount(); i++) {
			FaWebTreeItem child = (FaWebTreeItem) it.getChild(i);
			if (child.getItemDim().getUname().equals(text)) {

				String[] f = text.split("\\.");
				f[f.length - 1] = "[" + child.getItemDim().getName() + "]";
				String newval = "";
				boolean first = true;
				for (String s : f) {
					if (first) {
						first = false;
					}
					else {
						newval += ".";
					}
					newval += s;
				}
				return newval;
			}
			findCaption(child, text);
		}
		return text;
	}

	public String getImageChart() {
		return panelChart.getImageData();
	}

	@Override
	public List<FaWebFilterHTML> getFilters() {
		return filters;
	}
	
	public void addFilter(FaWebFilterHTML filter) {
		this.filters.add(filter);
	}

	@Override
	public boolean charging() {
		return false;
	}

	@Override
	public void trashFilter(Widget widget) {
		if (widget instanceof FaWebFilterHTML) {
			removeFilter((FaWebFilterHTML) widget);

			if (filters.isEmpty()) {
				dropFilterPanel.addStyleName(style.dropPanelHeight());
			}
		}
		else if (widget instanceof DraggableLabel) {
			removeDraggableLabel((DraggableLabel) widget);
		}
		widget.removeFromParent();
	}

	@Override
	public void addRow(String row) { }

	@Override
	public void addCol(String col) { }

	@Override
	public void addRowCol(AbsolutePanel dropTarget, String uname) { }

	@Override
	public void setRowsCols(GridCube gc) { }
	
	public ChartObject getCurrentChart() {
		return currentChart;
	}
	
	public ChartType getCurrentType() {
		return currentType;
	}
}
