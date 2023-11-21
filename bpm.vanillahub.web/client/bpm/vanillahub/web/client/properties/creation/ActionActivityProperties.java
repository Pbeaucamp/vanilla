package bpm.vanillahub.web.client.properties.creation;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanillahub.core.beans.activities.ActionActivity;
import bpm.vanillahub.core.beans.activities.ActionActivity.TypeAction;
import bpm.vanillahub.core.beans.resources.Source;
import bpm.vanillahub.core.beans.resources.Source.TypeSource;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;
import bpm.workflow.commons.beans.Activity;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

public class ActionActivityProperties extends PropertiesPanel<Activity> implements IManager<Source> {

	private ResourceManager resourceManager;
	private BoxItem item;
	private ActionActivity activity;

	private List<Source> resources;
	private List<Source> targets;

	private PropertiesListBox lstResources;
	private PropertiesListBox lstTargets;

	public ActionActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, ActionActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), false, false);
		this.resourceManager = (ResourceManager) resourceManager;
		this.activity = activity;
		this.item = item;

		setNameChecker(creationPanel);
		setNameChanger(item);

		List<ListItem> actions = new ArrayList<ListItem>();
		int i = 0;
		int selectedActionIndex = -1;
		for (TypeAction type : TypeAction.values()) {
			actions.add(new ListItem(type.toString(), type.getType()));

			if (activity.getTypeAction() == type) {
				selectedActionIndex = i;
			}
			i++;
		}

		PropertiesListBox lstType = addList(Labels.lblCnst.SelectAction(), actions, WidgetWidth.SMALL_PX, changeTypeSource, null);
		if (selectedActionIndex != -1) {
			lstType.setSelectedIndex(selectedActionIndex);
		}

		this.resources = this.resourceManager.getSources();

		List<ListItem> items = new ArrayList<ListItem>();
		int selectedIndex = -1;
		if (resources != null) {
			i = 0;
			for (Source resource : resources) {
				if (resource.getType() == TypeSource.FOLDER || resource.getType() == TypeSource.FTP || resource.getType() == TypeSource.SFTP) {
					items.add(new ListItem(resource.getName(), resource.getId()));
	
					if (activity.getResourceId() > 0 && activity.getResourceId() == resource.getId()) {
						selectedIndex = i;
					}
					i++;
				}
			}
		}

		lstResources = addList(Labels.lblCnst.SelectSource(), items, WidgetWidth.PCT, changeResource, refreshHandler);
		lstResources.setSelectedIndex(selectedIndex);
		
		this.targets = this.resourceManager.getSources();

		List<ListItem> itemConnectors = new ArrayList<ListItem>();
		int selectedConnectorIndex = -1;
		if (targets != null) {
			i = 0;
			for (Source source : targets) {
				if (source.getType() == TypeSource.FOLDER || source.getType() == TypeSource.FTP || source.getType() == TypeSource.SFTP) {
					itemConnectors.add(new ListItem(source.getName(), source.getId()));
	
					if (activity.getTargetId() > 0 && activity.getTargetId() == source.getId()) {
						selectedConnectorIndex = i;
					}
					i++;
				}
			}
		}

		lstTargets = addList(Labels.lblCnst.SelectTarget(), itemConnectors, WidgetWidth.PCT, changeResourceConnector, refreshHandler);
		lstTargets.setSelectedIndex(selectedConnectorIndex);
		
		addCheckbox(Labels.lblCnst.Loop(), activity.isLoop(), loopChangeHandler);
		
		updateUi(activity.getTypeAction());
	}

	private Source findResource(int cibleId) {
		if (resources != null) {
			for (Source resource : resources) {
				if (resource.getId() == cibleId) {
					return resource;
				}
			}
		}
		return null;
	}
	
	private void updateUi(TypeAction type) {
		if (type == TypeAction.DELETE) {
			lstTargets.setVisible(false);
		}
		else if (type == TypeAction.UNZIP || type == TypeAction.COPY || type == TypeAction.MOVE) {
			lstTargets.setVisible(true);
		}
	}

	private ValueChangeHandler<Boolean> loopChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			activity.setLoop(event.getValue());
			item.updateStyle(event.getValue());
		}
	};


	private ChangeHandler changeResource = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			int resourceId = Integer.parseInt(lstResources.getValue(lstResources.getSelectedIndex()));
			if (resourceId > 0) {
				activity.setResource(findResource(resourceId));
			}
		}
	};

	private ChangeHandler changeTypeSource = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			int type = Integer.parseInt(lst.getValue(lst.getSelectedIndex()));

			activity.setTypeAction(TypeAction.valueOf(type));
			updateUi(activity.getTypeAction());
		}
	};

	private ChangeHandler changeResourceConnector = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			int sourceId = Integer.parseInt(lstTargets.getValue(lstTargets.getSelectedIndex()));
			if (sourceId > 0) {
				activity.setTarget(findResource(sourceId));
			}
		}
	};

	private ClickHandler refreshHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			resourceManager.loadSources(ActionActivityProperties.this, ActionActivityProperties.this);
		}
	};

	@Override
	public void loadResources(List<Source> result) {
		this.resources = result;

		lstResources.clear();
		int selectedIndex = -1;
		if (resources != null) {
			int i = 0;
			for (Source resource : resources) {
				lstResources.addItem(resource.getName(), String.valueOf(resource.getId()));

				if (activity.getResourceId() > 0 && activity.getResourceId() == resource.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstResources.setSelectedIndex(selectedIndex);

		
		this.targets = result;
		
		lstTargets.clear();
		int selectedSourceIndex = -1;
		if (targets != null) {
			int i = 0;
			for (Source source : targets) {
				lstTargets.addItem(source.getName(), String.valueOf(source.getId()));

				if (activity.getTargetId() > 0 && activity.getTargetId() == source.getId()) {
					selectedSourceIndex = i;
				}
				i++;
			}
		}

		lstTargets.setSelectedIndex(selectedSourceIndex);
	}

	@Override
	public void loadResources() {
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public Activity buildItem() {
		return activity;
	}
}
