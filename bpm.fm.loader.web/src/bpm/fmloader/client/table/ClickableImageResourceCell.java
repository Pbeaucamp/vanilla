package bpm.fmloader.client.table;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.dom.client.Element;

public class ClickableImageResourceCell extends ImageResourceCell {
	public ClickableImageResourceCell() {
		super();
	}

	@Override
	public Set<String> getConsumedEvents() {
		Set<String> set = new HashSet<String>();
		set.add("click");
		return set;
	}

	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, com.google.gwt.dom.client.Element parent, ImageResource value, NativeEvent event, ValueUpdater<ImageResource> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);
		if ("click".equals(event.getType())) {
			EventTarget eventTarget = event.getEventTarget();
			if (!Element.is(eventTarget)) {
				return;
			}
			if (parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget))) {
				// Ignore clicks that occur outside of the main element.
				keyDown(context, parent, value, event, valueUpdater);
			}
		}
//		else if ("hover".equals(event.getType())) {
//			if(value.getName().equals("delete_gray")) {
//				
//			}
//		}
	}

	protected void keyDown(Context context, Element parent, ImageResource value, NativeEvent event, ValueUpdater<ImageResource> valueUpdater) {
		if (valueUpdater != null) {
			valueUpdater.update(value);
		}
	}
}
