package bpm.architect.web.client.panels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.images.Images;
import bpm.architect.web.client.services.ArchitectService;
import bpm.architect.web.client.utils.DataVizDatagridResource;
import bpm.architect.web.client.utils.ImageHeaderCell;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.DataPreparationResult;
import bpm.data.viz.core.preparation.PreparationRuleSort;
import bpm.data.viz.core.preparation.PreparationRuleSort.SortType;
import bpm.fwr.api.beans.FWRReport;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.charts.ChartPanel;
import bpm.gwt.commons.client.charts.IChartConstructor;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.analysis.ChartData;
import bpm.smart.core.model.RScriptModel;
import bpm.vanilla.platform.core.beans.chart.ChartColumn;
import bpm.vanilla.platform.core.beans.chart.ChartType;
import bpm.vanilla.platform.core.beans.chart.Serie;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class DataVizDataPanel extends Composite implements IChartConstructor<DataColumn> {

	private static DataVizDataPanelUiBinder uiBinder = GWT.create(DataVizDataPanelUiBinder.class);

	interface DataVizDataPanelUiBinder extends UiBinder<Widget, DataVizDataPanel> {
	}

	interface MyStyle extends CssResource {
		String pagerPanel();
		String gridPanel();
		String pager();

		String btn();
		String btnSelected();
	}

	@UiField
	MyStyle style;

	@UiField
	FocusPanel btnData, btnVisualization, btnCarte, btnStat, btnAnalyze;

	@UiField
	HTMLPanel tabsPanel;
	
	@UiField
	HTMLPanel contentPanel;

	private DataVizDesignPanel parent;

	private DataPreparationResult lastResult;
	private DataVizVisualPanel visualPanel;
	private DataVizMapPanel dvmp;
	private ChartPanel<DataColumn> graphePanel;
	
	private TabHeader reportHeader;
	private DataVizReportPanel reportPanel;

	private SingleSelectionModel<Map<DataColumn, Serializable>> selectionModel = new SingleSelectionModel<>();
	private ListDataProvider<Map<DataColumn, Serializable>> dataProvider;
	private ListHandler<Map<DataColumn, Serializable>> sortHandler;
	
	private boolean acceptBigData;

	private FocusPanel selectedTab;
	private Composite selectedTabPanel;

	public DataVizDataPanel(DataVizDesignPanel parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		selectTab(btnData);
		
		refresh(parent.getDataPreparation());

		this.visualPanel = new DataVizVisualPanel(parent);
		this.visualPanel.setVisible(false);
	}

	public DataVizVisualPanel getVisualPanel() {
		return visualPanel;
	}

	public void refresh(final DataPreparation dp) {
		if (dp.getDataset() != null) {
			if (!acceptBigData) {
				ArchitectService.Connect.getInstance().countDataPreparation(dp, new GwtCallbackWrapper<Integer>(parent, true, true) {
					@Override
					public void onSuccess(Integer result) {
						if (result > 10000) {
							StringBuffer buf = new StringBuffer();
							buf.append(Labels.lblCnst.EstimatedNumberOfData() + ": " + result + "<br/>");
							buf.append(Labels.lblCnst.TooManyDataMessage());
							SafeHtml html = SafeHtmlUtils.fromTrustedString(buf.toString());
							
							final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), html, true);
							dial.center();
							dial.addCloseHandler(new CloseHandler<PopupPanel>() {
								
								@Override
								public void onClose(CloseEvent<PopupPanel> event) {
									if (dial.isConfirm()) {
										acceptBigData = true;
										refreshData(dp);
									}
								}
							});
						}
						else {
							acceptBigData = true;
							refreshData(dp);
						}
					}
				}.getAsyncCallback());
			}
			else {
				refreshData(dp);
			}
		}
	}
	
	private void refreshData(DataPreparation dp) {
		if (acceptBigData) {
			ArchitectService.Connect.getInstance().executeDataPreparation(dp, new GwtCallbackWrapper<DataPreparationResult>(null, false, false) {
				@Override
				public void onSuccess(DataPreparationResult result) {
					if (!visualPanel.isVisible()) {
						HTMLPanel panelDatagrid = buildDataGrid(result);
						selectPanel(new CompositeContainer(panelDatagrid));
					}
				}
			}.getAsyncCallback());
		}
	}

	private HTMLPanel buildDataGrid(DataPreparationResult result) {
		this.lastResult = result;
		
		DataGrid.Resources resources = new DataVizDatagridResource();
		DataGrid<Map<DataColumn, Serializable>> datagrid = new DataGrid<>(50, resources);
		datagrid.setSize("100%", "100%");

		sortHandler = new ListHandler<Map<DataColumn, Serializable>>(result.getValues()) {
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				DataColumn col = ((ColumnData) event.getColumn()).getCol();

				PreparationRuleSort sort = new PreparationRuleSort();
				sort.setColumn(col);

				int index = parent.getDataPreparation().getRules().indexOf(sort);
				if (index > 0) {
					PreparationRuleSort rule = (PreparationRuleSort) parent.getDataPreparation().getRules().get(index);
					rule.setSortType(rule.getSortType() == SortType.ASC ? SortType.DESC : SortType.ASC);
				}
				else {
					sort.setSortType(SortType.ASC);
					parent.getDataPreparation().addRule(sort);
				}

				refresh(parent.getDataPreparation());
				parent.getRulePanel().refreshRulePanel();

				super.onColumnSort(event);
			}
		};
		datagrid.addColumnSortHandler(sortHandler);

		AbstractCell<Serializable> cell = new AbstractCell<Serializable>() {
			@Override
			public void render(Context context, Serializable value, SafeHtmlBuilder sb) {
				if (value != null) {
					sb.append(SafeHtmlUtils.fromString(value.toString()));
				}
			}
		};
		for (final DataColumn col : result.getValues().get(0).keySet()) {
			Column<Map<DataColumn, Serializable>, Serializable> c = new ColumnData(cell, col);

			datagrid.addColumn(c, new Header<String>(new ImageHeaderCell(col.getColumnLabel(), Images.INSTANCE.image_grid().getSafeUri().asString(), col, this)) {
				@Override
				public String getValue() {
					return col.getColumnLabel();
				}
			});
			datagrid.setColumnWidth(c, "100px");
			sortHandler.setComparator(c, new Comparator<Map<DataColumn, Serializable>>() {
				@Override
				@SuppressWarnings({ "unchecked", "rawtypes" })
				public int compare(Map<DataColumn, Serializable> o1, Map<DataColumn, Serializable> o2) {
					Comparable val1 = ((Comparable) o1.get(col));
					Comparable val2 = ((Comparable) o2.get(col));
					if (val1 == null) {
						return -1;
					}
					if (val2 == null) {
						return 1;
					}
					return val1.compareTo(val2);
				}
			});
		}

		dataProvider = new ListDataProvider<Map<DataColumn, Serializable>>();
		dataProvider.addDataDisplay(datagrid);
		dataProvider.setList(result.getValues());
		sortHandler.setList(dataProvider.getList());

		datagrid.setSelectionModel(selectionModel);

		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(style.pager());
		pager.setDisplay(datagrid);
		
		SimplePanel panelGrid = new SimplePanel();
		panelGrid.addStyleName(style.gridPanel());
		panelGrid.setWidget(datagrid);
		
		SimplePanel pagerPanel = new SimplePanel();
		pagerPanel.addStyleName(style.pagerPanel());
		pagerPanel.setWidget(pager);
		
		HTMLPanel dataPanel = new HTMLPanel("");
		dataPanel.add(panelGrid);
		dataPanel.add(pagerPanel);
		return dataPanel;
	}

	public void sortColumn(DataColumn col) {

		PreparationRuleSort sort = new PreparationRuleSort();
		sort.setColumn(col);

		int index = parent.getDataPreparation().getRules().indexOf(sort);
		if (index > 0) {
			PreparationRuleSort rule = (PreparationRuleSort) parent.getDataPreparation().getRules().get(index);
			rule.setSortType(rule.getSortType() == SortType.ASC ? SortType.DESC : SortType.ASC);
		}
		else {
			sort.setSortType(SortType.ASC);
			parent.getDataPreparation().addRule(sort);
		}

		parent.getDataPanel().refresh(parent.getDataPreparation());
		parent.getRulePanel().refreshRulePanel();
	}

	public int getSelectedRowIndex() {
		return dataProvider.getList().indexOf(selectionModel.getSelectedObject());
	}

	public DataPreparationResult getLastResult() {
		return lastResult;
	}
	
	public DataPreparation getDataPreparation() {
		return parent.getDataPreparation();
	}
	
	private void selectTab(FocusPanel tab) {
		if (selectedTab != null) {
			this.selectedTab.removeStyleName(style.btnSelected());
		}
		this.selectedTab = tab;
		this.selectedTab.addStyleName(style.btnSelected());
	}
	
	private void selectPanel(Composite tabPanel) {
		if (selectedTabPanel != null) {
			this.selectedTabPanel.setVisible(false);
		}
		this.selectedTabPanel = tabPanel;
		selectedTabPanel.setVisible(true);
		if (!selectedTabPanel.isAttached()) {
			contentPanel.add(selectedTabPanel);
		}
	}

	@UiHandler("btnData")
	public void onPaletteClick(ClickEvent event) {
		selectTab(btnData);

		this.visualPanel.setVisible(false);
		this.visualPanel.panelProperties.clear();
		this.visualPanel.setPropertiesPanel(null);
		refresh(parent.getDataPreparation());
	}

	@UiHandler("btnVisualization")
	public void onDataClick(ClickEvent event) {
		selectTab(btnVisualization);

		this.visualPanel.setVisible(true);
		selectPanel(visualPanel);
	}

	@UiHandler("btnCarte")
	public void onMapClick(ClickEvent event) {
		selectTab(btnCarte);

		if (dvmp == null) {
			dvmp = new DataVizMapPanel(parent);
		}
		selectPanel(dvmp);
	}
	
	public void openReport(String reportUrl, boolean display) {
		if (reportPanel == null) {
			this.reportPanel = new DataVizReportPanel(reportUrl);
			
			reportHeader = new TabHeader(LabelsConstants.lblCnst.Report());
			reportHeader.addStyleName(style.btn());
			reportHeader.setSelectHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					selectTab(reportHeader);
					selectPanel(reportPanel);
				}
			});
			reportHeader.setCloseHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					reportHeader.removeFromParent();
					reportPanel = null;
					if (selectedTab == reportHeader) {
						onMapClick(event);
					}
				}
			});
		}
		else {
			reportPanel.setReportUrl(reportUrl);
		}

		if (display) {
			tabsPanel.add(reportHeader);
			selectTab(reportHeader);
		}
		selectPanel(reportPanel);
	}

	@UiHandler("btnStat")
	public void onStatClick(ClickEvent event) {
		selectTab(btnStat);

		this.contentPanel.clear();
		ArchitectService.Connect.getInstance().getRScript(parent.getDataPreparation().getDataset(), new GwtCallbackWrapper<RScriptModel>(null, false, false) {

			@Override
			public void onSuccess(RScriptModel result) {
				if (result != null) {
					if (result.getOutputFiles() != null) {

						HTML outputVars = new HTML(result.getOutputVarstoString().get(0));
						HTML outputFiles = new HTML(result.getOutputFiles()[0]);
						outputVars.getElement().getStyle().setFloat(Float.RIGHT);
						outputVars.getElement().getStyle().setWidth(500, Unit.PX);

						outputFiles.getElement().getStyle().setFloat(Float.LEFT);

						HTMLPanel panelR = new HTMLPanel("");
						panelR.add(outputFiles);
						panelR.add(outputVars);
						
						selectPanel(new CompositeContainer(panelR));
					}
				}
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnAnalyze")
	public void onAnalyzeClick(ClickEvent event) {
		selectTab(btnAnalyze);

		List<ChartColumn<DataColumn>> columns = new ArrayList<>();
		
		List<Map<DataColumn, Serializable>> values = lastResult.getValues();
		for (DataColumn col : values.get(0).keySet()) {
			columns.add(new ChartColumn<DataColumn>(col.getColumnLabel(), col));
		}

		graphePanel = new ChartPanel<>(this, columns);
		selectPanel(graphePanel);
	}

	@Override
	public void refreshChart(final ChartType type, DataColumn axeX, final List<Serie<DataColumn>> series) {
		ArchitectService.Connect.getInstance().getChartData(lastResult, axeX, series, new GwtCallbackWrapper<List<ChartData>>(parent, true, true) {

			@Override
			public void onSuccess(List<ChartData> chartData) {
				graphePanel.buildChart(type, series, chartData);
			}
			
			@Override
			public void onFailure(Throwable t) {
				graphePanel.buildChart(type, series, null);
			}
		}.getAsyncCallback());
	}

	public class ColumnData extends Column<Map<DataColumn, Serializable>, Serializable> {

		private DataColumn col;

		public ColumnData(Cell<Serializable> cell, DataColumn col) {
			super(cell);
			this.col = col;
		}

		@Override
		public Serializable getValue(Map<DataColumn, Serializable> object) {
			Serializable val = object.get(col);
			return val;
		}

		public DataColumn getCol() {
			return col;
		}

	}

	@Override
	public FWRReport buildWebReport(String title, ChartType type, DataColumn axeX, List<Serie<DataColumn>> series) throws ServiceException {
		//Not supported
		return null;
	}
}