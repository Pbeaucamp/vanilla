package bpm.fm.designer.web.client.panel;

import java.util.ArrayList;
import java.util.List;

import bpm.fm.api.model.CalculatedFactTable;
import bpm.fm.api.model.CalculatedFactTableMetric;
import bpm.fm.api.model.Metric;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.custom.CustomDatagrid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;

public class CalculatedValuePanel extends ValuePanel {

	private static CalculatedValuePanelUiBinder uiBinder = GWT.create(CalculatedValuePanelUiBinder.class);

	interface CalculatedValuePanelUiBinder extends UiBinder<Widget, CalculatedValuePanel> {
	}

	@UiField
	CheckBox checkKaplan;
	
	@UiField
	HTMLPanel gridPanel;
	
	@UiField
	TextArea txtCalculation;
	
	@UiField
	ListBox lstKaplan;
	
	private Metric metric;
	
	private MultiSelectionModel<Metric> selectionModel = new MultiSelectionModel<Metric>();

	public CalculatedValuePanel(Metric metric) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.metric = metric;
		
		checkKaplan.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()) {
					txtCalculation.setEnabled(false);
					lstKaplan.setEnabled(true);
				}
				else {
					txtCalculation.setEnabled(true);
					lstKaplan.setEnabled(false);
				}
			}
		});
	}

	@Override
	public void fillData() {
		
		if(metric.getMetricType().equals(Metric.METRIC_TYPES.get(Metric.TYPE_KAPLAN))) {
			checkKaplan.setValue(true);
			txtCalculation.setEnabled(false);
			lstKaplan.setEnabled(true);
		}
		else {
			checkKaplan.setValue(false);
			txtCalculation.setEnabled(true);
			lstKaplan.setEnabled(false);
			
			txtCalculation.setText(((CalculatedFactTable)metric.getFactTable()).getCalculation());
		}
		int i = 0;
		for(String kap : Metric.KAPLAN_TYPES) {
			if(kap.equals(Metric.KAPLAN_TYPES.get(Metric.KAPLAN_ONE))) {
				lstKaplan.addItem(Messages.lbl.kaplanOne(), kap);
			}
			else if(kap.equals(Metric.KAPLAN_TYPES.get(Metric.KAPLAN_ALL))) {
				lstKaplan.addItem(Messages.lbl.kaplanAll(), kap);
			}
			else if(kap.equals(Metric.KAPLAN_TYPES.get(Metric.KAPLAN_MAJ))) {
				lstKaplan.addItem(Messages.lbl.kaplanMaj(), kap);
			}
			if(metric.getMetricType().equals(Metric.METRIC_TYPES.get(Metric.TYPE_KAPLAN))) {
				if(((CalculatedFactTable)metric.getFactTable()).getCalculation().equals(kap)) {
					lstKaplan.setSelectedIndex(i);
				}
			}
			
			i++;
		}
		
	}

	@Override
	public Metric getMetric() {
		CalculatedFactTable table = (CalculatedFactTable) metric.getFactTable();
		if(checkKaplan.getValue()) {
			metric.setMetricType(Metric.METRIC_TYPES.get(Metric.TYPE_KAPLAN));
			table.setCalculation(lstKaplan.getValue(lstKaplan.getSelectedIndex()));
		}
		else {
			metric.setMetricType(Metric.METRIC_TYPES.get(Metric.TYPE_CALCULATED));
			table.setCalculation(txtCalculation.getText());
		}
		
		table.getMetrics().clear();
		for(Metric m : selectionModel.getSelectedSet()) {
			CalculatedFactTableMetric ftm = new CalculatedFactTableMetric();
			ftm.setMetric(m);
			table.getMetrics().add(ftm);
		}
		return metric;
	}

	@Override
	public void refresh(final Metric metric) {
		int themeId = -1;
		try {
			themeId = MainPanel.getInstance().getSelectedTheme();
		} catch(Exception e) {
			
		}
		
		int obsId = -1;
		try {
			obsId = MainPanel.getInstance().getSelectedObservatory();
		} catch(Exception e) {
			
		}
		
		this.metric = metric;
		MetricService.Connection.getInstance().getMetrics(obsId, themeId, new AsyncCallback<List<Metric>>() {
			
			@Override
			public void onSuccess(List<Metric> result) {
				gridPanel.clear();
				
				CalculatedFactTable fact = (CalculatedFactTable) metric.getFactTable();
				
				for(CalculatedFactTableMetric ftm : fact.getMetrics()) {
					selectionModel.setSelected(ftm.getMetric(), true);
				}
				
				CustomDatagrid<Metric> dg = new CustomDatagrid<Metric>(result, selectionModel, 200, "No metric available", "Metrics");
				dg.loadItems(result, new ArrayList<Metric>(selectionModel.getSelectedSet()));
				gridPanel.add(dg);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
