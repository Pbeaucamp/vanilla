package bpm.faweb.client.openlayer;

import bpm.faweb.client.utils.ColorPicker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LegendItemAdd extends PopupPanel {

	private static LegendItemAddUiBinder uiBinder = GWT
			.create(LegendItemAddUiBinder.class);

	interface LegendItemAddUiBinder extends UiBinder<Widget, LegendItemAdd> {
	}

	@UiField FocusPanel colorPickerPanel;
	@UiField TextBox txtName, txtMin, txtMax;
	
	private ColorPicker colorPicker = new ColorPicker();
	private OpenLayerMapContainer openLayerMapContainer;
	
	public LegendItemAdd(int left, int top, OpenLayerMapContainer openLayerMapContainer) {
		setWidget(uiBinder.createAndBindUi(this));	
		this.openLayerMapContainer = openLayerMapContainer;
		this.addStyleName("legendPanel");
		this.setAutoHideEnabled(true);
		this.setPopupPosition(left, top);
		this.show();
		generatePlaceHolder(txtName, "Legend Name");
		generatePlaceHolder(txtMax, "Maximum");
		generatePlaceHolder(txtMin, "Minimum");
		colorPickerPanel.add(colorPicker);
	}
	
	private void generatePlaceHolder(TextBox txtBox, String value){
		txtBox.getElement().setAttribute("placeholder", value);
	}

	public String getName(){
		return txtName.getText();
	}
	
	public Double getMin(){
		return Double.parseDouble(txtMin.getText());
	}
	
	public double getMax(){
		return Double.parseDouble(txtMax.getText());
	}
	
	public String getColor(){
		return colorPicker.getTextBox().getText();
	}
	
	@UiHandler("btnSave")
	void onSave(ClickEvent e){
		if(txtName.getText().isEmpty() && txtMin.getText().isEmpty() && txtMax.getText().isEmpty()){
			Window.alert("Please provide all the Fields before saving.");
		}else{
			openLayerMapContainer.saveLegend(getName(), getColor(),getMin(), getMax());
			hide();
		}
		
	}
	
	@UiHandler("btnCancel")
	void onCancel(ClickEvent e){
		hide();
	}

}
