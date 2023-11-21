package bpm.smart.web.client.panels.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.VariablePropertiesText;
import bpm.smart.core.model.workflow.activity.RecodeActivity;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.panels.properties.custom.RecodeMappingProperties;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;

public class RecodeActivityProperties extends PropertiesPanel<Activity> {
	
	private RecodeActivity activity;
	
	private VariablePropertiesText outputText;
	
	private PropertiesListBox lstDatasets;
	private PropertiesListBox lstColumns;
	
	private SimplePanel mappingPanel;
	private RecodeMappingProperties mappingProperties;
	
	private WorkspacePanel parentCreationPanel;

	public RecodeActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, RecodeActivity recodeActivity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, recodeActivity.getName(), true, false);
		this.activity = recodeActivity;
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
		
		outputText = addVariableText(LabelsConstants.lblCnst.OutputDataset(), activity.getOutputDataset(), WidgetWidth.PCT, null);
		
		lstColumns = addList(LabelsConstants.lblCnst.Column(), new ArrayList<ListItem>(), WidgetWidth.PCT, datasetChangeHandler, null);
		
		mappingPanel = addSimplePanel(false);
		mappingProperties = new RecodeMappingProperties();
		mappingPanel.add(mappingProperties);
		
		refreshDatasets();
	}
	
	public void refreshDatasets() {
		CommonService.Connect.getInstance().getPermittedDatasets(new AsyncCallback<List<Dataset>>() {
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
						for(DataColumn col : dataset.getMetacolumns()) {
							lstColumns.addItem(col.getColumnLabel(), dataset.getMetacolumns().indexOf(col) + "");
						}
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
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}
		});
	}
	
	private ChangeHandler datasetChangeHandler = new ChangeHandler() {
		@Override
		public void onChange(ChangeEvent event) {
			if(event.getSource() == lstDatasets.getListBox()) {
				reloadColumns();
				
			}
			else {
				loadMapping();
			}
			
		}
	}; 
	
	private void reloadColumns() {
		String datasetName = lstDatasets.getValue(lstDatasets.getSelectedIndex());
		lstColumns.clear();
		
		SmartAirService.Connect.getInstance().getDatasetColumns(datasetName, new GwtCallbackWrapper<List<DataColumn>>(null, false) {
			@Override
			public void onSuccess(List<DataColumn> result) {
				for(DataColumn col : result) {
					lstColumns.addItem(col.getColumnLabel(), result.indexOf(col) + "");
				}
				
				if(activity.getColumnIndex() > -1) {
					lstColumns.setSelectedIndex(activity.getColumnIndex());
					loadMapping();
				}
			}
			
		}.getAsyncCallback());
		

		
	}
	
	private void loadMapping() {
		String datasetName = lstDatasets.getValue(lstDatasets.getSelectedIndex());
		int column = Integer.parseInt(lstColumns.getValue(lstColumns.getSelectedIndex()));
		SmartAirService.Connect.getInstance().getColumnDistinctValues(datasetName, column, new AsyncCallback<List<String>>() {
			@Override
			public void onSuccess(List<String> result) {
				mappingProperties.fillData(result, activity);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}
		});
	}

	@Override
	public Activity buildItem() {
		activity.setInputDataset(lstDatasets.getValue(lstDatasets.getSelectedIndex()));
		activity.setOutputDataset(outputText.getVariableText());

		int columnIndex = Integer.parseInt(lstColumns.getValue(lstColumns.getSelectedIndex()));
		
		activity.setColumnIndex(columnIndex);
		
		activity.setValueMapping(mappingProperties.getMappingValues());
		
		return activity;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
