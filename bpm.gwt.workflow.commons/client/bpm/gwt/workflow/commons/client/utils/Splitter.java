package bpm.gwt.workflow.commons.client.utils;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

public class Splitter extends Widget {

	private static final int MIN_SIZE = 20;
	private static final int MIN_BOTTOM = 80;

	private Widget firstWidget, lastWidget;
	private boolean mouseDown;

	public Splitter(Widget firstWidget, Widget lastWidget, String styleName) {
		this.firstWidget = firstWidget;
		this.lastWidget = lastWidget;

		setElement(Document.get().createDivElement());
		sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONMOUSEMOVE | Event.ONDBLCLICK);

		addStyleName(styleName);
	}

	private int splitterAbsoluteTop = 0;

	private int firstWidgetBottom = 0;
	private int lastWidgetHeight = 0;

	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONMOUSEDOWN:
			mouseDown = true;

			splitterAbsoluteTop = getAbsoluteTop();

			firstWidgetBottom = getWigdetBottom(firstWidget);
			lastWidgetHeight = getWidgetHeight(lastWidget) - 20;

			Event.setCapture(getElement());
			event.preventDefault();
			break;

		case Event.ONMOUSEUP:
			mouseDown = false;
			Event.releaseCapture(getElement());
			event.preventDefault();
			break;

		case Event.ONMOUSEMOVE:
			if (mouseDown) {
				int gap = event.getClientY() - splitterAbsoluteTop;

				int bottom = firstWidgetBottom - gap;
				if ((!heightValid(firstWidget) && gap < 0) || (bottom <= MIN_BOTTOM && gap > 0)) {
					return;
				}
				firstWidget.getElement().getStyle().setBottom(bottom, Unit.PX);

				int height = lastWidgetHeight - gap;
				lastWidget.setHeight(height + "px");

				event.preventDefault();
			}
			break;
		}
	}

	private boolean heightValid(Widget widget) {
		return widget.getOffsetHeight() > MIN_SIZE;
	}

	private int getWigdetBottom(Widget widget) {
		String bottomStr = widget.getElement().getStyle().getBottom();
		if (bottomStr.contains("px")) {
			return Integer.parseInt(bottomStr.substring(0, bottomStr.length() - 2));
		}
		return 0;
	}

	private int getWidgetHeight(Widget widget) {
		return widget.getOffsetHeight();
	}
}