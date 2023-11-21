package bpm.gwt.commons.client.calendar;

import java.util.Date;
import java.util.List;

import bpm.gwt.commons.client.calendar.CalendarPanel.CalendarManager;
import bpm.gwt.commons.client.utils.Tools;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Popup de détail d'une journée d'agenda
 */
public class DayDetailPopup extends PopupPanel {

	private static DayDetailPopupUiBinder uiBinder = GWT.create(DayDetailPopupUiBinder.class);

	interface DayDetailPopupUiBinder extends UiBinder<Widget, DayDetailPopup> {
	}
	
	@UiField
	Label lblDate;

	@UiField
	HTMLPanel contentPanel;

	public DayDetailPopup(CalendarManager manager, Date date, List<CalendarEvent> events, boolean edit) {
		setWidget(uiBinder.createAndBindUi(this));
		
		setAutoHideEnabled(true);
		setGlassEnabled(true);
		
		setDate(date);
		if (events != null) {
			for (CalendarEvent event : events) {
				addEvent(manager, date, event, edit);
			}
		}
	}

	private void setDate(Date date) {
		lblDate.setText(date != null ? Tools.getFullDateAsString(date) : "");
	}

	private void addEvent(CalendarManager manager, Date date, CalendarEvent event, boolean edit) {
		EventPanel eventPanel = new EventPanel(manager, event, edit, true, true);
		contentPanel.add(eventPanel);
	}
}
