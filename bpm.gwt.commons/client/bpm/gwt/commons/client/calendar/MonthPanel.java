package bpm.gwt.commons.client.calendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.gwt.commons.client.calendar.CalendarPanel.CalendarManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel d'un mois de calendrier
 * 
 * Contient une liste d'événements
 * 
 * Affiche par défault 3 événements et un bouton pour afficher les autres s'il y en a plus
 * 
 */
public class MonthPanel extends Composite {

	private static MonthPanelUiBinder uiBinder = GWT.create(MonthPanelUiBinder.class);

	interface MonthPanelUiBinder extends UiBinder<Widget, MonthPanel> {
	}
	
	interface MyStyle extends CssResource {
		String headerWithZIndex();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	Label nEvents;
	
	@UiField
	HTMLPanel header, contentPanel;
	
	@UiField
	FocusPanel btnDisplayEvents;
	
	private CalendarManager manager;
	private Date date;
	
	private List<CalendarEvent> events;
	
	public MonthPanel(CalendarManager manager, int monthNum) {
		this.manager = manager;
		
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void increaseZIndex() {
		header.addStyleName(style.headerWithZIndex());
	}

	@UiHandler("btnDisplayEvents")
	public void onDisplayEvents(ClickEvent event) {
		DayDetailPopup popup = new DayDetailPopup(manager, date, events, false);
		popup.center();
		
		manager.registerDayDetail(popup);
	}
	
	public void clear() {
		if (events != null && !events.isEmpty()) {
			events.clear();
			contentPanel.clear();
		}
	}

	public void addEvent(CalendarEvent event, boolean isFirst, boolean isLast) {
		if (events == null) {
			events = new ArrayList<CalendarEvent>();
		}
		events.add(event);
		
		if (events.size() > 3) {
			updateEventsNumber();
		}
		else {
			EventPanel eventPanel = new EventPanel(manager, event, false, isFirst, isLast);
			contentPanel.add(eventPanel);
		}
	}

	private void updateEventsNumber() {
		header.setVisible(true);
		btnDisplayEvents.setVisible(true);
		
		int nbEvents = events.size();
		nEvents.setText("+" + (nbEvents - 3));
	}

	public Date getDate() {
		return date;
	}
}
