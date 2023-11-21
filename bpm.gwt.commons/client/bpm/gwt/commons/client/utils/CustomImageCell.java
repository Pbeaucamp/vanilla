package bpm.gwt.commons.client.utils;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;

public class CustomImageCell<T> extends ImageResourceCell {

	private DatagridHandler<T> handler;

	public CustomImageCell(DatagridHandler<T> handler) {
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
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, ImageResource value, NativeEvent event, ValueUpdater<ImageResource> valueUpdater) {
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