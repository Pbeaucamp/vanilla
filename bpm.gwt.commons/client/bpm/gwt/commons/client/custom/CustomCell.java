package bpm.gwt.commons.client.custom;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;


public class CustomCell<T> extends TextCell {

	private IDoubleClickHandler<T> dblClickHandler;
	
	public CustomCell(IDoubleClickHandler<T> dblClickHandler) {
		super();
		this.dblClickHandler = dblClickHandler;
	}

	@Override
	public Set<String> getConsumedEvents() {
		Set<String> consumedEvents = new HashSet<String>();
		consumedEvents.add("dblclick");
		return consumedEvents;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
		if (event.getType().equals("dblclick")) {
			dblClickHandler.run((T) context.getKey());
		}
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
	}
}