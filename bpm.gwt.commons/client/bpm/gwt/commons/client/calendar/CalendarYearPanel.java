package bpm.gwt.commons.client.calendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.gwt.commons.client.calendar.CalendarPanel.CalendarManager;
import bpm.gwt.commons.client.custom.v2.YearListBox;
import bpm.gwt.commons.client.utils.Tools;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel de calendrier
 * 
 * Permet de visualiser des événements sous la forme d'un agenda Une légende
 * peut être affichée
 * 
 */
public class CalendarYearPanel extends Composite {

	private static DateTimeFormat df = DateTimeFormat.getFormat(Tools.DEFAULT_DATE_FORMAT);

	private static CalendarYearPanelUiBinder uiBinder = GWT.create(CalendarYearPanelUiBinder.class);

	interface CalendarYearPanelUiBinder extends UiBinder<Widget, CalendarYearPanel> {
	}

	@UiField
	SimplePanel contentJan, contentFeb, contentMar, contentApr, contentMay, contentJune, contentJul, contentAug, contentSep, contentOct, contentNov, contentDec;

	@UiField
	YearListBox lstYears;

	@UiField
	Label btnBefore, btnAfter;

	private CalendarManager manager;

	private Integer year;

	private List<MonthPanel> months = new ArrayList<MonthPanel>();

	public CalendarYearPanel(CalendarManager manager, boolean edit, boolean changeYear) {
		this.manager = manager;

		initWidget(uiBinder.createAndBindUi(this));

		lstYears.setVisible(changeYear);
		btnBefore.setVisible(changeYear);
		btnAfter.setVisible(changeYear);

		for (int month = 0; month < 12; month++) {
			MonthPanel panel = new MonthPanel(manager, month);
			months.add(panel);
			getMonthPanel(month).setWidget(panel);
		}
	}

	@SuppressWarnings("deprecation")
	public void buildCalendar(Date date) {
		int year = 1900 + date.getYear();

		buildCalendar(year);
	}

	public void buildCalendar(Integer year) {
		this.year = year;

		lstYears.loadListMonth(year, false);
		manager.refreshEvents();
	}

	public Integer getYear() {
		return year;
	}

	/**
	 * Get the first date in the selected month
	 * 
	 * @return Date
	 */
	public Date getFirstDate() {
		return df.parse("01/01/" + year);
	}
	
	public Date getLastDate() {
		return df.parse("31/12/" + year);
	}

	@UiHandler("lstYears")
	public void onYears(ChangeEvent event) {
		Integer selected = lstYears.getValue();
		buildCalendar(selected);
	}

	@UiHandler("btnBefore")
	public void onBefore(ClickEvent event) {
		Integer previous = lstYears.getValue() - 1;
		buildCalendar(previous);
	}

	@UiHandler("btnAfter")
	public void onAfter(ClickEvent event) {
		Integer next = lstYears.getValue() + 1;
		buildCalendar(next);
	}

	public void loadEvents(List<CalendarEvent> events) {
		clear();

		if (events != null) {
			for (CalendarEvent event : events) {
				int monthNumber = -1;
				for (int month = 0; month < 12; month++) {
					if (isSameMonth(event.getBeginDate(), month)) {
						monthNumber = 0;
						months.get(month).addEvent(event, true, event.getDuration() == 0);
					}
					else if (monthNumber >= event.getDuration()) {
						break;
					}
					else if (monthNumber >= 0) {
						monthNumber++;
						months.get(month).addEvent(event, false, monthNumber >= event.getDuration());
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private boolean isSameMonth(Date beginDate, int month) {
		return beginDate.getMonth() == month;
	}

	private SimplePanel getMonthPanel(int month) {
		switch (month) {
		case 0:
			return contentJan;
		case 1:
			return contentFeb;
		case 2:
			return contentMar;
		case 3:
			return contentApr;
		case 4:
			return contentMay;
		case 5:
			return contentJune;
		case 6:
			return contentJul;
		case 7:
			return contentAug;
		case 8:
			return contentSep;
		case 9:
			return contentOct;
		case 10:
			return contentNov;
		case 11:
			return contentDec;
		default:
			break;
		}
		return contentJan;
	}

	private void clear() {
		for (MonthPanel panel : months) {
			panel.clear();
		}
	}
}
