package bpm.fmloader.client.panel;

import bpm.fm.api.model.Metric;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MetricPanel extends Composite {

	private static MetricPanelUiBinder uiBinder = GWT.create(MetricPanelUiBinder.class);

	interface MetricPanelUiBinder extends UiBinder<Widget, MetricPanel> {
	}

	private Metric metric;
	
	@UiField
	Label lblMetric;
	
	@UiField
	CheckBox checkMetric;
	
	public MetricPanel(Metric metric) {
		initWidget(uiBinder.createAndBindUi(this));
		this.metric = metric;
		lblMetric.setText(metric.getName());
	}

	public boolean isChecked() {
		return checkMetric.getValue();
	}
	
	public Metric getMetric() {
		return metric;
	}

	public void setChecked(boolean check) {
		checkMetric.setValue(check);
	}
	
}
