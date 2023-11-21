package bpm.smart.web.client.panels.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.smart.core.model.workflow.activity.SimpleLinearRegActivity;
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

public class SimpleLinearRegActivityProperties extends PropertiesPanel<Activity> {

	private SimpleLinearRegActivity activity;

	private PropertiesListBox lstDatasets;
	private PropertiesListBox lstColumnsX;
	private PropertiesListBox lstColumnsY;
	
	private CheckBox chkGraph;
	
	private WorkspacePanel parentCreationPanel;

	public SimpleLinearRegActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, SimpleLinearRegActivity activity) {
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

		lstColumnsX = addList(LabelsConstants.lblCnst.Column() + " X", new ArrayList<ListItem>(), WidgetWidth.PCT, null, null);
		lstColumnsY = addList(LabelsConstants.lblCnst.Column() + " Y", new ArrayList<ListItem>(), WidgetWidth.PCT, null, null);
		
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
						for(DataColumn col : dataset.getMetacolumns()) {
							lstColumnsX.addItem(col.getColumnLabel(), dataset.getMetacolumns().indexOf(col) + "");
							lstColumnsY.addItem(col.getColumnLabel(), dataset.getMetacolumns().indexOf(col) + "");
						}
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

	private ChangeHandler datasetChangeHandler = new ChangeHandler() {
		@Override
		public void onChange(ChangeEvent event) {
			reloadColumns();
		}
	}; 
	
	private void reloadColumns() {
		String datasetName = lstDatasets.getValue(lstDatasets.getSelectedIndex());
		lstColumnsX.clear();
		lstColumnsY.clear();
		
		SmartAirService.Connect.getInstance().getDatasetColumns(datasetName, new GwtCallbackWrapper<List<DataColumn>>(null, false) {
			@Override
			public void onSuccess(List<DataColumn> result) {
				int i = 0;
				for(DataColumn col : result) {
					if(col.getColumnTypeName() .equals("numeric") || col.getColumnTypeName() .equals("DOUBLE") || col.getColumnTypeName() .equals("INT")){
						lstColumnsX.addItem(col.getColumnLabel(), col.getColumnName());
						lstColumnsY.addItem(col.getColumnLabel(), col.getColumnName());
					}
					
					
					if(activity.getxColumnName().equals(col.getColumnName())) {
						lstColumnsX.setSelectedIndex(i);
					} 
					if(activity.getyColumnName().equals(col.getColumnName())) {
						lstColumnsY.setSelectedIndex(i);
					}
					i++;
				}
				

			}
			
		}.getAsyncCallback());
		
	}

	@Override
	public Activity buildItem() {
		activity.setDatasetName(lstDatasets.getValue(lstDatasets.getSelectedIndex()));
		
		String columnXIndex = lstColumnsX.getValue(lstColumnsX.getSelectedIndex());
		String columnYIndex = lstColumnsY.getValue(lstColumnsY.getSelectedIndex());
		
		activity.setxColumnName(columnXIndex);
		activity.setyColumnName(columnYIndex);
		
		activity.setWithGraph(chkGraph.getValue());
		return activity;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
