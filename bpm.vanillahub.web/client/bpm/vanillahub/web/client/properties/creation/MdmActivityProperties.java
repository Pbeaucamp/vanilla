package bpm.vanillahub.web.client.properties.creation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;

import bpm.gwt.commons.client.meta.MetasPanel;
import bpm.gwt.commons.shared.VanillaServerInformations;
import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesTextBrowse;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.platform.core.beans.meta.MetaLink.TypeMetaLink;
import bpm.vanillahub.core.beans.activities.MdmActivity;
import bpm.vanillahub.core.beans.activities.attributes.MetaDispatch;
import bpm.vanillahub.core.beans.resources.ApplicationServer;
import bpm.vanillahub.core.beans.resources.Source;
import bpm.vanillahub.core.beans.resources.Source.TypeSource;
import bpm.vanillahub.core.beans.resources.VanillaServer;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.dialogs.MdmContractDialog;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;
import bpm.workflow.commons.beans.Activity;

public class MdmActivityProperties extends PropertiesPanel<Activity> implements IManager<ApplicationServer> {

	private static final String KEY_RADIO = "RadioIsFromMeta";

	private ResourceManager resourceManager;
	private BoxItem item;
	private MdmActivity activity;

	private RadioButton radioIsFromMeta;
	private CheckBox chkValidateData;
	private List<VanillaServer> serverVanillas;
	private PropertiesListBox lstServerVanillas;
	private List<Source> resources;

	private PropertiesTextBrowse txtContractName;
	private SourceManager sourceManager;
	private PropertiesListBox lstResources;
	
	private SimplePanel panelMeta;
	private bpm.vanillahub.web.client.properties.parameters.MetasPanel fromMetaPanel;

	public MdmActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, MdmActivity activity) {
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
		
		addRadioButton(KEY_RADIO, Labels.lblCnst.SelectContract(), !activity.isFromMeta(), isFromMetaHandler);
		
		txtContractName = addBrowse("", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final MdmContractDialog dial = new MdmContractDialog(getVanillaServer());
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						Contract contract = dial.getSelectedItem();
						
						MdmActivityProperties.this.activity.setContractId(contract.getId());
						MdmActivityProperties.this.activity.setContractName(contract.getName());
						txtContractName.setText(dial.getSelectedItem().getName());
						
						updateUi(activity.isFromMeta());
					}
				});
				dial.center();
			}
		});
		txtContractName.setText(activity.getContractName() != null ? activity.getContractName() : "");
		
		radioIsFromMeta = addRadioButton(KEY_RADIO, Labels.lblCnst.FilterContractByMetadata(), activity.isFromMeta(), isFromMetaHandler);
		chkValidateData = addCheckbox(Labels.lblCnst.ValidateData(), activity.validateData(), validateDataHandler);
		panelMeta = addSimplePanel(false);
		
		this.sourceManager = new SourceManager();
		this.resources = this.resourceManager.getSources();

		List<ListItem> itemsSources = new ArrayList<ListItem>();
		selectedIndex = -1;
		if (resources != null) {
			int i = 0;
			for (Source resource : resources) {
				if (resource.getType() == TypeSource.FOLDER || resource.getType() == TypeSource.FTP || resource.getType() == TypeSource.SFTP) {
					itemsSources.add(new ListItem(resource.getName(), resource.getId()));
	
					if (activity.getManageFileSourceId() > 0 && activity.getManageFileSourceId() == resource.getId()) {
						selectedIndex = i;
					}
					i++;
				}
			}
		}

		Label lblManageFiles = addLabel();
		lblManageFiles.setText(Labels.lblCnst.ManageTreatedFilesMessage());
		lstResources = addList(Labels.lblCnst.SelectSource(), itemsSources, WidgetWidth.PCT, changeSource, refreshSourceHandler);
		lstResources.setSelectedIndex(selectedIndex);
		
		addCheckbox(Labels.lblCnst.Loop(), activity.isLoop(), loopChangeHandler);
		
		updateUi(activity.isFromMeta());
	}

	protected void updateUi(boolean isFromMeta) {
		VanillaServer server = getVanillaServer();

		if (server != null && isFromMeta) {
			if (fromMetaPanel == null) {
				fromMetaPanel = new bpm.vanillahub.web.client.properties.parameters.MetasPanel(this, getServerInformations(server), 1, activity.getMetaDispatch(), resourceManager.getVariables(), resourceManager.getParameters());
			}
			panelMeta.setWidget(fromMetaPanel);
		}
		else if (server != null && activity.getContractId() > 0) {
			loadMeta(activity.getContractId());
		}
		else {
			panelMeta.clear();
		}
	}

	private VanillaServer getVanillaServer() {
		if (serverVanillas != null && !serverVanillas.isEmpty() && lstServerVanillas.getSelectedIndex() >= 0) {
			return serverVanillas.get(lstServerVanillas.getSelectedIndex());
		}
		return null;
	}

	private void loadMeta(int contractId) {
		VanillaServer server = getVanillaServer();

		MetasPanel metaPanel = new MetasPanel(this, getServerInformations(server), contractId, TypeMetaLink.ARCHITECT, 1, true);
		panelMeta.setWidget(metaPanel);
	}

	private VanillaServerInformations getServerInformations(VanillaServer server) {
		String url = server.getUrlDisplay();
		String login = server.getLoginDisplay();
		String password = server.getPasswordDisplay();
		int groupId = Integer.parseInt(server.getGroupId().getStringForTextbox());
		int repoId = Integer.parseInt(server.getGroupId().getStringForTextbox());

		return new VanillaServerInformations(url, login, password, groupId, repoId);
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

	private ChangeHandler changeSource = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			int sourceId = Integer.parseInt(lstResources.getValue(lstResources.getSelectedIndex()));
			if (sourceId > 0) {
				activity.setManageFileSource(findSource(sourceId));
			}
		}
	};
	
	private ValueChangeHandler<Boolean> isFromMetaHandler = new ValueChangeHandler<Boolean>() {
		
		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			activity.setFromMeta(radioIsFromMeta.getValue());
			updateUi(radioIsFromMeta.getValue());
		}
	};
	
	private ValueChangeHandler<Boolean> validateDataHandler = new ValueChangeHandler<Boolean>() {
		
		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			activity.setValidateData(chkValidateData.getValue());
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

	private Source findSource(int sourceId) {
		if (resources != null) {
			for (Source source : resources) {
				if (source.getId() == sourceId) {
					return source;
				}
			}
		}
		return null;
	}

	private ClickHandler refreshHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			resourceManager.loadApplicationServers(MdmActivityProperties.this, MdmActivityProperties.this);
		}
	};

	private ClickHandler refreshSourceHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			resourceManager.loadSources(MdmActivityProperties.this, sourceManager);
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
		return activity.getContractId() > 0;
	}

	@Override
	public Activity buildItem() {
		if (activity.isFromMeta()) {
			List<MetaDispatch> metaDispatch = fromMetaPanel.getMetaDispatch();
			activity.setMetaDispatch(metaDispatch);
		}
		
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

	private class SourceManager implements IManager<Source> {

		@Override
		public void loadResources() {
		}

		@Override
		public void loadResources(List<Source> resources) {
			MdmActivityProperties.this.resources = resources;
			
			lstResources.clear();
			int selectedSourceIndex = -1;
			if (MdmActivityProperties.this.resources != null) {
				int i = 0;
				for (Source source : MdmActivityProperties.this.resources) {
					lstResources.addItem(source.getName(), String.valueOf(source.getId()));

					if (activity.getManageFileSourceId() > 0 && activity.getManageFileSourceId() == source.getId()) {
						selectedSourceIndex = i;
					}
					i++;
				}
			}

			lstResources.setSelectedIndex(selectedSourceIndex);
		}
		
	}
}
