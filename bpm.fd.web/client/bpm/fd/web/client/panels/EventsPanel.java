package bpm.fd.web.client.panels;

import java.util.HashMap;

import bpm.fd.core.DashboardComponent;
import bpm.fd.core.component.ComponentType;
import bpm.fd.core.component.EventType;
import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.gwt.commons.client.custom.ListBoxWithButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class EventsPanel extends Composite {

	private static EventsPanelUiBinder uiBinder = GWT.create(EventsPanelUiBinder.class);

	interface EventsPanelUiBinder extends UiBinder<Widget, EventsPanel> {
	}
	
	@UiField
	ListBoxWithButton<EventType> lstEvents;
	
	@UiField
	LabelTextArea txtEvent;

	private HashMap<EventType, String> dashboardScripts = new HashMap<EventType, String>();
	private DashboardComponent component;

	public EventsPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		loadComponentEvents(null);
	}
	
	public void loadComponentEvents(DashboardComponent component) {
		this.component = component;
		if (component != null) {
			lstEvents.setList(component.getEventsType());
			changeEvent(null);
		}
	}
	
	public void loadDashboardEvents(HashMap<EventType, String> dashboardScripts) {
		this.dashboardScripts = dashboardScripts;
		lstEvents.setList(EventType.getAvailablesFor(ComponentType.DASHBOARD));
		changeEvent(null);
	}

	@UiHandler("lstEvents")
	public void changeEvent(ChangeEvent event) {
		EventType selectedType = lstEvents.getSelectedObject();
		if (selectedType != null && component != null) {
			String javascript = component.getJavaScript(selectedType);
			txtEvent.setText(javascript);
		}
		else if (selectedType != null) {
			String javascript = dashboardScripts.get(selectedType);
			txtEvent.setText(javascript);
		}
		else {
			txtEvent.setText("");
		}
	}
	
	public HashMap<EventType, String> getDashboardScripts() {
		return dashboardScripts;
	}	
	
	@UiHandler("txtEvent")
	public void onNameChange(KeyUpEvent event) {
		String javascript = txtEvent.getText();
		
		EventType selectedType = lstEvents.getSelectedObject();
		if (component != null) {
			component.setJavaScript(selectedType, javascript);
		}
		else {
			dashboardScripts.put(selectedType, javascript);
		}
	}
}
