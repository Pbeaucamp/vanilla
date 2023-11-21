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
import bpm.vanillahub.core.beans.activities.ValidationActivity;
import bpm.vanillahub.core.beans.resources.FileXSD;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;
import bpm.workflow.commons.beans.Activity;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class ValidationActivityProperties extends PropertiesPanel<Activity> implements IManager<FileXSD> {

	private ResourceManager resourceManager;
	private BoxItem item;
	private ValidationActivity activity;

	private List<FileXSD> cibles;
	private PropertiesListBox lstFileXSDs;

	public ValidationActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, ValidationActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), false, false);
		this.resourceManager = (ResourceManager) resourceManager;
		this.item = item;
		this.activity = activity;

		setNameChecker(creationPanel);
		setNameChanger(item);
		
		this.cibles = this.resourceManager.getFileXSDs();
		
		List<ListItem> items = new ArrayList<ListItem>();
		int selectedIndex = -1;
		if(cibles != null) {
			int i = 0;
			for(FileXSD cible : cibles) {
				items.add(new ListItem(cible.getName(), cible.getId()));
				
				if (activity.getResourceId() > 0 && activity.getResourceId() == cible.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstFileXSDs = addList(Labels.lblCnst.SelectFileXSD(), items, WidgetWidth.PCT, changeFileXSD, refreshHandler);
		lstFileXSDs.setSelectedIndex(selectedIndex);
		
		addCheckbox(Labels.lblCnst.Loop(), activity.isLoop(), loopChangeHandler);
	}

	private FileXSD findFileXSD(int cibleId) {
		if (cibles != null) {
			for (FileXSD cible : cibles) {
				if (cible.getId() == cibleId) {
					return cible;
				}
			}
		}
		return null;
	}

	private ChangeHandler changeFileXSD = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			int cibleId = Integer.parseInt(lstFileXSDs.getValue(lstFileXSDs.getSelectedIndex()));
			if (cibleId > 0) {
				activity.setResource(findFileXSD(cibleId));
			}
		}
	};
	
	private ClickHandler refreshHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			resourceManager.loadXSDs(ValidationActivityProperties.this, ValidationActivityProperties.this);
		}
	};

	private ValueChangeHandler<Boolean> loopChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			activity.setLoop(event.getValue());
			item.updateStyle(event.getValue());
		}
	};

	@Override
	public void loadResources(List<FileXSD> result) {
		this.cibles = result;
		
		lstFileXSDs.clear();
		int selectedIndex = -1;
		if(cibles != null) {
			int i = 0;
			for(FileXSD cible : cibles) {
				lstFileXSDs.addItem(cible.getName(), String.valueOf(cible.getId()));
				
				if (activity.getResourceId() > 0 && activity.getResourceId() == cible.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstFileXSDs.setSelectedIndex(selectedIndex);
	}

	@Override
	public void loadResources() { }

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public Activity buildItem() {
		return activity;
	}
}
