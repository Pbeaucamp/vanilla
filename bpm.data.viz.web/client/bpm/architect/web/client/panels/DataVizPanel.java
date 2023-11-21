package bpm.architect.web.client.panels;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.images.Images;
import bpm.architect.web.client.services.ArchitectService;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.PreparationRule;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomCell;
import bpm.gwt.commons.client.custom.IDoubleClickHandler;
import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.datasource.DatasourceDatasetManager;
import bpm.gwt.commons.client.dialog.MapServersDialog;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.shared.InfoUser;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class DataVizPanel extends Tab {

	private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";

	private static DataVizPanelUiBinder uiBinder = GWT.create(DataVizPanelUiBinder.class);

	interface DataVizPanelUiBinder extends UiBinder<Widget, DataVizPanel> {}

	interface MyStyle extends CssResource {
		String mainPanel();

		String imgGrid();

		String imgPlanned();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel gridPanel;

	@UiField
	Image btnAdd;

	@UiField
	TextHolderBox txtSearch;
	
	@UiField
	HTMLPanel toolbarPanel;
	
	@UiField
	FocusPanel toolbarFocusPanel;

	private InfoUser infoUser;

	private DataGrid<DataPreparation> dataGrid;
	private ListDataProvider<DataPreparation> dataProvider;
	private SingleSelectionModel<DataPreparation> selectionModel;
	private ListHandler<DataPreparation> sortHandler;

	List<DataPreparation> dataPreps;
	
	private TabManager tabManager;

	public DataVizPanel(InfoUser infoUser, TabManager tabManager) {
		super(tabManager, Labels.lblCnst.DataPreparation(), false);
		this.add(uiBinder.createAndBindUi(this));
		this.addStyleName(style.mainPanel());
		this.infoUser = infoUser;

		this.tabManager = tabManager;

		DataGrid<DataPreparation> datagrid = createGrid();
		gridPanel.setWidget(datagrid);

		toolbarFocusPanel.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					onSearch(null);
				}
			}
		});
		
		
		refresh();
	}

	private void refresh() {
		ArchitectService.Connect.getInstance().getDataPreparations(new GwtCallbackWrapper<List<DataPreparation>>(this, true) {
			@Override
			public void onSuccess(List<DataPreparation> result) {
				dataPreps = result;
				dataProvider.setList(result);
				sortHandler.setList(dataProvider.getList());
			}

		}.getAsyncCallback());

	}

	@UiHandler("btnAdd")
	public void onAddDataPreparation(ClickEvent event) {

		DataVizDesignPanel pan = new DataVizDesignPanel(infoUser, tabManager, new DataPreparation());
		tabManager.selectTab(pan.buildTabHeader());

	}
	
	@UiHandler("btnCopy")
	public void onCopyDataPreparation(ClickEvent event) {

		DataPreparation old = selectionModel.getSelectedObject();
		DataPreparation newDp = new DataPreparation();
		newDp.setName(old.getName() + "_copy");
		newDp.setRules(new ArrayList<PreparationRule>(old.getRules()));
		newDp.setDataset(old.getDataset());
		DataVizDesignPanel pan = new DataVizDesignPanel(infoUser, tabManager, newDp);
		tabManager.selectTab(pan.buildTabHeader());

	}

	@UiHandler("btnRefresh")
	public void onRefresh(ClickEvent event) {
		refresh();
	}
	
	@UiHandler("btnSearch")
	public void onSearch(ClickEvent event) {
		List<DataPreparation> list = new ArrayList<>(dataPreps);
		for(DataPreparation dp : dataPreps) {
			if(!dp.getName().toLowerCase().contains(txtSearch.getText().toLowerCase()) && !dp.getDescription().toLowerCase().contains(txtSearch.getText().toLowerCase())) {
				list.remove(dp);
			}
		}
		
		dataProvider.setList(list);
		sortHandler.setList(dataProvider.getList());
	}
	
	@UiHandler("btnDatasource")
	public void onDatasource(ClickEvent event) {
		DatasourceDatasetManager dial = new DatasourceDatasetManager(infoUser.getUser());
		dial.center();
	}

	@UiHandler("btnMapServer")
	public void onMapServers(ClickEvent event) {
		MapServersDialog dial = new MapServersDialog();
		dial.center();
	}

	private DataGrid<DataPreparation> createGrid() {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);

		CustomCell<DataPreparation> cell = new CustomCell<DataPreparation>(new IDoubleClickHandler<DataPreparation>() {
			@Override
			public void run(DataPreparation item) {
				DataVizDesignPanel pan = new DataVizDesignPanel(infoUser, tabManager, item);
				tabManager.selectTab(pan.buildTabHeader());
			}
		});
		Column<DataPreparation, String> colName = new Column<DataPreparation, String>(cell) {

			@Override
			public String getValue(DataPreparation object) {
				return object.getName();
			}
		};
		colName.setSortable(true);

		Column<DataPreparation, String> colDesc = new Column<DataPreparation, String>(cell) {

			@Override
			public String getValue(DataPreparation object) {
				return object.getDescription();
			}
		};
		colDesc.setSortable(true);

		Column<DataPreparation, String> colModificationDate = new Column<DataPreparation, String>(cell) {

			@Override
			public String getValue(DataPreparation object) {
				return dateFormatter.format(object.getCreationDate());
			}
		};
		colModificationDate.setSortable(true);
		
		Column<DataPreparation, String> colUser = new Column<DataPreparation, String>(cell) {

			@Override
			public String getValue(DataPreparation object) {
				return object.getUser().getName();
			}
		};
		colUser.setSortable(true);
		
		Column<DataPreparation, String> colNbCol = new Column<DataPreparation, String>(cell) {

			@Override
			public String getValue(DataPreparation object) {
				try {
					return String.valueOf(object.getNbColumns());
				} catch(Exception e) {
					return "0";
				}
			}
		};
		colNbCol.setSortable(true);

		ButtonImageCell viewCell = new ButtonImageCell(Images.INSTANCE.ic_visibility_black_18dp(), Labels.lblCnst.ViewCurrentDocumentVersion(), style.imgGrid());
		Column<DataPreparation, String> colView = new Column<DataPreparation, String>(viewCell) {

			@Override
			public String getValue(DataPreparation object) {
				return "";
			}
		};
		colView.setFieldUpdater(new FieldUpdater<DataPreparation, String>() {

			@Override
			public void update(int index, final DataPreparation object, String value) {
				DataVizDesignPanel pan = new DataVizDesignPanel(infoUser, tabManager, object);
				tabManager.selectTab(pan.buildTabHeader());
			}
		});

		ButtonImageCell deleteCell = new ButtonImageCell(Images.INSTANCE.ic_delete_black_18dp(), Labels.lblCnst.DeleteDocument(), style.imgGrid());
		Column<DataPreparation, String> colDelete = new Column<DataPreparation, String>(deleteCell) {

			@Override
			public String getValue(DataPreparation object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<DataPreparation, String>() {

			@Override
			public void update(int index, final DataPreparation object, String value) {
				
				final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), Labels.lblCnst.DeleteDataPrepConfirm(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							ArchitectService.Connect.getInstance().deleteDataPreparation(object, new GwtCallbackWrapper<Void>(DataVizPanel.this, true) {
								@Override
								public void onSuccess(Void result) {
									refresh();
								}

							}.getAsyncCallback());
						}
					}
				});
				dial.center();
			}
		});

		sortHandler = new ListHandler<DataPreparation>(new ArrayList<DataPreparation>());
		sortHandler.setComparator(colName, new Comparator<DataPreparation>() {

			@Override
			public int compare(DataPreparation o1, DataPreparation o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		sortHandler.setComparator(colDesc, new Comparator<DataPreparation>() {

			@Override
			public int compare(DataPreparation o1, DataPreparation o2) {
				return o1.getDescription().compareTo(o2.getDescription());
			}
		});
		sortHandler.setComparator(colModificationDate, new Comparator<DataPreparation>() {

			@Override
			public int compare(DataPreparation o1, DataPreparation o2) {
				return o1.getCreationDate().compareTo(o2.getCreationDate());
			}
		});
		sortHandler.setComparator(colUser, new Comparator<DataPreparation>() {

			@Override
			public int compare(DataPreparation o1, DataPreparation o2) {
				return o1.getUser().getName().compareTo(o2.getUser().getName());
			}
		});
		sortHandler.setComparator(colNbCol, new Comparator<DataPreparation>() {

			@Override
			public int compare(DataPreparation o1, DataPreparation o2) {
				return o1.getNbColumns() - o2.getNbColumns();
			}
		});

		DataGrid.Resources resources = new CustomResources();
		dataGrid = new DataGrid<DataPreparation>(1500, resources);
		dataGrid.setSize("100%", "100%");
		dataGrid.addColumn(colName, Labels.lblCnst.Name());
		dataGrid.setColumnWidth(colName, "150px");
		dataGrid.addColumn(colDesc, Labels.lblCnst.Description());
		dataGrid.setColumnWidth(colDesc, "200px");
		dataGrid.addColumn(colModificationDate, Labels.lblCnst.ModificationDate());
		dataGrid.setColumnWidth(colModificationDate, "150px");
		dataGrid.addColumn(colUser, Labels.lblCnst.User());
		dataGrid.setColumnWidth(colUser, "100px");
		dataGrid.addColumn(colNbCol, Labels.lblCnst.Columns());
		dataGrid.setColumnWidth(colNbCol, "50px");
		dataGrid.addColumn(colView);
		dataGrid.setColumnWidth(colView, "70px");
		dataGrid.addColumn(colDelete);
		dataGrid.setColumnWidth(colDelete, "70px");
		dataGrid.addColumnSortHandler(sortHandler);

		dataProvider = new ListDataProvider<DataPreparation>();
		dataProvider.addDataDisplay(dataGrid);

		this.selectionModel = new SingleSelectionModel<DataPreparation>();
		// selectionModel.addSelectionChangeHandler(selectionChange);
		dataGrid.setSelectionModel(selectionModel);

		return dataGrid;
	}

}
