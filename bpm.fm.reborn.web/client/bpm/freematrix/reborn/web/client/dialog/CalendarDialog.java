package bpm.freematrix.reborn.web.client.dialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.MetricValue;
import bpm.freematrix.reborn.web.client.i18n.LabelConstants;
import bpm.freematrix.reborn.web.client.services.MetricService;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.calendar.CalendarEvent;
import bpm.gwt.commons.client.calendar.CalendarYearPanel;
import bpm.gwt.commons.client.calendar.CalendarEvent.TypeEvent;
import bpm.gwt.commons.client.calendar.CalendarPanel;
import bpm.gwt.commons.client.calendar.CalendarPanel.CalendarManager;
import bpm.gwt.commons.client.calendar.DayDetailPopup;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class CalendarDialog extends AbstractDialogBox implements CalendarManager {

	private static CalendarDialogUiBinder uiBinder = GWT.create(CalendarDialogUiBinder.class);

	interface CalendarDialogUiBinder extends UiBinder<Widget, CalendarDialog> {
	}
	
	interface MyStyle extends CssResource {
		String smallMainPanel();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel mainPanel;
	
	private CalendarPanel calendar;
	private CalendarYearPanel calendarYear;
	
	private MetricValue metricValue;
	private int groupId;
	
	public CalendarDialog(MetricValue metricValue, Date date, int groupId) {
		super(LabelConstants.lblCnst.CalendarView(), false, true);
		this.metricValue = metricValue;
		this.groupId = groupId;

		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsConstants.lblCnst.Close(), closeHandler);
		
		String periodicity = ((FactTable) metricValue.getMetric().getFactTable()).getPeriodicity();
		if (periodicity.equals(FactTable.PERIODICITY_WEEKLY) || periodicity.equals(FactTable.PERIODICITY_DAILY)) {
			calendar = new CalendarPanel(this, false, true);
			mainPanel.add(calendar);
			calendar.buildCalendar(date);
		}
		else {
			calendarYear = new CalendarYearPanel(this, false, true);
			mainPanel.add(calendarYear);
			calendarYear.buildCalendar(date);
			
			mainPanel.setStyleName(style.smallMainPanel());
		}
		loadEvents();
	}
	
	private void loadEvents() {
		Date startDate = calendar != null ? calendar.getFirstDate() : calendarYear.getFirstDate();
		Date endDate = calendar != null ? calendar.getLastDate() : calendarYear.getLastDate();
		
		MetricService.Connection.getInstance().getValuesByMetricAndDateInterval(metricValue.getMetric().getId(), startDate, endDate, groupId, new GwtCallbackWrapper<List<MetricValue>>(this, true, true) {

			@Override
			public void onSuccess(List<MetricValue> result) {
				List<CalendarEvent> events = buildEvents(metricValue.getMetric(), result);
				if (calendar != null) {
					calendar.loadEvents(events);
				}
				else {
					calendarYear.loadEvents(events);
				}
			}
		}.getAsyncCallback());
	}
	
	private List<CalendarEvent> buildEvents(Metric metric, List<MetricValue> values) {
		List<CalendarEvent> event = new ArrayList<CalendarEvent>();
		if (values != null) {
			for (MetricValue value : values) {
				Date date = value.getDate();
				
				MetricCalendarPanel metricCalendarPanel = new MetricCalendarPanel(metric, value);
				event.add(new CalendarEvent(TypeEvent.KPI_METRIC_VALUE, -1, metricCalendarPanel, date, date, 0));
			}
		}
		return event;
	}

	private ClickHandler closeHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			CalendarDialog.this.hide();
		}
	};

	@Override
	public void refreshEvents() {
		loadEvents();
	}

	@Override
	public void addEvent(Date date) {
	}

	@Override
	public void openEvent(Date date, CalendarEvent event) {
	}

	@Override
	public void registerDayDetail(DayDetailPopup popup) {
	}
}
