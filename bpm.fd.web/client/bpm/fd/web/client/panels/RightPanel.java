package bpm.fd.web.client.panels;

import bpm.fd.web.client.handlers.IComponentSelectionHandler;
import bpm.fd.web.client.widgets.DashboardWidget;
import bpm.gwt.commons.client.loading.IWait;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class RightPanel extends Composite implements IComponentSelectionHandler {
	
	private static final int RIGHT_OPEN = 0;
	private static final int RIGHT_CLOSE = -476;

	private static FeedbackPanelUiBinder uiBinder = GWT.create(FeedbackPanelUiBinder.class);

	interface FeedbackPanelUiBinder extends UiBinder<Widget, RightPanel> {
	}
	
	interface MyStyle extends CssResource {
		String btnSelected();
	}
	
	@UiField
	MyStyle style;

	@UiField
	Label lblScripts;
	
	@UiField
	FocusPanel btnCss, btnEvents;
	
	@UiField
	HTMLPanel mainPanel;

	@UiField
	SimplePanel panelContent;

	private CssPanel cssPanel;
	private EventsPanel eventsPanel;
	
	private boolean isOpen = false;
	private int rightClose;

	public RightPanel(IWait waitPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rightClose = RIGHT_CLOSE - 20;
		
		mainPanel.getElement().getStyle().setRight(rightClose, Unit.PX);
		
		this.cssPanel = new CssPanel(waitPanel);
		this.eventsPanel = new EventsPanel();
		
		panelContent.setWidget(cssPanel);
	}

	@UiHandler("lblScripts")
	public void onScriptsClick(ClickEvent event) {
		updateUi();
	}

	@UiHandler("btnCss")
	public void onCssClick(ClickEvent event) {
		panelContent.setWidget(cssPanel);
		btnCss.addStyleName(style.btnSelected());
		btnEvents.removeStyleName(style.btnSelected());
	}

	@UiHandler("btnEvents")
	public void onEventsClick(ClickEvent event) {
		panelContent.setWidget(eventsPanel);
		btnCss.removeStyleName(style.btnSelected());
		btnEvents.addStyleName(style.btnSelected());
	}

	private void updateUi() {
		if (isOpen) {
			lblScripts.setVisible(true);
			mainPanel.getElement().getStyle().setRight(rightClose, Unit.PX);
		}
		else {
			lblScripts.setVisible(false);
			mainPanel.getElement().getStyle().setRight(RIGHT_OPEN, Unit.PX);
		}
		this.isOpen = !isOpen;
	}

	@UiHandler("btnHide")
	public void onCancelClick(ClickEvent event) {
		updateUi();
	}
	
	public CssPanel getCssPanel() {
		return cssPanel;
	}
	
	public EventsPanel getEventsPanel() {
		return eventsPanel;
	}

	@Override
	public void selectComponent(DashboardWidget widget) {
		eventsPanel.loadComponentEvents(widget.getComponent());
	}
}
