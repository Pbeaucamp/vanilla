package bpm.vanillahub.web.client.properties.creation;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesTextBrowse;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanillahub.core.beans.activities.RunVanillaItemActivity;
import bpm.vanillahub.core.beans.activities.attributes.VanillaItemParameter;
import bpm.vanillahub.core.beans.resources.ApplicationServer;
import bpm.vanillahub.core.beans.resources.VanillaServer;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.dialogs.RepositoryDialog;
import bpm.vanillahub.web.client.properties.parameters.RunVanillaItemParameterPanel;
import bpm.vanillahub.web.client.services.ResourcesService;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;
import bpm.workflow.commons.beans.Activity;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.PopupPanel;

public class RunVanillaItemActivityProperties extends PropertiesPanel<Activity> implements IManager<ApplicationServer> {

	private ResourceManager resourceManager;
	private BoxItem item;
	private RunVanillaItemActivity activity;

	private List<VanillaServer> serverVanillas;
	private PropertiesListBox lstServerVanillas;

	private PropertiesTextBrowse txtItemName;

	private RunVanillaItemParameterPanel parameterPanel;

	public RunVanillaItemActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, RunVanillaItemActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), true, false);

		this.resourceManager = (ResourceManager) resourceManager;
		this.item = item;
		this.activity = activity;

		setNameChecker(creationPanel);
		setNameChanger(item);

		this.serverVanillas = this.resourceManager.getVanillaServers(this.resourceManager.getApplicationServers());

		List<ListItem> items = new ArrayList<ListItem>();
		int selectedIndex = -1;
		if (serverVanillas != null) {
			int i = 0;
			for (VanillaServer cible : serverVanillas) {
				items.add(new ListItem(cible.getName(), cible.getId()));

				if (activity.getResourceId() > 0 && activity.getResourceId() == cible.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstServerVanillas = addList(Labels.lblCnst.SelectVanillaServer(), items, WidgetWidth.PCT, changeServerVanilla, refreshHandler);
		lstServerVanillas.setSelectedIndex(selectedIndex);

		txtItemName = addBrowse("", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final RepositoryDialog dial = new RepositoryDialog(serverVanillas.get(lstServerVanillas.getSelectedIndex()));
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							final RepositoryItem item = dial.getSelectedItem();
	
							RunVanillaItemActivityProperties.this.activity.setItemId(item.getId());
							RunVanillaItemActivityProperties.this.activity.setItemName(item.getName());
							
							ResourcesService.Connect.getInstance().getParameterForItem(serverVanillas.get(lstServerVanillas.getSelectedIndex()), item.getId(), new GwtCallbackWrapper<List<VanillaItemParameter>>(RunVanillaItemActivityProperties.this, false) {
	
								@Override
								public void onSuccess(List<VanillaItemParameter> result) {
									if (result != null && !result.isEmpty()) {
										RunVanillaItemActivityProperties.this.activity.setParameters(result);
									}
									
									loadItem(item.getId(), item.getName());
								}
	
							}.getAsyncCallback());
						}
					}
				});
				dial.center();
			}
		});

		addCheckbox(Labels.lblCnst.Loop(), activity.isLoop(), loopChangeHandler);

		if (activity.getItemId() != null && activity.getItemId() > 0) {
			loadItem(activity.getItemId(), activity.getItemName());
		}
	}

	private void loadItem(int itemId, String itemName) {
		if (itemName != null) {
			txtItemName.setText(itemName);
		}
		
		if (parameterPanel != null) {
			parameterPanel.removeFromParent();
		}

		parameterPanel = new RunVanillaItemParameterPanel(RunVanillaItemActivityProperties.this.resourceManager, RunVanillaItemActivityProperties.this.activity);
		addPanel(parameterPanel);
	}

	private ChangeHandler changeServerVanilla = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			int cibleId = Integer.parseInt(lstServerVanillas.getValue(lstServerVanillas.getSelectedIndex()));
			if (cibleId > 0) {
				activity.setResource(findServerVanilla(cibleId));
			}
		}
	};

	private VanillaServer findServerVanilla(int cibleId) {
		if (serverVanillas != null) {
			for (VanillaServer cible : serverVanillas) {
				if (cible.getId() == cibleId) {
					return cible;
				}
			}
		}
		return null;
	}

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
			resourceManager.loadApplicationServers(RunVanillaItemActivityProperties.this, RunVanillaItemActivityProperties.this);
		}
	};

	@Override
	public void loadResources() {
	}

	@Override
	public boolean isValid() {
		return activity.getItemId() != null;
	}

	@Override
	public Activity buildItem() {
		return activity;
	}

	@Override
	public void loadResources(List<ApplicationServer> resources) {
		this.serverVanillas = resourceManager.getVanillaServers(resources);

		lstServerVanillas.clear();
		int selectedIndex = -1;
		if (serverVanillas != null) {
			int i = 0;
			for (VanillaServer item : serverVanillas) {
				lstServerVanillas.addItem(item.getName(), String.valueOf(item.getId()));

				if (activity.getResourceId() > 0 && activity.getResourceId() == item.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstServerVanillas.setSelectedIndex(selectedIndex);
	}

}
