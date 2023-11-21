package bpm.gwt.commons.client.datasource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.dataset.DatasetColumnTypesDialog;
import bpm.gwt.commons.client.dataset.DatasetCreationDialog;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.dialog.LaunchContractETLDialog;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceArchitect;
import bpm.vanilla.platform.core.beans.data.DatasourceType;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
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

public class DatasourceDatasetManager extends AbstractDialogBox {

	private static DatasourceDatasetManagerUiBinder uiBinder = GWT.create(DatasourceDatasetManagerUiBinder.class);

	interface DatasourceDatasetManagerUiBinder extends UiBinder<Widget, DatasourceDatasetManager> {
	}

	interface MyStyle extends CssResource {
		String grid();
		String overflowHidden();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel datasourcePanel, datasetPanel, gridPanelDatasource, gridPanelDataset;

	@UiField
	Image imgAddDatasource, imgDeleteDatasource, imgRefreshDatasource, imgEditDatasource, imgAddDataset, imgDeleteDataset, imgRefreshDataset, imgEditDataset, imgRunETL;

	@UiField
	SimplePanel pagerDatasourcePanel, pagerDatasetPanel;
	
	@UiField
	Image btnClearDatasource, btnClearDataset;

	@UiField
	TextHolderBox txtSearchDatasource, txtSearchDataset;
	
	private User user;

	private DataGrid<Datasource> datasourceGrid;
	private ListDataProvider<Datasource> datasourceDataProvider;
	private ListHandler<Datasource> sortHandler;

	private SingleSelectionModel<Datasource> datasourceSelectionModel;

	private DataGrid<Dataset> datasetGrid;
	private ListDataProvider<Dataset> datasetDataProvider;
	private ListHandler<Dataset> datasetSortHandler;

	private SingleSelectionModel<Dataset> datasetSelectionModel;

	private List<Datasource> datasources;
	private List<Dataset> datasets;
	
	private boolean searchDatasource = false;
	private boolean searchDataset = false;
	
	public DatasourceDatasetManager(User user) {
		super(LabelsConstants.lblCnst.DatasourceManager(), true, true);
		this.user = user;
		
		setWidget(uiBinder.createAndBindUi(this));

		createDatasourceGrid();
		gridPanelDatasource.add(datasourceGrid);

		createDatasetGrid();
		gridPanelDataset.add(datasetGrid);

		refreshDatasource(null);

		datasourceSelectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				loadDatasets(datasourceSelectionModel.getSelectedObject().getDatasets());
			}
		});
		
		createButton(LabelsConstants.lblCnst.Close(), closeHandler);
	}
	
	private void loadDatasources(List<Datasource> datasources) {
		if (datasources == null) {
			datasources = new ArrayList<>();
		}
		this.datasources = datasources;

		List<Datasource> filterDatasources = filterDatasources(datasources);

		datasourceDataProvider.setList(filterDatasources);
		sortHandler.setList(datasourceDataProvider.getList());
	}

	private List<Datasource> filterDatasources(List<Datasource> datasources) {
		if (searchDatasource) {
			String query = txtSearchDatasource.getText();

			List<Datasource> filterDatasources = new ArrayList<>();
			for (Datasource datasource : datasources) {
				if (datasource.getName().startsWith(query)) {
					filterDatasources.add(datasource);
				}
			}
			return filterDatasources;
		}
		else {
			return datasources;
		}
	}
	
	private void loadDatasets(List<Dataset> datasets) {
		if (datasets == null) {
			datasets = new ArrayList<>();
		}
		this.datasets = datasets;

		List<Dataset> filterDatasets = filterDatasets(datasets);

		datasetDataProvider.setList(filterDatasets);
		datasetSortHandler.setList(datasetDataProvider.getList());
	}

	private List<Dataset> filterDatasets(List<Dataset> datasets) {
		if (searchDataset) {
			String query = txtSearchDataset.getText();

			List<Dataset> filterDatasets = new ArrayList<>();
			for (Dataset dataset : datasets) {
				if (dataset.getName().startsWith(query)) {
					filterDatasets.add(dataset);
				}
			}
			return filterDatasets;
		}
		else {
			return datasets;
		}
	}

	@UiHandler("btnClearDatasource")
	public void onClearDatasourceClick(ClickEvent event) {
		this.searchDatasource = false;
		txtSearchDatasource.setText("");
		btnClearDatasource.setVisible(false);
		loadDatasources(datasources);
	}

	@UiHandler("btnSearchDatasource")
	public void onSearchDatasourceClick(ClickEvent event) {
		this.searchDatasource = true;
		btnClearDatasource.setVisible(true);
		loadDatasources(datasources);
	}

	@UiHandler("btnClearDataset")
	public void onClearDatasetClick(ClickEvent event) {
		this.searchDataset = false;
		txtSearchDataset.setText("");
		btnClearDataset.setVisible(false);
		loadDatasets(datasets);
	}

	@UiHandler("btnSearchDataset")
	public void onSearchDatasetClick(ClickEvent event) {
		this.searchDataset = true;
		btnClearDataset.setVisible(true);
		loadDatasets(datasets);
	}
	
	private void createDatasourceGrid() {
		datasourceGrid = new DataGrid<Datasource>(30);
		datasourceGrid.addStyleName(style.grid());

		TextCell cell = new TextCell();

		Column<Datasource, String> colName = new Column<Datasource, String>(cell) {
			@Override
			public String getValue(Datasource object) {
				return object.getName();
			}
		};
		colName.setSortable(true);

		Column<Datasource, String> colType = new Column<Datasource, String>(cell) {
			@Override
			public String getValue(Datasource object) {
				return object.getType().getType();
			}
		};
		colType.setSortable(true);

		datasourceGrid.addColumn(colName, LabelsConstants.lblCnst.Datasource());
		datasourceGrid.addColumn(colType, LabelsConstants.lblCnst.Driver());

		datasourceGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoDatasource()));

		datasourceDataProvider = new ListDataProvider<Datasource>();
		datasourceDataProvider.addDataDisplay(datasourceGrid);

		sortHandler = new ListHandler<Datasource>(new ArrayList<Datasource>());
		sortHandler.setComparator(colName, new Comparator<Datasource>() {

			@Override
			public int compare(Datasource m1, Datasource m2) {
				return m1.getName().compareToIgnoreCase(m2.getName());
			}
		});
		sortHandler.setComparator(colType, new Comparator<Datasource>() {

			@Override
			public int compare(Datasource m1, Datasource m2) {
				return m1.getType().name().compareToIgnoreCase(m2.getType().name());
			}
		});

		datasourceGrid.addColumnSortHandler(sortHandler);

		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName("pageGrid");
		pager.setDisplay(datasourceGrid);
		pager.setPageSize(30);
		pagerDatasourcePanel.setWidget(pager);

		datasourceSelectionModel = new SingleSelectionModel<Datasource>();
		datasourceSelectionModel.addSelectionChangeHandler(new Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				updateUi();
			}
		});
		datasourceGrid.setSelectionModel(datasourceSelectionModel);
	}

	private void createDatasetGrid() {
		datasetGrid = new DataGrid<Dataset>(30);
		datasetGrid.addStyleName(style.grid());

		TextCell cell = new TextCell();

		Column<Dataset, String> colName = new Column<Dataset, String>(cell) {
			@Override
			public String getValue(Dataset object) {
				return object.getName();
			}
		};
		colName.setSortable(true);

		Column<Dataset, String> colReq = new Column<Dataset, String>(cell) {
			@Override
			public String getValue(Dataset object) {
				if(datasourceSelectionModel.getSelectedObject().getType() == DatasourceType.JDBC) {
					return object.getRequest();
				}
				else {
					return "";
				}
			}
		};

		datasetGrid.addColumn(colName, LabelsConstants.lblCnst.Name());
		datasetGrid.addColumn(colReq, LabelsConstants.lblCnst.Query());

		datasetGrid.setColumnWidth(colName, 150, Unit.PX);
		datasetGrid.setColumnWidth(colReq, 350, Unit.PX);

		datasetGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoDatasource()));

		datasetDataProvider = new ListDataProvider<Dataset>();
		datasetDataProvider.addDataDisplay(datasetGrid);
		
		datasetSortHandler = new ListHandler<Dataset>(new ArrayList<Dataset>());
		datasetSortHandler.setComparator(colName, new Comparator<Dataset>() {

			@Override
			public int compare(Dataset m1, Dataset m2) {
				return m1.getName().compareToIgnoreCase(m2.getName());
			}
		});

		datasetGrid.addColumnSortHandler(datasetSortHandler);

		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName("pageGrid");
		pager.setDisplay(datasetGrid);
		pager.setPageSize(30);
		pagerDatasetPanel.setWidget(pager);

		datasetSelectionModel = new SingleSelectionModel<Dataset>();
		datasetGrid.setSelectionModel(datasetSelectionModel);
	}

	private void updateUi() {
		imgRunETL.setVisible(hasInput());
	}

	@UiHandler("imgAddDatasource")
	public void onAdd(ClickEvent e) {
		DatasourceWizard dial = new DatasourceWizard();
		dial.increaseZIndex(4);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				refreshDatasource(null);
			}
		});

		dial.center();
	}

	protected void refreshDatasource(final Datasource selectedDatasource) {
		showWaitPart(true);
		CommonService.Connect.getInstance().getDatasources(new GwtCallbackWrapper<List<Datasource>>(this, true) {
			@Override
			public void onSuccess(List<Datasource> result) {
				loadDatasources(result);

				if (result != null && selectedDatasource != null) {
					for (Datasource datasource : result) {
						if (datasource.getId() == selectedDatasource.getId()) {
							datasourceSelectionModel.setSelected(datasource, true);
							
							loadDatasets(datasourceSelectionModel.getSelectedObject().getDatasets());
							break;
						}
					}
				}
			}
		}.getAsyncCallback());
	}

	@UiHandler("imgEditDatasource")
	public void onEdit(ClickEvent e) {
		final Datasource datasource = datasourceSelectionModel.getSelectedObject();

		DatasourceWizard dial = new DatasourceWizard(datasourceSelectionModel.getSelectedObject());
		dial.increaseZIndex(4);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				refreshDatasource(datasource);
			}
		});

		dial.center();
	}
	
	@UiHandler("imgColumnType")
	public void onEditColumnTypes(ClickEvent e) {
		DatasetColumnTypesDialog dial = new DatasetColumnTypesDialog(datasetSelectionModel.getSelectedObject());
		dial.center();
	}

	@UiHandler("imgRefreshDatasource")
	public void onRefresh(ClickEvent e) {
		final Datasource datasource = datasourceSelectionModel.getSelectedObject();
		refreshDatasource(datasource);
	}

	@UiHandler("imgRunETL")
	public void onRunETL(ClickEvent e) {
		final Datasource datasource = datasourceSelectionModel.getSelectedObject();
		if (hasInput()) {
			LaunchContractETLDialog dial = new LaunchContractETLDialog(((DatasourceArchitect) datasource.getObject()).getContractId());
			dial.center();
		}
	}
	
	private boolean hasInput() {
		final Datasource datasource = datasourceSelectionModel.getSelectedObject();
		if (datasource.getObject() instanceof DatasourceArchitect) {
			DatasourceArchitect dsArchitect = (DatasourceArchitect) datasource.getObject();
			return dsArchitect.hasInput();
		}
		return false;
	}

	@UiHandler("imgDeleteDatasource")
	public void onDelete(ClickEvent e) {
		final Datasource ds = datasourceSelectionModel.getSelectedObject();
		if (ds != null) {
			final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.DeleteDatasource(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.DeleteDatasourcePartOne() + " " + ds.getName() + " " + LabelsConstants.lblCnst.DeleteDatasourcePartTwo(), true);
			dial.increaseZIndex(4);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						CommonService.Connect.getInstance().deleteDatasource(ds, new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Success(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.DeleteDatasourceSuccess(), false);
								dial.center();

								refreshDatasource(null);
							}

							@Override
							public void onFailure(Throwable caught) {
								InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.DeleteDatasourceError(), caught.getMessage(), caught);
								dial.center();
							}
						});
					}
				}
			});
			dial.center();
		}
	}

	@UiHandler("imgAddDataset")
	public void onAddDataset(ClickEvent e) {
		Datasource selectedDatasource = datasourceSelectionModel.getSelectedObject();

		if (selectedDatasource != null) {
			DatasetCreationDialog dial = new DatasetCreationDialog(user, selectedDatasource, null);
			dial.increaseZIndex(4);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					refreshDatasets();
				}
			});

			dial.center();
		}
	}

	@UiHandler("imgEditDataset")
	public void onEditDataset(ClickEvent e) {
		Datasource selectedDatasource = datasourceSelectionModel.getSelectedObject();
		Dataset selectedDataset = datasetSelectionModel.getSelectedObject();

		if (selectedDatasource != null && selectedDataset != null) {
			DatasetCreationDialog dial = new DatasetCreationDialog(user, datasourceSelectionModel.getSelectedObject(), selectedDataset);
			dial.increaseZIndex(4);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					refreshDatasets();
				}
			});
			dial.center();
		}
	}

	protected void refreshDatasets() {
		Datasource selectedDatasource = datasourceSelectionModel.getSelectedObject();
		refreshDatasource(selectedDatasource);
	}

	@UiHandler("imgDeleteDataset")
	public void onDeleteDataset(ClickEvent e) {
		final Dataset ds = datasetSelectionModel.getSelectedObject();
		if (ds != null) {
			final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.DeleteDataset(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.DeleteDatasetPartOne() + " " + ds.getName() + " " + LabelsConstants.lblCnst.DeleteDatasetPartTwo(), true);
			dial.increaseZIndex(4);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						CommonService.Connect.getInstance().deleteDataset(ds, new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Success(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.DeleteDatasetSuccess(), false);
								dial.center();

								refreshDatasets();
							}

							@Override
							public void onFailure(Throwable caught) {
								InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.DeleteDatasetError(), caught.getMessage(), caught);
								dial.center();
							}
						});
					}
				}
			});
			dial.center();
		}
	}

	private ClickHandler closeHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
