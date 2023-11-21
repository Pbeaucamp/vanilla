package bpm.fm.designer.web.client.dialog;

import bpm.fm.api.model.AbstractFactTable;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.FactTableAxis;
import bpm.fm.api.model.Metric;
import bpm.fm.designer.web.client.ClientSession;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AssociationDialog extends AbstractDialogBox {

	private static AssociationDialogUiBinder uiBinder = GWT.create(AssociationDialogUiBinder.class);

	interface AssociationDialogUiBinder extends UiBinder<Widget, AssociationDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	ListBox lstAxis;

	@UiField
	TextBox txtColumnMetricId, txtColumnObjId;
	
	@UiField
	Button btnColumnMetricBrowse, btnColumnObjBrowse;

	private Metric metric;

	private FactTableAxis axisAsso;

	public AssociationDialog(Metric metric, FactTableAxis axisAsso) {
		super(Messages.lbl.AxisAssociation(), false, true);

		this.metric = metric;
		this.axisAsso = axisAsso;

		setWidget(uiBinder.createAndBindUi(this));
		fillData();

		createButtonBar(Messages.lbl.Ok(), okHandler, Messages.lbl.Cancel(), cancelHandler);
	}

	private void fillData() {
		lstAxis.clear();
		int i = 0;
		for (Axis axis : ClientSession.getInstance().getAxis()) {
			lstAxis.addItem(axis.getName(), String.valueOf(axis.getId()));
			if (axisAsso != null) {
				if (axis.getId() == axisAsso.getAxisId()) {
					lstAxis.setSelectedIndex(i);
				}
			}
			i++;
		}

		if (axisAsso != null) {
			txtColumnMetricId.setText(axisAsso.getColumnId());
			txtColumnObjId.setText(axisAsso.getObjectiveColumnId());
		}
	}
	
	@UiHandler("btnColumnMetricBrowse")
	public void onColumnMetricBrowse(ClickEvent event) {
		AbstractFactTable table = metric.getFactTable();
		if (table instanceof FactTable) {
			int datasourceId = ((FactTable) table).getDatasourceId();
			String tableName = ((FactTable) table).getTableName();
			
			ColumnTableSelectionDialog dial = new ColumnTableSelectionDialog(datasourceId, txtColumnMetricId, false, tableName);
			dial.center();
		}
	}
	
	@UiHandler("btnColumnObjBrowse")
	public void onColumnObjBrowse(ClickEvent event) {
		AbstractFactTable table = metric.getFactTable();
		if (table instanceof FactTable) {
			int datasourceId = ((FactTable) table).getDatasourceId();
			String tableName = ((FactTable) table).getObjectives().getTableName();
			
			ColumnTableSelectionDialog dial = new ColumnTableSelectionDialog(datasourceId, txtColumnObjId, false, tableName);
			dial.center();
		}
	}

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {

			if (axisAsso == null) {
				axisAsso = new FactTableAxis();
			}

			int axisId = Integer.parseInt(lstAxis.getValue(lstAxis.getSelectedIndex()));
			axisAsso.setColumnId(txtColumnMetricId.getText());
			axisAsso.setObjectiveColumnId(txtColumnObjId.getText());
			axisAsso.setAxisId(axisId);
			for (Axis axis : ClientSession.getInstance().getAxis()) {
				if (axisId == axis.getId()) {
					axisAsso.setAxis(axis);
					break;
				}
			}

			boolean exists = false;
			for (FactTableAxis fta : ((FactTable) metric.getFactTable()).getFactTableAxis()) {
				if (fta.getAxisId() == axisId) {
					exists = true;
					break;
				}
			}

			if (!exists || axisAsso.getId() > 0) {
				if (axisAsso.getId() <= 0) {
					((FactTable) metric.getFactTable()).getFactTableAxis().add(axisAsso);
				}

				MetricService.Connection.getInstance().updateMetric(metric, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						AssociationDialog.this.hide();
					}

					@Override
					public void onFailure(Throwable caught) {
						AssociationDialog.this.hide();
					}
				});
			}
			else {
				InformationsDialog dial = new InformationsDialog(Messages.lbl.existingAssociation(), Messages.lbl.Ok(), Messages.lbl.Cancel(), Messages.lbl.existingAssociationMessage(), false);
				dial.center();
			}
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			AssociationDialog.this.hide();
		}
	};

}
