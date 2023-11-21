package bpm.vanilla.portal.client.wizard.map;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapDataSource;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.client.widget.custom.CustomResources;
import bpm.vanilla.portal.shared.MapFeatures;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Style.Display;
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
import com.google.gwt.user.client.ui.Composite;
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

/*
 * Kevin Monnery
 * 
 */

public class AddMapDataSetPage extends Composite implements IGwtPage {
	private static AddMapDataSetPageUiBinder uiBinder = GWT.create(AddMapDataSetPageUiBinder.class);

	interface AddMapDataSetPageUiBinder extends UiBinder<Widget, AddMapDataSetPage> {}

	interface MyStyle extends CssResource {
		
	}
	
	@UiField
	HTMLPanel panelToolbar;
	
	@UiField
	SimplePanel panelContent, panelPager, panelPreviewMap;
	
	@UiField
	Image btnEditDataSet, btnDeleteDataSet, btnPreviewDataSet, btnAddDataSet;
	
	
	@UiField
	MyStyle style;

	
	public ListDataProvider<MapDataSet> dataProvider;
	private SingleSelectionModel<MapDataSet> selectionModel;
	private ListHandler<MapDataSet> sortHandler;
	
	public AddMapWizard parent;
	private int index;
	private MapDataSource dtSource;
	
	private List<MapFeatures> mapFeatures;
	private String selectedType;
	
	private HTMLPanel osmPanel;
	
	private List<MapDataSet> datasets;

	public AddMapDataSetPage(AddMapWizard parent, int index, MapDataSource dtSource) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;
		this.dtSource = dtSource;
		
		panelContent.add(createGridData());
		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
		sortHandler.setList(dataProvider.getList());
		selectionModel.clear();
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return false;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isComplete() {
		
//		if(dataProvider.getList().size() > 0){
			return true;
//		}else {
//			return false;
//		}
		
	}
	
	
	
	public List<MapDataSet> getMapDataSetList(){
		List<MapDataSet> mapDataSetList = new ArrayList<MapDataSet>(dataProvider.getList());
		
		return mapDataSetList;
	}
	
	public void setDataSetList(List<MapDataSet> dataSetList) {
		if(dataSetList != null && !dataSetList.isEmpty()) {
			datasets = dataSetList;
			for(MapDataSet dtS : dataSetList) {
				dataProvider.getList().add(dtS);
			}
		}
	}
	
	public List<MapDataSet> getDatasets() {
		return dataProvider.getList();
	}

	
	private DataGrid<MapDataSet> createGridData() {
		TextCell cell = new TextCell();
		
//		Column<MapDataSet, String> idColumn = new Column<MapDataSet, String>(cell) {
//
//			@Override
//			public String getValue(MapDataSet object) {
//				return Integer.toString(object.getId());
//			}
//		};
		
		Column<MapDataSet, String> nameColumn = new Column<MapDataSet, String>(cell) {

			@Override
			public String getValue(MapDataSet object) {
				return object.getName();
			}
		};
		
//		Column<MapDataSet, String> queryColumn = new Column<MapDataSet, String>(cell) {
//
//			@Override
//			public String getValue(MapDataSet object) {
//				return object.getQuery();
//			}
//		};
		
		Column<MapDataSet, String> latitudeColumn = new Column<MapDataSet, String>(cell) {

			@Override
			public String getValue(MapDataSet object) {
				return object.getLatitude();
			}
		};

		Column<MapDataSet, String> longitudeColumn = new Column<MapDataSet, String>(cell) {

			@Override
			public String getValue(MapDataSet object) {
				return object.getLongitude();
			}
		};

		Column<MapDataSet, String> idZoneColumn = new Column<MapDataSet, String>(cell) {

			@Override
			public String getValue(MapDataSet object) {
				return object.getIdZone();
			}
		};
		
		Column<MapDataSet, String> labelZone = new Column<MapDataSet, String>(cell) {

			@Override
			public String getValue(MapDataSet object) {
				return object.getZoneLabel();
			}
		};

		Column<MapDataSet, String> parentColumn = new Column<MapDataSet, String>(cell) {

			@Override
			public String getValue(MapDataSet object) {
				return object.getParent();
			}
		};
		parentColumn.setSortable(true);
		Column<MapDataSet, String> typeColumn = new Column<MapDataSet, String>(cell) {

			@Override
			public String getValue(MapDataSet object) {
				return object.getType();
			}
		};
		
//		Column<MapDataSet, String> idMapVanillaColumn = new Column<MapDataSet, String>(cell) {
//
//			@Override
//			public String getValue(MapDataSet object) {
//				return Integer.toString(object.getIdMapVanilla());
//			}
//		};
		
		
		
		DataGrid.Resources resources = new CustomResources();
		DataGrid<MapDataSet> dataGrid = new DataGrid<MapDataSet>(5, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		
//		dataGrid.addColumn(idColumn, ToolsGWT.lblCnst.DataSetId()); 
		dataGrid.addColumn(nameColumn, ToolsGWT.lblCnst.DataSetName());
//		dataGrid.addColumn(queryColumn, ToolsGWT.lblCnst.DataSetQuery());
		dataGrid.addColumn(idZoneColumn, ToolsGWT.lblCnst.DataSetIdZone());
		dataGrid.addColumn(labelZone, ToolsGWT.lblCnst.MapLabelZone());
		dataGrid.addColumn(latitudeColumn, ToolsGWT.lblCnst.DataSetLat());
		dataGrid.addColumn(longitudeColumn, ToolsGWT.lblCnst.DataSetLong());
		
		dataGrid.addColumn(parentColumn, ToolsGWT.lblCnst.DataSetParent());
		dataGrid.addColumn(typeColumn, ToolsGWT.lblCnst.DataSetType());
//		dataGrid.addColumn(idMapVanillaColumn, ToolsGWT.lblCnst.DataSetIdMap());
		
		dataGrid.setEmptyTableWidget(new Label(ToolsGWT.lblCnst.NoResult()));

		dataProvider = new ListDataProvider<MapDataSet>();
		dataProvider.addDataDisplay(dataGrid);
		
		sortHandler = new ListHandler<MapDataSet>(new ArrayList<MapDataSet>());
//		sortHandler.setComparator(idColumn, new Comparator<MapDataSet>() {
//			
//			@Override
//			public int compare(MapDataSet m1, MapDataSet m2) {
//				return m1.getId() - m2.getId();
//			}
//		});
		
		dataGrid.addColumnSortHandler(sortHandler);

		// Add a selection model so we can select cells.
		selectionModel = new SingleSelectionModel<MapDataSet>();
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
	
	
	@UiHandler("btnAddDataSet")
	public void onAddDataSetClick(ClickEvent event) {
		MapDataSetDialog wiz = new MapDataSetDialog(this, dtSource);
		wiz.center();
	}

	
	
	@UiHandler("btnEditDataSet")
	public void onEditClick(ClickEvent event) {
		MapDataSet selectedDataSet = selectionModel.getSelectedObject();
		if (selectedDataSet == null) {
			return;
		}

		MapDataSetDialog wiz = new MapDataSetDialog(this, dtSource, selectedDataSet);
		wiz.center();
	}
	
	@UiHandler("btnDeleteDataSet")
	public void onDeleteClick(ClickEvent event) {
		deleteDataSet();
	}
	
	@UiHandler("btnPreviewDataSet")
	public void onPreviewDataSetClick(ClickEvent event) {
		MapDataSet selectedDataSet = selectionModel.getSelectedObject();
		if (selectedDataSet == null) {
			return;
		}
		selectedType = selectedDataSet.getType();
		if(panelContent.isVisible() == true) {
			
			parent.showWaitPart(true);
			panelContent.setVisible(false);
			panelPager.setVisible(false);
			panelPreviewMap.setVisible(true);
			panelPreviewMap.getElement().getStyle().setDisplay(Display.BLOCK);
			//panelPreviewMap.setWidget();
			// Modifier l'option selectionnee
			
				BiPortalService.Connect.getInstance().getOsmValuesbyDataSet(parent.getCurrentMapVanilla(), selectedDataSet, new AsyncCallback<List<MapFeatures>>() {

					@Override
					public void onFailure(Throwable caught) {
						ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableToLoadMap());
						parent.showWaitPart(false);
					}

					@Override
					public void onSuccess(List<MapFeatures> result) {
						parent.showWaitPart(false);
						mapFeatures = result;
						buildOsmMap(panelPreviewMap, mapFeatures);
						
						try {
							Load();
						}
						catch (Throwable e){
							e.printStackTrace();
						}
					}
				});
		}
		else {
			panelContent.setVisible(true);
			panelPager.setVisible(true);
			panelPreviewMap.setVisible(false);
			panelPreviewMap.getElement().getStyle().setDisplay(Display.NONE);
			panelPreviewMap.clear();
		}
	}


	private void deleteDataSet() {
		final MapDataSet selectedDataSet = selectionModel.getSelectedObject();
		if (selectedDataSet == null) {
			return;
		}
		
		final InformationsDialog dialConfirm = new InformationsDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.Ok(), ToolsGWT.lblCnst.Cancel(), ToolsGWT.lblCnst.ConfirmDeleteDataSet(), true);
		dialConfirm.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dialConfirm.isConfirm()) {
					
					dataProvider.getList().remove(selectedDataSet);
					dataProvider.refresh();
				
				}
			}
		});
		dialConfirm.center();
	}
	
	private Handler selectionChangeHandler = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			MapDataSet selectedDataSet = selectionModel.getSelectedObject();
			btnEditDataSet.setVisible(selectedDataSet != null);
			btnPreviewDataSet.setVisible(selectedDataSet != null);
			btnDeleteDataSet.setVisible(selectedDataSet != null);
		}
	};
	
	
	protected void Load() {
		JsArray<JsArray> jsArray = JsArray.createArray().cast();
		MapVanilla map = mapFeatures.get(0).getMap();
		
		for(MapFeatures val : mapFeatures) {
			JsArray<JsArrayString> zone = JsArray.createArray().cast();
			for(int i = 0 ; i < val.getLatitudes().size(); i++) {
				JsArrayString  latLong = JsArrayString.createArray().cast();
				
				String lati = val.getLatitudes().get(i);
				String longi = val.getLongitudes().get(i);
				
				latLong.push(longi);
				latLong.push(lati);
				zone.push(latLong);
			}
			jsArray.push(zone);
		}
		MapDataSet selectedDataSet = selectionModel.getSelectedObject();
		
		String markerUrl = selectedDataSet.getMarkerUrl();
		if(markerUrl != null && markerUrl.contains("webapps"))  {
			markerUrl = markerUrl.substring(markerUrl.indexOf("webapps") + "webapps".length(), markerUrl.length());
			markerUrl = GWT.getHostPageBaseURL() + ".." + markerUrl.replace("\\", "/");
		}
		else if(markerUrl == null){
			markerUrl = "";
		}
		
//		markerUrl = "http://192.168.2.11:8889" + markerUrl;
		
		previewMap("osmMapContainer", map.getOriginLat(), map.getOriginLong(), map.getZoom(), map.getBoundLeft(), map.getBoundRight(), map.getBoundTop(), map.getBoundBottom(), map.getProjection(), selectedType, jsArray, markerUrl);
	}
	
	private final native void previewMap(String div, double lat, double longi, int zoom, double minLat, double maxLat, double minLong, double maxLong, String projection, String type, JsArray features, String marker) /*-{		
		$wnd.previewMap(div, lat, longi, zoom, minLong, minLat, maxLong, maxLat, projection, type, features, marker);
	}-*/;
	
	private void buildOsmMap(SimplePanel mainPanel, List<MapFeatures> values) {
//		mainPanel.clear();
		mainPanel.setWidth("100%");
		this.mapFeatures = values;
		
		this.osmPanel = new HTMLPanel("");
		osmPanel.setSize("100%", "100%");
		osmPanel.getElement().setId("osmMapContainer");
		if(mainPanel.getWidget() == null) {
			mainPanel.add(osmPanel);
		}
	}
}
