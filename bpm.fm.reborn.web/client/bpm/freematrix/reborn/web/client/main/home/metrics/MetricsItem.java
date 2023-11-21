package bpm.freematrix.reborn.web.client.main.home.metrics;

import bpm.fm.api.model.AlertRaised;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.MetricValue;
import bpm.freematrix.reborn.web.client.main.home.HomeView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MetricsItem extends Composite{

	private static MetricsItemUiBinder uiBinder = GWT
			.create(MetricsItemUiBinder.class);

	interface MetricsItemUiBinder extends UiBinder<Widget, MetricsItem> {
	}
	
	private static final DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private static NumberFormat nbf = NumberFormat.getDecimalFormat();

	@UiField Label lblName, lblDate, lblMax, lblMin, lblObjective, lblValue;
	@UiField HTMLPanel lblHealthUp, lblHealthDown, lblHealthEqual, lblSub, lblTendUp, lblTendDown, lblTendEqual, lblAlert;
	@UiField HTMLPanel metricItemPanel;
	
	private HomeView homeView;
	private Metric metric;
	private MetricValue metricValue;
	private MetricsPanel metricsPanel;
	
	public MetricsItem(Metric metric, MetricValue val, int num, HomeView homeView, MetricsPanel metricsPanel, boolean isChild) {
		initWidget(uiBinder.createAndBindUi(this));
		this.homeView = homeView;
		this.metric = metric;
		this.metricsPanel = metricsPanel;
		this.metricValue = val;
		lblName.setText(metric.getName());
		lblDate.setText(dtf.format(val.getDate()));
		
		boolean isRaised = false;
		if(val.getRaised() != null && !val.getRaised().isEmpty()) {
			for(AlertRaised raised : val.getRaised()) {
				if(!raised.isHasBeenHandled()) {
					isRaised = true;
					break;
				}
			}
		}
		if(!isRaised) {
			lblAlert.getElement().getStyle().setDisplay(Display.NONE);
		}
		
		if(!isChild) {
			lblSub.getElement().getStyle().setDisplay(Display.NONE);
		}
		
		if(val.getHealth() < 0) {
			lblHealthDown.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			lblHealthUp.getElement().getStyle().setDisplay(Display.NONE);
			lblHealthEqual.getElement().getStyle().setDisplay(Display.NONE);
		}
		else if(val.getHealth() == 0) {
			lblHealthDown.getElement().getStyle().setDisplay(Display.NONE);
			lblHealthUp.getElement().getStyle().setDisplay(Display.NONE);
			lblHealthEqual.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		}
		else if(val.getHealth() > 0) {
			lblHealthDown.getElement().getStyle().setDisplay(Display.NONE);
			lblHealthUp.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			lblHealthEqual.getElement().getStyle().setDisplay(Display.NONE);
		}
		
		if(val.getTendancy() < 0) {
			lblTendDown.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			lblTendUp.getElement().getStyle().setDisplay(Display.NONE);
			lblTendEqual.getElement().getStyle().setDisplay(Display.NONE);
		}
		else if(val.getTendancy() == 0) {
			lblTendDown.getElement().getStyle().setDisplay(Display.NONE);
			lblTendUp.getElement().getStyle().setDisplay(Display.NONE);
			lblTendEqual.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		}
		else if(val.getTendancy() > 0) {
			lblTendDown.getElement().getStyle().setDisplay(Display.NONE);
			lblTendUp.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			lblTendEqual.getElement().getStyle().setDisplay(Display.NONE);
		}
		
		lblMax.setText(nbf.format(val.getMaximum()));
		lblMin.setText(nbf.format(val.getMinimum()));
		lblObjective.setText(nbf.format(val.getObjective()));
		lblValue.setText(nbf.format(val.getValue()));
		
		if(num % 2 != 0){
			metricItemPanel.addStyleName("oddItem");
		}
	}
	
//	@UiHandler("item")
//	void onClickMetrics(ClickEvent e){
//		homeView.setMetric(metric);
//		homeView.generateInformationPanel(false,metric,metricValue);
//		
//		for(Widget w : metricsPanel.metricsItemPanel){
//			w.removeStyleName("active");
//		}
//		
//		metricItemPanel.addStyleName("active");
//	}
	
	


}
