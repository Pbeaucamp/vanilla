package bpm.fd.web.client.widgets;

import bpm.fd.core.DashboardComponent;
import bpm.fd.web.client.handlers.HasComponentSelectionHandler;
import bpm.fd.web.client.handlers.WidgetContextHandler;
import bpm.fd.web.client.panels.IDropPanel;
import bpm.fd.web.client.utils.WidgetManager;
import bpm.gwt.commons.client.loading.IWait;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasContextMenuHandlers;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;

public abstract class WidgetComposite extends Composite implements HasContextMenuHandlers, DoubleClickHandler, CloseHandler<PopupPanel> {
	
	protected static final int DEFAULT_WIDTH = 150;
	protected static final int DEFAULT_HEIGHT = 150;

	private IWait waitPanel;
	private IDropPanel dropPanel;

	private HasComponentSelectionHandler componentHandler;
	private HandlerManager manager = new HandlerManager(this);
	private WidgetContextHandler ctxHandler;

	protected DashboardComponent component;

	private String generatedId;
	
	private int positionLeft;
	private int positionTop;

	public WidgetComposite(IWait waitPanel, HasComponentSelectionHandler componentHandler, int positionLeft, int positionTop) {
		this.waitPanel = waitPanel;
		this.componentHandler = componentHandler;
		this.positionLeft = positionLeft;
		this.positionTop = positionTop;

		sinkEvents(Event.ONCONTEXTMENU);
		this.ctxHandler = new WidgetContextHandler(this);
		addContextMenuHandler(ctxHandler);
	}

	public abstract DashboardComponent getComponent();
	
	public void updatePosition(int left, int top) {
		this.positionLeft = left;
		this.positionTop = top;
		
		getElement().getStyle().setTop(top, Unit.PX);
		getElement().getStyle().setLeft(left, Unit.PX);
	}
	
	public abstract int getWidth();
	
	public abstract int getHeight();
	
	public int getLeft() {
		return positionLeft;
	}
	
	public int getTop() {
		return positionTop;
	}

	public void setDropPanel(IDropPanel dropPanel) {
		this.dropPanel = dropPanel;
		ctxHandler.setDropPanel(dropPanel);
	}
	
	public IDropPanel getDropPanel() {
		return dropPanel;
	}
	
	public void removeFromWidget() {
		dropPanel.removeWidget(this, false);
	}

	@Override
	public HandlerRegistration addContextMenuHandler(ContextMenuHandler handler) {
		return manager.addHandler(ContextMenuEvent.getType(), handler);
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		manager.fireEvent(event);
		super.fireEvent(event);
	}
	
	public HasComponentSelectionHandler getComponentHandler() {
		return componentHandler;
	}
	
	public IWait getWaitPanel() {
		return waitPanel;
	}
	
	public String getGeneratedId() {
		return generatedId;
	}
	
	public void setGeneratedId(String generatedId) {
		this.generatedId = generatedId;
	}
	
	public abstract WidgetManager getWidgetManager();
}
