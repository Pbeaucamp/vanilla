package bpm.vanilla.portal.client.panels;

import bpm.vanilla.platform.core.beans.Widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DraggableWidget extends Composite {

	private static DraggableWidgetUiBinder uiBinder = GWT.create(DraggableWidgetUiBinder.class);

	interface DraggableWidgetUiBinder extends UiBinder<Widget, DraggableWidget> {
	}
	
	@UiField
	FocusPanel focus;
	
	@UiField
	TextBox htmlContent;
	
	private Widgets widget;

	private WidgetPanel parent;

	public DraggableWidget(Widgets w, WidgetPanel parentt) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parentt;
		this.widget = w;
		focus.getElement().setDraggable(Element.DRAGGABLE_TRUE);
		focus.addDragStartHandler(dragStartHandler);
		focus.addFocusHandler(new FocusHandler() {			
			@Override
			public void onFocus(FocusEvent event) {
				parent.setSelected(DraggableWidget.this);
			}
		});
		
		this.getElement().getStyle().setTop(w.getPositionY(), Unit.PX);
		this.getElement().getStyle().setLeft(w.getPositionX(), Unit.PX);
		this.getElement().getStyle().setWidth(w.getWidth(), Unit.PX);
		this.getElement().getStyle().setHeight(w.getHeight(), Unit.PX);
		
		htmlContent.setText(w.getWidgetUrl());
	}
	
	private DragStartHandler dragStartHandler = new DragStartHandler() {
		@Override
		public void onDragStart(DragStartEvent event) {
			event.stopPropagation();
			
			int absoluteLeft = focus.getAbsoluteLeft();
			int absoluteTop = focus.getAbsoluteTop();
			
			int x = event.getNativeEvent().getClientX() - absoluteLeft;
			int y = event.getNativeEvent().getClientY() - absoluteTop;
			
			event.setData("widget_id", widget.hashCode() + "");
			event.setData("left", x + "");
			event.setData("top", y + "");
			event.getDataTransfer().setDragImage(getElement(), x, y);
		}
	};

	public void setNewPosition(DropEvent event, FocusPanel focusPanel) {
		
		int absoluteLeft = focusPanel.getAbsoluteLeft();
		int absoluteTop = focusPanel.getAbsoluteTop();
		
		int x = event.getNativeEvent().getClientX() - absoluteLeft - Integer.parseInt(event.getData("left"));
		int y = event.getNativeEvent().getClientY() - absoluteTop - Integer.parseInt(event.getData("top"));
		
		widget.setPositionX(x);
		widget.setPositionY(y);
		
		this.getElement().getStyle().setTop(y, Unit.PX);
		this.getElement().getStyle().setLeft(x, Unit.PX);
		
	}

	public Widgets getWidgetElement() {
		widget.setWidgetUrl(htmlContent.getText());
		widget.setWidth(focus.getOffsetWidth());
		widget.setHeight(focus.getOffsetHeight());
		return widget;
	}

}
