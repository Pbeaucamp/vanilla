package bpm.faweb.client.openlayer;

import bpm.faweb.client.openlayer.OpenLayerMapContainer.ListType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DraggableWMSLabel extends Composite  {

	private static DraggableWMSLabelUiBinder uiBinder = GWT
			.create(DraggableWMSLabelUiBinder.class);

	interface DraggableWMSLabelUiBinder extends
			UiBinder<Widget, DraggableWMSLabel> {
	}
	
	@UiField Label lblName;
	
	private ListType type;
	private String text;
	private OpenLayerMapContainer openLayerMapContainer;
	private boolean isDimension;
	
	
	public DraggableWMSLabel(OpenLayerMapContainer openLayerMapContainer, String text, ListType type, boolean isDimension) {
		initWidget(uiBinder.createAndBindUi(this));
		this.text = text;
		this.type = type;
		this.isDimension  = isDimension;
		this.addStyleName("panel");
		this.openLayerMapContainer = openLayerMapContainer;
		lblName.setText(text);
		if(isDimension){
		
			
		}else{
			this.addStyleName("measure");
		}
		init();
	}

	public String getLabel() {
		return text;
	}

	public ListType getType(){
		return type;
	}
	
	@UiHandler("focusPanel")
	void onClickPanel(ClickEvent e){
		init();
	}
	
	private void init(){
	lblName.getElement().setAttribute("style", "color: #fff;");
		if(isDimension){
			this.addStyleName("active");
		}
		openLayerMapContainer.setDraggableWMSLabel(this);
	}
	
	

	@UiHandler("btnRemove")
	void onRemove(ClickEvent e){
		this.removeFromParent();
		if(isDimension){
			openLayerMapContainer.removeSerieItem(getLabel());
		}else{
			openLayerMapContainer.removeMeasureItem(getLabel());
		}
	}
}
