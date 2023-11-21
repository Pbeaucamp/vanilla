package bpm.gwt.commons.client.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.listeners.FinishListener;
import bpm.gwt.commons.client.listeners.HasFinishListener;
import bpm.gwt.commons.client.loading.GreyAbsolutePanel;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.loading.WaitAbsolutePanel;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * This class is the Base for every dialog used in a Vanilla Web App Application
 * 
 * In need to be use with the help of {link#AbstractDialogBox}
 * 
 * (THIS CLASS IS NOT INTEND TO BE SUBCLASS)
 * 
 */
public class BaseDialogBox extends PopupPanel implements HasFinishListener, IWait {

	private static BaseDialogBoxUiBinder uiBinder = GWT.create(BaseDialogBoxUiBinder.class);

	interface BaseDialogBoxUiBinder extends UiBinder<Widget, BaseDialogBox> {
	}

	interface MyStyle extends CssResource {
		String customDialogZIndex();

		String customDialogGlassZIndex();

		String panelButton();

		String btnBottom();

		String customDialogMax();

		String maxMainPanel();

		String maxContentPanelWithButtons();

		String maxContentPanelNoButton();

		String maxBottomPanel();
	}

	@UiField
	MyStyle style;

	@UiField
	FocusPanel panelTop;

	@UiField
	HTMLPanel dialog, contentPanel, panelButton;

	@UiField
	Image btnReduce, btnMax, btnClose;

	@UiField
	Label lblTitle;

	private AbstractDialogBox dialogManager;

	private final List<FinishListener> listeners = new ArrayList<FinishListener>();

	// DND Property
	private boolean dragging;
	private int dragStartX, dragStartY;
	private int windowWidth;
	private int clientLeft;
	private int clientTop;

	// Wait Part
	private GreyAbsolutePanel greyPanel;
	private WaitAbsolutePanel waitPanel;
	private boolean isCharging;

	private int indexCharging = 0;

	// Variables
	private boolean close = false;
	private boolean hasButton;

	public BaseDialogBox(AbstractDialogBox dialogManager, String title, boolean canBeResize, boolean hasButton) {
		super.setWidget(uiBinder.createAndBindUi(this));
		this.hasButton = hasButton;
		this.dialogManager = dialogManager;

		lblTitle.setText(title);

		if (!canBeResize) {
			btnReduce.setVisible(false);
			btnMax.setVisible(false);
		}
		else {
			btnReduce.setVisible(false);
		}

		this.setGlassEnabled(true);
		this.setGlassStyleName(style.customDialogGlassZIndex());

		windowWidth = Window.getClientWidth();
		clientLeft = Document.get().getBodyOffsetLeft();
		clientTop = Document.get().getBodyOffsetTop();

		MouseHandler mouseHandler = new MouseHandler();
		panelTop.addDomHandler(mouseHandler, MouseDownEvent.getType());
		panelTop.addDomHandler(mouseHandler, MouseUpEvent.getType());
		panelTop.addDomHandler(mouseHandler, MouseMoveEvent.getType());
		panelTop.addDomHandler(mouseHandler, MouseOverEvent.getType());
		panelTop.addDomHandler(mouseHandler, MouseOutEvent.getType());

		dialog.addStyleName(VanillaCSS.DIALOG);
		panelTop.addStyleName(VanillaCSS.DIALOG_TOP);
		btnReduce.addStyleName(VanillaCSS.DIALOG_TOP_BUTTON);
		btnMax.addStyleName(VanillaCSS.DIALOG_TOP_BUTTON);
		btnClose.addStyleName(VanillaCSS.DIALOG_TOP_BUTTON);
	}
	
	@Override
	public void setTitle(String title) {
		lblTitle.setText(title);
	}

	public void increaseZIndex() {
		dialog.addStyleName(style.customDialogZIndex());
		setGlassStyleName(style.customDialogGlassZIndex());
	}
	
	public void increaseZIndex(int zIndex) {
		dialog.getElement().getStyle().setZIndex(zIndex);
		setGlassStyleName(style.customDialogGlassZIndex());
		getGlassElement().getStyle().setZIndex(zIndex - 1);
//		dialog.addStyleName(style.customDialogZIndex());
	}

	public Button createButton(String label, ClickHandler clickHandler) {
		Button btn = new Button(label);
		btn.addStyleName(style.btnBottom());
		btn.addClickHandler(clickHandler);

		panelButton.add(btn);
		panelButton.setStyleName(style.panelButton());

		return btn;
	}

	public void createButtonBar(String lblConfirm, ClickHandler confirmHandler, String lblCancel, ClickHandler cancelHandler) {
		createButton(lblCancel, cancelHandler);
		createButton(lblConfirm, confirmHandler);
	}

	@Override
	public void setWidget(Widget w) {
		contentPanel.add(w);
	}

	@UiHandler("btnReduce")
	public void onReduceClick(ClickEvent event) {
		changeDisplay(true);
	}

	@UiHandler("btnMax")
	public void onMaximiseClick(ClickEvent event) {
		changeDisplay(false);
	}

	private void changeDisplay(boolean reduce) {
		btnMax.setVisible(reduce);
		btnReduce.setVisible(!reduce);

		String styleContentPanel = style.maxContentPanelNoButton();
		if (hasButton) {
			styleContentPanel = style.maxContentPanelWithButtons();
		}

		if (reduce) {
			this.removeStyleName(style.customDialogMax());
			dialog.removeStyleName(style.maxMainPanel());
			contentPanel.removeStyleName(styleContentPanel);
			panelButton.removeStyleName(style.maxBottomPanel());
		}
		else {
			this.addStyleName(style.customDialogMax());
			dialog.addStyleName(style.maxMainPanel());
			contentPanel.addStyleName(styleContentPanel);
			panelButton.addStyleName(style.maxBottomPanel());
		}

		dialogManager.maximize(!reduce);
	}

	@UiHandler("btnClose")
	public void onCloseClick(ClickEvent event) {
		this.close = true;
		hide();
	}
	
	public boolean isClose() {
		return close;
	}

	protected void finish(Object result, Object source, String result1) {
		fireFinishClicked(result, source, result1);
	}

	@Override
	public void addFinishListener(FinishListener listener) {
		listeners.add(listener);
	}

	public void removeFinishListener(FinishListener listener) {
		listeners.remove(listener);
	}

	public List<FinishListener> getFinishListeners() {
		return listeners;
	}
	
	@Override
	protected void onDetach() {
		dialogManager.onDetach();
		super.onDetach();
	}
	
	@Override
	protected void onAttach() {
		dialogManager.onAttach();
		super.onAttach();
	}

	private void fireFinishClicked(Object result, Object source, String result1) {
		for (FinishListener listener : getFinishListeners()) {
			if (listener != null) {
				listener.onFinish(result, source, result1);
			}
		}
	}

	@Override
	public void center() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				BaseDialogBox.super.center();

				int clientWidth = Window.getClientWidth();
				int offsetWidth = contentPanel.getOffsetWidth();

				int clientHeight = Window.getClientHeight();
				int offsetHeight = contentPanel.getOffsetHeight();

				int left = (clientWidth - offsetWidth) >> 1;
				int top = (clientHeight - offsetHeight) >> 1;

				left -= Document.get().getBodyOffsetLeft();
				top -= Document.get().getBodyOffsetTop();

				Element elem = getElement();
				elem.getStyle().setPropertyPx("left", left);
				elem.getStyle().setPropertyPx("top", top);
			}
		});
	}

	@Override
	public void showWaitPart(boolean visible) {

		if (visible) {
			indexCharging++;

			if (!isCharging) {
				isCharging = true;

				greyPanel = new GreyAbsolutePanel();
				waitPanel = new WaitAbsolutePanel();

				dialog.add(greyPanel);
				dialog.add(waitPanel);

				int height = this.getOffsetHeight();
				int width = this.getOffsetWidth();

				if (height != 0) {
					waitPanel.getElement().getStyle().setTop(((height / 2) - 50), Unit.PX);
				}
				else {
					waitPanel.getElement().getStyle().setTop(42, Unit.PCT);
				}

				if (width != 0) {
					waitPanel.getElement().getStyle().setLeft(((width / 2) - 100), Unit.PX);
				}
				else {
					waitPanel.getElement().getStyle().setLeft(35, Unit.PCT);
				}
			}
		}
		else if (!visible) {
			indexCharging--;

			if (isCharging && indexCharging <= 0) {
				isCharging = false;

				dialog.remove(greyPanel);
				dialog.remove(waitPanel);
			}

			if (indexCharging < 0) {
				indexCharging = 0;
			}
		}
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
}
