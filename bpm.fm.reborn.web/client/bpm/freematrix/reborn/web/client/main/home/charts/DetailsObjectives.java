package bpm.freematrix.reborn.web.client.main.home.charts;

import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.Metric;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DetailsObjectives extends Composite {

	private static DetailsObjectivesUiBinder uiBinder = GWT
			.create(DetailsObjectivesUiBinder.class);

	interface DetailsObjectivesUiBinder extends
			UiBinder<Widget, DetailsObjectives> {
	}

	@UiField
	Label lblName, lblMaxColumn, lblMinColumn, lblTableName, lblDateColumn, lblTrend, lblToleranceColumn;

	public DetailsObjectives(FactTable f, Metric m) {
		initWidget(uiBinder.createAndBindUi(this));

		lblName.setText(f.getObjectives().getObjectiveColumn());
		lblMaxColumn.setText(f.getObjectives().getMaxColumn());
		lblMinColumn.setText(f.getObjectives().getMinColumn());
		lblTableName.setText(f.getObjectives().getTableName());
		lblDateColumn.setText(f.getObjectives().getDateColumn());
		lblTrend.setText(m.getDirection());
		lblToleranceColumn.setText(f.getObjectives().getTolerance());
	}

}
