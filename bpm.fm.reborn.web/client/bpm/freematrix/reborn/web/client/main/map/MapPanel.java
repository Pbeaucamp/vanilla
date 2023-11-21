package bpm.freematrix.reborn.web.client.main.map;

import java.util.List;

import bpm.fm.api.model.utils.MapZoneValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MapPanel extends Composite {

	private static MapPanelUiBinder uiBinder = GWT.create(MapPanelUiBinder.class);

	interface MapPanelUiBinder extends UiBinder<Widget, MapPanel> {
	}

	private List<MapZoneValue> mapValues;

	public MapPanel(List<MapZoneValue> result) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.mapValues = result;
		
	}
	
	
	
	@Override
	protected void onLoad() {
		
		double minLat = Double.MAX_VALUE;
		double maxLat = -200;
		double minLong = Double.MAX_VALUE;
		double maxLong = -200;
		
		String mapType = "polygon";
		String marker = "";
		int minSize = 0, maxSize = 0;
		
		JsArray<JsArray> jsArray = JsArray.createArray().cast();
		JsArrayNumber  values = JsArrayNumber.createArray().cast();
		for(MapZoneValue val : mapValues) {
			marker = val.getMarker();
			mapType = val.getMapType();
			minSize = val.getMinSize();
			maxSize = val.getMaxSize();
			JsArray<JsArrayString> zone = JsArray.createArray().cast();
			for(int i = 0 ; i < val.getLatitudes().size(); i++) {
				JsArrayString  latLong = JsArrayString.createArray().cast();
				
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
				
				latLong.push(longi);
				latLong.push(lati);
				zone.push(latLong);
			}
			jsArray.push(zone);
			values.push(val.getValues().get(0).getHealth());
		}
		
//		double zoomLat = maxLat - minLat;
//		double zoomLong = maxLong - minLong;
//		
//		int zoom = 1;
//		
//		if(zoomLat < (zoomLong * 2)) {
//			
//		}
//		else {
//			
//		}
		
		renderMap("mapContainer", jsArray, values, ((maxLat + minLat) * 0.5), ((maxLong + minLong) * 0.5), 6, minLat, maxLat, minLong, maxLong, mapType, marker, minSize, maxSize);
	}

	private final native void renderMap(String div, JsArray features, JsArrayNumber values, double lat, double longi, int zoom, double minLat, double maxLat, double minLong, double maxLong, String mapType, String marker, int minSize, int maxSize) /*-{		
		$wnd.renderMap(div, lat, longi, zoom, minLong, minLat, maxLong, maxLat, values, features, mapType, marker, minSize, maxSize);
	}-*/;
}
