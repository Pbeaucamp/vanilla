package bpm.freematrix.reborn.web.client.main.home.metrics;

import java.util.Date;
import java.util.HashMap;

import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.MetricValue;
import bpm.freematrix.reborn.web.client.ClientSession;
import bpm.freematrix.reborn.web.client.dialog.WaitDialog;
import bpm.freematrix.reborn.web.client.main.header.FreeMatrixHeader;
import bpm.freematrix.reborn.web.client.main.home.HomeView;
import bpm.freematrix.reborn.web.client.services.MetricService;
import bpm.gwt.commons.client.free.metrics.DateTimePickerDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class MetricsPanel extends Composite {

	private static MetricsPanelUiBinder uiBinder = GWT.create(MetricsPanelUiBinder.class);

	interface MetricsPanelUiBinder extends UiBinder<Widget, MetricsPanel> {
	}

	private static MetricsPanel instance;

	@UiField
	HTMLPanel gridPanel;

	@UiField
	Label lblDateTitle;

	private HomeView homeView;

	private DateTimePickerDialog datePickerDialog;

	private MetricDatagrid metricDatagrid;

	public MetricsPanel(HomeView home) {
		initWidget(uiBinder.createAndBindUi(this));
		this.homeView = home;

		instance = this;

		metricDatagrid = new MetricDatagrid(homeView);

		Date initialDate = ClientSession.getInstance().getDefaultDate();

		initText(initialDate);

		gridPanel.add(metricDatagrid);

		homeView.setFilterDate(initialDate);
		datePickerDialog = new DateTimePickerDialog(initialDate);

		datePickerDialog.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				Date date = datePickerDialog.getSelectedDate();
//				generateDashboard();
				String dateString = DateTimeFormat.getFormat("dd MMMM yyyy HH:mm").format(date);
				lblDateTitle.setText(dateString);
				homeView.setFilterDate(date);
				
				homeView.getMainParent().filterChange(FreeMatrixHeader.getInstance().getSelectedGroupObject(), FreeMatrixHeader.getInstance().getSelectedObsObject(), FreeMatrixHeader.getInstance().getSelectedThemeObject());
			}
		});
		// datePicker.setValue(initialDate, true);

		// onRegisterDatePickerHandler();

	}

	private void initText(Date initialDate) {
		String dateString = DateTimeFormat.getFormat("dd MMMM yyyy HH:mm").format(initialDate);
		lblDateTitle.setText(dateString);
	}

	public void generateDashboard() {
		final Date date = datePickerDialog.getSelectedDate();
		WaitDialog.showWaitPart(true);

		MetricService.Connection.getInstance().getMetricValue(date, FreeMatrixHeader.getInstance().getSelectedGroup(), FreeMatrixHeader.getInstance().getSelectedTheme(), new AsyncCallback<HashMap<Metric, MetricValue>>() {

			@Override
			public void onSuccess(HashMap<Metric, MetricValue> result) {
				metricDatagrid.fill(result, date);
				WaitDialog.showWaitPart(false);
			}

			@Override
			public void onFailure(Throwable caught) {
				if (caught.getMessage().contains("Index: 0, Size: 0")) {
					Window.alert("No Metric Result Found");
				}
				else {
					Window.alert(caught.getMessage());
				}
				WaitDialog.showWaitPart(false);
			}
		});
	}

	@UiHandler("lblDatePicker")
	void onChooseDate(ClickEvent e) {
		datePickerDialog.center();
	}
	
//	@UiHandler("btnCalendar")
//	public void onShowCalendar(ClickEvent event) {
//		CalendarDialog dial = new CalendarDialog();
//		dial.center();
//	}

	public static MetricsPanel getInstance() {
		return instance;
	}

	public MetricDatagrid getDatagrid() {
		return metricDatagrid;
	}
}
