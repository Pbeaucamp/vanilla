package bpm.freematrix.reborn.web.client.main.home.charts;

import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.FactTableAxis;
import bpm.fm.api.model.Metric;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Details extends Composite {

	private static DetailsUiBinder uiBinder = GWT.create(DetailsUiBinder.class);

	interface DetailsUiBinder extends UiBinder<Widget, Details> {
	}
	
	@UiField Label lblName, lblDateCol, lblResponsible, lblPeriodicity, lblTableName, lblValueColumn,lblAggreg;
	@UiField HTML lblDesc;
	@UiField HTMLPanel axisInfo, objectiveInfo;
	
	public Details(Metric metric) {
		initWidget(uiBinder.createAndBindUi(this));
		lblName.setText(metric.getName());
		lblDesc.setHTML(metric.getDescription());
		try {
			lblDateCol.setText(((FactTable)metric.getFactTable()).getDateColumn());
			lblResponsible.setText(metric.getResponsible());
			lblPeriodicity.setText(((FactTable)metric.getFactTable()).getPeriodicity());
			lblTableName.setText(((FactTable)metric.getFactTable()).getTableName());
			lblValueColumn.setText(((FactTable)metric.getFactTable()).getValueColumn());
			lblAggreg.setText(metric.getOperator());
		} catch (Exception e) {
		}
		
		try {
			//Display the Axis Informations
			for(FactTableAxis f : ((FactTable)metric.getFactTable()).getFactTableAxis()){
				axisInfo.add(new DetailsAxis(f));
			}
			
			//Display the Objective Informations
			objectiveInfo.add(new DetailsObjectives(((FactTable)metric.getFactTable()), metric));
		} catch (Exception e) {
		}
		
	}

}
