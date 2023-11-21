package bpm.gwt.workflow.commons.client.resources.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.NameChanger;
import bpm.gwt.workflow.commons.client.NameChecker;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesText;
import bpm.gwt.workflow.commons.client.workflow.properties.VariablePropertiesText;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.workflow.commons.resources.Cible;
import bpm.workflow.commons.resources.Cible.TypeCible;
import bpm.workflow.commons.resources.attributes.FTPAction;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

public class CibleResourceProperties extends PropertiesPanel<Resource> implements NameChanger {

	private Cible cible;
	private IResourceManager resourceManager;

	private VariablePropertiesText txtUrl, txtHttpFileParam, txtPort, txtFolder;
	private PropertiesText txtLogin, txtPassword;
	private CheckBox checkSecured, checkOverride;
	private CheckBox checkNetwork;
	
	private SimplePanel panelActions;
	private FTPActionsPanel ftpActions;
	private CkanProperties ckanProperties;
	private D4CProperties d4cProperties;

	private boolean isNameValid = true;

	public CibleResourceProperties(NameChecker dialog, IResourceManager resourceManager, Cible myCible) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.SMALL_PX, myCible != null ? myCible.getId() : 0, myCible != null ? myCible.getName() : LabelsCommon.lblCnst.Cible(), true, true);
		this.cible = myCible != null ? myCible : new Cible(LabelsCommon.lblCnst.Cible());
		this.resourceManager = resourceManager;
		
		setNameChecker(dialog);
		setNameChanger(this);

		List<ListItem> items = new ArrayList<ListItem>();
		int i = 0;
		int selectedIndex = -1;
		for (TypeCible type : TypeCible.values()) {
			items.add(new ListItem(type.toString(), type.getType()));
			
			if (cible.getType() == type) {
				selectedIndex = i;
			}
			i++;
		}

		PropertiesListBox lstType = addList(null, items, WidgetWidth.SMALL_PX, changeTypeCible, null);
		if (selectedIndex != -1) {
			lstType.setSelectedIndex(selectedIndex);
		}
		
		txtUrl = addVariableText(LabelsCommon.lblCnst.URL(), cible.getUrlVS(), WidgetWidth.SMALL_PX, null);
		txtHttpFileParam = addVariableText(LabelsCommon.lblCnst.HttpFileParam(), cible.getHttpFileParamVS(), WidgetWidth.SMALL_PX, null);
		
		txtPort = addVariableText(LabelsCommon.lblCnst.Port(), cible.getPortVS(), WidgetWidth.PORT, null);
		txtFolder = addVariableText(LabelsCommon.lblCnst.StartFolder(), cible.getFolderVS(), WidgetWidth.SMALL_PX, null);

		checkOverride = addCheckbox(LabelsCommon.lblCnst.OverrideExistingFile(), cible.isOverride(), overrideChangeHandler);
		checkSecured = addCheckbox(LabelsCommon.lblCnst.Secured(), cible.isSecured(), securedChangeHandler);
		checkNetwork = addCheckbox(LabelsCommon.lblCnst.IsNetworkFolder(), cible.isNetworkFolder(), networkChangeHandler);

		txtLogin = addText(LabelsCommon.lblCnst.Login(), cible.getLogin(), WidgetWidth.SMALL_PX, false);
		txtPassword = addText(LabelsCommon.lblCnst.Password(), cible.getPassword(), WidgetWidth.SMALL_PX, true);

		panelActions = addSimplePanel(true);
		panelActions.add(ftpActions);

		if (!cible.isSecured() || !cible.isNetworkFolder()) {
			txtLogin.setEnabled(false);
			txtPassword.setEnabled(false);
		}
		
		checkName(getTxtName(), cible.getName());
		updateUi(cible.getType());
	}

	private void updateUi(TypeCible typeCible) {
		switch (typeCible) {
		case FTP:	
			checkOverride.setVisible(true);
			txtPort.setVisible(true);
			txtFolder.setVisible(true);
			checkSecured.setVisible(true);
			checkNetwork.setVisible(false);
			txtLogin.setVisible(true);
			txtPassword.setVisible(true);
			
			if (ftpActions == null) {
				ftpActions = new FTPActionsPanel(resourceManager, cible);
			}
			panelActions.setWidget(ftpActions);
			panelActions.setVisible(true);
			panelActions.addStyleName(getStyle().simplePanelSize());

			txtUrl.setVisible(true);
			txtHttpFileParam.setVisible(false);
			break;
		case SFTP:	
			checkOverride.setVisible(true);
			txtPort.setVisible(true);
			txtFolder.setVisible(true);
			checkSecured.setVisible(true);
			checkNetwork.setVisible(false);
			txtLogin.setVisible(true);
			txtPassword.setVisible(true);
			panelActions.setVisible(false);

			txtUrl.setVisible(true);
			txtHttpFileParam.setVisible(false);
			break;
		case HTTP:
			checkOverride.setVisible(false);
			txtPort.setVisible(false);
			txtFolder.setVisible(false);
			checkSecured.setVisible(false);
			checkNetwork.setVisible(false);
			txtLogin.setVisible(false);
			txtPassword.setVisible(false);
			panelActions.setVisible(false);

			txtUrl.setVisible(true);
			txtHttpFileParam.setVisible(true);
			break;
		case FOLDER:
			checkOverride.setVisible(true);
			txtFolder.setVisible(true);
			checkNetwork.setVisible(true);
			txtLogin.setVisible(true);
			txtPassword.setVisible(true);
			
			txtPort.setVisible(false);
			checkSecured.setVisible(false);
			panelActions.setVisible(false);
			txtHttpFileParam.setVisible(false);
			txtUrl.setVisible(false);
			break;
		case CKAN:
			checkOverride.setVisible(false);
			txtFolder.setVisible(false);
			checkNetwork.setVisible(false);
			txtLogin.setVisible(false);
			txtPassword.setVisible(false);
			
			txtPort.setVisible(false);
			checkSecured.setVisible(false);
			txtHttpFileParam.setVisible(false);
			txtUrl.setVisible(false);
			
			if (ckanProperties == null) {
				
				String url = cible != null ? cible.getUrlDisplay() : null;
				String org = cible != null ? cible.getOrg() : null;
				String apiKey = cible != null ? cible.getApiKey() : null;
				CkanPackage pack = cible != null ? cible.getCkanPackage() : null;
				
				ckanProperties = new CkanProperties(this, pack, url, org, apiKey, true);
			}
			panelActions.setWidget(ckanProperties);
			panelActions.setVisible(true);
			panelActions.removeStyleName(getStyle().simplePanelSize());
			
			break;
		case D4C:
			checkOverride.setVisible(false);
			txtFolder.setVisible(false);
			checkNetwork.setVisible(false);
			
			txtPort.setVisible(false);
			txtHttpFileParam.setVisible(false);
			txtUrl.setVisible(false);

			checkSecured.setVisible(true);
			txtLogin.setVisible(true);
			txtPassword.setVisible(true);
			
			if (d4cProperties == null) {
				
				String url = cible != null ? cible.getUrlDisplay() : null;
				String org = cible != null ? cible.getOrg() : null;
//				String apiKey = cible != null ? cible.getPassword() : null;
				CkanPackage pack = cible != null ? cible.getCkanPackage() : null;
				
				d4cProperties = new D4CProperties(this, pack, url, org, true);
			}
			panelActions.setWidget(d4cProperties);
			panelActions.setVisible(true);
			panelActions.removeStyleName(getStyle().simplePanelSize());
			
			break;

		default:
			break;
		}
	}

	private ChangeHandler changeTypeCible = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			int type = Integer.parseInt(lst.getValue(lst.getSelectedIndex()));
			
			cible.setType(TypeCible.valueOf(type));
			updateUi(cible.getType());
		}
	};

	private ValueChangeHandler<Boolean> securedChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			cible.setSecured(event.getValue());

			txtLogin.setEnabled(event.getValue());
			txtPassword.setEnabled(event.getValue());
		}
	};

	private ValueChangeHandler<Boolean> overrideChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			cible.setOverride(event.getValue());
		}
	};

	private ValueChangeHandler<Boolean> networkChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			cible.setNetworkFolder(event.getValue());

			txtLogin.setEnabled(event.getValue());
			txtPassword.setEnabled(event.getValue());
		}
	};

	@Override
	public boolean isValid() {
		boolean isValid = true;
		switch (cible.getType()) {
		case FTP:
		case SFTP:
			if (txtUrl.getText().isEmpty()) {
				isValid = false;
				txtUrl.setTxtError(LabelsCommon.lblCnst.UrlIsNeeded());
			}
			else {
				txtUrl.setTxtError("");
			}

			if (txtPort.getText().isEmpty()) {
				isValid = false;
				txtPort.setTxtError(LabelsCommon.lblCnst.PortIsNeeded());
			}
			else {
				txtPort.setTxtError("");
			}
			break;
		case HTTP:
			if (txtUrl.getText().isEmpty()) {
				isValid = false;
				txtUrl.setTxtError(LabelsCommon.lblCnst.UrlIsNeeded());
			}
			else {
				txtUrl.setTxtError("");
			}
			
			if (txtHttpFileParam.getText().isEmpty()) {
				isValid = false;
				txtHttpFileParam.setTxtError(LabelsCommon.lblCnst.HttpFileParamNameIsNeeded());
			}
			else {
				txtHttpFileParam.setTxtError("");
			}
			break;
		case FOLDER:
			if (txtFolder.getText().isEmpty()) {
				isValid = false;
				txtFolder.setTxtError(LabelsCommon.lblCnst.FolderPathIsNeeded());
			}
			else {
				txtFolder.setTxtError("");
			}
			break;
		case CKAN:
			if (ckanProperties == null || !ckanProperties.isValid()) {
				isValid = false;
			}
			break;
		case D4C:
			if (d4cProperties == null || !d4cProperties.isValid()) {
				isValid = false;
			}
			break;

		default:
			break;
		}
		return isNameValid && isValid;
	}

	@Override
	public Resource buildItem() {
		VariableString url = txtUrl.getVariableText();
		VariableString httpFileParam = txtHttpFileParam.getVariableText();
		VariableString port = txtPort.getVariableText();
		VariableString folder = txtFolder.getVariableText();
		
		boolean secured = checkSecured.getValue();
		boolean isNetworkFolder = checkNetwork.getValue();
		
		String login = txtLogin.getText();
		String password = txtPassword.getText();
		
		List<FTPAction> actions = ftpActions != null ? ftpActions.getActions() : null;
		
		String org = null;
		String apiKey = null;
		CkanPackage pack = null;
		if (cible.getType() == TypeCible.CKAN) {
			url.setString((ckanProperties != null ? ckanProperties.getUrl() : ""), null, null);
			org = ckanProperties != null ? ckanProperties.getOrg() : "";
			apiKey = ckanProperties != null ? ckanProperties.getApiKey() : "";
			pack = ckanProperties != null ? ckanProperties.getCkanPackage() : null;
		}
		else if (cible.getType() == TypeCible.D4C) {
			url.setString((d4cProperties != null ? d4cProperties.getUrl() : ""), null, null);
			org = d4cProperties != null ? d4cProperties.getOrg() : "";
			pack = d4cProperties != null ? d4cProperties.getCkanPackage() : null;
		}
		
//		String script = txtScript.getText();
//		String jointFile = txtFile.getText();

		cible.setUrl(url);
		cible.setHttpFileParam(httpFileParam);
		cible.setPort(port);
		cible.setFolder(folder);
		cible.setSecured(secured);
		cible.setNetworkFolder(isNetworkFolder);
		cible.setLogin(login);
		cible.setPassword(password);
		cible.setActions(actions);
		cible.setCkanPackage(pack);
		cible.setOrg(org);
		cible.setApiKey(apiKey);
		
		return cible;
	}

	@Override
	public void changeName(String value, boolean isValid) {
		this.isNameValid = isValid;
		cible.setName(value);
	}
}
