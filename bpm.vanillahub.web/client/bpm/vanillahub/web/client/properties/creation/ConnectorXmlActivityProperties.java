package bpm.vanillahub.web.client.properties.creation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.service.WorkflowsService;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanillahub.core.beans.activities.ConnectorXmlActivity;
import bpm.vanillahub.core.beans.resources.Connector;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;
import bpm.workflow.commons.beans.Activity;

public class ConnectorXmlActivityProperties extends PropertiesPanel<Activity> {

	private ResourceManager resourceManager;
	private BoxItem item;
	private ConnectorXmlActivity activity;

	private List<Connector> connectors;

	private PropertiesListBox lstConnectors;

	public ConnectorXmlActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, ConnectorXmlActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), false, false);
		this.resourceManager = (ResourceManager) resourceManager;
		this.item = item;
		this.activity = activity;

		setNameChecker(creationPanel);
		setNameChanger(item);

		this.connectors = this.resourceManager.getConnectors();

		List<ListItem> itemConnectors = new ArrayList<ListItem>();
		int selectedConnectorIndex = -1;
		if (connectors != null) {
			int i = 0;
			for (Connector connector : connectors) {
				itemConnectors.add(new ListItem(connector.getName(), connector.getName()));

				if (activity.getConnector() != null && activity.getConnector().getName().equals(connector.getName())) {
					selectedConnectorIndex = i;
				}
				i++;
			}
		}

		lstConnectors = addList(Labels.lblCnst.SelectConnectorXml(), itemConnectors, WidgetWidth.PCT, changeResourceConnector, null);
		lstConnectors.setSelectedIndex(selectedConnectorIndex);
		
		addCheckbox(Labels.lblCnst.Loop(), activity.isLoop(), loopChangeHandler);

		if (connectors == null) {
			loadConnectors();
		}
	}

	private Connector findConnector(String connectorName) {
		if (connectors != null) {
			for (Connector connector : connectors) {
				if (connector.getName().equals(connectorName)) {
					return connector;
				}
			}
		}
		return null;
	}

	private ChangeHandler changeResourceConnector = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			String connectorName = lstConnectors.getValue(lstConnectors.getSelectedIndex());
			if (!connectorName.isEmpty()) {
				activity.setConnector(findConnector(connectorName));
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

	public void loadConnectors() {
		showWaitPart(true);

		WorkflowsService.Connect.getInstance().getResources(TypeResource.CONNECTOR, new GwtCallbackWrapper<List<? extends Resource>>(this, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				connectors = (List<Connector>) result;
				resourceManager.setConnectors(connectors);

				lstConnectors.clear();
				int selectedIndex = -1;
				if (connectors != null) {
					int i = 0;
					for (Connector connector : connectors) {
						lstConnectors.addItem(connector.getName(), connector.getName());

						if (activity.getConnector() != null && activity.getConnector().getName().equals(connector.getName())) {
							selectedIndex = i;
						}
						i++;
					}
				}

				lstConnectors.setSelectedIndex(selectedIndex);
			}
		}.getAsyncCallback());
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
