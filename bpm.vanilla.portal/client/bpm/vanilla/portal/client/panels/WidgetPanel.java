package bpm.vanilla.portal.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.platform.core.beans.Widgets;
import bpm.vanilla.portal.client.services.BiPortalService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class WidgetPanel extends Composite implements DragOverHandler, DragLeaveHandler, DropHandler {

	private static WidgetPanelUiBinder uiBinder = GWT.create(WidgetPanelUiBinder.class);

	interface WidgetPanelUiBinder extends UiBinder<Widget, WidgetPanel> {
	}
	
	@UiField
	HTMLPanel panelContent;
	
	@UiField
	FocusPanel focusPanel;
	
	@UiField
	Image btnAdd, btnDelete;
	
	private boolean isDesign = false;
	
	private List<DraggableWidget> widgets = new ArrayList<DraggableWidget>();

	private DraggableWidget selectedWidget;

	public WidgetPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		focusPanel.addDragOverHandler(this);
		focusPanel.addDragLeaveHandler(this);
		focusPanel.addDropHandler(this);
		
		updateUi();
	}

	private void fillWidgets() {
		BiPortalService.Connect.getInstance().getWidgets(new GwtCallbackWrapper<List<Widgets>>(null, false) {
			@Override
			public void onSuccess(List<Widgets> result) {		
				widgets.clear();			
				
				panelContent.clear();
				if(isDesign) {
					for(Widgets w : result) {
						DraggableWidget panel = new DraggableWidget(w, WidgetPanel.this);
						panelContent.add(panel);
						widgets.add(panel);
					}
				}
				else {
					for(Widgets w : result) {
						Frame frame = new Frame(w.getWidgetUrl());
						frame.getElement().getStyle().setPosition(Position.ABSOLUTE);
						frame.getElement().getStyle().setTop(w.getPositionY(), Unit.PX);
						frame.getElement().getStyle().setLeft(w.getPositionX(), Unit.PX);
						frame.getElement().getStyle().setWidth(w.getWidth(), Unit.PX);
						frame.getElement().getStyle().setHeight(w.getHeight(), Unit.PX);
						frame.getElement().getStyle().setBorderColor("lightgrey");
						frame.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
						frame.getElement().getStyle().setBorderWidth(1, Unit.PX);
						panelContent.add(frame);
					}
				}
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnSwitch")
	public void onSwitch(ClickEvent event) {
		if(isDesign) {
			BiPortalService.Connect.getInstance().saveWidgets(getWidgetList(), new GwtCallbackWrapper<Void>(null, false) {
				@Override
				public void onSuccess(Void result) {		
					isDesign = !isDesign;
					updateUi();
				}
			}.getAsyncCallback());
		}
		else {
			isDesign = !isDesign;
			updateUi();
		}
	}
	
	private List<Widgets> getWidgetList() {
		List<Widgets> ws = new ArrayList<Widgets>();
		for(DraggableWidget w : widgets) {
			ws.add(w.getWidgetElement());
		}
		return ws;
	}

	@UiHandler("btnAdd")
	public void onAdd(ClickEvent event) {
		Widgets w = new Widgets();
		w.setHeight(100);
		w.setWidth(100);
		w.setPositionX(100);
		w.setPositionY(100);
		DraggableWidget panel = new DraggableWidget(w, this);
		widgets.add(panel);
		panelContent.add(panel);
	}
	
	@UiHandler("btnDelete")
	public void onDelete(ClickEvent event) {
		widgets.remove(selectedWidget);
		panelContent.remove(selectedWidget);
	}
	
	private void updateUi() {
		btnAdd.setVisible(isDesign);
		btnDelete.setVisible(isDesign);
		
		fillWidgets();
	}
	
	@Override
	public void onDrop(DropEvent event) {
		event.preventDefault();

		int hash = Integer.parseInt(event.getData("widget_id"));
		for(DraggableWidget w : widgets) {
			if(w.getWidgetElement().hashCode() == hash) {
				w.setNewPosition(event, focusPanel);
			}
		}
	}

	@Override
	public void onDragLeave(DragLeaveEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDragOver(DragOverEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void setSelected(DraggableWidget draggableWidget) {
		this.selectedWidget = draggableWidget;
	}
	
}
