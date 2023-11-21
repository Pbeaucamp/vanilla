package bpm.fd.web.client.panels.properties;

import bpm.fd.core.IComponentData;
import bpm.fd.core.component.GaugeComponent;
import bpm.gwt.commons.client.custom.LabelValueTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.data.Dataset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class GaugeDataProperties extends CompositeProperties<IComponentData> implements IComponentDataProperties {

	private static GaugeDataPropertiesUiBinder uiBinder = GWT.create(GaugeDataPropertiesUiBinder.class);

	interface GaugeDataPropertiesUiBinder extends UiBinder<Widget, GaugeDataProperties> {
	}

	@UiField
	ListBoxWithButton valueField, minField, maxField, minThresholdField, maxThresholdField, targetField;

	@UiField
	CheckBox minMaxFromFields, minMaxThresholdFromFields, targetFromField;

	@UiField
	HTMLPanel minMaxFields, minMaxTexts, minMaxThresholdFields, minMaxThresholdTexts, targetFields, targetText;

	@UiField
	LabelValueTextBox minValue, maxValue, minThresholdValue, maxThresholdValue, targetValue, tolerance;

	private GaugeComponent component;

	public GaugeDataProperties(GaugeComponent component) {
		initWidget(uiBinder.createAndBindUi(this));

		this.component = component;
		minMaxFromFields.setValue(component.isMinMaxFromField());
		minMaxThresholdFromFields.setValue(component.isThresholdFromField());
		targetFromField.setValue(component.isTargetFromField());

		if (component.isMinMaxFromField()) {
			minMaxTexts.getElement().getStyle().setDisplay(Display.NONE);
			minMaxFields.getElement().getStyle().setDisplay(Display.BLOCK);
		}
		else {
			minMaxFields.getElement().getStyle().setDisplay(Display.NONE);
			minMaxTexts.getElement().getStyle().setDisplay(Display.BLOCK);
		}
		if (component.isThresholdFromField()) {
			minMaxThresholdTexts.getElement().getStyle().setDisplay(Display.NONE);
			minMaxThresholdFields.getElement().getStyle().setDisplay(Display.BLOCK);
		}
		else {
			minMaxThresholdFields.getElement().getStyle().setDisplay(Display.NONE);
			minMaxThresholdTexts.getElement().getStyle().setDisplay(Display.BLOCK);
		}
		if (component.isTargetFromField()) {
			targetText.getElement().getStyle().setDisplay(Display.NONE);
			targetFields.getElement().getStyle().setDisplay(Display.BLOCK);
		}
		else {
			targetFields.getElement().getStyle().setDisplay(Display.NONE);
			targetText.getElement().getStyle().setDisplay(Display.BLOCK);
		}

		if (!component.isMinMaxFromField()) {
			minValue.setText(component.getMin().intValue());
			maxValue.setText(component.getMax().intValue());
		}
		if (!component.isThresholdFromField()) {
			minThresholdValue.setText(component.getMinThreshold().intValue());
			maxThresholdValue.setText(component.getMaxThreshold().intValue());
		}
		if (!component.isThresholdFromField()) {
			targetValue.setText(component.getTarget().intValue());
		}
		tolerance.setText(component.getTolerance());
	}

	@UiHandler("minMaxFromFields")
	public void onMinMaxFields(ValueChangeEvent<Boolean> event) {
		if (event.getValue()) {
			minMaxTexts.getElement().getStyle().setDisplay(Display.NONE);
			minMaxFields.getElement().getStyle().setDisplay(Display.BLOCK);
		}
		else {
			minMaxFields.getElement().getStyle().setDisplay(Display.NONE);
			minMaxTexts.getElement().getStyle().setDisplay(Display.BLOCK);
		}
	}

	@UiHandler("minMaxThresholdFromFields")
	public void onMinMaxThresholdFromFields(ValueChangeEvent<Boolean> event) {
		if (event.getValue()) {
			minMaxThresholdTexts.getElement().getStyle().setDisplay(Display.NONE);
			minMaxThresholdFields.getElement().getStyle().setDisplay(Display.BLOCK);
		}
		else {
			minMaxThresholdFields.getElement().getStyle().setDisplay(Display.NONE);
			minMaxThresholdTexts.getElement().getStyle().setDisplay(Display.BLOCK);
		}
	}

	@UiHandler("targetFromField")
	public void onTargetFromField(ValueChangeEvent<Boolean> event) {
		if (event.getValue()) {
			targetText.getElement().getStyle().setDisplay(Display.NONE);
			targetFields.getElement().getStyle().setDisplay(Display.BLOCK);
		}
		else {
			targetFields.getElement().getStyle().setDisplay(Display.NONE);
			targetText.getElement().getStyle().setDisplay(Display.BLOCK);
		}
	}
	
	@Override
	public void setDataset(Dataset dataset, boolean refresh) {
		valueField.setList(dataset.getMetacolumns());
		minField.setList(dataset.getMetacolumns());
		maxField.setList(dataset.getMetacolumns());

		minThresholdField.setList(dataset.getMetacolumns(), true);
		maxThresholdField.setList(dataset.getMetacolumns(), true);

		targetField.setList(dataset.getMetacolumns(), true);

		if (component.getDataset() != null && component.getDataset().getId() == dataset.getId()) {
			valueField.setSelectedIndex(component.getValueField());
			if (component.isMinMaxFromField()) {
				minField.setSelectedIndex(component.getMin().intValue());
				maxField.setSelectedIndex(component.getMax().intValue());
			}
			if (component.isThresholdFromField()) {
				minThresholdField.setSelectedIndex(component.getMinThreshold().intValue() + 1);
				maxThresholdField.setSelectedIndex(component.getMaxThreshold().intValue() + 1);
			}
			if (component.isTargetFromField()) {
				targetField.setSelectedIndex(component.getTarget().intValue() + 1);
			}
		}
	}

	@Override
	public void setDataset(Dataset dataset) {
		setDataset(dataset, true);
	}

	@Override
	public void buildProperties(IComponentData component) {
		GaugeComponent gauge = (GaugeComponent) component;

		gauge.setMinMaxFromField(minMaxFromFields.getValue());
		gauge.setThresholdFromField(minMaxThresholdFromFields.getValue());
		gauge.setTargetFromField(targetFromField.getValue());

		gauge.setValueField(valueField.getSelectedIndex());
		gauge.setTolerance(tolerance.getValue());

		if (minMaxFromFields.getValue()) {
			gauge.setMin((float) minField.getSelectedIndex());
			gauge.setMax((float) maxField.getSelectedIndex());
		}
		else {
			gauge.setMin((float) minValue.getValue());
			gauge.setMax((float) maxValue.getValue());
		}
		if (minMaxThresholdFromFields.getValue()) {
			gauge.setMinThreshold((float) minThresholdField.getSelectedIndex());
			gauge.setMaxThreshold((float) maxThresholdField.getSelectedIndex());
		}
		else {
			gauge.setMinThreshold((float) minThresholdValue.getValue());
			gauge.setMaxThreshold((float) maxThresholdValue.getValue());
		}
		if (targetFromField.getValue()) {
			gauge.setTarget((float) targetField.getSelectedIndex());
		}
		else {
			gauge.setTarget((float) targetValue.getValue());
		}

	}

}
