package bpm.faweb.client.openlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dnd.DraggableGridItem;
import bpm.faweb.client.dnd.DraggableTreeItem;
import bpm.faweb.client.dnd.FaWebDragController;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.shared.GroupChart;
import bpm.faweb.shared.OpenLayer;
import bpm.faweb.shared.SerieChart;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class OpenLayerMapContainer extends Composite {

	private static OpenLayerMapContainerUiBinder uiBinder = GWT
			.create(OpenLayerMapContainerUiBinder.class);

	interface OpenLayerMapContainerUiBinder extends
			UiBinder<Widget, OpenLayerMapContainer> {
	}
	
	public enum ListType {
		MEASURES,
		SERIES
	}
	
	@UiField
	HTMLPanel mapPanel, legendPanel;
	
	@UiField 
	public AbsolutePanel lstCategory, lstMeasure;
	
//	private Vector wfsLayer;
	private OpenLayer openLayer;

	private FaWebDragController dragCtrl;
	public String measure = "";
	public String color = "";
	private List<String> serieList = new ArrayList<String>();
	
	private OpenLayerDropController seriesDC;
	private OpenLayerDropController measuresDC;

	private DraggableGridItem geolocalizable ;
	private List<String> addressList = new ArrayList<String>();
	private DraggableWMSLabel draggableWMSLabel;
	private MainPanel mainCompParent;
	
	public OpenLayerMapContainer(MainPanel mainCompParent, DraggableGridItem geolocalizable, String id, FaWebDragController dragCtrl) {
		initWidget(uiBinder.createAndBindUi(this));
		this.dragCtrl = dragCtrl;
		this.geolocalizable = geolocalizable;
		this.mainCompParent = mainCompParent;
		seriesDC = new OpenLayerDropController(lstCategory, this, ListType.SERIES);
		measuresDC = new OpenLayerDropController(lstMeasure, this, ListType.MEASURES);
		buildOpenLayerMap(geolocalizable, Integer.parseInt(id));
	}

	private void buildOpenLayerMap(DraggableGridItem geolocalizable, Integer id) {
		FaWebService.Connect.getInstance().buildOpenLayerMap(
				mainCompParent.getKeySession(), id,
				geolocalizable.getName(),geolocalizable.getUname(), new AsyncCallback<OpenLayer>() {

					@Override
					public void onSuccess(OpenLayer result) {
						generateWMSMap(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}
				});
	}
	
	private void generateWMSMap(OpenLayer openLayer){
//			this.openLayer = openLayer;
//			
//	        //create some MapOptions
//	        MapOptions defaultMapOptions = new MapOptions();
//	        defaultMapOptions.setNumZoomLevels(16);
//	        defaultMapOptions.setMaxExtent(new Bounds(0,-560,590,0));
//	      
//	        //Create a MapWidget
//	        MapWidget mapWidget = new MapWidget("100%", "100%", defaultMapOptions);
//	        //Create a WMS layer as base layer
//	        WMSParams wmsParams = new WMSParams();
//	        wmsParams.setFormat("image/png");
//	        wmsParams.setLayers(openLayer.getLayers());
//	        wmsParams.setStyles("");
//	 
//	        WMSOptions wmsLayerParams = new WMSOptions();
//	        wmsLayerParams.setUntiled();
//	        wmsLayerParams.setDisplayOutsideMaxExtent(true);
//	        String wmsUrl = openLayer.getBaseLayerUrl();	
//	 
//	        WMS wmsLayer = new WMS(openLayer.getBaseLayerName(), wmsUrl, wmsParams, wmsLayerParams);
//	 
//	        //Add the WMS to the map
//	        Map map = mapWidget.getMap();
//	        map.addLayer(wmsLayer);
//	 
//	        //Create a WFS layer
//	        WFSProtocolOptions wfsProtocolOptions = new WFSProtocolOptions();
//	        wfsProtocolOptions.setUrl(openLayer.getVectorLayerUrl());
//	        wfsProtocolOptions.setFeatureType(openLayer.getFeatureType());
//	        wfsProtocolOptions.setFeatureNameSpace("http://mapserver.gis.umn.edu/mapserver");
//	        wfsProtocolOptions.setSrsName(openLayer.getProjection());
//	        wfsProtocolOptions.setVersion("1.0.0");
//	        wfsProtocolOptions.setGeometryName("msGeometry");
//	      
//	        //if your wms is in a different projection use wfsProtocolOptions.setSrsName(LAMBERT72);
//	 
//	        WFSProtocol wfsProtocol = new WFSProtocol(wfsProtocolOptions);
//	 
//	        VectorOptions vectorOptions = new VectorOptions();
//	        vectorOptions.setProtocol(wfsProtocol);
//	        vectorOptions.setStrategies(new Strategy[]{new BBoxStrategy()});
//	        //if your wms is in a different projection use vectorOptions.setProjection(LAMBERT72);
//	 
//	        wfsLayer = new Vector(openLayer.getFeatureType(), vectorOptions);
//	        wfsLayer.setOpacity(1f);
//	        map.addLayer(wfsLayer);
//	        //Lets add some default controls to the map
//	        map.addControl(new LayerSwitcher()); //+ sign in the upperright corner to display the layer switcher
//	        map.addControl(new OverviewMap()); //+ sign in the lowerright to display the overviewmap
//	        map.addControl(new ScaleLine()); //Display the scaleline
//	        
//	        //Center and zoom to a location
//	        map.setCenter(new LonLat(0, 0), 1);
//	 
//	        mapPanel.add(mapWidget);
//	 
//	        mapWidget.getElement().getFirstChildElement().getStyle().setZIndex(0); //force the map to fall behind popups
	}


//	private void addStyle(Vector wfsLayer){
//		final VectorFeature[] features = wfsLayer.getFeatures();
//		
////		for(Entry<Integer, String> entry : openLayer.getAddressChild().entrySet()){
////			System.out.println(entry.getValue());
////		}
////		
//		
//		HashMap<Integer, String> mapAddress = openLayer.getAddressChild();
//        for (VectorFeature feature : features)
//        {
//            if(mapAddress.get(Integer.parseInt(feature.getFID().replace(feature.getFID().substring(0, feature.getFID().indexOf(".")+1), ""))) != null){
//            	int fid = Integer.parseInt(feature.getFID().replace(feature.getFID().substring(0, feature.getFID().indexOf(".")+1), ""));
//                if(!addressList.contains(geolocalizable.getUname() + ".[" + mapAddress.get(fid) + "]")){
//                	 addressList.add(geolocalizable.getUname() + ".[" + mapAddress.get(fid) + "]");
//                }
//            }
//        }
//        executeMapData();
//        
//	}
//
//	private void setMapValues(List<GroupChart> groupsChart){
//		final VectorFeature[] features = wfsLayer.getFeatures();
//		HashMap<Integer, String> mapAddress = openLayer.getAddressChild();
//		int index = 0;
//		for (VectorFeature feature : features){
//			 if(mapAddress.get(Integer.parseInt(feature.getFID().replace(feature.getFID().substring(0, feature.getFID().indexOf(".")+1), ""))) != null){
//				 int fid = Integer.parseInt(feature.getFID().replace(feature.getFID().substring(0, feature.getFID().indexOf(".")+1), ""));
//				 
//				 double itemValue = calculateMapValue(groupsChart.get(index));
//				 
//				 Style style = new Style();
//	             style.setStrokeWidth(1);
//	             style.setStrokeColor("#333333");
//	             style.setFillColor(generateRightColor(itemValue));
//	             style.setLabel(mapAddress.get(fid) + " :" + String.valueOf(itemValue));
//	             feature.setStyle(style);
//	             index++;
//			 }
//			
//        }
//		wfsLayer.redraw();
//	}
	
	private double calculateMapValue(GroupChart groupChart){
		double d = 0;
		
		for(SerieChart s : groupChart.getSeries()){
			d = d + s.getDoubleValue();
		}
		
		if(d == 1.2345e-8){
			return 0;
		}
		
		return d;
	}
	
	private String generateRightColor(double value){
		String color = "#fff";
		
		for(Widget w : legendPanel){
			if(w instanceof LegendItem){
				double min = ((LegendItem)w).getMin();
				double max = ((LegendItem) w).getMax();
				if(value > min && value < max ){
					return "#"+((LegendItem) w).getColor();
				}
			}
		}
		return color;
	}

	public void manageWidget(DraggableTreeItem item, ListType type){
		
		if(type == ListType.MEASURES){
			if(item.getUname().contains("[Measures]")){
				lstMeasure.clear();
				DraggableWMSLabel lblMeasure = new DraggableWMSLabel(this, item.getUname(), ListType.MEASURES, false);
				
				lstMeasure.add(lblMeasure);
				measure = item.getUname();
			}
		}
		else if(type == ListType.SERIES){
			if(!item.getUname().contains("[Measures]")){
				if(!item.getUname().contains(geolocalizable.getUname())){
					boolean exist = false;
					for(String serie : serieList) {
						if(item.getUname().equals(serie)) {
							exist = true;
							break;
						}
					}
					
					if(!exist) {
						DraggableWMSLabel lblSeries = new DraggableWMSLabel(this, item.getUname(), ListType.SERIES, true);
						
						lstCategory.add(lblSeries);
						serieList.add(item.getUname());
					}
				}
			}
		}
	}
	
	@Override
	protected void onAttach() {
		dragCtrl.clearSelection();
		dragCtrl.registerDropController(seriesDC);
		dragCtrl.registerDropController(measuresDC);
		super.onAttach();
	}
	
	@Override
	protected void onDetach() {
		dragCtrl.clearSelection();
		dragCtrl.removeAllDropControllers();
		super.onDetach();
	}
	
	@UiHandler("btnAdd")
	void onAddLegend(ClickEvent e){
		new LegendItemAdd(e.getClientX(),e.getClientY(),this);
	}
	
	@UiHandler("btnAddStyle")
	void onStyle(ClickEvent e){
//		addStyle(wfsLayer);
	}

	@UiHandler("btnClear")
	void onClear(ClickEvent e){
		serieList.clear();
		measure = "";
		lstCategory.clear();
		lstMeasure.clear();
	}
	
	private void executeMapData(){
		List<String> filters = new ArrayList<String>();
		List<String> groups = new ArrayList<String>();
		
		List<String> correctList = new ArrayList<String>();
		correctList.addAll(addressList);
		correctList.addAll(serieList);
		FaWebService.Connect.getInstance().executeQuery(mainCompParent.getKeySession(), correctList, groups, filters, measure,
				new AsyncCallback<List<GroupChart>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<GroupChart> groupsChart) {
//				setMapValues(groupsChart);
			}
			
		});
	}
	
	public void setDraggableWMSLabel(DraggableWMSLabel draggableWMSLabel){
		this.draggableWMSLabel = draggableWMSLabel;
	}
	
	public DraggableWMSLabel getDraggabelWMSLabel(){
		return draggableWMSLabel;
	}
	
	public void removeSerieItem(String item){
		serieList.remove(item);
	}
	
	public void removeMeasureItem(String item){
		measure = "";
	}
	
	public void saveLegend(String name, String color, double min, double max) {
		legendPanel.add(new LegendItem(name, color, min, max));
	}
	
}
