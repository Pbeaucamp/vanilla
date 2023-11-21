package bpm.gwt.commons.client.utils;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;

public class CustomCell<T> extends TextCell {

	private DatagridHandler<T> handler;
	
	public CustomCell(DatagridHandler<T> handler) {
		super();
		this.handler = handler;
	}

	@Override
	public Set<String> getConsumedEvents() {
		Set<String> consumedEvents = new HashSet<String>();
		consumedEvents.add("dblclick");
		consumedEvents.add("contextmenu");
		return consumedEvents;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
		T obj = (T) context.getKey();
		if (event.getButton() == NativeEvent.BUTTON_RIGHT) {
			handler.onRightClick(obj, event);
		}
		else if (event.getType().equals("dblclick")) {
			handler.onDoubleClick(obj);
		}
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
	}
}