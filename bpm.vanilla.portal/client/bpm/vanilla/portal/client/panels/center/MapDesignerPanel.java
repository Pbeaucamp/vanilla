package bpm.vanilla.portal.client.panels.center;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.portal.client.dialog.DialogMapMetadataMapping;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.client.widget.custom.CustomResources;
import bpm.vanilla.portal.client.wizard.map.AddMapWizard;
import bpm.vanilla.portal.shared.MapFeatures;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.ChangeEvent;
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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;


public class MapDesignerPanel extends Tab {
	
	private static final String MAP_ID = "osmMapContainer2";

	private static MapDesignerPanelUiBinder uiBinder = GWT.create(MapDesignerPanelUiBinder.class);

	interface MapDesignerPanelUiBinder extends UiBinder<Widget, MapDesignerPanel> {
	}
	
	interface MyStyle extends CssResource {
		String mainPanel();
		String pager();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelToolbar, panelMap;

	@UiField
	ListBox lstDataSet;

	@UiField
	SimplePanel panelContent, panelPager, panelPreviewMap;

	@UiField
	Image btnEditMap, btnDeleteMap, btnPreviewMap, btnRefresh, btnAddMap, btnMappingMetadata;

	private ListDataProvider<MapVanilla> dataProvider;
	private SingleSelectionModel<MapVanilla> selectionModel;
	private ListHandler<MapVanilla> sortHandler;

	private List<MapFeatures> mapFeatures;
	private String selectedType;

	private HorizontalPanel main;

	public MapDesignerPanel(TabManager tabManager) {
		super(tabManager, ToolsGWT.lblCnst.Map(), true);
		this.add(uiBinder.createAndBindUi(this));
		panelContent.add(createGridData());
		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
		this.addStyleName(style.mainPanel());
		
		panelMap.getElement().setId(MAP_ID);
		
		lstDataSet.setVisible(false);

		loadEvent();
	}

	private void loadEvent() {
		BiPortalService.Connect.getInstance().getMaps(new GwtCallbackWrapper<List<MapVanilla>>(this, true, true) {

			@Override
			public void onSuccess(List<MapVanilla> result) {
				dataProvider.setList(result);
				sortHandler.setList(dataProvider.getList());
				selectionModel.clear();
			}
		}.getAsyncCallback());
	}

	private DataGrid<MapVanilla> createGridData() {
		TextCell cell = new TextCell();
		Column<MapVanilla, String> nameColumn = new Column<MapVanilla, String>(cell) {

			@Override
			public String getValue(MapVanilla object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);

		Column<MapVanilla, String> descriptionColumn = new Column<MapVanilla, String>(cell) {

			@Override
			public String getValue(MapVanilla object) {
				return object.getDescription();
			}
		};

		Column<MapVanilla, String> zoomColumn = new Column<MapVanilla, String>(cell) {

			@Override
			public String getValue(MapVanilla object) {
				return Integer.toString(object.getZoom());
			}
		};

		Column<MapVanilla, String> originLatColumn = new Column<MapVanilla, String>(cell) {

			@Override
			public String getValue(MapVanilla object) {
				return Double.toString(object.getOriginLat());
			}
		};

		Column<MapVanilla, String> originLongColumn = new Column<MapVanilla, String>(cell) {

			@Override
			public String getValue(MapVanilla object) {
				return Double.toString(object.getOriginLong());
			}
		};

		Column<MapVanilla, String> boundLeftColumn = new Column<MapVanilla, String>(cell) {

			@Override
			public String getValue(MapVanilla object) {
				return Double.toString(object.getBoundLeft());
			}
		};

		Column<MapVanilla, String> boundBottomColumn = new Column<MapVanilla, String>(cell) {

			@Override
			public String getValue(MapVanilla object) {
				return Double.toString(object.getBoundBottom());
			}
		};

		Column<MapVanilla, String> boundRightColumn = new Column<MapVanilla, String>(cell) {

			@Override
			public String getValue(MapVanilla object) {
				return Double.toString(object.getBoundRight());
			}
		};

		Column<MapVanilla, String> boundTopColumn = new Column<MapVanilla, String>(cell) {

			@Override
			public String getValue(MapVanilla object) {
				return Double.toString(object.getBoundTop());
			}
		};

		Column<MapVanilla, String> projectionColumn = new Column<MapVanilla, String>(cell) {

			@Override
			public String getValue(MapVanilla object) {
				return object.getProjection();
			}
		};

		DataGrid.Resources resources = new CustomResources();
		DataGrid<MapVanilla> dataGrid = new DataGrid<MapVanilla>(12, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");

		// Attention au label
		dataGrid.addColumn(nameColumn, ToolsGWT.lblCnst.MapName());
		dataGrid.addColumn(descriptionColumn, ToolsGWT.lblCnst.MapDescription());
		dataGrid.addColumn(zoomColumn, ToolsGWT.lblCnst.MapZoom());
		dataGrid.addColumn(originLatColumn, ToolsGWT.lblCnst.MapOriginLat());
		dataGrid.addColumn(originLongColumn, ToolsGWT.lblCnst.MapOriginLong());
		dataGrid.addColumn(boundLeftColumn, ToolsGWT.lblCnst.MapBoundLeft());
		dataGrid.addColumn(boundBottomColumn, ToolsGWT.lblCnst.MapBoundBottom());
		dataGrid.addColumn(boundRightColumn, ToolsGWT.lblCnst.MapBoundRight());
		dataGrid.addColumn(boundTopColumn, ToolsGWT.lblCnst.MapBoundTop());
		dataGrid.addColumn(projectionColumn, ToolsGWT.lblCnst.MapProjection());

		dataGrid.setEmptyTableWidget(new Label(ToolsGWT.lblCnst.NoResult()));

		dataProvider = new ListDataProvider<MapVanilla>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<MapVanilla>(new ArrayList<MapVanilla>());
		sortHandler.setComparator(nameColumn, new Comparator<MapVanilla>() {

			@Override
			public int compare(MapVanilla m1, MapVanilla m2) {
				return m1.getName().compareTo(m2.getName());
			}
		});
		dataGrid.addColumnSortHandler(sortHandler);

		// Add a selection model so we can select cells.
		selectionModel = new SingleSelectionModel<MapVanilla>();
		selectionModel.addSelectionChangeHandler(selectionChangeHandler);
		dataGrid.setSelectionModel(selectionModel);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(style.pager());
		pager.setDisplay(dataGrid);

		panelPager.setWidget(pager);

		return dataGrid;
	}

	@UiHandler("btnRefresh")
	public void onRefreshClick(ClickEvent event) {
		if (panelContent.isVisible() == false) {
			panelContent.setVisible(true);
			panelPager.setVisible(true);
			panelPreviewMap.setVisible(false);
			lstDataSet.setVisible(false);
		}

		loadEvent();
	}

	@UiHandler("btnAddMap")
	public void onAddMapClick(ClickEvent event) {
		if (panelContent.isVisible() == false) {
			panelContent.setVisible(true);
			panelPager.setVisible(true);
			panelPreviewMap.setVisible(false);
			lstDataSet.setVisible(false);
		}

		AddMapWizard wiz = new AddMapWizard(this);
		wiz.center();
	}

	public void addMap(MapVanilla map) {
		BiPortalService.Connect.getInstance().addOrEditMap(map, new GwtCallbackWrapper<Void>(this, true, true) {

			@Override
			public void onSuccess(Void result) {
				loadEvent();
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnEditMap")
	public void onEditClick(ClickEvent event) {
		if (panelContent.isVisible() == false) {
			panelContent.setVisible(true);
			panelPager.setVisible(true);
			panelPreviewMap.setVisible(false);
			lstDataSet.setVisible(false);
		}

		MapVanilla selectedMap = selectionModel.getSelectedObject();
		if (selectedMap == null) {
			return;
		}

		AddMapWizard wiz = new AddMapWizard(this, selectedMap);
		wiz.center();
	}

	@UiHandler("btnDeleteMap")
	public void onDeleteClick(ClickEvent event) {
		if (panelContent.isVisible() == false) {
			panelContent.setVisible(true);
			panelPager.setVisible(true);
			panelPreviewMap.setVisible(false);
			lstDataSet.setVisible(false);
		}

		deleteMap();
	}

	@UiHandler("btnPreviewMap")
	public void onPreviewMapClick(ClickEvent event) {
		if (panelContent.isVisible() == true) {
			panelContent.setVisible(false);
			panelPager.setVisible(false);
			lstDataSet.setVisible(true);

			panelPreviewMap.setVisible(true);

			previewMap();
		}
		else {
			panelContent.setVisible(true);
			panelPager.setVisible(true);
			panelPreviewMap.setVisible(false);
			lstDataSet.setVisible(false);
		}
	}
	
	@UiHandler("btnMappingMetadata")
	public void onMappingMetadataClick(ClickEvent event) {
		MapVanilla selectedMap = selectionModel.getSelectedObject();
		DialogMapMetadataMapping dial = new DialogMapMetadataMapping(selectedMap);
		dial.center();
	}

	@UiHandler("lstDataSet")
	public void onDataSetChange(ChangeEvent event) {
		previewMap();
	}

	private void previewMap() {
		MapVanilla selectedMap = selectionModel.getSelectedObject();
		if (selectedMap == null) {
			return;
		}
		MapDataSet selectedDataSet = new MapDataSet();
		String res = lstDataSet.getValue(lstDataSet.getSelectedIndex());
		for (MapDataSet dtS : selectedMap.getDataSetList()) {
			if (dtS.getName().equals(res)) {
				selectedDataSet = dtS;
				break;
			}

		}
		if (selectedDataSet.equals(null)) {
			return;
		}
		this.selectedType = selectedDataSet.getType();

		lstDataSet.setEnabled(false);

		panelMap.clear();
		BiPortalService.Connect.getInstance().getOsmValuesbyDataSet(selectedMap, selectedDataSet, new GwtCallbackWrapper<List<MapFeatures>>(this, true, true) {

			@Override
			public void onSuccess(List<MapFeatures> result) {
				if(result.get(0).getMap().getType().equalsIgnoreCase("KML") || result.get(0).getMap().getType().equalsIgnoreCase("WFS")) {
					selectedType = result.get(0).getLatitudes().size() > 1 ? "polygon" : "point";
				}
				MapDesignerPanel.this.mapFeatures = result;
				try {
					loadMap();
					lstDataSet.setEnabled(true);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}.getAsyncCallback());
	}

	protected void loadMap() {
		JsArray<JsArray> jsArray = JsArray.createArray().cast();
		MapVanilla map = mapFeatures.get(0).getMap();
		
		for (MapFeatures val : mapFeatures) {
			JsArray<JsArrayString> zone = JsArray.createArray().cast();
			for (int i = 0; i < val.getLatitudes().size(); i++) {
				JsArrayString latLong = JsArrayString.createArray().cast();

				String lati = val.getLatitudes().get(i);
				String longi = val.getLongitudes().get(i);

				latLong.push(longi);
				latLong.push(lati);
				zone.push(latLong);
			}
			jsArray.push(zone);
		}

		MapVanilla selectedMap = selectionModel.getSelectedObject();

		MapDataSet selectedDataSet = new MapDataSet();
		String res = lstDataSet.getValue(lstDataSet.getSelectedIndex());
		for (MapDataSet dtS : selectedMap.getDataSetList()) {
			if (dtS.getName().equals(res)) {
				selectedDataSet = dtS;
				break;
			}

		}

		String markerUrl = selectedDataSet.getMarkerUrl();
		if (markerUrl != null && markerUrl.contains("webapps")) {
			markerUrl = markerUrl.substring(markerUrl.indexOf("webapps") + "webapps".length(), markerUrl.length());
			markerUrl = GWT.getHostPageBaseURL() + ".." + markerUrl.replace("\\", "/");
		}
		else if (markerUrl == null) {
			markerUrl = "";
		}

		previewMap(MAP_ID, map.getOriginLat(), map.getOriginLong(), map.getZoom(), map.getBoundLeft(), map.getBoundRight(), map.getBoundTop(), map.getBoundBottom(), map.getProjection(), selectedType, jsArray, markerUrl);
	}

	private void deleteMap() {
		final MapVanilla selectedMap = selectionModel.getSelectedObject();
		if (selectedMap == null) {
			return;
		}

		final InformationsDialog dialConfirm = new InformationsDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.Ok(), ToolsGWT.lblCnst.Cancel(), ToolsGWT.lblCnst.ConfirmDeleteMap(), true);
		dialConfirm.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dialConfirm.isConfirm()) {
					BiPortalService.Connect.getInstance().deleteMap(selectedMap, new GwtCallbackWrapper<Void>(MapDesignerPanel.this, true, true) {

						@Override
						public void onSuccess(Void result) {
							MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.DeleteMapSuccessfull());

							loadEvent();
						}
					}.getAsyncCallback());
				}
			}
		});
		dialConfirm.center();
	}

	private Handler selectionChangeHandler = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			MapVanilla selectedMap = selectionModel.getSelectedObject();
			btnEditMap.setVisible(selectedMap != null);
			btnPreviewMap.setVisible(selectedMap != null);
			btnDeleteMap.setVisible(selectedMap != null);

			lstDataSet.clear();
			if (selectedMap != null && selectedMap.getDataSetList() != null && !selectedMap.getDataSetList().isEmpty()) {
				for (MapDataSet dtS : selectedMap.getDataSetList()) {
					lstDataSet.addItem(dtS.getName());
				}
			}
		}
	};

	private final native void previewMap(String div, double lat, double longi, int zoom, double minLat, double maxLat, double minLong, double maxLong, String projection, String type, JsArray features, String marker) /*-{		
		$wnd.previewMapPortail(div, lat, longi, zoom, minLong, minLat, maxLong, maxLat, projection, type, features, marker);
	}-*/;
}
