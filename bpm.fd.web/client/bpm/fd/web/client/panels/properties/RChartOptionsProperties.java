package bpm.fd.web.client.panels.properties;

import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.RChartNature;
import bpm.fd.core.IComponentOption;
import bpm.fd.core.component.ChartComponent;
import bpm.fd.core.component.ChartOption;
import bpm.fd.core.component.KpiChartComponent;
import bpm.fd.core.component.RChartComponent;
import bpm.fd.core.component.RChartOption;
import bpm.fd.core.component.kpi.KpiChart;
import bpm.gwt.commons.client.custom.ColorPanel;
import bpm.gwt.commons.client.custom.CustomCheckbox;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.LabelValueTextBox;
import bpm.gwt.commons.client.custom.SectionPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class RChartOptionsProperties extends CompositeProperties<IComponentOption> {
	
	private static OptionsPropertiesUiBinder uiBinder = GWT.create(OptionsPropertiesUiBinder.class);

	interface OptionsPropertiesUiBinder extends UiBinder<Widget, RChartOptionsProperties> {
	}

	@UiField
	SectionPanel panelPie, panelNoPie , panelBar, panelHist;
	
	@UiField
	CustomCheckbox showLabel, dynamicR , seperationBar , density , specifyBins;	// seperationBar : Cas ou Chart est un Bar

	public CustomCheckbox getDensity() {
		return density;
	}

	public void setDensity(CustomCheckbox density) {
		this.density = density;
	}

	
	@UiField
	LabelValueTextBox binsNumber;
	
	public LabelValueTextBox getBinsNumber() {
		return binsNumber;
	}

	public void setBinsNumber(LabelValueTextBox binsNumber) {
		this.binsNumber = binsNumber;
	}


	@UiField
	LabelTextBox yAxisName, xAxisName ;//,chartTitle; 
		
	
	private RChartComponent component;
	
	public RChartOptionsProperties(RChartComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.component = component ;
		
		if( component != null ){
			
			buildContent(component.getOption());
		}
	}
	
	/*
	@UiHandler("showLabel")
	public void onChange(ClickEvent event) {
		showAxisLabel();
		xAxisName.clear();
		yAxisName.clear();

	}*/
	
	@UiHandler("specifyBins")
	public void onchange(ClickEvent event) {
		if( this.specifyBins.getValue() ){
			this.binsNumber.setVisible(true);
		}
		else{
			this.binsNumber.setVisible(false);
		}
		
	}
	
	public void showAxisLabel(){
		if(showLabel.getValue()){
			yAxisName.setVisible(true);
			xAxisName.setVisible(true);
		}else{
			yAxisName.setVisible(false);
			xAxisName.setVisible(false);
		}
	}
	
	private void buildContent(RChartOption options) {
		// TODO Auto-generated method stub
		showLabel.setValue(options.isShowLabel());
		this.dynamicR.setValue(options.isDynamicR());
		// non pie
		yAxisName.setText(options.getyAxisName());
		xAxisName.setText(options.getxAxisName());
		//Bar
		this.seperationBar.setValue(options.isSeperationBar()) ;
		
		//
		this.density.setValue( options.isDensity() );
		this.binsNumber.setVisible(false);
		this.binsNumber.setText( options.getBins() );
		// General
		//chartTitle.setText("Title");
		refreshOptions(component);
	}

	@Override
	public void buildProperties(IComponentOption component) {
		// TODO Auto-generated method stub
		RChartOption option = null;
		
		option = ((RChartComponent) component).getOption();
		if (option == null) {
			option = new RChartOption();
			((RChartComponent) component).setOption(option);
		}
		
		option.setDynamicR( this.dynamicR.getValue());
		option.setShowLabel(showLabel.getValue());
		option.setyAxisName(yAxisName.getText());
		option.setxAxisName(xAxisName.getText());
		option.setSeperationBar(this.seperationBar.getValue());
		option.setBins( this.binsNumber.getValue() );
		option.setDensity( this.density.getValue());
	}
	
	public void refreshOptions(IComponentOption component) {
		
		RChartNature nature = ((RChartComponent)component).getNature();
		if(nature.getNature() == RChartNature.PIE) {
			panelPie.setVisible(true);
			panelNoPie.setVisible(false);
		}
		else {
			panelPie.setVisible(false);
			panelNoPie.setVisible(true);
		}
		if(nature.getNature() == RChartNature.BAR_H || nature.getNature() == RChartNature.BAR_V ) {
			panelBar.setVisible(true);
		}
		else{
			panelBar.setVisible(false);
		}
		if( nature.getNature() == RChartNature.HIST ){
			panelHist.setVisible(true);
		}
		else{
			panelHist.setVisible(false);
		}
		showAxisLabel();
		super.refreshOptions(component);
	}
	

}
