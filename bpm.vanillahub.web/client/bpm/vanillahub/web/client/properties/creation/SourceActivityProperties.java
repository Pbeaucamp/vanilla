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
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel.WidgetWidth;
import bpm.vanillahub.core.beans.activities.SourceActivity;
import bpm.vanillahub.core.beans.resources.Source;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;
import bpm.workflow.commons.beans.Activity;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class SourceActivityProperties extends PropertiesPanel<Activity> implements IManager<Source> {

	private ResourceManager resourceManager;
	private BoxItem item;
	private SourceActivity activity;

	private List<Source> sources;
	private PropertiesListBox lstSources;

	public SourceActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, SourceActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), false, false);
		this.resourceManager = (ResourceManager) resourceManager;
		this.item = item;
		this.activity = activity;

		setNameChecker(creationPanel);
		setNameChanger(item);

		this.sources = this.resourceManager.getSources();

		List<ListItem> items = new ArrayList<ListItem>();
		int selectedIndex = -1;
		if (sources != null) {
			int i = 0;
			for (Source source : sources) {
				items.add(new ListItem(source.getName(), source.getId()));

				if (activity.getResourceId() > 0 && activity.getResourceId() == source.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstSources = addList(Labels.lblCnst.SelectSource(), items, WidgetWidth.PCT, changeSource, refreshHandler);
		lstSources.setSelectedIndex(selectedIndex);

		addVariableText(Labels.lblCnst.Filter(), activity.getCibleItemVS(), WidgetWidth.PCT, null);
		
		addCheckbox(Labels.lblCnst.Loop(), activity.isLoop(), loopChangeHandler);
	}

	private Source findSource(int sourceId) {
		if (sources != null) {
			for (Source source : sources) {
				if (source.getId() == sourceId) {
					return source;
				}
			}
		}
		return null;
	}

	private ChangeHandler changeSource = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			int sourceId = Integer.parseInt(lstSources.getValue(lstSources.getSelectedIndex()));
			if (sourceId > 0) {
				activity.setResource(findSource(sourceId));
			}
		}
	};

	private ValueChangeHandler<Boolean> loopChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			activity.setLoop(event.getValue());
			item.updateStyle(event.getValue());
		}
	};

	private ClickHandler refreshHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			resourceManager.loadSources(SourceActivityProperties.this, SourceActivityProperties.this);
		}
	};

	@Override
	public void loadResources(List<Source> result) {
		this.sources = result;

		lstSources.clear();
		int selectedIndex = -1;
		if (sources != null) {
			int i = 0;
			for (Source source : sources) {
				lstSources.addItem(source.getName(), String.valueOf(source.getId()));

				if (activity.getResourceId() > 0 && activity.getResourceId() == source.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstSources.setSelectedIndex(selectedIndex);
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
