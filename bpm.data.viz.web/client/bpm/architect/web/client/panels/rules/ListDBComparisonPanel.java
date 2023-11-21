package bpm.architect.web.client.panels.rules;

import java.util.ArrayList;
import java.util.List;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.panels.IRulePanel;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.datasource.DatasourceDatasetManager;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.beans.resources.ClassField;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ClassRule.TypeRule;
import bpm.vanilla.platform.core.beans.resources.OperatorListe;
import bpm.vanilla.platform.core.beans.resources.RuleDBComparison;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListDBComparisonPanel extends Composite implements IRulePanel {

	private static ListDBComparisonPanelUiBinder uiBinder = GWT.create(ListDBComparisonPanelUiBinder.class);

	interface ListDBComparisonPanelUiBinder extends UiBinder<Widget, ListDBComparisonPanel> {
	}
	
	@UiField
	ListBoxWithButton<OperatorListe> lstOperators;

	@UiField
	ListBoxWithButton<Datasource> lstDatasources;

	@UiField
	ListBoxWithButton<Dataset> lstDatasets;

	@UiField
	ListBoxWithButton<DataColumn> lstColumns;

	private IWait waitPanel;
	private User user;

	private ClassRule rule;

	public ListDBComparisonPanel(IWait waitPanel, User user, ClassRule rule, ClassField fieldParent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.user = user;
		this.rule = rule;
		
		lstOperators.setList(OperatorListe.values());

		refreshDatasourceDataset();
	}

	public void refreshDatasourceDataset() {
		CommonService.Connect.getInstance().getDatasources(new GwtCallbackWrapper<List<Datasource>>(waitPanel, true, true) {

			@Override
			public void onSuccess(List<Datasource> result) {
				List<Datasource> datasources = new ArrayList<>();
				if (result != null) {
					for (Datasource ds : result) {
						if (ds.getType() == DatasourceType.JDBC) {
							datasources.add(ds);
						}
					}
				}

				loadData(datasources);
			}
		}.getAsyncCallback());
	}

	private void loadData(List<Datasource> datasources) {
		lstDatasources.setList(datasources);

		if (rule != null && rule.getType() == TypeRule.LISTE_DB_COMPARAISON) {
			RuleDBComparison ruleDef = (RuleDBComparison) rule.getRule();

			lstOperators.setSelectedObject(ruleDef.getOperator());
			
			int datasourceId = ruleDef.getDatasourceId();
			int datasetId = ruleDef.getDatasetId();
			String selectedColumn = ruleDef.getColumnName();

			int i = 0;
			LOOP: for (Datasource ds : datasources) {
				if (ds.getId() == datasourceId) {
					lstDatasources.setSelectedIndex(i);
					lstDatasets.setList(ds.getDatasets());
					int j = 0;
					for (Dataset d : ds.getDatasets()) {
						if (d.getId() == datasetId) {
							lstDatasets.setSelectedIndex(j);
							break LOOP;
						}
						j++;
					}
				}
				i++;
			}

			loadColumns(selectedColumn);
		}
		else {
			lstDatasources.fireChange();
		}
	}

	private void loadColumns(final String selectedColumn) {
		Datasource datasource = lstDatasources.getSelectedObject();
		Dataset dataset = lstDatasets.getSelectedObject();

		if (datasource == null || dataset == null) {
			return;
		}

		DatasourceJdbc dsJdbc = (DatasourceJdbc) datasource.getObject();
		String query = dataset.getRequest();

		CommonService.Connect.getInstance().getDataSetMetaData(dsJdbc, query, new GwtCallbackWrapper<ArrayList<DataColumn>>(waitPanel, true, true) {

			@Override
			public void onSuccess(ArrayList<DataColumn> result) {
				if (result.size() == 0) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.UnableToLoadDataSetMetaData(), LabelsConstants.lblCnst.NoData());
					return;
				}

				lstColumns.setList(result);

				if (selectedColumn != null && !selectedColumn.isEmpty()) {
					for (DataColumn col : result) {
						if (col.getColumnName().equals(selectedColumn)) {
							lstColumns.setSelectedObject(col);
							break;
						}
					}
				}
			}
		}.getAsyncCallback());

	}

	@UiHandler("lstDatasources")
	public void onDatasourceChange(ChangeEvent event) {
		Datasource selected = (Datasource) lstDatasources.getSelectedObject();
		lstDatasets.setList(selected.getDatasets());
		
		lstDatasets.fireChange();
	}

	@UiHandler("lstDatasets")
	public void onDatasetChange(ChangeEvent event) {
		loadColumns(null);
	}

	@UiHandler("lstDatasources")
	public void onDatasourceClick(ClickEvent event) {
		DatasourceDatasetManager dialog = new DatasourceDatasetManager(user);
		dialog.setModal(true);
		dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				refreshDatasourceDataset();
			}
		});
		dialog.center();
	}

	@Override
	public ClassRule getClassRule() {
		OperatorListe op = lstOperators.getSelectedObject();
		
		Datasource datasource = lstDatasources.getSelectedObject();
		Dataset dataset = lstDatasets.getSelectedObject();
		DataColumn selectedColumn = lstColumns.getSelectedObject();

		if (datasource == null || dataset == null || selectedColumn == null) {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Labels.lblCnst.NeedSelectDatasourceAndDataset());
			return null;
		}

		RuleDBComparison ruleDef = new RuleDBComparison();
		ruleDef.setOperator(op);
		ruleDef.setDatasourceId(datasource.getId());
		ruleDef.setDatasourceName(datasource.getName());
		ruleDef.setDatasetId(dataset.getId());
		ruleDef.setDatasetName(dataset.getName());
		ruleDef.setColumnName(selectedColumn.getColumnName());

		if (rule == null) {
			rule = new ClassRule();
		}
		rule.setType(TypeRule.LISTE_DB_COMPARAISON);
		rule.setRule(ruleDef);

		return rule;
	}

}
