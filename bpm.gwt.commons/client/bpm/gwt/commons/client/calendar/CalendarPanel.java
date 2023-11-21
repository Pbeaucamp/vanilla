package bpm.gwt.commons.client.calendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.gwt.commons.client.custom.v2.MonthYearListBox;
import bpm.gwt.commons.client.utils.MonthYear;
import bpm.gwt.commons.client.utils.Tools;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * Panel de calendrier
 * 
 * Permet de visualiser des événements sous la forme d'un agenda
 * Une légende peut être affichée
 * 
 */
public class CalendarPanel extends Composite {

	private static DateTimeFormat df = DateTimeFormat.getFormat(Tools.DEFAULT_DATE_FORMAT);

	private static CalendarPanelUiBinder uiBinder = GWT.create(CalendarPanelUiBinder.class);

	interface CalendarPanelUiBinder extends UiBinder<Widget, CalendarPanel> {
	}

	@UiField
	HTMLPanel panelContent;

	@UiField
	MonthYearListBox lstMonths;

	@UiField
	Label btnBefore, btnAfter;

	private CalendarManager manager;

	private MonthYear monthYear;
	private Date firstDate, lastDate;

	private List<DayPanel> days;

	public CalendarPanel(CalendarManager manager, boolean edit, boolean changeMonth) {
		this.manager = manager;

		initWidget(uiBinder.createAndBindUi(this));

		lstMonths.setVisible(changeMonth);
		btnBefore.setVisible(changeMonth);
		btnAfter.setVisible(changeMonth);
	}

	@SuppressWarnings("deprecation")
	public void buildCalendar(Date date) {
		int month = date.getMonth() + 1;
		int year = 1900 + date.getYear();

		buildCalendar(new MonthYear(month, year));
	}

	@SuppressWarnings("deprecation")
	public void buildCalendar(MonthYear monthYear) {
		this.monthYear = monthYear;
		panelContent.clear();

		Date date = getMonthDate();

		Date dateNextMonth = CalendarUtil.copyDate(date);
		CalendarUtil.addMonthsToDate(dateNextMonth, 1);

		Date dateLastMonth = CalendarUtil.copyDate(date);
		CalendarUtil.addMonthsToDate(dateLastMonth, -1);

		// We get the day of the week for the first of the month (and we set to
		// 7 if it is sunday)
		int dayNumber = date.getDay() != 0 ? date.getDay() : 7;
		// We get the number of days in the month before
		int daysInLastMonth = CalendarUtil.getDaysBetween(dateLastMonth, date);
		// We get the number of days in the selected month
		int daysInMonth = CalendarUtil.getDaysBetween(date, dateNextMonth);

		// We check if we need six weeks to display this month
		boolean hasSixWeek = (dayNumber - 1) + daysInMonth > 35;

		// We get the first day
		boolean isCurrentMonth = dayNumber == 1;
		int dayNum = isCurrentMonth ? dayNumber : daysInLastMonth - (dayNumber - 2);

		// We build the first day of the calendar
		this.firstDate = df.parse(dayNum + "/" + (isCurrentMonth ? monthYear.getMonth() : dateLastMonth.getMonth() + 1) + "/" + (isCurrentMonth ? monthYear.getYear() : dateLastMonth.getYear() + 1900));

		this.days = new ArrayList<DayPanel>();
		int currentMonth = isCurrentMonth ? monthYear.getMonth() : dateLastMonth.getMonth() + 1;
		int currentYear = isCurrentMonth ? monthYear.getYear() : dateLastMonth.getYear() + 1900;
		for (int weekNum = 0; weekNum < (hasSixWeek ? 6 : 5); weekNum++) {

			HTMLPanel weekPanel = new HTMLPanel("");

			for (int dayInWeek = 0; dayInWeek < 7; dayInWeek++) {

				String firstDateStr = dayNum + "/" + currentMonth + "/" + currentYear;
				Date firstDate = df.parse(firstDateStr);

				DayPanel dayPanel = new DayPanel(manager, dayNum, firstDate);
				days.add(dayPanel);
				weekPanel.add(dayPanel);

				// We skip the increment to get the lastDate
				if (weekNum == (hasSixWeek ? 6 : 5) - 1 && dayInWeek == 6) {
					break;
				}

				if (!isCurrentMonth && dayNum == daysInLastMonth) {
					isCurrentMonth = true;
					dayNum = 1;

					currentMonth = monthYear.getMonth();
					currentYear = monthYear.getYear();
				}
				else if (isCurrentMonth && dayNum == daysInMonth) {
					isCurrentMonth = false;
					dayNum = 1;

					currentMonth = dateNextMonth.getMonth() + 1;
					currentYear = dateNextMonth.getYear() + 1900;
				}
				else {
					dayNum++;
				}
			}

			panelContent.add(weekPanel);
		}

		this.lastDate = df.parse(dayNum + "/" + (isCurrentMonth ? monthYear.getMonth() : dateNextMonth.getMonth() + 1) + "/" + (isCurrentMonth ? monthYear.getYear() : dateNextMonth.getYear() + 1900));

//		lblMonth.setText(monthYear.toString());

		lstMonths.loadListMonth(monthYear, false);
		manager.refreshEvents();
	}
	
	public MonthYear getMonthYear() {
		return monthYear;
	}

	public Date getFirstDate() {
		return firstDate;
	}

	public Date getLastDate() {
		return lastDate;
	}
	
	/**
	 * Get the first date in the selected month
	 * 
	 * @return Date
	 */
	public Date getMonthDate() {
		return df.parse("01/" + monthYear.getMonth() + "/" + monthYear.getYear());
	}

	@UiHandler("lstMonths")
	public void onMonths(ChangeEvent event) {
		MonthYear selected = lstMonths.getValue();
		buildCalendar(selected);
	}

	@UiHandler("btnBefore")
	public void onBefore(ClickEvent event) {
		MonthYear previous = monthYear.getPrevious();
		buildCalendar(previous);
	}

	@UiHandler("btnAfter")
	public void onAfter(ClickEvent event) {
		MonthYear next = monthYear.getNext();
		buildCalendar(next);
	}

	public void loadEvents(List<CalendarEvent> events) {
		clear();
		
		if (events != null) {
			for (CalendarEvent event : events) {
				if (days != null) {
					int dayNumber = -1;
					for (DayPanel day : days) {
						if (CalendarUtil.isSameDate(event.getBeginDate(), day.getDate())) {
							dayNumber = 0;
							day.addEvent(event, true, event.getDuration() == 0);
						}
						else if (dayNumber >= event.getDuration()) {
							break;
						}
						else if (dayNumber >= 0) {
							dayNumber++;
							day.addEvent(event, false, dayNumber >= event.getDuration());
						}
					}
				}
			}
		}
	}

	private void clear() {
		if (days != null) {
			for (DayPanel day : days) {
				day.clear();
			}
		}
	}

	public interface CalendarManager {
		
		public void refreshEvents();

		public void addEvent(Date date);

		public void openEvent(Date date, CalendarEvent event);

		public void registerDayDetail(DayDetailPopup popup);
	}
}
