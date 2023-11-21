package bpm.gwt.commons.client.panels;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

public class Splitter2 extends Widget {



	private Widget firstWidget, lastWidget;
	private int minPercentagefirstWidget = 5, minPercentagelastWidget = 5;
	private boolean mouseDown;

	public Splitter2(Widget firstWidget, Widget lastWidget, String styleName, int minPercentagefirstWidget, int minPercentagelastWidget) {
		this.firstWidget = firstWidget;
		this.lastWidget = lastWidget;
		this.minPercentagefirstWidget = minPercentagefirstWidget;
		this.minPercentagelastWidget = minPercentagelastWidget;

		setElement(Document.get().createDivElement());
		sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONMOUSEMOVE | Event.ONDBLCLICK);

		addStyleName(styleName);
	}
	
	public Splitter2(Widget firstWidget, Widget lastWidget, String styleName) {
		this.firstWidget = firstWidget;
		this.lastWidget = lastWidget;

		setElement(Document.get().createDivElement());
		sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONMOUSEMOVE | Event.ONDBLCLICK);

		addStyleName(styleName);
	}

	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONMOUSEDOWN:
			mouseDown = true;

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
//				int gap = event.getClientY() - splitterAbsoluteTop;

//				int bottom = firstWidgetBottom - gap;
//				if ((!heightValid(firstWidget) && gap < 0) || (bottom <= MIN_BOTTOM && gap > 0)) {
//					return;
//				}
//				firstWidget.getElement().getStyle().setBottom(bottom, Unit.PX);
//
//				int height = lastWidgetHeight - gap;
//				lastWidget.setHeight(height + "px");
				float gap = event.getClientY() - firstWidget.getAbsoluteTop();
				
				float heightfirst  = firstWidget.getOffsetHeight();
				float heightlast  = lastWidget.getOffsetHeight();
			
				
				float firstHeight = (float) (gap/(heightfirst + heightlast)*100)-1;
				float lastHeight = (float) ((heightfirst + heightlast - gap)/(heightfirst + heightlast)*100)-1;
				
				if(firstHeight < minPercentagefirstWidget)
					return;

				if(lastHeight < minPercentagelastWidget)
					return;
				
				firstWidget.setHeight(firstHeight + "%");
				lastWidget.setHeight(lastHeight + "%");
				event.preventDefault();
			}
			break;
		}
	}

	
}