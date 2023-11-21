package bpm.map.viewer.web.client.panel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.fm.api.model.ComplexMap;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.map.viewer.web.client.I18N.LabelsConstants;
import bpm.map.viewer.web.client.panel.viewer.MapViewer;
import bpm.map.viewer.web.client.services.MapViewerService;
import bpm.map.viewer.web.client.wizard.AddComplexMapWizard;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
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

public class DesignerPanel extends Tab {

	private static DesignerPanelUiBinder uiBinder = GWT.create(DesignerPanelUiBinder.class);
	public static LabelsConstants lblCnst = (LabelsConstants) GWT.create(LabelsConstants.class);

	interface DesignerPanelUiBinder extends UiBinder<Widget, DesignerPanel> {
	}
	
	interface MyStyle extends CssResource {
		String mainPanel();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel panelToolbar;
	
	@UiField
	SimplePanel panelContent, panelPager;
	
	@UiField
	Image btnEditMap, btnDeleteMap, btnRefresh, btnAddMap;
	
	private ContentDisplayPanel mainPanel;
	private ListDataProvider<ComplexMap> dataProvider;
	private SingleSelectionModel<ComplexMap> selectionModel;
	private ListHandler<ComplexMap> sortHandler;
	private DataGrid<ComplexMap> datagrid;
	private boolean edit;

	public DesignerPanel(ContentDisplayPanel mainPanel) {
		super(mainPanel, lblCnst.Designer(), false);
		this.add(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		
		
		this.addStyleName(style.mainPanel());
		this.datagrid = createGridData();
		panelContent.add(datagrid);
		this.edit = false;
		
		loadEvent();
	
	}
	
	
	private void loadEvent() {
		showWaitPart(true);
		MapViewerService.Connect.getInstance().getMaps(new AsyncCallback<List<ComplexMap>>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, lblCnst.UnableToLoadMaps());
			}

			@Override
			public void onSuccess(List<ComplexMap> result) {
				showWaitPart(false);

				dataProvider.setList(result);
				sortHandler.setList(dataProvider.getList());
				selectionModel.clear();
				
				
			}
		});
	}

	private DataGrid<ComplexMap> createGridData() {
		TextCell cell = new TextCell();
		Column<ComplexMap, String> nameColumn = new Column<ComplexMap, String>(cell) {

			@Override
			public String getValue(ComplexMap object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);

		Column<ComplexMap, String> metricColumn = new Column<ComplexMap, String>(cell) {

			@Override
			public String getValue(ComplexMap object) {
				return object.getMetricsName().toString();
			}
		};
		
		Column<ComplexMap, String> axisColumn = new Column<ComplexMap, String>(cell) {

			@Override
			public String getValue(ComplexMap object) {
				return object.getAxisName().toString();
			}
		};

		
		
		//DataGrid.Resources resources = new CustomResources();
		DataGrid<ComplexMap> dataGrid = new DataGrid<ComplexMap>(12);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		
		// Attention au label 
		dataGrid.addColumn(nameColumn, lblCnst.MapName());
		dataGrid.addColumn(metricColumn, lblCnst.MetricName());
		dataGrid.addColumn(axisColumn, lblCnst.AxisName());
		
		
		dataGrid.setEmptyTableWidget(new Label(lblCnst.NoResult()));

		dataProvider = new ListDataProvider<ComplexMap>();
		dataProvider.addDataDisplay(dataGrid);
		
		sortHandler = new ListHandler<ComplexMap>(new ArrayList<ComplexMap>());
		sortHandler.setComparator(nameColumn, new Comparator<ComplexMap>() {
			
			@Override
			public int compare(ComplexMap m1, ComplexMap m2) {
				return m1.getName().compareTo(m2.getName());
			}
		});
		
		dataGrid.addColumnSortHandler(sortHandler);

		// Add a selection model so we can select cells.
		selectionModel = new SingleSelectionModel<ComplexMap>();
		selectionModel.addSelectionChangeHandler(selectionChangeHandler);
		dataGrid.setSelectionModel(selectionModel);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		//pager.addStyleName(style.pager());
		pager.setDisplay(dataGrid);
		
		panelPager.setWidget(pager);

		return dataGrid;
	}
	
	@UiHandler("btnRefresh")
	public void onRefreshClick(ClickEvent event) {
		loadEvent();
	}
	
	@UiHandler("btnAddMap")
	public void onAddMapClick(ClickEvent event) {
		AddComplexMapWizard wiz = new AddComplexMapWizard(this);
		wiz.center();
	}

	public void addMap(ComplexMap map) {
		showWaitPart(true);

		MapViewerService.Connect.getInstance().addOrEditMap(map, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				showWaitPart(false);

				loadEvent();
				
				mainPanel.updateViewerPanel();
				
				
			}

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, lblCnst.UnableToAddAMap());
			}
		});
	}
	
	
	@UiHandler("btnEditMap")
	public void onEditClick(ClickEvent event) {
		ComplexMap selectedMap = selectionModel.getSelectedObject();
		if (selectedMap == null) {
			return;
		}

		AddComplexMapWizard wiz = new AddComplexMapWizard(this, selectedMap);
		wiz.center();
	}
	
	@UiHandler("btnDeleteMap")
	public void onDeleteClick(ClickEvent event) {
		deleteMap();
		mainPanel.updateViewerPanel();
	}
	
	

	private Widget createDataGrid() {
		return null;
	}

	private void deleteMap() {
		final ComplexMap selectedMap = selectionModel.getSelectedObject();
		if (selectedMap == null) {
			return;
		}
		
		final InformationsDialog dialConfirm = new InformationsDialog(lblCnst.Information(), lblCnst.Ok(), lblCnst.Cancel(), lblCnst.ConfirmDeleteMap(), true);
		dialConfirm.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dialConfirm.isConfirm()) {
					showWaitPart(true);

					MapViewerService.Connect.getInstance().deleteMap(selectedMap, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							showWaitPart(false);

							caught.printStackTrace();

							ExceptionManager.getInstance().handleException(caught,lblCnst.UnableToDeleteMap());
						}

						@Override
						public void onSuccess(Void result) {
							showWaitPart(false);
							MessageHelper.openMessageDialog(lblCnst.Information(), lblCnst.DeleteMapSuccessfull());

							loadEvent();
						}
					});
				}
			}
		});
		dialConfirm.center();
	}
	
	private Handler selectionChangeHandler = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			ComplexMap selectedMap = selectionModel.getSelectedObject();
			btnEditMap.setVisible(selectedMap != null);
//			btnPreviewMap.setVisible(selectedMap != null);
			btnDeleteMap.setVisible(selectedMap != null);
			
			
		}
	};

	public DataGrid<ComplexMap> getDatagrid() {
		return datagrid;
	}

	

}
