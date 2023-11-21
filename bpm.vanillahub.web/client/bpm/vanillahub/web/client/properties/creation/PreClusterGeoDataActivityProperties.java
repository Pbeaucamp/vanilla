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
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel.ListItem;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel.WidgetWidth;
import bpm.vanillahub.core.beans.activities.GeojsonActivity;
import bpm.vanillahub.core.beans.activities.PreClusterGeoDataActivity;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.resources.Cible;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class PreClusterGeoDataActivityProperties extends PropertiesPanel<Activity> implements IManager<Cible> {

	private ResourceManager resourceManager;
	private BoxItem item;
	private PreClusterGeoDataActivity activity;
	
	private List<Cible> cibles;
	private PropertiesListBox lstCibles;

	public PreClusterGeoDataActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, PreClusterGeoDataActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), true, false);
		this.item = item;
		this.activity = activity;
		this.resourceManager = (ResourceManager) resourceManager;

		setNameChecker(creationPanel);
		setNameChanger(item);

		addVariableText(Labels.lblCnst.NodeJsClusteringServerUrl(), activity.getUrlServerNode(), WidgetWidth.PCT, null);
		
		this.cibles = this.resourceManager.getCibles();
		
		List<ListItem> items = new ArrayList<ListItem>();
		int selectedIndex = -1;
		if(cibles != null) {
			int i = 0;
			for(Cible cible : cibles) {
				items.add(new ListItem(cible.getName(), cible.getId()));
				
				if (activity.getResourceId() > 0 && activity.getResourceId() == cible.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstCibles = addList(Labels.lblCnst.SelectCibleD4CToCLusterize(), items, WidgetWidth.PCT, changeCible, refreshHandler);
		lstCibles.setSelectedIndex(selectedIndex);
		//addCheckbox(Labels.lblCnst.Loop(), activity.isLoop(), loopChangeHandler);
	}

//	private ValueChangeHandler<Boolean> loopChangeHandler = new ValueChangeHandler<Boolean>() {
//
//		@Override
//		public void onValueChange(ValueChangeEvent<Boolean> event) {
//			activity.setLoop(event.getValue());
//			item.updateStyle(event.getValue());
//		}
//	};

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public Activity buildItem() {
		return activity;
	}
	
	private Cible findCible(int cibleId) {
		if (cibles != null) {
			for (Cible cible : cibles) {
				if (cible.getId() == cibleId) {
					return cible;
				}
			}
		}
		return null;
	}

	private ChangeHandler changeCible = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			int cibleId = Integer.parseInt(lstCibles.getValue(lstCibles.getSelectedIndex()));
			if (cibleId > 0) {
				activity.setResource(findCible(cibleId));
			}
		}
	};
	
	private ClickHandler refreshHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			resourceManager.loadCibles(PreClusterGeoDataActivityProperties.this, PreClusterGeoDataActivityProperties.this);
		}
	};
	
	@Override
	public void loadResources(List<Cible> result) {
		this.cibles = result;
		
		lstCibles.clear();
		int selectedIndex = -1;
		if(cibles != null) {
			int i = 0;
			for(Cible cible : cibles) {
				lstCibles.addItem(cible.getName(), String.valueOf(cible.getId()));
				
				if (activity.getResourceId() > 0 && activity.getResourceId() == cible.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstCibles.setSelectedIndex(selectedIndex);
	}

	@Override
	public void loadResources() {
	}

}
