package bpm.freematrix.reborn.web.client.dialog;

import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.MetricValue;
import bpm.freematrix.reborn.web.client.images.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MetricCalendarPanel extends Composite {

	private static MetricCalendarUiBinder uiBinder = GWT.create(MetricCalendarUiBinder.class);

	interface MetricCalendarUiBinder extends UiBinder<Widget, MetricCalendarPanel> {
	}

	@UiField
	Label lblMetric, lblValue, lblObjective, lblMin, lblMax;

	@UiField
	Image imgTendancy, imgHealth;

	public MetricCalendarPanel(Metric metric, MetricValue value) {
		initWidget(uiBinder.createAndBindUi(this));

		imgTendancy.setResource(getTendancy(value));
		imgHealth.setResource(getHealth(value));
		
		lblMetric.setText(metric.getName());
		lblMetric.setTitle(metric.getName());
		lblValue.setText(String.valueOf(value.getValue()));
		lblObjective.setText(String.valueOf(value.getObjective()));
		lblMin.setText(String.valueOf(value.getMinimum()));
		lblMax.setText(String.valueOf(value.getMaximum()));
	}

	private ImageResource getTendancy(MetricValue value) {
		if (value.getTendancy() < 0) {
			return Images.INSTANCE.tend_down();
		}
		else if (value.getTendancy() == 0) {
			return Images.INSTANCE.tend_equal();
		}
		else {
			return Images.INSTANCE.tend_up();
		}
	}

	private ImageResource getHealth(MetricValue value) {
		if (value.getHealth() < 0) {
			return Images.INSTANCE.thumb_down();
		}
		else if (value.getHealth() == 0) {
			return Images.INSTANCE.thumb_equal();
		}
		else {
			return Images.INSTANCE.thumb_up();
		}
	}

}
