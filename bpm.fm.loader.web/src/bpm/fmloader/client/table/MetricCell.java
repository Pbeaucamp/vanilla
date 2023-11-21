package bpm.fmloader.client.table;

import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import bpm.fmloader.client.dto.MetricDTO;

public class MetricCell extends SimplePanel {

	private MetricDTO metric;
	
	public MetricCell(MetricDTO metric) {
		this.metric = metric;
		Label lbl = new Label(metric.getName());
		lbl.setWordWrap(false);
		lbl.setSize("100%", "100%");
		lbl.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
		lbl.getElement().getStyle().setFontSize(15, Unit.PX);
		this.add(lbl);
	}

	public MetricDTO getMetric() {
		return metric;
	}

	public void setMetric(MetricDTO metric) {
		this.metric = metric;
	}
	
}
