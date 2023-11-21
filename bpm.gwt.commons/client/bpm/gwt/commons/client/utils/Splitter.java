package bpm.gwt.commons.client.utils;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

public class Splitter extends Widget {

	private static final int MIN_HEIGHT_SIZE = 20;
	private static final int MIN_WIDTH_SIZE = 100;
	private static final int MIN_BOTTOM = 80;

	private boolean vertical;
	
	private Widget firstWidget, lastWidget;
	private boolean mouseDown;

	public Splitter(Widget firstWidget, Widget lastWidget, String styleName, boolean vertical) {
		this.firstWidget = firstWidget;
		this.lastWidget = lastWidget;
		this.vertical = vertical;

		setElement(Document.get().createDivElement());
		sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONMOUSEMOVE | Event.ONDBLCLICK);

		addStyleName(styleName);
	}

	private int splitterAbsoluteTop = 0;

	private int firstWidgetBottom = 0;
	private int lastWidgetHeight = 0;

	private int splitterAbsoluteLeft = 0;
	private int horizontalReference = 0;

	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONMOUSEDOWN:
			mouseDown = true;

			if (vertical) {
				splitterAbsoluteTop = getAbsoluteTop();
	
				firstWidgetBottom = getWigdetBottom(firstWidget);
				lastWidgetHeight = getWidgetHeight(lastWidget);
			}
			else {
				splitterAbsoluteLeft = getAbsoluteLeft();
				horizontalReference = getWidgetWidth(firstWidget) - 20;
			}

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
				if (vertical) {
					int gap = event.getClientY() - splitterAbsoluteTop;
	
					int bottom = firstWidgetBottom - gap;
					if ((!heightValid(firstWidget) && gap < 0) || (bottom <= MIN_BOTTOM && gap > 0)) {
						return;
					}
					firstWidget.getElement().getStyle().setBottom(bottom, Unit.PX);
	
					int height = lastWidgetHeight - gap;
					lastWidget.setHeight(height + "px");
				}
				else {
					int gap = event.getClientX() - splitterAbsoluteLeft;
					
					int left = horizontalReference + gap + 40;
					if ((!widthValid(firstWidget) && gap < 0) || (left <= MIN_BOTTOM && gap > 0)) {
						return;
					}
					lastWidget.getElement().getStyle().setLeft(left, Unit.PX);
	
					int width = horizontalReference + gap + 20;
					if (width > 0) {
						firstWidget.setWidth(width + "px");
					}
				}

				event.preventDefault();
			}
			break;
		}
	}

	private boolean heightValid(Widget widget) {
		return widget.getOffsetHeight() > MIN_HEIGHT_SIZE;
	}

	private boolean widthValid(Widget widget) {
		return widget.getOffsetWidth() > MIN_WIDTH_SIZE;
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

	private int getWidgetWidth(Widget widget) {
		return widget.getOffsetWidth();
	}
}