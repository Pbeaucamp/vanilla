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
import bpm.smart.core.model.workflow.activity.HAClustActivity;
import bpm.smart.core.model.workflow.activity.HAClustActivity.TypeClust;
import bpm.smart.core.model.workflow.activity.HAClustActivity.TypeDist;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.panels.properties.handler.RefreshWorkflowHandler;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.Workflow;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;

public class HAClustActivityProperties extends PropertiesPanel<Activity> {

	private HAClustActivity activity;

	private PropertiesListBox lstDatasets;
	private PropertiesListBox lstDist;
	private PropertiesListBox lstClust;
	
	private CheckBox chkRotate;
	
	private WorkspacePanel parentCreationPanel;

	public HAClustActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, HAClustActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), true, false);
		this.activity = activity;
		this.parentCreationPanel = creationPanel;

		setNameChecker(creationPanel);
		setNameChanger(item);

		addButton(LabelsConstants.lblCnst.RefreshWorkflow(), new RefreshWorkflowHandler(creationPanel, activity));
		
		lstDatasets = addList(LabelsConstants.lblCnst.Dataset(), new ArrayList<ListItem>(), WidgetWidth.PCT, null, new ClickHandler() {		
			@Override
			public void onClick(ClickEvent event) {
				refreshDatasets();
			}
		});

		List<ListItem> dists = new ArrayList<ListItem>();
		int i = 0;
		int selectedDistIndex = -1;
		for (TypeDist dist : TypeDist.values()) {
			dists.add(new ListItem(dist.toString(), dist.toString()));

			if (activity.getDistType() == dist) {
				selectedDistIndex = i;
			}
			i++;
		}
		lstDist = addList(LabelsConstants.lblCnst.DistanceMeasure(), dists, WidgetWidth.PCT, changeTypeDist, null);
		if (selectedDistIndex != -1) {
			lstDist.setSelectedIndex(selectedDistIndex);
		}
		
		List<ListItem> clusts = new ArrayList<ListItem>();
		int j = 0;
		int selectedClustIndex = -1;
		for (TypeClust clust : TypeClust.values()) {
			clusts.add(new ListItem(clust.toString(), clust.toString()));

			if (activity.getClustType() == clust) {
				selectedClustIndex = j;
			}
			j++;
		}
		lstClust = addList(LabelsConstants.lblCnst.AgglomerationMethod(), clusts, WidgetWidth.PCT, changeTypeClust, null);
		if (selectedClustIndex != -1) {
			lstClust.setSelectedIndex(selectedClustIndex);
		}
		
		
		chkRotate = addCheckbox(LabelsConstants.lblCnst.Rotate(), activity.isRotate(), null);
	}

	private void refreshDatasets() {
		CommonService.Connect.getInstance().getPermittedDatasets(new GwtCallbackWrapper<List<Dataset>>(null, false) {
			@Override
			public void onSuccess(List<Dataset> result) {
				List<ListItem> items = new ArrayList<ListItem>();
				
				Integer dsIndex = null;
				int i = 0;
				for(Dataset dataset : result) {
					ListItem item = new ListItem(dataset.getName(), dataset.getName());
					items.add(item);
					
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
				}
			}
		}.getAsyncCallback());
	}

	private ChangeHandler changeTypeDist = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			String type = lst.getValue(lst.getSelectedIndex());

			activity.setDistType(TypeDist.valueOf(type));
		}
	};
	
	private ChangeHandler changeTypeClust = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			String type = lst.getValue(lst.getSelectedIndex());

			activity.setClustType(TypeClust.valueOf(type));
		}
	};
	
	@Override
	public Activity buildItem() {
		activity.setDatasetName(lstDatasets.getValue(lstDatasets.getSelectedIndex()));
		activity.setRotate(chkRotate.getValue());
		return activity;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
