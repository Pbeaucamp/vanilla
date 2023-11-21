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
import bpm.gwt.workflow.commons.client.workflow.properties.VariablePropertiesText;
import bpm.smart.core.model.workflow.activity.FieldSelectionActivity;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.MultiSelectionModel;

public class FieldSelectionActivityProperties extends PropertiesPanel<Activity> {

	private FieldSelectionActivity activity;

	private PropertiesListBox lstDatasets;
	private VariablePropertiesText outputText;

	private WorkspacePanel parentCreationPanel;

	private SimplePanel columnPanel;

	private ColumnAirPanel airColumnPanel;

	public FieldSelectionActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, FieldSelectionActivity fieldActivity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, fieldActivity.getName(), true, false);

		this.activity = fieldActivity;
		this.parentCreationPanel = creationPanel;

		setNameChecker(creationPanel);
		setNameChanger(item);

		addButton(LabelsConstants.lblCnst.RefreshWorkflow(), new RefreshWorkflowHandler(creationPanel, fieldActivity));

		outputText = addVariableText(LabelsConstants.lblCnst.OutputDataset(), activity.getOutputDataset(), WidgetWidth.PCT, null);
		
		lstDatasets = addList(LabelsConstants.lblCnst.Dataset(), new ArrayList<ListItem>(), WidgetWidth.PCT, datasetChangeHandler, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refreshDatasets();
			}
		});

		columnPanel = addSimplePanel(false);
		airColumnPanel = new ColumnAirPanel(true);
		columnPanel.add(airColumnPanel);
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
						airColumnPanel.init(dataset.getMetacolumns(), null);
						first = false;
					}
					if(activity.getInputDataset().equals(dataset.getName())) {
						dsIndex = i;
					}
					i++;
				}
				
				Workflow workflow = parentCreationPanel.buildWorkflow(false);
				List<String> outputs = workflow.getOutputs();
				for(String out : outputs) {
					ListItem item = new ListItem(out, out);
					items.add(item);
					if(activity.getInputDataset().equals(out)) {
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

	private void reloadColumns() {
		final String datasetName = lstDatasets.getValue(lstDatasets.getSelectedIndex());

		SmartAirService.Connect.getInstance().getDatasetColumns(datasetName, new GwtCallbackWrapper<List<DataColumn>>(null, false) {
			@Override
			public void onSuccess(List<DataColumn> result) {
				if(!activity.getInputDataset().isEmpty() && activity.getInputDataset().equals(datasetName)) {
					if(activity.getSelectedColumns() != null && ! activity.getSelectedColumns().isEmpty()) {
						MultiSelectionModel<DataColumn> selection = new MultiSelectionModel<DataColumn>();
						for(DataColumn col : result) {
							if(activity.getSelectedColumns().contains(col)) {
								selection.setSelected(col, true);
							}
						}
						airColumnPanel.init(result, selection);
					}
					else {
						airColumnPanel.init(result, null);
					}
				}
				else {
					airColumnPanel.init(result, null);
				}
			}
		}.getAsyncCallback());
	}

	private ChangeHandler datasetChangeHandler = new ChangeHandler() {
		@Override
		public void onChange(ChangeEvent event) {
			reloadColumns();
		}
	};

	@Override
	public Activity buildItem() {
		String datasetName = lstDatasets.getValue(lstDatasets.getSelectedIndex());
		activity.setInputDataset(datasetName);
		activity.setOutputDataset(outputText.getVariableText());
		
		activity.setSelectedColumns(airColumnPanel.getSelectedColumns());
		
		return activity;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
