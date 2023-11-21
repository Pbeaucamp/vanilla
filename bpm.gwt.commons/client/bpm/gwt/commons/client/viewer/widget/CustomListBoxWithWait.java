package bpm.gwt.commons.client.viewer.widget;

import java.util.LinkedHashMap;

import bpm.gwt.commons.client.images.CommonImages;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class CustomListBoxWithWait extends HorizontalPanel implements IListParameter  {
	
	private VanillaParameter parameter;
	private ListBox listBox;
	private Image imgWait;
	
	public CustomListBoxWithWait(ListBox listBox, VanillaParameter parameter) {
		this.listBox = listBox;
		this.parameter = parameter;
		
		imgWait = new Image(CommonImages.INSTANCE.loading());
		imgWait.setVisible(false);
		imgWait.getElement().getStyle().setMarginLeft(5, Unit.PX);
		imgWait.getElement().getStyle().setMarginTop(7, Unit.PX);
	
		this.add(listBox);
		this.add(imgWait);
		this.setWidth("100%");
	}
	
	public void showWait(boolean visible){
		this.listBox.setEnabled(!visible);
		this.imgWait.setVisible(visible);
	}
	
	public String getName(){
		return listBox.getName();
	}
	
	public void addItem(String item){
		this.listBox.addItem(item);
	}
	
	public void addItem(String item, String value){
		this.listBox.addItem(item, value);
	}
	
	public ListBox getListBox(){
		return listBox;
	}

	@Override
	public Widget getWidget() {
		return this;
	}

	@Override
	public CustomListBoxWithWait getCustomListBox() {
		return this;
	}
	
	public void setValues(LinkedHashMap<String, String> values){
		this.parameter.setValues(values);
	}
	
	@Override
	public void clear() {
		this.listBox.clear();
	}
}
