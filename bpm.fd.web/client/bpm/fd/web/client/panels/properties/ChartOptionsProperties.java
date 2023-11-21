package bpm.fd.web.client.panels.properties;

import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.core.IComponentOption;
import bpm.fd.core.component.ChartComponent;
import bpm.fd.core.component.ChartOption;
import bpm.fd.core.component.KpiChartComponent;
import bpm.fd.core.component.RChartComponent;
import bpm.fd.core.component.kpi.KpiChart;
import bpm.gwt.commons.client.custom.ColorPanel;
import bpm.gwt.commons.client.custom.CustomCheckbox;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.LabelValueTextBox;
import bpm.gwt.commons.client.custom.SectionPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class ChartOptionsProperties extends CompositeProperties<IComponentOption> {

	private static OptionsPropertiesUiBinder uiBinder = GWT.create(OptionsPropertiesUiBinder.class);

	interface OptionsPropertiesUiBinder extends UiBinder<Widget, ChartOptionsProperties> {
	}
	@UiField
	SectionPanel panelPie, panelNoPie;
	
	@UiField
	CustomCheckbox showLabel, showValues, showBorder, multiLineLabels, dynamicLegend, exportEnable;
	
	@UiField
	LabelValueTextBox bgAlpha, bgSWFAlpha;
	
	@UiField
	ColorPanel bgColor, borderColor, baseFontColor;
	
	@UiField
	LabelValueTextBox borderThickness, baseFontSize, labelSize;

	// pie
	@UiField
	LabelValueTextBox slicingDistance, pieSliceDepth, pieRadius;

	// non pie
	@UiField
	CustomCheckbox rotateLabels, slantLabels, rotateValues, placeValuesInside, rotateYAxisName;

	@UiField
	LabelTextBox PYAxisName, SYAxisName, xAxisName;
	
	//Chart + Line
	@UiField
	LabelTextBox lineSerieName;

	// number format
	@UiField
	CustomCheckbox formatNumber, formatNumberScale, forceDecimal;
	
	@UiField
	LabelTextBox numberPrefix, numberSuffix, decimalSeparator, thousandSeparator;
	
	@UiField
	LabelValueTextBox decimals;

	private ChartComponent component;
	
	public ChartOptionsProperties(ChartComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.component = component;
		
		if (component != null) {
			buildContent(component.getOption());
		}
	}
	
	
	/*public ChartOptionsProperties(RChartComponent component) {
		initWidget(uiBinder.createAndBindUi(this));

	}
	*/
	private void buildContent(ChartOption options) {
		showLabel.setValue(options.isShowLabel());
		showValues.setValue(options.isShowValues());
		showBorder.setValue(options.isShowBorder());
		multiLineLabels.setValue(options.isMultiLineLabels());
		dynamicLegend.setValue(options.isDynamicLegend());
		exportEnable.setValue(options.isExportEnable());
		
		bgAlpha.setText(options.getBgAlpha());
		bgSWFAlpha.setText(options.getBgSWFAlpha());
		
		bgColor.setColor(options.getBgColor());
		borderColor.setColor(options.getBorderColor());
		baseFontColor.setColor(options.getBaseFontColor());
		
		borderThickness.setText(options.getBorderThickness());
		baseFontSize.setText(options.getBaseFontSize());
		labelSize.setText(options.getLabelSize());
		
		slicingDistance.setText(options.getSlicingDistance());
		pieSliceDepth.setText(options.getPieSliceDepth());
		pieRadius.setText(options.getPieRadius());

		// non pie
		rotateLabels.setValue(options.isRotateLabels());
		slantLabels.setValue(options.isSlantLabels());
		rotateValues.setValue(options.isRotateValues());
		placeValuesInside.setValue(options.isPlaceValuesInside());
		rotateYAxisName.setValue(options.isRotateYAxisName());
		
		PYAxisName.setText(options.getPYAxisName());
		SYAxisName.setText(options.getSYAxisName());
		xAxisName.setText(options.getxAxisName());
		
		lineSerieName.setText(options.getLineSerieName());
		
		formatNumber.setValue(options.isShowLabel());
		formatNumberScale.setValue(options.isShowLabel());
		forceDecimal.setValue(options.isShowLabel());
		
		numberPrefix.setText(options.getNumberPrefix());
		numberSuffix.setText(options.getNumberSuffix());
		decimalSeparator.setText(options.getDecimalSeparator());
		thousandSeparator.setText(options.getThousandSeparator());
		
		decimals.setText(options.getDecimals());
		
		refreshOptions(component);
	}

	@Override
	public void buildProperties(IComponentOption component) {
		ChartOption option = null;
		if(component instanceof KpiChartComponent) {
			option = ((KpiChart)((KpiChartComponent) component).getElement()).getOption().getOption();
		}
		else {
			option = ((ChartComponent) component).getOption();
			if (option == null) {
				option = new ChartOption();
				((ChartComponent) component).setOption(option);
			}
		}
		
		option.setShowLabel(showLabel.getValue());
		
		option.setShowValues(showValues.getValue());
		option.setShowBorder(showBorder.getValue());
		option.setMultiLineLabels(multiLineLabels.getValue());
		option.setDynamicLegend(dynamicLegend.getValue());
		option.setExportEnable(exportEnable.getValue());
		
		option.setBgAlpha(bgAlpha.getValue());
		option.setBgSWFAlpha(bgSWFAlpha.getValue());
		
		option.setBgColor(bgColor.getColor());
		option.setBorderColor(borderColor.getColor());
		option.setBaseFontColor(baseFontColor.getColor());
		
		option.setBorderThickness(borderThickness.getValue());
		option.setBaseFontSize(baseFontSize.getValue());
		option.setLabelSize(labelSize.getValue());
		
		option.setSlicingDistance(slicingDistance.getValue());
		option.setPieSliceDepth(pieSliceDepth.getValue());
		option.setPieRadius(pieRadius.getValue());

		// non pie
		option.setRotateLabels(rotateLabels.getValue());
		option.setSlantLabels(slantLabels.getValue());
		option.setRotateValues(rotateValues.getValue());
		option.setPlaceValuesInside(placeValuesInside.getValue());
		option.setRotateYAxisName(rotateYAxisName.getValue());
		
		option.setPYAxisName(PYAxisName.getText());
		option.setSYAxisName(SYAxisName.getText());
		option.setxAxisName(xAxisName.getText());
		
		option.setLineSerieName(lineSerieName.getText());
		
		option.setFormatNumber(formatNumber.getValue());
		option.setFormatNumberScale(formatNumberScale.getValue());
		option.setForceDecimal(forceDecimal.getValue());
		
		option.setNumberPrefix(numberPrefix.getText());
		option.setNumberSuffix(numberSuffix.getText());
		option.setDecimalSeparator(decimalSeparator.getText());
		option.setThousandSeparator(thousandSeparator.getText());
		
		option.setDecimals(decimals.getValue());
	}
	
	@Override
	public void refreshOptions(IComponentOption component) {
		
		ChartNature nature = ((ChartComponent)component).getNature();
		if(nature.getNature() == ChartNature.PIE || nature.getNature() == ChartNature.PIE_3D) {
			panelPie.setVisible(true);
			panelNoPie.setVisible(false);
		}
		else {
			panelPie.setVisible(false);
			panelNoPie.setVisible(true);
		}
		
		super.refreshOptions(component);
	}
}
