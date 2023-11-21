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
import bpm.vanilla.platform.core.beans.resources.LimeSurveyResponseFormat;
import bpm.vanilla.platform.core.beans.resources.LimeSurveyType;
import bpm.vanillahub.core.beans.activities.LimeSurveyInputActivity;
import bpm.vanillahub.core.beans.resources.ApplicationServer;
import bpm.vanillahub.core.beans.resources.LimeSurveyServer;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;
import bpm.workflow.commons.beans.Activity;

public class LimeSurveyInputActivityProperties extends PropertiesPanel<Activity> implements IManager<ApplicationServer> {

	private ResourceManager resourceManager;
	private BoxItem item;
	private LimeSurveyInputActivity activity;

	private List<LimeSurveyServer> servers;
	private PropertiesListBox lstServers, lstTypeLimeSurvey, lstFormats;

	public LimeSurveyInputActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, final LimeSurveyInputActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), true, false);
		this.resourceManager = (ResourceManager) resourceManager;
		this.item = item;
		this.activity = activity;

		setNameChecker(creationPanel);
		setNameChanger(item);

		this.servers = this.resourceManager.getLimeSurveyServers(this.resourceManager.getApplicationServers());

		List<ListItem> items = new ArrayList<ListItem>();
		int selectedIndex = -1;
		if (servers != null) {
			int i = 0;
			for (LimeSurveyServer server : servers) {
				items.add(new ListItem(server.getName(), server.getId()));

				if (activity.getResourceId() > 0 && activity.getResourceId() == server.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstServers = addList(Labels.lblCnst.SelectLimeSurveyServer(), items, WidgetWidth.PCT, changeServer, refreshHandler);
		lstServers.setSelectedIndex(selectedIndex);

		addVariableText(Labels.lblCnst.LimeSurveyId(), activity.getLimeSurveyIdVS(), WidgetWidth.PCT, null);
		addVariableText(LabelsCommon.lblCnst.OutputFileName(), activity.getOutputNameVS(), WidgetWidth.PCT, null);
		
		List<ListItem> types = new ArrayList<ListItem>();
		types.add(new ListItem(Labels.lblCnst.ClassicLimeSurvey(), LimeSurveyType.LIMESURVEY.getType()));
		types.add(new ListItem(Labels.lblCnst.LimeSurveyWithShapes(), LimeSurveyType.LIMESURVEY_SHAPES.getType()));
		types.add(new ListItem(Labels.lblCnst.LimeSurveyVMAP(), LimeSurveyType.LIMESURVEY_VMAP.getType()));

		lstTypeLimeSurvey = addList(Labels.lblCnst.LimeSurveyType(), types, WidgetWidth.LARGE_PX, changeType, null);
		lstTypeLimeSurvey.setSelectedIndex(activity.getLimeSurveyType().getType());
		
		List<ListItem> formats = new ArrayList<ListItem>();
		formats.add(new ListItem(LimeSurveyResponseFormat.CSV.toString(), LimeSurveyResponseFormat.CSV.getType()));
		formats.add(new ListItem(LimeSurveyResponseFormat.PDF.toString(), LimeSurveyResponseFormat.PDF.getType()));
		formats.add(new ListItem(LimeSurveyResponseFormat.XLS.toString(), LimeSurveyResponseFormat.XLS.getType()));
		formats.add(new ListItem(LimeSurveyResponseFormat.DOC.toString(), LimeSurveyResponseFormat.DOC.getType()));
		formats.add(new ListItem(LimeSurveyResponseFormat.JSON.toString(), LimeSurveyResponseFormat.JSON.getType()));

		lstFormats = addList(Labels.lblCnst.Format(), formats, WidgetWidth.LARGE_PX, changeFormat, null);
		lstFormats.setSelectedIndex(activity.getFormat().getType());
		
		addCheckbox(Labels.lblCnst.Loop(), activity.isLoop(), loopChangeHandler);
		
		updateUi();
	}
	
	private void updateUi() {
		lstFormats.setVisible(activity.getLimeSurveyType() == LimeSurveyType.LIMESURVEY);
	}

	private ChangeHandler changeServer = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			int cibleId = Integer.parseInt(lstServers.getValue(lstServers.getSelectedIndex()));
			if (cibleId > 0) {
				activity.setResource(findServer(cibleId));
			}
		}
	};

	private LimeSurveyServer findServer(int serverId) {
		if (servers != null) {
			for (LimeSurveyServer server : servers) {
				if (server.getId() == serverId) {
					return server;
				}
			}
		}
		return null;
	}
	
	private ChangeHandler changeType = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			LimeSurveyType selectedType = LimeSurveyType.valueOf(Integer.parseInt(lstTypeLimeSurvey.getValue()));
			activity.setLimeSurveyType(selectedType);
			
			updateUi();
		}
	};
	
	private ChangeHandler changeFormat = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			LimeSurveyResponseFormat selectedFormat = LimeSurveyResponseFormat.valueOf(Integer.parseInt(lstFormats.getValue()));
			activity.setFormat(selectedFormat);
		}
	};

	private ClickHandler refreshHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			resourceManager.loadApplicationServers(LimeSurveyInputActivityProperties.this, LimeSurveyInputActivityProperties.this);
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
	public void loadResources() {
	}

	@Override
	public boolean isValid() {
		return activity.getLimeSurveyIdDisplay() != null && !activity.getLimeSurveyIdDisplay().isEmpty();
	}

	@Override
	public Activity buildItem() {
		return activity;
	}

	@Override
	public void loadResources(List<ApplicationServer> resources) {
		this.servers = resourceManager.getLimeSurveyServers(resources);

		lstServers.clear();
		int selectedIndex = -1;
		if (servers != null) {
			int i = 0;
			for (LimeSurveyServer item : servers) {
				lstServers.addItem(item.getName(), String.valueOf(item.getId()));

				if (activity.getResourceId() > 0 && activity.getResourceId() == item.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstServers.setSelectedIndex(selectedIndex);
	}

}
