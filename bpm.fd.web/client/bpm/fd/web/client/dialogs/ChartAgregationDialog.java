package bpm.fd.web.client.dialogs;

import java.util.List;

import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.panels.properties.ChartDataProperties;
import bpm.fd.web.client.panels.properties.widgets.DataAggregationProperties;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ChartAgregationDialog extends AbstractDialogBox {

	private static ChartAgregationDialogUiBinder uiBinder = GWT.create(ChartAgregationDialogUiBinder.class);

	interface ChartAgregationDialogUiBinder extends UiBinder<Widget, ChartAgregationDialog> {
	}

	@UiField
	HTMLPanel mainPanel;
	
	private ChartDataProperties parent;
	private DataAggregationProperties panelAggreg;
	
	private boolean edit;

	public ChartAgregationDialog(ChartDataProperties parent, DataAggregation aggreg, List<DataColumn> metacolumns) {
		super(Labels.lblCnst.AgregationProperties(), false, false);
		this.parent = parent;
		this.edit = aggreg != null;
		
		setWidget(uiBinder.createAndBindUi(this));

		this.panelAggreg = new DataAggregationProperties(this, aggreg, metacolumns);
		mainPanel.add(panelAggreg);
	}
	
	public void apply(DataAggregation agg) {
		if (edit) {
			parent.refresh();
		}
		else {
			parent.add(agg);
		}
		
		hide();
	}
	
	public void cancel() {
		hide();
	}
}
