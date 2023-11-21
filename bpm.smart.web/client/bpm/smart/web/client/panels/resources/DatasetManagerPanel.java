package bpm.smart.web.client.panels.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.dataset.DatasetWizard;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.workflow.commons.client.tabs.HorizontalTab;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.StatDataColumn;
import bpm.smart.web.client.MainPanel;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.images.Images;
import bpm.smart.web.client.services.SmartAirService;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;

import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class DatasetManagerPanel extends HorizontalTab {

	private static DatasetManagerPanelUiBinder uiBinder = GWT.create(DatasetManagerPanelUiBinder.class);

	interface DatasetManagerPanelUiBinder extends UiBinder<Widget, DatasetManagerPanel> {
	}
	
	interface MyStyle extends CssResource {
		String childCell();
		String littlechildCell();
		String groupHeaderCell();
		String btnGrid();
		String mainPanel();
	}
	
	@UiField
	HTMLPanel panelToolbar, panelStats;
	
	@UiField
	SimplePanel panelGrid, panelPager;
	
	@UiField
	Image btnEditDataset, btnDeleteDataset, btnRefresh, btnAddDataset;
	
	@UiField
	Button btnTest;

	@UiField
	MyStyle style;

	private MainPanel mainPanel;
	private AirPanel airPanel;
	private ListDataProvider<Dataset> dataProvider;
	private SingleSelectionModel<Dataset> selectionModel;
	private ListHandler<Dataset> sortHandler;
	private DataGrid<Dataset> datagrid;
	
	private List<StatDataColumn> stats;
	private Set<Integer> showingStats = new HashSet<Integer>();
	private Set<Integer> showingDetails = new HashSet<Integer>();
	
	private DatasetWizard dial;
	
	private User user;
	private boolean firstload = true;

	public DatasetManagerPanel(MainPanel mainPanel, AirPanel airPanel) {
		super(LabelsConstants.lblCnst.TabDatasetManager(), Images.INSTANCE.ic_equalizer_black_18dp());
		this.add(uiBinder.createAndBindUi(this));
		addStyleName(style.mainPanel());
		this.mainPanel = mainPanel;
		this.airPanel = airPanel;
		this.datagrid = createGridData();
		this.user = mainPanel.getUserSession().getInfoUser().getUser();
		init(this);
		
		panelGrid.add(datagrid);
		loadDatasets();
		panelStats.clear();
		btnTest.setVisible(false);
	}
	
//	@Override
//	public void onLoad(){
//		if(firstload){
//			loadDatasets();
//			firstload = false;
//		}
//	}
	
	private DataGrid<Dataset> createGridData() {
		TextCell cell = new TextCell();
		final Column<Dataset, String> nameColumn = new Column<Dataset, String>(cell) {

			@Override
			public String getValue(Dataset object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);

		final Column<Dataset, String> reqColumn = new Column<Dataset, String>(cell) {

			@Override
			public String getValue(Dataset object) {
				if (object.getRequest().length() > 100) {
					return object.getRequest().substring(0, 100) + "...";
				}
				return object.getRequest();
			}
		};

		ImageCell imageCell = new ImageCell() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
				if (value != null) {
					sb.appendHtmlConstant("<img src = '" + bpm.smart.web.client.images.Images.INSTANCE.ic_add_R_32().getSafeUri().asString() + "' height = '18px' width = '18px' class='" + style.btnGrid() + "' onclick='addToR(" + value + ")'/>");
				}
			}
		};

		final Column<Dataset, String> addRColumn = new Column<Dataset, String>(imageCell) {

			@Override
			public String getValue(Dataset object) {
				return String.valueOf(object.getId());
			}
		};

		// DataGrid.Resources resources = new CustomResources();
		DataGrid<Dataset> dataGrid = new DataGrid<Dataset>(30);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");

		// Attention au label
		dataGrid.addColumn(nameColumn, bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.Name());
		dataGrid.addColumn(reqColumn, LabelsConstants.lblCnst.Request());
		dataGrid.addColumn(addRColumn, LabelsConstants.lblCnst.SendToR());

		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoResult()));

		dataProvider = new ListDataProvider<Dataset>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<Dataset>(new ArrayList<Dataset>());
		sortHandler.setComparator(nameColumn, new Comparator<Dataset>() {

			@Override
			public int compare(Dataset m1, Dataset m2) {
				return m1.getName().compareTo(m2.getName());
			}
		});

		dataGrid.addColumnSortHandler(sortHandler);

		// Add a selection model so we can select cells.
		selectionModel = new SingleSelectionModel<Dataset>();
		selectionModel.addSelectionChangeHandler(selectionChangeHandler);
		dataGrid.setSelectionModel(selectionModel);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		// pager.addStyleName(style.pager());
		pager.setDisplay(dataGrid);

		panelPager.setWidget(pager);

		return dataGrid;
	}

	private Handler selectionChangeHandler = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			final Dataset selectedDataset = selectionModel.getSelectedObject();
			btnEditDataset.setVisible(selectedDataset != null);
			btnDeleteDataset.setVisible(selectedDataset != null);
			if(selectedDataset == null) return;
			panelStats.clear();
			
			if(getStatsbyDataset(selectedDataset).size() > 0){
				panelStats.add(new StatsPanel(selectedDataset, getStatsbyDataset(selectedDataset), user, false, false, mainPanel, getAirPanel().getWorkspacePanel()));
			} else {
				showWaitPart(true);
				SmartAirService.Connect.getInstance().calculateRStats(selectedDataset.getMetacolumns(), selectedDataset, new AsyncCallback<List<StatDataColumn>>() {

					@Override
					public void onFailure(Throwable caught) {
						showWaitPart(false);

						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, caught.getMessage());
					}

					@Override
					public void onSuccess(List<StatDataColumn> result) {
						showWaitPart(false);
						panelStats.add(new StatsPanel(selectedDataset, result, user, false, false, mainPanel, getAirPanel().getWorkspacePanel()));
					}
				});
			}
			
			
		}
	};
	
	private final native void init(DatasetManagerPanel panel) /*-{
		var panel = panel;	
		$wnd.clickDetail = function(id, index){
			panel.@bpm.smart.web.client.panels.resources.DatasetManagerPanel::handleDetailClick(Ljava/lang/String;Ljava/lang/String;)(id.toString(), index.toString());
		};
		$wnd.addToR = function(id){
			panel.@bpm.smart.web.client.panels.resources.DatasetManagerPanel::handleAddToRClick(Ljava/lang/String;)(id.toString());
		};
	}-*/;
	
	public void handleDetailClick(String id, String index) {
		if (showingDetails.contains(Integer.parseInt(id))) {
			showingDetails.remove(Integer.parseInt(id));
		}
		else {
			showingDetails.add(Integer.parseInt(id));
		}

		// Redraw the modified row.
		datagrid.redrawRow(Integer.parseInt(index));
	}
	
	public void handleAddToRClick(String id) {
		Dataset datasetToAdd = new Dataset();
		for (Dataset dts : dataProvider.getList()) {
			if (dts.getId() == Integer.parseInt(id)) {
				datasetToAdd = dts;
				break;
			}
		}

		addDatasetToR(datasetToAdd);
	}

	public void addDatasetToR(Dataset dts) {
		showWaitPart(true);
		SmartAirService.Connect.getInstance().addDatasetToR(dts, new AsyncCallback<RScriptModel>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToSendToR());
			}

			@Override
			public void onSuccess(RScriptModel result) {
				showWaitPart(false);
				mainPanel.getLogPanel().addLog(result.getOutputLog());
				loadStats(new ArrayList<Dataset>(dataProvider.getList()));
				selectionChangeHandler.onSelectionChange(null);
			}
		});
	}

	public DataGrid<Dataset> getDatagrid() {
		return datagrid;
	}

	private void loadDatasets() {
		showWaitPart(true);
		CommonService.Connect.getInstance().getPermittedDatasets(new AsyncCallback<List<Dataset>>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToLoadDatasets());
			}

			@Override
			public void onSuccess(List<Dataset> result) {
				
				showWaitPart(false);
				
				loadStats(result);
				Collections.sort(result, new Comparator<Dataset>() {
					@Override
					public int compare(Dataset d1, Dataset d2) {
						return d1.getName().compareToIgnoreCase(d2.getName());
					}
				});
				dataProvider.setList(result);
				sortHandler.setList(dataProvider.getList());
				selectionModel.clear();
			}

		});
	}

	@UiHandler("btnRefresh")
	public void onRefreshClick(ClickEvent event) {
		loadDatasets();
	}

	@UiHandler("btnAddDataset")
	public void onAddDatasetClick(ClickEvent event) {
		dial = new DatasetWizard(user);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					showWaitPart(true);
					SmartAirService.Connect.getInstance().addDatasetToR(dial.getDataset(), new AsyncCallback<RScriptModel>() {

						@Override
						public void onFailure(Throwable caught) {
							showWaitPart(false);

							caught.printStackTrace();

							ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToSendToR());
						}

						@Override
						public void onSuccess(RScriptModel result) {
							showWaitPart(false);
							mainPanel.getLogPanel().addLog(result.getOutputLog());
							createStats(dial.getDataset());
							panelStats.clear();
						}
					});
				}
			}

		});
		dial.center();
	}

	@UiHandler("btnEditDataset")
	public void onEditClick(ClickEvent event) {
		Dataset selectedDataset = selectionModel.getSelectedObject();
		if (selectedDataset == null) {
			return;
		}

		dial = new DatasetWizard(selectedDataset, user);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					showWaitPart(true);
					SmartAirService.Connect.getInstance().addDatasetToR(dial.getDataset(), new AsyncCallback<RScriptModel>() {

						@Override
						public void onFailure(Throwable caught) {
							showWaitPart(false);

							caught.printStackTrace();

							ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToSendToR());
						}

						@Override
						public void onSuccess(RScriptModel result) {
							showWaitPart(false);
							mainPanel.getLogPanel().addLog(result.getOutputLog());
							createStats(dial.getDataset());
							panelStats.clear();
						}
					});
				}
			}

		});
		dial.center();
	}

	@UiHandler("btnDeleteDataset")
	public void onDeleteClick(ClickEvent event) {
		Dataset selectedDataset = selectionModel.getSelectedObject();
		deleteDataset(selectedDataset);
	}

	@UiHandler("btnTest")
	public void onTestClick(ClickEvent event) {
//		Dataset dataset = dataProvider.getList().get(3);
		//TODO: Redo method
//		SmartAirService.Connect.getInstance().zipProject(mainPanel.getNavigationPanel().getCurrentProject(), false, new AsyncCallback<String>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				showWaitPart(false);
//
//				caught.printStackTrace();
//
//				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Test());
//			}
//
//			@Override
//			public void onSuccess(String result) {
//				showWaitPart(false);
//			}
//		});
	}

	private void deleteDataset(final Dataset dataset) {
		showWaitPart(true);
		SmartAirService.Connect.getInstance().deleteLinkedDatasets(dataset, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToDeleteDataset());
			}

			@Override
			public void onSuccess(Void result) {
				CommonService.Connect.getInstance().deleteDataset(dataset, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						showWaitPart(false);
						caught.printStackTrace();
						ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToDeleteDataset());
					}

					@Override
					public void onSuccess(Void result) {
						showWaitPart(false);
						onRefreshClick(null);
					}
				});
				
			}
		});
	}

	private void loadStats(List<Dataset> datasets) {
		showWaitPart(true);
		SmartAirService.Connect.getInstance().getStatsDataset(datasets, new AsyncCallback<List<StatDataColumn>>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToLoadStats());
			}

			@Override
			public void onSuccess(List<StatDataColumn> result) {
				showWaitPart(false);
				stats = result;
				datagrid.redraw();
			}
		});
	}

	private List<StatDataColumn> getStatsbyDataset(Dataset dataset) {
		Set<StatDataColumn> result = new HashSet<StatDataColumn>();
		for (StatDataColumn stat : stats) {
			for (DataColumn col : dataset.getMetacolumns()) {
				if (stat.getIdDatacolumn() == col.getId()) {
					result.add(stat);
				}
			}
		}
		return new ArrayList<StatDataColumn>(result);
	}
	
	public void createStats(Dataset dts) {
		showWaitPart(true);
		SmartAirService.Connect.getInstance().createStatsDataset(dts, new AsyncCallback<List<StatDataColumn>>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}

			@Override
			public void onSuccess(List<StatDataColumn> result) {
				showWaitPart(false);
				loadDatasets();
			}
		});
	}

	public AirPanel getAirPanel() {
		return airPanel;
	}
}
