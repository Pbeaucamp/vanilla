package bpm.faweb.client.panels.center;

import java.util.ArrayList;
import java.util.List;

import bpm.data.viz.core.preparation.LinkItem;
import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dialog.ColorDialog;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.shared.MapOptions;
import bpm.faweb.shared.MapValues;
import bpm.faweb.shared.infoscube.MapInfo;
import bpm.gwt.commons.client.dialog.SelectLayersDialog;
import bpm.gwt.commons.client.panels.MapBullePopup;
import bpm.gwt.commons.client.panels.MapPopup;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapInformation;
import bpm.vanilla.map.core.design.MapLayer;
import bpm.vanilla.map.core.design.MapLayerOption;
import bpm.vanilla.map.core.design.MapServer.TypeServer;
import bpm.vanilla.map.core.design.MapVanilla;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class MapTab extends Tab implements IColor {
	private enum TypeFeature {
		POLYGON("Polygon"),
		LINE("LineString"),
		POINT("Point");
		
		private String label;
		
		private TypeFeature(String label) {
			this.label = label;
		}
		
		public String getLabel() {
			return label;
		}
	}
	
	private static MapTabUiBinder uiBinder = GWT.create(MapTabUiBinder.class);

	interface MapTabUiBinder extends UiBinder<Widget, MapTab> {
	}
	
	interface MyStyle extends CssResource {
		String padding();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelToolbar, osmPanel, optionsPanel, colorsPanel, panelPopup;

	private MainPanel mainPanel;

	private ListBox lstDatasets;
	private ListBox listDimensions;
	private ListBox listMeasures;
	private ListBox listMapAvailable;
	
	private FlexTable flexTable;

	private List<String> measuresDisplay = new ArrayList<String>();
	private List<String> dimensions = new ArrayList<String>();
	private List<String> mapAvailables = new ArrayList<String>();

	private List<MapVanilla> maps = new ArrayList<MapVanilla>();
	
	private MapOptions mapOptions;
	private List<MapValues> mapValues;
	private List<List<String>> colors;

	public MapTab(TabManager tabManager, MainPanel mainPanel, MapInfo mapInfo, MapOptions mapOptions, List<MapValues> values) {
		super(tabManager, FreeAnalysisWeb.LBL.Maps(), true);
		add(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.measuresDisplay = mapInfo.getMeasures();
		this.maps = mapInfo.getMaps();
		this.dimensions = mapInfo.getDimensions();
		this.mapOptions = mapOptions;
		this.mapValues = values;
		this.colors = mapOptions.getColors();

		osmPanel.getElement().setId("osmMapContainer");
		buildComboList(mapOptions, values);
		buildColorTable();

		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
		
		refreshPopup();
	}
	
	private void refreshPopup() {
		panelPopup.clear();
		panelPopup.add(new MapPopup(new ArrayList<LinkItem>()));
		panelPopup.add(new MapBullePopup());
	}

	@UiHandler("imgSave")
	public void onSaveClick(ClickEvent event) {
		mainPanel.getDisplayPanel().getCubeViewerTab().save();
	}
	
	@UiHandler("imgUpdate")
	public void onUpdateClick(ClickEvent event) {
		mainPanel.getDisplayPanel().getCubeViewerTab().update();
	}

	@UiHandler("btnLayers")
	public void onLayers(ClickEvent event) {
		MapInformation mapInfo = mapOptions.getMapInfo();
		
		final SelectLayersDialog dial = new SelectLayersDialog(mapInfo);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					MapInformation mapInformation = dial.getMapInformation();
					mapOptions.setMapInfo(mapInformation);

					onRefresh(null);
				}
			}
		});
	}

	@Override
	protected void onLoad() {
		mainPanel.showWaitPart(true);
		try {
			MapVanilla map = mapValues.get(0).getMap();

//			JsArray<JsArray> jsArray = JsArray.createArray().cast();
//			JsArrayNumber values = JsArrayNumber.createArray().cast();
//			for (MapValues val : mapValues) {
//				JsArray<JsArrayString> zone = JsArray.createArray().cast();
//				for (int i = 0; i < val.getLatitudes().size(); i++) {
//					JsArrayString latLong = JsArrayString.createArray().cast();
//
//					String lati = val.getLatitudes().get(i);
//					String longi = val.getLongitudes().get(i);
//
//					latLong.push(longi);
//					latLong.push(lati);
//					zone.push(latLong);
//				}
//				jsArray.push(zone);
//				try {
//					values.push(Double.parseDouble(val.getValue()));
//				} catch (Exception e) {
//					values.push(0.0);
//				}
//			}
			
			String geoJson = buildGeoJson(map, mapOptions.getSelectedMeasure(), mapValues);

			JsArray<JsArrayString> colorArray = JsArray.createArray().cast();
			for (List<String> col : colors) {
				JsArrayString color = JsArrayString.createArray().cast();
				color.push(col.get(1));
				color.push(col.get(2));
				color.push(col.get(3));
				colorArray.push(color);
			}
			
			String marker = "";

			String wmtsSource = null;
			JsArrayMixed wmsSource = JavaScriptObject.createArray().cast();
			
			MapInformation mapInformation = mapOptions.getMapInfo();
			if (mapInformation != null) {
				if (mapInformation.getWmts() != null) {
					wmtsSource = mapInformation.getWmts().getUrl();
				}
				
				if (mapInformation.getLayers() != null) {
					for (MapLayer layer : mapInformation.getLayers()) {
						if (layer.isSelected()) {
							TypeServer type = layer.getParent().getType();
							
							String wmsUrl = type == TypeServer.WFS ? layer.getUrl() : layer.getParent().getUrl();
							String layerName = layer.getName();
							Integer opacity = layer.getOptions() != null ? layer.getOptions().getOpacity() : MapLayerOption.DEFAULT_OPACITY;
							String layerType = type.toString();
							
							JsArrayString arr = JavaScriptObject.createArray().cast();

							arr.push(wmsUrl);
							arr.push(layerName);
							arr.push(String.valueOf(opacity));
							arr.push(layerType);

							wmsSource.push(arr);
						}
					}
				}
			}
			
			JsArrayMixed bigArray = JavaScriptObject.createArray().cast();

			renderMap("osmMapContainer", geoJson, null, map.getOriginLat(), map.getOriginLong(), map.getZoom(), map.getBoundLeft(), map.getBoundRight(), map.getBoundTop(), map.getBoundBottom(), map.getProjection(), marker, bigArray, colorArray, wmtsSource, wmsSource, map.getDataSetList().get(0).getType());
			
			onMeasureChange(mapOptions);
			mainPanel.showWaitPart(false);
		} catch (Exception e) {
			mainPanel.showWaitPart(false);
			e.printStackTrace();
		}
	}

	@UiHandler("btnRefresh")
	public void onRefresh(ClickEvent event) {
		if (maps != null && !maps.isEmpty()) {
			String uname = mapOptions.getUname();

			final int datasetId = Integer.parseInt(lstDatasets.getValue(lstDatasets.getSelectedIndex()));
			final String selectedMeasure = listMeasures.getValue(listMeasures.getSelectedIndex());
			final String selectedDimension = listDimensions.getValue(listDimensions.getSelectedIndex());

			FaWebService.Connect.getInstance().getOsmValues(mainPanel.getKeySession(), uname, selectedMeasure, datasetId, selectedDimension, colors, new GwtCallbackWrapper<List<MapValues>>(mainPanel, true, true) {

				@Override
				public void onSuccess(List<MapValues> result) {
					mapValues = result;
					
					mapOptions.updateMap(datasetId, selectedMeasure, selectedDimension, colors);
					onLoad();
				}

			}.getAsyncCallback());
		}
	}

	private void buildComboList(MapOptions mapOptions, List<MapValues> values) {
		Integer selectedMap = values != null && !values.isEmpty() ? values.get(0).getMap().getId() : null;
		Integer selectedDatasetId = mapOptions != null ? mapOptions.getDatasetId() : null;
		String selectedMeasure = mapOptions != null ? mapOptions.getSelectedMeasure() : null;
		String selectedDimension = mapOptions != null ? mapOptions.getSelectedDimension() : null;

		int index = -1;
		int i = 0;
		if (maps != null && !maps.isEmpty()) {
			HTML labelMap = new HTML("<br><b>" + FreeAnalysisWeb.LBL.ChooseMap() + "</b>");
			labelMap.addStyleName(style.padding());
			listMapAvailable = new ListBox(false);
			
			for (MapVanilla availableMap : maps) {
				String mapName = availableMap.getName();
				listMapAvailable.addItem(mapName, availableMap.getId() + "");
				
				if (availableMap.getId() == selectedMap) {
					index = i;
				}
				i++;
			}
			listMapAvailable.setStyleName("gwt-ListBox-mystyle");
			
			if (index != -1) {
				listMapAvailable.setSelectedIndex(index);
			}

			optionsPanel.add(labelMap);
			optionsPanel.add(listMapAvailable);

			listMapAvailable.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					int id = Integer.parseInt(listMapAvailable.getValue(listMapAvailable.getSelectedIndex()));
					MapVanilla map = null;
					for (MapVanilla m : maps) {
						if (m.getId() == id) {
							map = m;
							break;
						}
					}
					lstDatasets.clear();
					try {
						for (MapDataSet ds : map.getDataSetList()) {
							lstDatasets.addItem(ds.getName(), ds.getId() + "");
						}
					} catch(Exception e) {
					}
				}
			});

			HTML labelDataset = new HTML("<br><b>" + FreeAnalysisWeb.LBL.ChooseDataset() + "</b>");
			labelDataset.addStyleName(style.padding());
			lstDatasets = new ListBox(false);
			
			index = -1;
			i = 0;
			for (MapVanilla availableMap : maps) {
				try {
					for (MapDataSet ds : availableMap.getDataSetList()) {
						lstDatasets.addItem(ds.getName(), ds.getId() + "");
						
						if (ds.getId() == selectedDatasetId) {
							index = i;
						}
						i++;
					}
				} catch(Exception e) {
				}
				break;
			}
			lstDatasets.setStyleName("gwt-ListBox-mystyle");
			
			if (index != -1) {
				lstDatasets.setSelectedIndex(index);
			}

			optionsPanel.add(labelDataset);
			optionsPanel.add(lstDatasets);
		}

		HTML labelMeasure = new HTML("<br><b>" + FreeAnalysisWeb.LBL.ChooseMeasure() + "</b>");
		labelMeasure.addStyleName(style.padding());
		listMeasures = new ListBox(false);
		
		index = -1;
		i = 0;
		for (String measureName : measuresDisplay) {
			listMeasures.addItem(measureName);
			
			if (measureName.equals(selectedMeasure)) {
				index = i;
			}
			i++;
		}
		listMeasures.setStyleName("gwt-ListBox-mystyle");
		
		if (index != -1) {
			listMeasures.setSelectedIndex(index);
		}

		optionsPanel.add(labelMeasure);
		optionsPanel.add(listMeasures);

		HTML labelDimension = new HTML("<br><b>" + FreeAnalysisWeb.LBL.ChooseDimension() + "</b>");
		labelDimension.addStyleName(style.padding());
		listDimensions = new ListBox(false);

		index = -1;
		i = 0;
		for (String dimensionName : dimensions) {
			listDimensions.addItem(dimensionName);

			if (dimensionName.equals(selectedDimension)) {
				index = i;
			}
			i++;
		}
		listDimensions.setStyleName("gwt-ListBox-mystyle");
		
		if (index != -1) {
			listDimensions.setSelectedIndex(index);
		}

		optionsPanel.add(labelDimension);
		optionsPanel.add(listDimensions);
	}

	private void buildColorTable() {
		// Create a Flex Table
		flexTable = new FlexTable();
		flexTable.addStyleName("gwt-colorTable");

		// Add a button that will add more rows to the table
		Image icone_add_color = new Image(FaWebImage.INSTANCE.add());
		PushButton addRowButton = new PushButton(icone_add_color, new ClickHandler() {
			public void onClick(ClickEvent event) {
				ColorDialog dial = new ColorDialog(MapTab.this);
				dial.show();
				dial.setPopupPosition(-event.getX(), -event.getY());
			}
		});
		addRowButton.setStyleName("gwt-rowButton");

		Image icone_del_color = new Image(FaWebImage.INSTANCE.del());
		PushButton removeRowButton = new PushButton(icone_del_color, new ClickHandler() {
			public void onClick(ClickEvent event) {
				removeRow(flexTable);
			}
		});
		removeRowButton.setStyleName("gwt-rowButton");

		Image icone_refresh_color = new Image(FaWebImage.INSTANCE.Refresh_16());
		PushButton refreshColorButton = new PushButton(icone_refresh_color, new ClickHandler() {
			public void onClick(ClickEvent event) {
				refreshColor(flexTable);
			}
		});
		refreshColorButton.setStyleName("gwt-rowButton");

		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setStyleName("cw-FlexTable-buttonPanel");
		buttonPanel.addStyleName(style.padding());
		buttonPanel.add(addRowButton);
		buttonPanel.add(removeRowButton);

		colorsPanel.add(buttonPanel);
		colorsPanel.add(flexTable);

		for (List<String> color : colors) {
			addRow(color, false);
		}
	}

	@Override
	public void addRow(List<String> colorTemp, boolean addColorToList) {
		if (addColorToList) {
			colors.add(colorTemp);
		}

		int numRows = flexTable.getRowCount();
		flexTable.setWidget(numRows, 0, new HTML(colorTemp.get(0)));
		flexTable.setWidget(numRows, 1, new HTML(colorTemp.get(1)));
		flexTable.setWidget(numRows, 2, new HTML(colorTemp.get(2)));
		flexTable.setWidget(numRows, 3, new HTML(colorTemp.get(3)));
	}

	private void refreshColor(FlexTable flexTable) {
		int numRows = flexTable.getRowCount();
		while (numRows >= 1) {
			flexTable.removeRow(numRows - 1);
			colors.remove(numRows - 1);
			numRows--;
		}
		for (List<String> color : colors) {
			addRow(color, false);
		}
	}

	/**
	 * Remove a row from the flex table.
	 */
	private void removeRow(FlexTable flexTable) {
		int numRows = flexTable.getRowCount();
		if (numRows >= 1) {
			flexTable.removeRow(numRows - 1);
			colors.remove(numRows - 1);
		}
	}

	public MapOptions getMapOptions() {
		return mapOptions;
	}

	private void onMeasureChange(MapOptions mapOptions) {
		String measure = mapOptions.getSelectedMeasure();
		
		JsArrayMixed bigArray = JavaScriptObject.createArray().cast();
		JsArrayString row = JavaScriptObject.createArray().cast();
		row.push(measure);
		row.push("SUM");
		bigArray.push(row);
		changeMeasures(bigArray);
	}
	
	private String buildGeoJson(MapVanilla map, String measure, List<MapValues> values) {
		String typeDataset = map.getDataSetList().get(0).getType();
		
		StringBuilder buf = new StringBuilder();
		buf.append("{\n");
		buf.append("	\"features\": [\n");
		if (values != null) {
			for (int i = 0; i < values.size(); i++) {
				MapValues value = values.get(i);
				
				TypeFeature typeFeature = typeDataset.equalsIgnoreCase("polygon") ? TypeFeature.POLYGON : typeDataset.equalsIgnoreCase("line") ? TypeFeature.LINE : TypeFeature.POINT;

				buf.append("		{\n");
				buf.append("			\"properties\": {\n");
				buf.append("				\"geoId\": \"" + value.getGeoId() + "\",\n");
				try {
					buf.append("				\"" + measure + "\": " + Double.parseDouble(value.getValue()) + ",\n");
				} catch (Exception e) {
					buf.append("				\"" + measure + "\": 0.0,\n");
				}
				try {
					buf.append("				\"Value\": " + Double.parseDouble(value.getValue()) + "\n");
				} catch (Exception e) {
					buf.append("				\"Value\": 0.0\n");
				}
				buf.append("			},\n");
				buf.append("			\"type\": \"Feature\",\n");
				buf.append("			\"geometry\": {\n");
				buf.append("				\"type\": \"" + typeFeature.getLabel() + "\",\n");
				buf.append("				\"coordinates\": [\n");
				
				if (typeFeature != TypeFeature.POINT) {
					buf.append("					[\n");
					for (int j = 0; j < value.getLatitudes().size(); j++) {
						String lati = value.getLatitudes().get(j);
						String longi = value.getLongitudes().get(j);
	
						buf.append("						[" + longi + "," + lati + "]" + (j == value.getLatitudes().size() - 1 ? "" : ",") + "\n");
					}
					buf.append("					]\n");
				}
				else {
					if (!value.getLatitudes().isEmpty()) {
						String lati = value.getLatitudes().get(0);
						String longi = value.getLongitudes().get(0);
	
						buf.append("						" + longi + "," + lati + "\n");
					}
				}
				buf.append("				]\n");
				buf.append("			}\n");
				buf.append("		}" + (i == values.size() - 1 ? "" : ",") + "\n");
			}
		}
		buf.append("	],\n");
		buf.append("	\"type\": \"FeatureCollection\"\n");
		buf.append("}\n");
		System.out.println(buf.toString());
		return buf.toString();
	}

	private final native void renderMap(String div, String obj, JsArrayNumber values, double lat, double longi, int zoom, double minLat, double maxLat, double minLong, double maxLong, String projection, String marker, JsArrayMixed bigArray, JsArray colorArray, String wmtsSource, JsArrayMixed wmsSource, String type) /*-{
		$wnd.previewMapV2WithValues(div, lat, longi, zoom, minLong, minLat, maxLong, maxLat, projection, type, obj, marker, bigArray, values, colorArray, wmtsSource, wmsSource);
	}-*/;

	private final native void changeMeasures(JsArrayMixed bigArray) /*-{
		$wnd.changeMeasures(bigArray);
	}-*/;
}
