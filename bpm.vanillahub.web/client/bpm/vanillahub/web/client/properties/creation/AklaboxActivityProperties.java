package bpm.vanillahub.web.client.properties.creation;

import java.util.ArrayList;
import java.util.List;

import bpm.document.management.core.model.IObject.ItemTreeType;
import bpm.gwt.aklabox.commons.client.dialogs.AklaboxDialog;
import bpm.gwt.aklabox.commons.shared.AklaboxConnection;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesTextBrowse;
import bpm.gwt.workflow.commons.client.workflow.properties.VariablePropertiesText;
import bpm.vanillahub.core.beans.activities.AklaboxActivity;
import bpm.vanillahub.core.beans.activities.AklaboxActivity.AklaboxApp;
import bpm.vanillahub.core.beans.resources.AklaboxServer;
import bpm.vanillahub.core.beans.resources.ApplicationServer;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.properties.parameters.AklaboxDispatchPanel;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class AklaboxActivityProperties extends PropertiesPanel<Activity> implements IManager<ApplicationServer> {

	private ResourceManager resourceManager;
	private BoxItem item;
	private AklaboxActivity activity;

	private List<AklaboxServer> servers;
	private PropertiesListBox lstServers, lstTypeApp;

	private PropertiesTextBrowse txtItemName;
	private SimplePanel panelRules;
	private AklaboxDispatchPanel dispatchPanel;
	private VariablePropertiesText txtProjectName;
	private PropertiesListBox lstLanguages;
	private CheckBox chkOcr;

	public AklaboxActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, final AklaboxActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), true, false);
		this.resourceManager = (ResourceManager) resourceManager;
		this.item = item;
		this.activity = activity;

		setNameChecker(creationPanel);
		setNameChanger(item);

		this.servers = this.resourceManager.getAklaboxServers(this.resourceManager.getApplicationServers());

		List<ListItem> items = new ArrayList<ListItem>();
		int selectedIndex = -1;
		if (servers != null) {
			int i = 0;
			for (AklaboxServer server : servers) {
				items.add(new ListItem(server.getName(), server.getId()));

				if (activity.getResourceId() > 0 && activity.getResourceId() == server.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstServers = addList(Labels.lblCnst.SelectAklaboxServer(), items, WidgetWidth.PCT, changeServer, refreshHandler);
		lstServers.setSelectedIndex(selectedIndex);

		List<ListItem> types = new ArrayList<ListItem>();
		types.add(new ListItem(Labels.lblCnst.Aklabox(), AklaboxApp.AKLABOX.getType()));
		types.add(new ListItem(Labels.lblCnst.Aklad(), AklaboxApp.AKLAD.getType()));

		lstTypeApp = addList(Labels.lblCnst.ServerType(), types, WidgetWidth.LARGE_PX, new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				updateUi();
			}
		}, null);

		txtItemName = addBrowse("", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				AklaboxConnection server = getAklaboxConnection();
				Integer selectedItem = activity.getItemId();

				final AklaboxDialog dial = new AklaboxDialog(server, server.getLogin(), ItemTreeType.ENTERPRISE, selectedItem);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						AklaboxActivityProperties.this.activity.setItemId(dial.getSelectedItem().getId());
						AklaboxActivityProperties.this.activity.setItemName(dial.getSelectedItem().getName());

						txtItemName.setText(dial.getSelectedItem().getName());
					}
				});
				dial.center();
			}
		});
		txtItemName.setText(activity != null && activity.getItemName() != null ? activity.getItemName() : "");

		dispatchPanel = new AklaboxDispatchPanel(this, activity);
		panelRules = addSimplePanel(false);
		panelRules.setWidget(dispatchPanel);
		
		txtProjectName = addVariableText(Labels.lblCnst.ProjectName(), activity.getProjectNameVS(), WidgetWidth.PCT, null);
		lstLanguages = addList(Labels.lblCnst.Language(), new ArrayList<ListItem>(), WidgetWidth.LARGE_PX, changeLanguageHandler, refreshLanguageHandler);
		chkOcr = addCheckbox(Labels.lblCnst.RunOcr(), activity.isRunOcr(), ocrChangeHandler);

		addCheckbox(Labels.lblCnst.Loop(), activity.isLoop(), loopChangeHandler);

		if (activity != null && activity.getApp() != null) {
			lstTypeApp.setSelectedIndex(activity.getApp().getType());

			refreshLang();
		}

		updateUi();
	}

	private void updateUi() {
		AklaboxApp selectedApp = AklaboxApp.valueOf(Integer.parseInt(lstTypeApp.getValue()));

		txtItemName.setVisible(selectedApp == AklaboxApp.AKLABOX);
		panelRules.setVisible(selectedApp == AklaboxApp.AKLABOX);
		
		txtProjectName.setVisible(selectedApp == AklaboxApp.AKLAD);
		chkOcr.setVisible(selectedApp == AklaboxApp.AKLAD);
		lstLanguages.setVisible(selectedApp == AklaboxApp.AKLAD);
	}

	private ChangeHandler changeServer = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			int cibleId = Integer.parseInt(lstServers.getValue(lstServers.getSelectedIndex()));
			if (cibleId > 0) {
				activity.setResource(findServer(cibleId));

				refreshLang();
			}
		}
	};

	private AklaboxServer findServer(int serverId) {
		if (servers != null) {
			for (AklaboxServer server : servers) {
				if (server.getId() == serverId) {
					return server;
				}
			}
		}
		return null;
	}

	public AklaboxConnection getAklaboxConnection() {
		if (lstServers.getSelectedIndex() < 0) {
			return null;
		}
		
		AklaboxServer server = servers.get(lstServers.getSelectedIndex());
		if (server != null) {
			return new AklaboxConnection(server.getUrlDisplay(), server.getLoginDisplay(), server.getPasswordDisplay());
		}
		return null;
	}

	private ChangeHandler changeLanguageHandler = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			String lang = lstLanguages.getValue(lstLanguages.getSelectedIndex());
			activity.setLang(lang);
		}
	};

	private ClickHandler refreshLanguageHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			refreshLang();
		}
	};

	private void refreshLang() {
		int cibleId = Integer.parseInt(lstServers.getValue(lstServers.getSelectedIndex()));
		if (cibleId > 0) {
			ResourcesService.Connect.getInstance().getAvailableLangs(findServer(cibleId), new GwtCallbackWrapper<List<String>>(this, true, true) {

				@Override
				public void onSuccess(List<String> result) {
					List<ListItem> langs = new ArrayList<ListItem>();
					int index = 0;
					int selectedIndex = -1;
					if (result != null) {
						for (String lang : result) {
							langs.add(new ListItem(lang, lang));

							if (activity.getLang() != null && activity.getLang().equals(lang)) {
								selectedIndex = index;
							}

							index++;
						}
					}
					lstLanguages.clear();
					lstLanguages.setItems(langs);

					if (selectedIndex > -1) {
						lstLanguages.setSelectedIndex(selectedIndex);
					}
				}
			}.getAsyncCallback());
		}
	}

	private ClickHandler refreshHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			resourceManager.loadApplicationServers(AklaboxActivityProperties.this, AklaboxActivityProperties.this);
		}
	};

	private ValueChangeHandler<Boolean> ocrChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			activity.setRunOcr(event.getValue());
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
		return activity.getItemId() != null;
	}

	@Override
	public Activity buildItem() {
		AklaboxApp selectedApp = AklaboxApp.valueOf(Integer.parseInt(lstTypeApp.getValue()));
		this.activity.setApp(selectedApp);

		return activity;
	}

	@Override
	public void loadResources(List<ApplicationServer> resources) {
		this.servers = resourceManager.getAklaboxServers(resources);

		lstServers.clear();
		int selectedIndex = -1;
		if (servers != null) {
			int i = 0;
			for (AklaboxServer item : servers) {
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
