package bpm.fwr.client.wizard.chart;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import bpm.fwr.api.beans.components.ChartComponent;
import bpm.fwr.api.beans.components.ChartType;
import bpm.fwr.api.beans.components.ChartTypes;
import bpm.fwr.api.beans.components.IChart;
import bpm.fwr.api.beans.components.IReportComponent;
import bpm.fwr.api.beans.components.OptionsFusionChart;
import bpm.fwr.api.beans.components.VanillaChartComponent;
import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.DataSource;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.dialogs.JoinDatasetDialogBox;
import bpm.fwr.client.draggable.dragcontrollers.DataDragController;
import bpm.fwr.client.draggable.dropcontrollers.TextDropController;
import bpm.fwr.client.draggable.widgets.DraggableColumn;
import bpm.fwr.client.services.FwrServiceMetadata;
import bpm.fwr.client.tree.DatasetsTree;
import bpm.fwr.client.tree.MetadataTree;
import bpm.fwr.client.utils.ChartOperations;
import bpm.fwr.client.utils.ChartUtils;
import bpm.fwr.client.utils.ServletURL;
import bpm.fwr.client.widgets.TextBoxWidget;
import bpm.fwr.client.widgets.TextBoxWidget.TextBoxType;
import bpm.fwr.client.wizard.IManageTextBoxData;
import bpm.fwr.shared.models.metadata.FwrBusinessModel;
import bpm.fwr.shared.models.metadata.FwrMetadata;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.charts.Aggregation;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.listeners.FinishListener;
import bpm.gwt.commons.client.loading.GreyAbsolutePanel;
import bpm.gwt.commons.client.loading.WaitAbsolutePanel;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.shared.InfoUser;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ChartSelectionColumnPage extends Composite implements IGwtPage, IManageTextBoxData {
	private static final String CSS_TXT_BOX_WIDGET = "textBoxWidget";

	private static ChartSelectionColumnPageUiBinder uiBinder = GWT.create(ChartSelectionColumnPageUiBinder.class);

	interface ChartSelectionColumnPageUiBinder extends UiBinder<Widget, ChartSelectionColumnPage> {
	}

	interface MyStyle extends CssResource {
		String frameChart();
	}

	@UiField
	AbsolutePanel rootPanel, previewPanel;

	@UiField
	Label lblTitle, lblDetail, lblHeight, lblWidth, lblGroup;

	@UiField
	ListBox lstDetail;

	@UiField
	ListBoxWithButton<Aggregation> lstAgg;

	@UiField
	Button btnDelSeries, btnPreview;

	@UiField
	Image imgChart;

	@UiField
	TextBox txtHeight, txtWidth;

	@UiField
	SimplePanel panelGroup, panelDetail, datasetTreePanel, datasetPanel;

	@UiField
	MyStyle style;

	private TextBoxWidget txtDetail, txtGroup;

	private ChartCreationWizard parent;
	private int index;
	private String chartTitle;
	private ChartType chartType;
	private OptionsFusionChart chartOptions;

	private TextDropController txtDetDropCtrl, txtDropCtrl;
	private PickupDragController puDragCtrl;

	private DataSet dataset;

	private int listDetailIndex = 0;
	private LinkedHashMap<Integer, Column> selectedDetails = new LinkedHashMap<Integer, Column>();
	private List<FwrMetadata> metadatas;
	
	private List<Aggregation> aggregations;

	public ChartSelectionColumnPage(ChartCreationWizard parent, int index, String chartTitle, ChartType chartType, OptionsFusionChart chartOptions, List<FwrMetadata> metadatas, List<DataSet> dsAvailable, IChart chartComponent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;
		this.chartTitle = chartTitle;
		this.chartType = chartType;
		this.metadatas = metadatas;
		this.aggregations = buildAggregations();
		setChartOptions(chartOptions);

		puDragCtrl = new DataDragController(rootPanel, false);

		// Add datasetTree part
		MetadataTree metadataTree = new MetadataTree(parent, puDragCtrl, null, false);
		metadataTree.setHeight("100%");
		metadataTree.setMetadatas(metadatas);
		datasetTreePanel.add(metadataTree);

		DatasetsTree datasetsTree = new DatasetsTree(dsAvailable, puDragCtrl);
		datasetsTree.setHeight("100%");
		datasetPanel.add(datasetsTree);

		// Add the rest part
		lblTitle.setText(Bpm_fwr.LBLW.ChartSelectGrpDet());
		lblDetail.setText(Bpm_fwr.LBLW.ChartSelectDetail());

		btnDelSeries.setText(Bpm_fwr.LBLW.ChartSelectButtonDel());
		btnDelSeries.addClickHandler(btnClickHandler);

		btnPreview.setText(Bpm_fwr.LBLW.TabPreview());
		btnPreview.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				updatePreview();
			}
		});

		if (chartType.getType() == ChartTypes.PIE || chartType.getType() == ChartTypes.DOUGHNUT) {
			lstDetail.setVisible(false);
			btnDelSeries.setVisible(false);
		}

		refreshImageChart();

		lblHeight.setText(Bpm_fwr.LBLW.HeightByPT());
		lblWidth.setText(Bpm_fwr.LBLW.WidthByPT());

		lblGroup.setText(Bpm_fwr.LBLW.ChartSelectGroup());

		txtHeight.setText("300");
		txtWidth.setText("600");
		
		lstAgg.setList(aggregations);

		lstDetail.addChangeHandler(lstDetailChangeHandler);
		lstDetail.addItem("Serie 1", "0");
		lstDetail.addItem("New serie", "-1");

		// Drag And Drop part
		txtDetail = new TextBoxWidget(this, TextBoxType.DETAIL);
		txtDetail.addStyleName(CSS_TXT_BOX_WIDGET);
		panelDetail.add(txtDetail);

		txtGroup = new TextBoxWidget(this, TextBoxType.GROUP);
		txtGroup.addStyleName(CSS_TXT_BOX_WIDGET);
		panelGroup.add(txtGroup);

		txtDetDropCtrl = new TextDropController(txtDetail);
		txtDropCtrl = new TextDropController(txtGroup);

		if (chartComponent != null) {
			for (int i = 0; i < chartComponent.getColumnDetails().size(); i++) {
				selectedDetails.put(i, chartComponent.getColumnDetails().get(i));
				if (i == 0) {
					updateDetail(chartComponent.getColumnDetails().get(i));
				}
				else {
					lstDetail.insertItem("Serie " + (lstDetail.getItemCount()), "" + (lstDetail.getItemCount() - 1), lstDetail.getItemCount() - 1);
				}
			}

			txtGroup.setColumn(chartComponent.getColumnGroup());
			txtGroup.setText(chartComponent.getColumnGroup().getName());

			dataset = chartComponent.getDataset();
			dataset.getColumns().clear();
			if (chartComponent.getColumnDetails() != null) {
				for (Column col : chartComponent.getColumnDetails()) {
					manageColumn(col, TextBoxType.DETAIL);
				}
			}
			manageColumn(chartComponent.getColumnGroup(), TextBoxType.GROUP);

			if (chartComponent instanceof VanillaChartComponent) {
				VanillaChartComponent comp = (VanillaChartComponent) chartComponent;
				txtWidth.setText(comp.getOptions().getWidth() + "");
				txtHeight.setText(comp.getOptions().getHeight() + "");
			}
		}
	}
	
	private List<Aggregation> buildAggregations() {
		String[] aggregations = new String[] { "COUNT", "SUM", "AVG", "MAX", "MIN" };

		List<Aggregation> aggregs = new ArrayList<Aggregation>();
		for (String agg : aggregations) {
			aggregs.add(new Aggregation(getAggLabel(agg), agg));
		}
		return aggregs;
	}

	private String getAggLabel(String agg) {
		if (agg.equals("SUM")) {
			return LabelsConstants.lblCnst.Sum();
		}
		else if (agg.equals("AVG")) {
			return LabelsConstants.lblCnst.Average();
		}
		else if (agg.equals("COUNT")) {
			return LabelsConstants.lblCnst.Count();
		}
		else if (agg.equals("MAX")) {
			return LabelsConstants.lblCnst.Max();
		}
		else if (agg.equals("MIN")) {
			return LabelsConstants.lblCnst.Min();
		}
		return "";
	}

	private void refreshImageChart() {
		if (parent.isVanillaChart()) {
			imgChart.setResource(ChartUtils.getImageForChart(chartType.getType(), chartOptions));
		}
		else {
			imgChart.setResource(ChartUtils.getImageForChart(chartType.getType()));
		}
	}
	
	private void updateDetail(Column col) {
		if (col == null) {
			txtDetail.setText("");
			txtDetail.setValue("");
			lstAgg.setSelectedIndex(0);
		}
		else {
			txtDetail.setColumn(col);
			txtDetail.setText(col.getName());
			lstAgg.setSelectedObject(getAggregation(col.getAggregation()));
		}
	}

	private Aggregation getAggregation(String aggregation) {
		if (aggregation != null) {
			for (Aggregation agg : aggregations) {
				if (agg.getValue().equalsIgnoreCase(aggregation)) {
					return agg;
				}
			}
		}
		return null;
	}

	@Override
	protected void onAttach() {
		puDragCtrl.registerDropController(txtDetDropCtrl);
		puDragCtrl.registerDropController(txtDropCtrl);
		super.onAttach();
	}

	@Override
	protected void onDetach() {
		puDragCtrl.unregisterDropControllers();
		super.onDetach();
	}

	private void manageColumn(Column col, TextBoxType type) {
		// FIXME Create agg for measures

		if (type == TextBoxType.DETAIL) {
			col.setName(col.getName());
			col.setAgregate(true);
			col.setCalculated(true);
//			col.setFormula(chartOptions.getChartOperationQuery() + "(" + col.getOriginName() + ")");
			List<String> involvedDatastreams = new ArrayList<String>();
			involvedDatastreams.add(col.getDatasourceTable());
			col.setInvolvedDatastreams(involvedDatastreams);
			checkOrCreateDataset(col);
		}
		else if (type == TextBoxType.GROUP) {
			checkOrCreateDataset(col);
		}
		parent.updateBtn();
	}

	public void manageWidget(DraggableColumn widget, TextBoxType type) {
		// FIXME Create agg for measures

		if (type == TextBoxType.DETAIL) {
			Aggregation agg = lstAgg.getSelectedObject();
			
			Column col = widget.getColumn().getClone();
			col.setName(col.getName());
			col.setAgregate(true);
			col.setCalculated(true);
			col.setFormula(agg.getValue() + "(" + col.getOriginName() + ")");
			List<String> involvedDatastreams = new ArrayList<String>();
			involvedDatastreams.add(col.getDatasourceTable());
			col.setInvolvedDatastreams(involvedDatastreams);

			updateDetail(col);
			
			checkOrCreateDataset(col);
		}
		else if (type == TextBoxType.GROUP) {
			txtGroup.setColumn(widget.getColumn());
			txtGroup.setText(widget.getColumn().getName());
			checkOrCreateDataset(widget.getColumn());
		}
		parent.updateBtn();
	}
	
	@UiHandler("lstAgg")
	public void onAggChange(ChangeEvent event) {
		Aggregation agg = lstAgg.getSelectedObject();
		
		Column column = txtDetail.getColumn();
		if (column != null) {
			column.setFormula(agg.getValue() + "(" + column.getOriginName() + ")");
		}
	}

	private ChangeHandler lstDetailChangeHandler = new ChangeHandler() {
		
		@Override
		public void onChange(ChangeEvent event) {
			selectedDetails.put(listDetailIndex, txtDetail.getColumn());
			listDetailIndex = Integer.parseInt(lstDetail.getValue(lstDetail.getSelectedIndex()));
			if (listDetailIndex == -1) {
				lstDetail.insertItem("Serie " + (lstDetail.getItemCount()), "" + (lstDetail.getItemCount() - 1), lstDetail.getItemCount() - 1);
				lstDetail.setSelectedIndex(lstDetail.getItemCount() - 2);
				listDetailIndex = lstDetail.getItemCount() - 2;
				
				updateDetail(null);
			}
			else {
				if (selectedDetails.keySet().contains(listDetailIndex)) {
					updateDetail(selectedDetails.get(listDetailIndex));
				}
				else {
					updateDetail(null);
				}
			}
		}
	};

	public IChart generateChart(boolean isPreview) {
		IChart chart = null;

		if (parent.isVanillaChart()) {
			chart = new VanillaChartComponent();
		}
		else {
			chart = new ChartComponent();
		}

		if (chartType.getType() == ChartTypes.PIE || chartType.getType() == ChartTypes.DOUGHNUT) {
			List<Column> detail = new ArrayList<Column>();
			detail.add(txtDetail.getColumn());
			chart.setColumnDetails(detail);
		}
		else {
			selectedDetails.put(listDetailIndex, txtDetail.getColumn());
			List<Column> detail = new ArrayList<Column>();
			for (Column column : selectedDetails.values()) {
				detail.add(column);
			}
			chart.setColumnDetails(detail);
		}
		chart.setChartType(chartType);
		chart.setColumnGroup(txtGroup.getColumn());
		chart.setDataset(dataset);

		if (parent.isVanillaChart()) {
			chartOptions.setChartTitle(chartTitle);

			if (isPreview) {
				chartOptions.setHeight(250);
				chartOptions.setWidth(380);
				((VanillaChartComponent) chart).setOptions(chartOptions);
			}
			else {
				try {
					int height = Integer.parseInt(txtHeight.getText());
					chartOptions.setHeight(height);
				} catch (Exception e) {
					e.printStackTrace();
					chartOptions.setHeight(300);
				}
				try {
					int width = Integer.parseInt(txtWidth.getText());
					chartOptions.setWidth(width);
				} catch (Exception e) {
					e.printStackTrace();
					chartOptions.setWidth(300);
				}
				((VanillaChartComponent) chart).setOptions(chartOptions);
			}
		}
		else {
			((ChartComponent) chart).setChartTitle(chartTitle);

			if (isPreview) {
				((ChartComponent) chart).setHeight(250);
				((ChartComponent) chart).setWidth(380);
			}
			else {
				Integer height = null;
				try {
					height = Integer.parseInt(txtHeight.getText());
				} catch (Exception e) {
					e.printStackTrace();
					height = 300;
				}

				Integer width = null;
				try {
					width = Integer.parseInt(txtWidth.getText());
				} catch (Exception e) {
					e.printStackTrace();
					width = 300;
				}
				((ChartComponent) chart).setHeight(height);
				((ChartComponent) chart).setWidth(width);
			}
		}

		return chart;
	}

	private ClickHandler btnClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (event.getSource().equals(btnDelSeries)) {
				selectedDetails.remove(listDetailIndex);
				List<Integer> lstKeys = new ArrayList<Integer>();
				for (int ind : selectedDetails.keySet()) {
					if (ind > listDetailIndex) {
						lstKeys.add(ind);
					}
				}

				for (int ind : lstKeys) {
					Column s = selectedDetails.get(ind);
					selectedDetails.remove(ind);
					selectedDetails.put(ind - 1, s);
				}

				if (lstDetail.getItemCount() != 2) {
					lstDetail.removeItem(lstDetail.getItemCount() - 2);
					lstDetail.setSelectedIndex(lstDetail.getItemCount() - 2);
					Column s = selectedDetails.get(lstDetail.getItemCount() - 2);
					
					updateDetail(s);
					
					listDetailIndex = lstDetail.getItemCount() - 2;
				}
				else {
					updateDetail(null);
				}
			}
		}
	};

	public void updatePreview() {
		if (!txtGroup.getValue().equalsIgnoreCase("") && !txtDetail.getValue().equalsIgnoreCase("")) {

			previewPanel.clear();

			final GreyAbsolutePanel greyPanel = new GreyAbsolutePanel();
			final WaitAbsolutePanel waitPanel = new WaitAbsolutePanel();

			previewPanel.add(greyPanel);
			previewPanel.add(waitPanel);
			previewPanel.setWidgetPosition(waitPanel, 120, 120);

			IChart chart = generateChart(true);
			IReportComponent component = null;
			if (chart instanceof VanillaChartComponent) {
				component = (VanillaChartComponent) chart;
			}
			else if (chart instanceof ChartComponent) {
				component = (ChartComponent) chart;
			}

			FwrServiceMetadata.Connect.getInstance().saveComponentForPreview(component, new AsyncCallback<Void>() {
				public void onSuccess(Void result) {

					Frame frame = new Frame();
					frame.setUrl(GWT.getHostPageBaseURL() + ServletURL.PREVIEW_COMPONENT_URL + "?" + ServletURL.PARAM_TYPE + "=html");
					frame.addStyleName(style.frameChart());
					frame.addLoadHandler(new LoadHandler() {

						@Override
						public void onLoad(LoadEvent event) {
							previewPanel.remove(greyPanel);
							previewPanel.remove(waitPanel);
						}
					});
					previewPanel.add(frame);
				}

				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
			});
		}
	}

	private void checkOrCreateDataset(Column column) {
		if (dataset == null) {
			dataset = createDataSet(column);
			dataset.addColumn(column);
			column.setDatasetParent(dataset);
		}
		else {

			if (!dataset.getColumns().isEmpty()) {
				if (!column.getBusinessPackageParent().equals(dataset.getColumns().get(0).getBusinessPackageParent())) {

					DataSet dataset1 = dataset;
					DataSet dataset2 = createDataSet(column);
					dataset2.addColumn(column);
					column.setDatasetParent(dataset2);

					createJoinDataset(dataset1, dataset2);
				}
				else {
					dataset.addColumn(column);
					column.setDatasetParent(dataset);
				}
			}
			else {
				dataset.addColumn(column);
				column.setDatasetParent(dataset);
			}
		}
	}

	private DataSet createDataSet(Column column) {

		InfoUser infoUser = Bpm_fwr.getInstance().getInfoUser();

		// Datasource creation
		DataSource dataS = new DataSource();
		dataS.setBusinessModel(column.getBusinessModelParent());
		dataS.setBusinessPackage(column.getBusinessPackageParent());
		dataS.setConnectionName("Default");
		dataS.setGroup(infoUser.getGroup().getName());
		dataS.setItemId(column.getMetadataId());
		dataS.setRepositoryId(infoUser.getRepository().getId());
		dataS.setName("datasource_" + System.currentTimeMillis());
		dataS.setPassword(infoUser.getUser().getPassword());
		dataS.setUrl(infoUser.getRepository().getUrl());
		dataS.setUser(infoUser.getUser().getLogin());
		dataS.setOnOlap(searchedModel(column.getBusinessModelParent()).isOnOlap());

		DataSet dataset = new DataSet();
		dataset.setLanguage("fr");
		dataset.setDatasource(dataS);
		dataset.setName("dataset_" + System.currentTimeMillis());
		return dataset;
	}

	private FwrBusinessModel searchedModel(String modelName) {

		for (FwrMetadata metadata : metadatas) {
			if (metadata.isBrowsed()) {
				for (FwrBusinessModel model : metadata.getBusinessModels()) {
					if (model.getName().equals(modelName)) {
						return model;
					}
				}
			}
		}

		return null;
	}

	private void createJoinDataset(DataSet dataset1, DataSet dataset2) {
		JoinDatasetDialogBox dial = new JoinDatasetDialogBox(dataset1, dataset2);
		dial.addFinishListener(finishListener);
		dial.center();
	}

	private FinishListener finishListener = new FinishListener() {

		@Override
		public void onFinish(Object result, Object source, String result1) {
			if (result instanceof DataSet) {
				dataset = (DataSet) result;
			}
		}
	};

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return true;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isComplete() {
		return (!txtGroup.getValue().equalsIgnoreCase("") && !txtDetail.getValue().equalsIgnoreCase("")) ? true : false;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}

	public void setChartOptions(OptionsFusionChart chartOptions) {
		this.chartOptions = chartOptions;
//		try {
//			if (dataset.getColumns() != null && !dataset.getColumns().isEmpty()) {
//				for (Column col : dataset.getColumns()) {
//					if (col.isAgregate()) {
//						col.setFormula(chartOptions.getChartOperationQuery() + "(" + col.getOriginName() + ")");
//					}
//				}
//			}
//		} catch (Exception e) {
//		}
	}

	public void initPage() {
		imgChart = new Image();
		previewPanel.clear();
		previewPanel.add(imgChart);
		refreshImageChart();

		if (chartType.getType() == ChartTypes.PIE || chartType.getType() == ChartTypes.DOUGHNUT) {
			lstDetail.setVisible(false);
			btnDelSeries.setVisible(false);
		}
	}

}
