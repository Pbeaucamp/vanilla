package bpm.smart.web.client.panels.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.ColumnAirPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.smart.core.model.workflow.activity.CorMatrixActivity;
import bpm.smart.core.model.workflow.activity.CorMatrixActivity.TypeCor;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.panels.properties.handler.RefreshWorkflowHandler;
import bpm.smart.web.client.services.SmartAirService;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.Workflow;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.MultiSelectionModel;

public class CorMatrixActivityProperties extends PropertiesPanel<Activity> {

	private CorMatrixActivity activity;

	private PropertiesListBox lstDatasets;
	private PropertiesListBox lstCorMethod;
	
	private SimplePanel columnPanel;

	private ColumnAirPanel airColumnPanel;
	
	private CheckBox chkGraph;
	
	private WorkspacePanel parentCreationPanel;

	public CorMatrixActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, CorMatrixActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), true, false);
		this.activity = activity;
		this.parentCreationPanel = creationPanel;

		setNameChecker(creationPanel);
		setNameChanger(item);

		addButton(LabelsConstants.lblCnst.RefreshWorkflow(), new RefreshWorkflowHandler(creationPanel, activity));
		
		lstDatasets = addList(LabelsConstants.lblCnst.Dataset(), new ArrayList<ListItem>(), WidgetWidth.PCT, datasetChangeHandler, new ClickHandler() {		
			@Override
			public void onClick(ClickEvent event) {
				refreshDatasets();
			}
		});
		
		columnPanel = addSimplePanel(false);
		airColumnPanel = new ColumnAirPanel(true);		
		
		columnPanel.add(airColumnPanel);

		List<ListItem> cors = new ArrayList<ListItem>();
		int i = 0;
		int selectedCorIndex = -1;
		for (TypeCor cor : TypeCor.values()) {
			cors.add(new ListItem(cor.toString(), cor.toString()));

			if (activity.getCorType() == cor) {
				selectedCorIndex = i;
			}
			i++;
		}
		lstCorMethod = addList(LabelsConstants.lblCnst.CorrelationCoef(), cors, WidgetWidth.PCT, changeTypeCor, null);
		if (selectedCorIndex != -1) {
			lstCorMethod.setSelectedIndex(selectedCorIndex);
		}
		
		chkGraph = addCheckbox(LabelsConstants.lblCnst.AddOutputgraph(), activity.isWithGraph(), null);
	}

	private void refreshDatasets() {
		CommonService.Connect.getInstance().getPermittedDatasets(new GwtCallbackWrapper<List<Dataset>>(null, false) {
			@Override
			public void onSuccess(List<Dataset> result) {
				List<ListItem> items = new ArrayList<ListItem>();
				boolean first = true;
				Integer dsIndex = null;
				int i = 0;
				for(Dataset dataset : result) {
					ListItem item = new ListItem(dataset.getName(), dataset.getName());
					items.add(item);
					if(first) {
						List<DataColumn> numCols = new ArrayList<DataColumn>();
						for(DataColumn col : dataset.getMetacolumns()){
							if(col.getColumnTypeName() .equals("numeric") || col.getColumnTypeName() .equals("DOUBLE") || col.getColumnTypeName() .equals("INT")){
								numCols.add(col);
							}
						}
						airColumnPanel.init(numCols, null);
						first = false;
					}
					if(activity.getDatasetName().equals(dataset.getName())) {
						dsIndex = i;
					}
					i++;
				}
				
				Workflow workflow = parentCreationPanel.buildWorkflow(false);
				List<String> outputs = workflow.getOutputs();
				for(String out : outputs) {
					ListItem item = new ListItem(out, out);
					items.add(item);
					if(activity.getDatasetName().equals(out)) {
						dsIndex = i;
					}
					i++;
				}
				 
				lstDatasets.setItems(items);
				if(dsIndex != null) {
					lstDatasets.setSelectedIndex(dsIndex);
					reloadColumns();
				}
			}
		}.getAsyncCallback());
	}

	private ChangeHandler changeTypeCor = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			String type = lst.getValue(lst.getSelectedIndex());

			activity.setCorType(TypeCor.valueOf(type));
		}
	};
	
	private ChangeHandler datasetChangeHandler = new ChangeHandler() {
		@Override
		public void onChange(ChangeEvent event) {
			reloadColumns();
		}
	}; 
	
	private void reloadColumns() {
		final String datasetName = lstDatasets.getValue(lstDatasets.getSelectedIndex());

		SmartAirService.Connect.getInstance().getDatasetColumns(datasetName, new GwtCallbackWrapper<List<DataColumn>>(null, false) {
			@Override
			public void onSuccess(List<DataColumn> result) {
				List<DataColumn> numCols = new ArrayList<DataColumn>();
				for(DataColumn col : result){
					if(col.getColumnTypeName() .equals("numeric") || col.getColumnTypeName() .equals("DOUBLE") || col.getColumnTypeName() .equals("INT")){
						numCols.add(col);
					}
				}
				
				if(activity.getColnames() != null && ! activity.getColnames().isEmpty()) {
					MultiSelectionModel<DataColumn> selection = new MultiSelectionModel<DataColumn>();
					for(DataColumn col : numCols) {
						if(activity.getColnames().contains(col.getColumnLabel())) {
							selection.setSelected(col, true);
						}
					}
					airColumnPanel.init(numCols, selection);
				}
				else {
					airColumnPanel.init(numCols, null);
				}
			}
		}.getAsyncCallback());
	}
	
	@Override
	public Activity buildItem() {
		activity.setDatasetName(lstDatasets.getValue(lstDatasets.getSelectedIndex()));
		activity.setWithGraph(chkGraph.getValue());
		List<String> colnames = new ArrayList<String>();
		for(DataColumn col : airColumnPanel.getSelectedColumns()){
			colnames.add(col.getColumnLabel());
		}
		activity.setColnames(colnames);
		return activity;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
