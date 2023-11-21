package bpm.vanillahub.web.client.properties.creation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanillahub.core.beans.activities.CibleActivity;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.resources.Cible;

public class CibleActivityProperties extends PropertiesPanel<Activity> implements IManager<Cible> {
	
	private ResourceManager resourceManager;
	private BoxItem item;
	private CibleActivity activity;

	private List<Cible> cibles;
	private PropertiesListBox lstCibles;

	public CibleActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, CibleActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), false, false);
		this.resourceManager = (ResourceManager) resourceManager;
		this.item = item;
		this.activity = activity;

		setNameChecker(creationPanel);
		setNameChanger(item);
		
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

		lstCibles = addList(LabelsCommon.lblCnst.SelectCible(), items, WidgetWidth.PCT, changeCible, refreshHandler);
		lstCibles.setSelectedIndex(selectedIndex);

		addVariableText(Labels.lblCnst.TargetItem(), activity.getTargetItemVS(), WidgetWidth.PCT, null);
		
		addCheckbox(Labels.lblCnst.Loop(), activity.isLoop(), loopChangeHandler);
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
			resourceManager.loadCibles(CibleActivityProperties.this, CibleActivityProperties.this);
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
