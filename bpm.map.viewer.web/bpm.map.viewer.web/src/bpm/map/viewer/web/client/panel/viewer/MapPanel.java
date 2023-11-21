package bpm.map.viewer.web.client.panel.viewer;

import java.util.ArrayList;
import java.util.List;

import bpm.fm.api.model.ComplexMapLevel;
import bpm.fm.api.model.ComplexMapMetric;
import bpm.fm.api.model.ComplexObjectViewer;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.utils.MapZoneValue;
import bpm.fm.api.model.utils.MetricValue;
import bpm.map.viewer.web.client.panel.ViewerPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class MapPanel extends Composite {
	private static MapViewerUiBinder uiBinder = GWT.create(MapViewerUiBinder.class);

	interface MapViewerUiBinder extends UiBinder<Widget, MapPanel> {
	}
	
	@UiField
	HTMLPanel mapContainer, mapInfo;

	private List<ComplexObjectViewer> result;
	//private List<MapZoneValue> mapValues;
	private List<ComplexMapLevel> levelList = new ArrayList<ComplexMapLevel>();
	private List<ComplexMapMetric> displayedMetrics;
	private MapViewer parent;
	private double maxLat = -200;
	private double minLat = Double.MAX_VALUE;
	private double maxLong = -200;
	private double minLong = Double.MAX_VALUE;
	private String mapType;
	
	private int levelIndex;

	public MapPanel(MapViewer parent, List<ComplexObjectViewer> result, List<ComplexMapMetric> displayedMetrics) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.parent = parent;
		this.result = result;
		this.displayedMetrics = displayedMetrics;
		mapContainer.getElement().setId("mapContainer");
		mapInfo.getElement().setId("info");
		mapContainer.addAttachHandler(new AttachEvent.Handler() {
			
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if(event.isAttached()){
					loadMap();
					Timer timer = new Timer() {
				      public void run() {
				    	  mapResize();
				      }
				    };
				    timer.schedule(500);
					initJS();
				}
			}
		});
		if(result != null && !result.isEmpty()){
			this.levelIndex = result.get(0).getLevel().getParent().getChildren().indexOf(result.get(0).getLevel());
		}
		
	}

	
//	@Override
//	protected void onLoad() {
//		
//	}
//	
	public void loadMap() {	
		JSONObject obj = new JSONObject();
		obj.put("levels", new JSONArray());
		
		if(displayedMetrics.size() == 0){
			maxLat = 51;
			minLat = 41;
			maxLong = 10;
			minLong = -5;
		}
		
		for(ComplexObjectViewer object : result){
			
			if(levelList.contains(object.getCpxLevel())){
				JSONArray array = obj.get("levels").isArray();
				for(int i=0; i< array.size(); i++){
					JSONObject lev = array.get(i).isObject();
					if(lev.get("id").equals(object.getCpxLevel().getIdLevel())){
						
						JSONObject metricobj = metricToJson(object.getCpxMetric(), object.getMapvalues());
	
						JSONArray metricArray = lev.get("metrics").isArray();
						metricArray.set(metricArray.size(), metricobj);
					}
				}
				
				
			} else {
				
				ComplexMapLevel level = object.getCpxLevel();
				JSONObject levelobj = new JSONObject();
				
				levelobj.put("id", new JSONNumber(level.getIdLevel()));
				levelobj.put("couleur", new JSONString(level.getColor()));
				
				
				JSONObject metricobj = metricToJson(object.getCpxMetric(), object.getMapvalues());
				
				
				JSONArray metricArray = new JSONArray();
				metricArray.set(0, metricobj);
				
				levelobj.put("metrics", metricArray);
				
				
				
				JSONArray array= obj.get("levels").isArray();
				array.set(array.size(), levelobj);
				//System.out.print(obj);
				
			}
			
		}

		obj.put("minLat", new JSONNumber(minLat));
		obj.put("maxLat", new JSONNumber(maxLat));
		obj.put("minLong", new JSONNumber(minLong));
		obj.put("maxLong", new JSONNumber(maxLong));
		obj.put("lat", new JSONNumber(((maxLat + minLat) * 0.5)));
		obj.put("long", new JSONNumber(((maxLong + minLong) * 0.5)));
		obj.put("zoom", new JSONNumber(6));

		try {
			obj.put("maptype", new JSONString(mapType));
		} catch(Exception e) {
			obj.put("maptype", new JSONString("polygon"));
		}
		
		renderMap("mapContainer", obj, parent.getViewer().getActionDate().toString());
		
	}

	
	
	private JSONObject metricToJson(ComplexMapMetric metric, List<MapZoneValue> list) {
		JSONObject metricobj = new JSONObject();
		
		metricobj.put("id", new JSONNumber(metric.getId()));
		metricobj.put("name", new JSONString(metric.getMetric().getName()));
		metricobj.put("couleur", new JSONString(metric.getColor()));
		metricobj.put("type", new JSONString(metric.getRepresentation()));
		int displayed = 0;
		for(ComplexMapMetric met : displayedMetrics){
			if(met.getId() == metric.getId()){
				displayed= 1;
				break;
			}
		}
		metricobj.put("displayed", new JSONNumber(displayed));
		
		
		
		
		JSONArray mzv = mapValuesToJson(metric, list);
		metricobj.put("mapzonevalues", mzv);
		
		return metricobj;
	}
	
	private JSONArray mapValuesToJson(ComplexMapMetric metric, List<MapZoneValue> list) {
		JSONArray result = new JSONArray();
		if(list.size() == 0)
			return result;
		

		for(MapZoneValue val : list) {
			mapType = val.getMapType();
			JSONObject feature = new JSONObject();
			JSONArray zoneArray = new JSONArray();
			JSONArray dataArray = new JSONArray();
			for(int i = 0 ; i < val.getLatitudes().size(); i++) {
				
				JSONObject latlongObject = new JSONObject();
				
				String lati = val.getLatitudes().get(i);
				String longi = val.getLongitudes().get(i);
				
				try {
					double la = Double.parseDouble(lati);
					double lo = Double.parseDouble(longi);
					if(la < minLat) {
						minLat = la;
					}
					if(la > maxLat) {
						maxLat = la;
					}
					if(lo < minLong) {
						minLong = lo;
					}
					if(lo > maxLong) {
						maxLong = lo;
					}
				} catch (Exception e) {
					
				}

				latlongObject.put("lat", new JSONString(lati));
				latlongObject.put("long", new JSONString(longi));
				zoneArray.set(zoneArray.size(), latlongObject);
			}
			feature.put("zone", zoneArray);
			
			if(parent.getViewer().getActionDate() == ViewerPanel.ActionDate.VALEUR){
				feature.put("value", new JSONNumber(val.getValues().get(0).getValue()));
			} else {
				feature.put("value", new JSONNumber(val.getValues().get(val.getValues().size() -1).getValue() - val.getValues().get(0).getValue()));
			}
			
			feature.put("name", new JSONString(val.getValues().get(0).getAxis().get(levelIndex).getLabel().replace("'", "")));
			
			for(MetricValue metval :  val.getValues()) {
				
				JSONObject dataObject = new JSONObject();
				String date = "";
				if(metric.getMetric().getFactTable() instanceof FactTable){
					FactTable table = (FactTable) metric.getMetric().getFactTable();
					if(table.getPeriodicity().equals(FactTable.PERIODICITY_HOURLY) || table.getPeriodicity().equals(FactTable.PERIODICITY_MINUTE)){
						date = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT).format(metval.getDate());
					} else {
						date = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).format(metval.getDate());
					}
				} else {
					date = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT).format(metval.getDate());
				}
				String data = "<strong>" + metval.getValue()+ "</strong>";

				dataObject.put("text", new JSONString(date + " - " + data));
				dataArray.set(dataArray.size(), dataObject);
			}
			feature.put("datas", dataArray);
			
			result.set(result.size(),feature);
		}
		

		
		return result;
	}

	public void updateDisplayByMetrics(List<ComplexMapMetric> metrics){
		this.displayedMetrics = metrics;
		JSONObject json = new JSONObject();
		JSONArray array= new JSONArray();
		for(ComplexMapMetric metric : displayedMetrics){
			JSONObject met = new JSONObject();
			met.put("name", new JSONString(metric.getMetric().getName()));
			array.set(array.size(), met);
		}
		json.put("display", array);
		updateMap(json, parent.getViewer().getActionDate().toString());
	}

	public void updateSize() {
		mapResize();	
	}
	
	public void changeTileMap(String name) {
		mapChangeTile(name);	
	}
	
	private final native void renderMap(String div, JSONObject obj, String action) /*-{		
		$wnd.renderComplexMap(div, obj, action);
	}-*/;
	
	private final native void updateMap(JSONObject obj, String action) /*-{		
		$wnd.updateComplexMap(obj, action);
	}-*/;

	private final native void mapResize() /*-{		
		$wnd.resizeMap();
	}-*/;
	
	private final native void mapChangeTile(String name) /*-{		
		$wnd.changeTile(name);
	}-*/;
	
	private final native void initJS() /*-{		
//		$wnd.initTooltip();
		$wnd.setTimeout('initTooltip()', 500);
	}-*/;
}

