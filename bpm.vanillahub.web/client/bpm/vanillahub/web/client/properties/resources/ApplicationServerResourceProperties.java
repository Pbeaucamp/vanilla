package bpm.vanillahub.web.client.properties.resources;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.NameChanger;
import bpm.gwt.workflow.commons.client.NameChecker;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesButton;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesText;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.resources.AklaboxServer;
import bpm.vanillahub.core.beans.resources.ApplicationServer;
import bpm.vanillahub.core.beans.resources.ApplicationServer.TypeServer;
import bpm.vanillahub.core.beans.resources.LimeSurveyServer;
import bpm.vanillahub.core.beans.resources.VanillaServer;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.services.ResourcesService;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class ApplicationServerResourceProperties extends PropertiesPanel<Resource> implements NameChanger {

	private PropertiesText txtUrl, txtUser, txtPassword;
	private PropertiesButton btnConnection;
	private PropertiesListBox lstTypeServer, lstGroups, lstRepositories;

//	private ApplicationServer appServer;
	
	private String appName;
	private boolean edit;
	private boolean isNameValid = true;
	
	public ApplicationServerResourceProperties(NameChecker resourceDialog, IResourceManager resourceManager, final ApplicationServer appServer) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.LARGE_PX, appServer != null ? appServer.getId() : 0, appServer != null ? appServer.getName() : "", false, true);
		
		this.edit = appServer != null;
		this.appName = appServer != null ? appServer.getName() : null;
//		this.appServer = appServer;

		setNameChecker(resourceDialog);
		setNameChanger(this);
		
		txtUrl = addText(LabelsCommon.lblCnst.URL(), appServer != null && appServer.getUrlVS() != null ? appServer.getUrlDisplay() : "", WidgetWidth.LARGE_PX, false);
		txtUser = addText(LabelsCommon.lblCnst.Login(), appServer != null && appServer.getLoginVS() != null ? appServer.getLoginDisplay() : "", WidgetWidth.LARGE_PX, false);
		txtPassword = addText(LabelsCommon.lblCnst.Password(), appServer != null && appServer.getPasswordVS() != null ? appServer.getPasswordDisplay() : "", WidgetWidth.LARGE_PX, true);

		List<ListItem> types = new ArrayList<PropertiesPanel<Resource>.ListItem>();
		types.add(new ListItem(Labels.lblCnst.VanillaServers(), TypeServer.VANILLA.getType()));
		types.add(new ListItem(Labels.lblCnst.AklaboxServers(), TypeServer.AKLABOX.getType()));
		types.add(new ListItem(Labels.lblCnst.LimeSurveyServers(), TypeServer.LIMESURVEY.getType()));
		
		lstTypeServer = addList(Labels.lblCnst.ServerType(), types, WidgetWidth.LARGE_PX, new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateUi();
			}
		}, null);
		if (appServer != null) {
			lstTypeServer.setSelectedIndex(appServer.getTypeServer().getType());
		}
		
		btnConnection = addButton(Labels.lblCnst.VanillaConnection(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ResourcesService.Connect.getInstance().getVanillaGroups(txtUrl.getText(), txtUser.getText(), txtPassword.getText(), new GwtCallbackWrapper<List<Group>>(ApplicationServerResourceProperties.this, edit) {
					@Override
					public void onSuccess(List<Group> result) {
						lstGroups.clear();
//						boolean first = true;
						for(Group group : result) {
//							if(first) {
//								ApplicationServerResourceProperties.this.appServer.setGroupId(new VariableString(group.getId()));
//								ApplicationServerResourceProperties.this.appServer.setGroupName(group.getName());
//								first = false;
//							}
							lstGroups.addItem(group.getName(), group.getId() + "");
						}
					}
				}.getAsyncCallback());
				ResourcesService.Connect.getInstance().getVanillaRepositories(txtUrl.getText(), txtUser.getText(), txtPassword.getText(), new GwtCallbackWrapper<List<Repository>>(ApplicationServerResourceProperties.this, edit) {
					@Override
					public void onSuccess(List<Repository> result) {
						lstRepositories.clear();
//						boolean first = true;
						for(Repository repo : result) {
//							if(first) {
//								ApplicationServerResourceProperties.this.appServer.setRepositoryId(new VariableString(repo.getId()));
//								ApplicationServerResourceProperties.this.appServer.setRepositoryName(repo.getName());
//								first = false;
//							}
							lstRepositories.addItem(repo.getName(), repo.getId() + "");
						}
					}
				}.getAsyncCallback());
			}
		});
		
		lstGroups = addList(Labels.lblCnst.Group(), new ArrayList<ListItem>(), WidgetWidth.LARGE_PX, null, null);
		lstRepositories = addList(Labels.lblCnst.Repository(), new ArrayList<ListItem>(), WidgetWidth.LARGE_PX, null, null);
		
		updateUi();
	}

	private void updateUi() {
		TypeServer selectedType = TypeServer.valueOf(Integer.parseInt(lstTypeServer.getValue()));
		
		btnConnection.setVisible(selectedType == TypeServer.VANILLA);
		lstGroups.setVisible(selectedType == TypeServer.VANILLA);
		lstRepositories.setVisible(selectedType == TypeServer.VANILLA);
	}

	@Override
	public void changeName(String value, boolean isValid) {
		this.isNameValid = isValid;
		this.appName = value;
	}

	@Override
	public Resource buildItem() {
		ApplicationServer appServer = null;
		
		TypeServer selectedType = TypeServer.valueOf(Integer.parseInt(lstTypeServer.getValue()));
		switch (selectedType) {
		case VANILLA:
			VariableString groupId = new VariableString(lstGroups.getValue(lstGroups.getSelectedIndex()));
			String groupName = lstGroups.getListBox().getItemText(lstGroups.getSelectedIndex());
			
			VariableString repositoryId = new VariableString(lstRepositories.getValue(lstRepositories.getSelectedIndex()));
			String repositoryName = lstRepositories.getListBox().getItemText(lstRepositories.getSelectedIndex());
			
			VanillaServer vanillaServer = new VanillaServer();
			vanillaServer.setGroupId(groupId);
			vanillaServer.setGroupName(groupName);
			
			vanillaServer.setRepositoryId(repositoryId);
			vanillaServer.setRepositoryName(repositoryName);
			
			appServer = vanillaServer;
			break;
		case AKLABOX:
			AklaboxServer aklaboxServer = new AklaboxServer();
			
			appServer = aklaboxServer;
			break;
		case LIMESURVEY:
			LimeSurveyServer limeSurveyServer = new LimeSurveyServer();
			
			appServer = limeSurveyServer;
			break;

		default:
			break;
		}

		appServer.setId(getId());
		appServer.setName(appName);
		appServer.setUrl(new VariableString(txtUrl.getText()));
		appServer.setLogin(new VariableString(txtUser.getText()));
		appServer.setPassword(new VariableString(txtPassword.getText()));
		
		return appServer;
	}

	@Override
	public boolean isValid() {
		boolean commonInfoValid = isNameValid && txtUrl.getText() != null && !txtUrl.getText().isEmpty() && txtUser.getText() != null && !txtUser.getText().isEmpty();
		
		boolean complementaryValid = true;
		
		TypeServer selectedType = TypeServer.valueOf(Integer.parseInt(lstTypeServer.getValue()));
		if (selectedType == TypeServer.VANILLA) {
			VariableString groupId = new VariableString(lstGroups.getValue(lstGroups.getSelectedIndex()));
			VariableString repositoryId = new VariableString(lstRepositories.getValue(lstRepositories.getSelectedIndex()));
			complementaryValid = groupId != null && repositoryId != null;
		}
		
		return commonInfoValid && complementaryValid;
	}
}
