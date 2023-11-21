package bpm.fmloader.client.panel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.fmloader.client.FMLoaderWeb;
import bpm.fmloader.client.constante.Constantes;
import bpm.fmloader.client.dialog.ErrorDialog;
import bpm.fmloader.client.images.ImageResources;
import bpm.fmloader.client.infos.InfosUser;
import bpm.fmloader.client.table.MetricValuesPanel;
import bpm.gwt.commons.client.free.metrics.DateTimePickerDialog;
import bpm.gwt.commons.client.free.metrics.FMFilterPanel;
import bpm.gwt.commons.client.free.metrics.IFilterChangeHandler;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class FMDataPanel extends Composite implements IFilterChangeHandler {

	private static final String CSS_IMG = "imgMenu";

	private static FMDataPanelUiBinder uiBinder = GWT.create(FMDataPanelUiBinder.class);

	interface FMDataPanelUiBinder extends UiBinder<Widget, FMDataPanel> {
	}

	@UiField
	Image imgAccueil, imgLogo;

	@UiField
	StackLayoutPanel stackMetricPanel;

	@UiField
	SimplePanel tableContentPanel;

	@UiField
	DockLayoutPanel dockPanel;

	@UiField
	HTMLPanel panelFilter;

	@UiField
	Label lblDateTitle;

	private FMLoaderWeb mainPanel;
	private InfoUser infoUser;

	private HTMLPanel compteurPanel, indicateurPanel;

	private StackHeaderPanel prefHeader, prefHeader2;

	private DateTimePickerDialog datePickerDialog;

	private MetricValuesPanel valuePanel;

	private FMFilterPanel filterPanel;

	public FMDataPanel(FMLoaderWeb mainPanel, InfoUser infoUser) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.infoUser = infoUser;

		filterPanel = new FMFilterPanel(this);
		panelFilter.add(filterPanel);

		valuePanel = new MetricValuesPanel();
		tableContentPanel.add(valuePanel);

		datePickerDialog = new DateTimePickerDialog(new Date());
		String dateString = DateTimeFormat.getFormat("dd MMMM yyyy HH:mm").format(new Date());
		lblDateTitle.setText(dateString);

		datePickerDialog.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				Date date = datePickerDialog.getSelectedDate();
				String dateString = DateTimeFormat.getFormat("dd MMMM yyyy HH:mm").format(date);
				lblDateTitle.setText(dateString);

				if (valuePanel.getSelectedMetric() != null) {
					valuePanel.fillTable(date, valuePanel.getSelectedMetric());
				}

			}
		});

		dockPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);

		imgAccueil.setResource(ImageResources.INSTANCE.home());
		imgAccueil.setTitle(Constantes.LBL.btnQuit());
		imgAccueil.addStyleName(CSS_IMG);
		imgAccueil.getElement().getStyle().setMargin(3, Unit.PX);
		imgAccueil.addClickHandler(imgHandler);

		imgAccueil.getElement().getStyle().setFloat(Float.RIGHT);

		imgLogo.setResource(ImageResources.INSTANCE.VanillaKpiLoader_LogoLettering_32());
		imgLogo.getElement().getStyle().setMargin(4, Unit.PX);
		imgLogo.getElement().getStyle().setFloat(Float.RIGHT);

		prefHeader = new StackHeaderPanel(this, Constantes.LBL.metricCompteur(), true);

		prefHeader2 = new StackHeaderPanel(this, Constantes.LBL.metricIndicateur(), false);

		prefHeader.addStyleName("headerStackFirst");
		prefHeader2.addStyleName("headerStackEnd");

		compteurPanel = new HTMLPanel("");
		indicateurPanel = new HTMLPanel("");

		stackMetricPanel.add(compteurPanel, prefHeader, 30);

		stackMetricPanel.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
			@Override
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
				if (event.getItem() == 1) {
					prefHeader2.getParent().removeStyleName("headerStackEnd");
					prefHeader2.getParent().addStyleName("headerStackBorder");
				}
				else {
					prefHeader2.getParent().addStyleName("headerStackEnd");
					prefHeader2.getParent().removeStyleName("headerStackBorder");
				}
			}
		});

		fillMetrics();
	}

	private void fillMetrics() {
		compteurPanel.clear();

		Observatory selectedObs = filterPanel.getSelectedObservatory();
		Theme selectedTheme = filterPanel.getSelectedTheme();

		// find the metric list
		List<Metric> metrics = new ArrayList<Metric>();
		if (selectedObs != null) {
			if (selectedTheme != null) {
				metrics.addAll(selectedTheme.getMetrics());
			}
			else {
				for (Theme th : selectedObs.getThemes()) {
					metrics.addAll(th.getMetrics());
				}
				Set<Metric> set = new HashSet<Metric>(metrics);
				metrics = new ArrayList<Metric>(set);
			}
		}
		else {
			LOOK: for (Observatory obs : InfosUser.getInstance().getObservatories()) {
				if (selectedTheme != null) {
					for (Theme th : obs.getThemes()) {
						if (th.getId() == selectedTheme.getId()) {
							metrics.addAll(th.getMetrics());
							break LOOK;
						}
					}
				}
				else {
					for (Theme th : obs.getThemes()) {
						metrics.addAll(th.getMetrics());
					}
				}
			}
			Set<Metric> set = new HashSet<Metric>(metrics);
			metrics = new ArrayList<Metric>(set);
		}

		// create the grid
		MetricDataGrid datagrid = new MetricDataGrid(infoUser, valuePanel, this);
		compteurPanel.add(datagrid);

		// remove calculated metrics
		List<Metric> toRm = new ArrayList<Metric>();
		for (Metric m : metrics) {
			if (!(m.getMetricType().equals("Classic"))) {
				toRm.add(m);
			}
			else {
				if(((FactTable)m.getFactTable()).getDatasource() != null && !(((FactTable)m.getFactTable()).getDatasource().getObject() instanceof DatasourceJdbc)) {
					toRm.add(m);
				}
			}
		}
		metrics.removeAll(toRm);

		// fill the grid
		datagrid.fill(metrics);

	}

	private ClickHandler imgHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (event.getSource().equals(imgAccueil)) {
				mainPanel.onLogout();
			}
		}
	};

	public void refreshMetricList(String selectedPeriod) {

		final GreyAbsolutePanel greyPan = new GreyAbsolutePanel();
		final WaitAbsolutePanel waitPan = new WaitAbsolutePanel();

		waitPan.getElement().getStyle().setTop(stackMetricPanel.getOffsetHeight() / 2 - 50, Unit.PX);
		waitPan.getElement().getStyle().setLeft(stackMetricPanel.getOffsetWidth() / 2 - 100, Unit.PX);

		((HTMLPanel) stackMetricPanel.getVisibleWidget()).add(greyPan);
		((HTMLPanel) stackMetricPanel.getVisibleWidget()).add(waitPan);

		InfosUser.getInstance().setSelectedPeriode(selectedPeriod);
		Constantes.DATAS_SERVICES.getMetrics(InfosUser.getInstance(), new AsyncCallback<InfosUser>() {
			@Override
			public void onSuccess(InfosUser result) {
				InfosUser.getInstance().setMetrics(result.getMetrics());
				fillMetrics();

				((HTMLPanel) stackMetricPanel.getVisibleWidget()).remove(greyPan);
				((HTMLPanel) stackMetricPanel.getVisibleWidget()).remove(waitPan);
			}

			@Override
			public void onFailure(Throwable caught) {
				ErrorDialog dial = new ErrorDialog(Constantes.LBL.errorGetMetrics());
				dial.center();
				dial.show();

				((HTMLPanel) stackMetricPanel.getVisibleWidget()).remove(greyPan);
				((HTMLPanel) stackMetricPanel.getVisibleWidget()).remove(waitPan);
			}
		});
	}

	public List<Metric> getSelectedMetrics() {
		List<Metric> result = new ArrayList<Metric>();
		HTMLPanel metricPanel = null;
		if (stackMetricPanel.getVisibleIndex() == 0) {
			metricPanel = compteurPanel;
		}
		else {
			metricPanel = indicateurPanel;
		}

		for (int i = 0; i < metricPanel.getWidgetCount(); i++) {
			MetricPanel panel = (MetricPanel) metricPanel.getWidget(i);
			if (panel.isChecked()) {
				result.add(panel.getMetric());
			}
		}
		return result;
	}

	public void checkUnCheckMetrics(boolean check) {
		HTMLPanel metricPanel = (HTMLPanel) stackMetricPanel.getVisibleWidget();

		for (int i = 0; i < metricPanel.getWidgetCount(); i++) {
			MetricPanel panel = (MetricPanel) metricPanel.getWidget(i);
			panel.setChecked(check);
		}

	}

	@UiHandler("lblDatePicker")
	public void onChooseDate(ClickEvent e) {

		datePickerDialog.center();
	}

	public Date getSelectedDate() {
		return datePickerDialog.getSelectedDate();
	}

	@Override
	public void selectionChanged(Group group, Observatory obs, Theme theme) {
		fillMetrics();
	}
}
