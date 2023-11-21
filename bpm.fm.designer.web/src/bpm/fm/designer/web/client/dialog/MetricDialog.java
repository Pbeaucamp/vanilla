package bpm.fm.designer.web.client.dialog;

import bpm.fm.api.model.Metric;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MetricDialog extends AbstractDialogBox {

	private static MetricDialogUiBinder uiBinder = GWT.create(MetricDialogUiBinder.class);

	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	TextBox txtName;
	
	@UiField
	CheckBox checkCalc;
	
	private Metric metric;
	
	interface MetricDialogUiBinder extends UiBinder<Widget, MetricDialog> {
	}

	public MetricDialog(Metric metric) {
		super(Messages.lbl.AddEditMetric(), false, true);
		
		setWidget(uiBinder.createAndBindUi(this));
		
		this.metric = metric;
		
		if(metric != null) {
			txtName.setText(metric.getName());
		}
		
		createButtonBar(Messages.lbl.Ok(), okHandler, Messages.lbl.Cancel(), cancelHandler);
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if(txtName.getText() != null && !txtName.getText().isEmpty()) {
				if(metric == null) {
					metric = new Metric();
				}
				metric.setName(txtName.getText());
				if(checkCalc.getValue()) {
					metric.setMetricType(Metric.METRIC_TYPES.get(Metric.TYPE_CALCULATED));
				}
				
				MetricService.Connection.getInstance().addMetric(metric, new AsyncCallback<Metric>() {
					@Override
					public void onFailure(Throwable caught) {
						InformationsDialog dial = new InformationsDialog(Messages.lbl.Error(), Messages.lbl.Ok(), Messages.lbl.ProblemSaveMetric(), caught.getMessage(), caught);
						dial.center();
					}
					@Override
					public void onSuccess(Metric result) {
						InformationsDialog dial = new InformationsDialog(Messages.lbl.Success(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.MetricCreatedSuccess(), false);
						dial.center();
						
						
						if(metric.getId() <= 0) {
							LinkThemeDialog d = new LinkThemeDialog(result);
							d.center();
						}
						MetricDialog.this.hide();
					}
				});
			}
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			MetricDialog.this.hide();
		}
	};
}
