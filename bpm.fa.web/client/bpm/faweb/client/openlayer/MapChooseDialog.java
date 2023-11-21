//package com.bpm.faweb.client.openlayer;
//
//import java.util.List;
//
//import com.bpm.faweb.client.FreeAnalysisWeb;
//import com.bpm.faweb.client.composites.FaWebMainComposite;
//import com.bpm.faweb.client.dialogbox.ErrorDialog;
//import com.bpm.faweb.client.dialogbox.MapChoiceDialog;
//import com.bpm.faweb.client.dnd.DraggableGridItem;
//import com.bpm.faweb.client.dnd.FaWebDragController;
//import com.bpm.faweb.client.infoscube.ItemDim;
//import com.bpm.faweb.client.services.FaWebService;
//import com.google.gwt.core.client.GWT;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.uibinder.client.UiBinder;
//import com.google.gwt.uibinder.client.UiHandler;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.ui.DialogBox;
//import com.google.gwt.user.client.ui.Widget;
//
//public class MapChooseDialog extends DialogBox  {
//
//	private static MapChooseDialogUiBinder uiBinder = GWT
//			.create(MapChooseDialogUiBinder.class);
//
//	interface MapChooseDialogUiBinder extends UiBinder<Widget, MapChooseDialog> {
//	}
//
//	private DraggableGridItem geolocalizable;
//	private FaWebDragController dragCtrl;
//
//	public MapChooseDialog(DraggableGridItem geolocalizable,FaWebDragController dragCtrl) {
//		super();
//		setWidget(uiBinder.createAndBindUi(this));
//		this.setText("Choose your Map");
//		this.center();
//		this.geolocalizable = geolocalizable;
//		this.dragCtrl = dragCtrl;
//	}
//	private void showFusionMaps(){
//		boolean isGeolocalisable = false;
//				
//				int pointIndex = geolocalizable.getUname().indexOf(".");
//				String selectedDimensionUname = geolocalizable.getUname().substring(0, pointIndex);
//				int beginIndex = selectedDimensionUname.indexOf("[") + "[".length();
//				String selectedName = selectedDimensionUname.substring(beginIndex);
//				
//				selectedName = selectedName.replace("]", "").replace("[", "");
//				
//				for(ItemDim itemDim : FaWebMainComposite.getInstance().getInfosReport().getDims()){
//					String dimensionUname = itemDim.getUname();
//					int begin = dimensionUname.indexOf("[") + "[".length();
//					int end = dimensionUname.lastIndexOf("]");
//					String name = dimensionUname.substring(begin, end);
//					if(selectedName.equals(name)){
//						isGeolocalisable = itemDim.isGeolocalisable();
//						break;
//					}
//				}
//				
//				if(isGeolocalisable){
//					
//							MapChooseDialog.this.hide();
//							FaWebService.Connect.getInstance().getMeasureDimsAndMap(FreeAnalysisWeb.getMain().getKeySession(), geolocalizable.getUname(), 
//									geolocalizable.getName(), new AsyncCallback<List<List<String>>>() {
//								public void onSuccess(List<List<String>> result) {
//								    MapChoiceDialog dialogBox = new MapChoiceDialog(geolocalizable.getUname(), 
//								    		geolocalizable.getName(), result);
//									dialogBox.show();
//									dialogBox.center();
//								}
//								
//								public void onFailure(Throwable caught) {
//									ErrorDialog dialogError = new ErrorDialog(caught.toString());
//									dialogError.show();
//									dialogError.center();
//								}
//							});
//				}
//					
//				
//	}
//	
//	
//	@UiHandler("imgFusionMap")
//	void onFusionMap(ClickEvent e){
//		showFusionMaps();
//	}
//	
//	@UiHandler("imgOpenLayer")
//	void onOpenLayer(ClickEvent e){
//		OpenLayerDialog openLayer = new OpenLayerDialog(geolocalizable, dragCtrl);
//		openLayer.show();
//		hide();
//	}
//	
//	@UiHandler("btnCancel")
//	void onCancel(ClickEvent e){
//		this.hide();
//	}
//
//}
