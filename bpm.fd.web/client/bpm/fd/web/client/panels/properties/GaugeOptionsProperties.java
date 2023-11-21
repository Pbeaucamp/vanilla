package bpm.fd.web.client.panels.properties;

import bpm.fd.core.IComponentOption;
import bpm.fd.core.component.GaugeComponent;
import bpm.gwt.commons.client.custom.ColorPanel;
import bpm.gwt.commons.client.custom.CustomCheckbox;
import bpm.gwt.commons.client.custom.LabelValueTextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class GaugeOptionsProperties extends CompositeProperties<IComponentOption> {

	private static GaugeOptionsPropertiesUiBinder uiBinder = GWT.create(GaugeOptionsPropertiesUiBinder.class);

	interface GaugeOptionsPropertiesUiBinder extends UiBinder<Widget, GaugeOptionsProperties> {
	}
	
	@UiField
	LabelValueTextBox bgAlpha, innerRadius, outerRadius, startAngle, stopAngle;

	@UiField
	CustomCheckbox showValues, bulb;
	
	@UiField
	ColorPanel bgColor, badColor, mediumColor, goodColor;

	public GaugeOptionsProperties(GaugeComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		
		bgAlpha.setText(component.getBgAlpha());
		innerRadius.setText(component.getInnerRadius());
		outerRadius.setText(component.getOuterRadius());
		startAngle.setText(component.getStartAngle());
		stopAngle.setText(component.getStopAngle());
		
		showValues.setValue(component.isShowValues());
		bulb.setValue(component.isBulb());
		
		bgColor.setColor(component.getBgColor());
		badColor.setColor(component.getColorBadValue());
		mediumColor.setColor(component.getColorMediumValue());
		goodColor.setColor(component.getColorGoodValue());
	}

	@Override
	public void buildProperties(IComponentOption component) {
		GaugeComponent gauge = (GaugeComponent) component;
		
		gauge.setBgAlpha(bgAlpha.getValue());
		gauge.setInnerRadius(innerRadius.getValue());
		gauge.setOuterRadius(outerRadius.getValue());
		gauge.setStartAngle(startAngle.getValue());
		gauge.setStopAngle(stopAngle.getValue());
		
		gauge.setShowValues(showValues.getValue());
		gauge.setBulb(bulb.getValue());
		
		gauge.setBgColor(bgColor.getColor());
		gauge.setColorBadValue(badColor.getColor());
		gauge.setColorMediumValue(mediumColor.getColor());
		gauge.setColorGoodValue(goodColor.getColor());
	}

}
