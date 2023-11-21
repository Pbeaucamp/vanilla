package bpm.fd.web.client.popup;

import bpm.fd.core.DashboardComponent;
import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.handlers.HasComponentSelectionHandler;
import bpm.fd.web.client.panels.properties.PropertiesPanel;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class PropertiesPopup extends PopupPanel {
	
	private static FormatUiBinder uiBinder = GWT.create(FormatUiBinder.class);

	interface FormatUiBinder extends UiBinder<Widget, PropertiesPopup> {
	}
	
	@UiField
	HTMLPanel popupProperties;
	
	@UiField
	FocusPanel panelTop;
	
	@UiField
	Label lblTitle;
	
	@UiField
	Image btnReduce, btnMax;

	@UiField
	SimplePanel contentPanel;
	
	private PropertiesPanel propertiesPanel;
	
	// DND Property
	private boolean dragging;
	private int dragStartX, dragStartY;
	private int windowWidth;
	private int clientLeft;
	private int clientTop;
	
	public PropertiesPopup(IWait waitPanel, HasComponentSelectionHandler componentSelectionHandler, DashboardComponent component, boolean compact) {
		setWidget(uiBinder.createAndBindUi(this));
		lblTitle.setText(Labels.lblCnst.PropertiesOf() + " " + component.getTitle());

		propertiesPanel = new PropertiesPanel(waitPanel, componentSelectionHandler, this, component, compact);
		contentPanel.setWidget(propertiesPanel);
		popupProperties.addStyleName(VanillaCSS.POPUP_PANEL);
		
		if (compact) {
			panelTop.removeFromParent();
		}
		else {
			this.setAnimationEnabled(true);
	
			windowWidth = Window.getClientWidth();
			clientLeft = Document.get().getBodyOffsetLeft();
			clientTop = Document.get().getBodyOffsetTop();
	
			MouseHandler mouseHandler = new MouseHandler();
			panelTop.addDomHandler(mouseHandler, MouseDownEvent.getType());
			panelTop.addDomHandler(mouseHandler, MouseUpEvent.getType());
			panelTop.addDomHandler(mouseHandler, MouseMoveEvent.getType());
			panelTop.addDomHandler(mouseHandler, MouseOverEvent.getType());
			panelTop.addDomHandler(mouseHandler, MouseOutEvent.getType());
		}
	}

	@UiHandler("btnReduce")
	public void onReduceClick(ClickEvent event) {
		resize(true);
	}

	@UiHandler("btnMax")
	public void onMaximiseClick(ClickEvent event) {
		resize(false);
	}

	private void resize(boolean reduce) {
		btnMax.setVisible(reduce);
		btnReduce.setVisible(!reduce);

		propertiesPanel.resize(reduce);

		center();
	}
	
	public PropertiesPanel getPropertiesPanel() {
		return propertiesPanel;
	}
	
	private class MouseHandler implements MouseDownHandler, MouseUpHandler, MouseOutHandler, MouseOverHandler, MouseMoveHandler {

		@Override
		public void onMouseDown(MouseDownEvent event) {
			beginDragging(event);
		}

		@Override
		public void onMouseMove(MouseMoveEvent event) {
			continueDragging(event);
		}

		@Override
		public void onMouseUp(MouseUpEvent event) {
			endDragging(event);
		}

		@Override
		public void onMouseOver(MouseOverEvent event) {
		}

		@Override
		public void onMouseOut(MouseOutEvent event) {
		}
	}

	@Override
	public void onBrowserEvent(Event event) {
		// If we're not yet dragging, only trigger mouse events if the event
		// occurs
		// in the caption wrapper
		switch (event.getTypeInt()) {
		case Event.ONMOUSEDOWN:
		case Event.ONMOUSEUP:
		case Event.ONMOUSEMOVE:
		case Event.ONMOUSEOVER:
		case Event.ONMOUSEOUT:
			if (!dragging) {
				return;
			}
		}

		super.onBrowserEvent(event);
	}

	protected void beginDragging(MouseDownEvent event) {
		if (DOM.getCaptureElement() == null) {
			/*
			 * Need to check to make sure that we aren't already capturing an
			 * element otherwise events will not fire as expected. If this check
			 * isn't here, any class which extends custom button will not fire
			 * its click event for example.
			 */
			dragging = true;
			DOM.setCapture(panelTop.getElement());
			dragStartX = event.getX();
			dragStartY = event.getY();
		}
	}

	protected void continueDragging(MouseMoveEvent event) {
		if (dragging) {
			int absX = event.getX() + getAbsoluteLeft();
			int absY = event.getY() + getAbsoluteTop();

			// if the mouse is off the screen to the left, right, or top, don't
			// move the dialog box. This would let users lose dialog boxes,
			// which
			// would be bad for modal popups.
			if (absX < clientLeft || absX >= windowWidth || absY < clientTop) {
				return;
			}

			setPopupPosition(absX - dragStartX, absY - dragStartY);
		}
	}

	protected void endDragging(MouseUpEvent event) {
		dragging = false;
		DOM.releaseCapture(panelTop.getElement());
	}
	
	@Override
	public void center() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				PropertiesPopup.super.center();
			}
		});
	}
}
