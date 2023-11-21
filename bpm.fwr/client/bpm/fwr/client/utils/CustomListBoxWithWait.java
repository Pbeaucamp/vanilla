package bpm.fwr.client.utils;

import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.fwr.client.images.WysiwygImage;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;

public class CustomListBoxWithWait extends HorizontalPanel {
	
	private FwrPrompt prompt;
	private ListBox listBox;
	private Image imgWait;
	
	public CustomListBoxWithWait(ListBox listBox, FwrPrompt prompt) {
		this.listBox = listBox;
		this.prompt = prompt;
		
		imgWait = new Image(WysiwygImage.INSTANCE.loading());
		imgWait.setVisible(false);
		imgWait.getElement().getStyle().setMarginLeft(5, Unit.PX);
		imgWait.getElement().getStyle().setMarginTop(7, Unit.PX);
	
		this.add(listBox);
		this.add(imgWait);
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
	public void clear() {
		this.listBox.clear();
	}

	public FwrPrompt getPrompt() {
		return prompt;
	}
}
