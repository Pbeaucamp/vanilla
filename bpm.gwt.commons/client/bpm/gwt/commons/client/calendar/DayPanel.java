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
 * Panel d'une journée de calendrier
 * 
 * Contient une liste d'événements
 * 
 * Affiche par défault 3 événements et un bouton pour afficher les autres s'il y en a plus
 * 
 */
public class DayPanel extends Composite {

	private static DayPanelUiBinder uiBinder = GWT.create(DayPanelUiBinder.class);

	interface DayPanelUiBinder extends UiBinder<Widget, DayPanel> {
	}
	
	interface MyStyle extends CssResource {
		String headerWithZIndex();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	Label nDay, nEvents;
	
	@UiField
	HTMLPanel header, contentPanel;
	
	@UiField
	FocusPanel btnDisplayEvents;
	
	private CalendarManager manager;
	private Date date;
	
	private List<CalendarEvent> events;
	
	public DayPanel(CalendarManager manager, int dayNum, Date firstDate) {
		this.manager = manager;
		this.date = firstDate;
		
		initWidget(uiBinder.createAndBindUi(this));
		
		nDay.setText(String.valueOf(dayNum));
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
		btnDisplayEvents.setVisible(true);
		
		int nbEvents = events.size();
		nEvents.setText("+" + (nbEvents - 3));
	}

	public Date getDate() {
		return date;
	}
}
