package bpm.fd.web.client.panels.properties.widgets;

import java.util.Arrays;
import java.util.List;

import bpm.fd.api.core.model.components.definition.chart.ChartOrderingType;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation.MeasureRendering;
import bpm.fd.web.client.dialogs.ChartAgregationDialog;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DataAggregationProperties extends Composite {

	private static DataAggregationPropertiesUiBinder uiBinder = GWT.create(DataAggregationPropertiesUiBinder.class);

	interface DataAggregationPropertiesUiBinder extends UiBinder<Widget, DataAggregationProperties> {
	}

	@UiField
	LabelTextBox measureName;

	@UiField
	ListBoxWithButton<DataColumn> valueField;

	@UiField
	ListBoxWithButton<MeasureRendering> rendering;

	@UiField
	ListBoxWithButton<String> aggregator;

	@UiField
	ListBoxWithButton<ChartOrderingType> orderType;

	@UiField
	CheckBox applyOnDistinctSerie, primaryAxis;

	@UiField
	Button btnApply, btnCancel;

	private ChartAgregationDialog parent;
	private DataAggregation currentAggreg;

	public DataAggregationProperties(ChartAgregationDialog parent, DataAggregation aggreg, List<DataColumn> metacolumns) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;

		aggregator.setList(Arrays.asList(DataAggregation.AGGREGATORS_NAME));
		rendering.setList(DataAggregation.MeasureRendering.getList());
		orderType.setList(ChartOrderingType.getOrderTypes());
		
		this.currentAggreg = aggreg != null ? aggreg : new DataAggregation();
		setAggregation(currentAggreg, metacolumns);
	}

	public void setAggregation(DataAggregation aggreg, List<DataColumn> metacolumns) {
		valueField.setList(metacolumns);

		measureName.setText(aggreg.getMeasureName());
		aggregator.setSelectedIndex(aggreg.getAggregator());
		rendering.setSelectedIndex(DataAggregation.MeasureRendering.getList().indexOf(aggreg.getRendering()));
		orderType.setSelectedIndex(ChartOrderingType.getOrderTypes().indexOf(aggreg.getOrderType()));
		if (currentAggreg.getValueFieldIndex() != null && currentAggreg.getValueFieldIndex() > 0) {
			valueField.setSelectedIndex(currentAggreg.getValueFieldIndex() - 1);
		}
	}

	@UiHandler("btnApply")
	public void onApply(ClickEvent event) {
		currentAggreg.setMeasureName(measureName.getText());
		currentAggreg.setApplyOnDistinctSerie(applyOnDistinctSerie.getValue());
		currentAggreg.setAggregator(aggregator.getSelectedIndex());
		currentAggreg.setOrderType((ChartOrderingType) orderType.getSelectedObject());
		currentAggreg.setPrimaryAxis(primaryAxis.getValue());
		currentAggreg.setRendering((MeasureRendering) rendering.getSelectedObject());
		currentAggreg.setValueFieldIndex(valueField.getSelectedIndex() + 1);

		parent.apply(currentAggreg);
	}

	@UiHandler("btnCancel")
	public void onCancel(ClickEvent event) {
		parent.cancel();
	}
}
