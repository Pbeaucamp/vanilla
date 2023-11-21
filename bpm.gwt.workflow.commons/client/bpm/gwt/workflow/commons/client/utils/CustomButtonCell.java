package bpm.gwt.workflow.commons.client.utils;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;

public class CustomButtonCell extends ButtonCell {
	
	private int clientX;
	private int clientY;
	
	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		
		this.clientX = event.getClientX();
		this.clientY = event.getClientY();
	}
	
	public int getClientX() {
		return clientX;
	}
	
	public int getClientY() {
		return clientY;
	}
}