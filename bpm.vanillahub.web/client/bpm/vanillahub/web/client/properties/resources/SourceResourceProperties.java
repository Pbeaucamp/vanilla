package bpm.vanillahub.web.client.properties.resources;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.NameChanger;
import bpm.gwt.workflow.commons.client.NameChecker;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.resources.properties.CkanProperties;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesText;
import bpm.gwt.workflow.commons.client.workflow.properties.VariablePropertiesText;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.resources.Source;
import bpm.vanillahub.core.beans.resources.Source.TypeSource;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceMethodDefinition;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceParameter;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.properties.parameters.WebServiceParametersPanel;
import bpm.vanillahub.web.client.services.ResourcesService;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

public class SourceResourceProperties extends PropertiesPanel<Resource> implements NameChanger {

	private ResourceManager resourceManager;
	private Source source;

	private CheckBox checkNetwork, checkSubfolder, checkSecured, checkOutputName, checkRejectNonMatchingMail, checkCopy;
	private VariablePropertiesText txtUrlProtocol, txtPort, txtFolderPath, txtTreatedFolderPath, txtRejectedFolderPath, txtFilter, txtAttachmentFilter;
	private PropertiesText txtLogin, txtPassword;
	private Label lblMessageManageFolder;
	
	private VariablePropertiesText txtOutputName, txtWebServiceUrl;
	private PropertiesListBox /*lstScanners,*/ lstMethods;
	private WebServiceParametersPanel parameters;
	private SimplePanel panelParameters;
	private CkanProperties ckanProperties;

	private List<WebServiceMethodDefinition> methods;
	private boolean isNameValid = true;

	public SourceResourceProperties(NameChecker dialog, IResourceManager resourceManager, Source mySource) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.SMALL_PX, mySource != null ? mySource.getId() : 0, mySource != null ? mySource.getName() : Labels.lblCnst.Source(), true, true);
		this.source = mySource != null ? mySource : new Source(Labels.lblCnst.Source());
		this.resourceManager = (ResourceManager) resourceManager;
		
		setNameChecker(dialog);
		setNameChanger(this);

		List<ListItem> items = new ArrayList<ListItem>();
		int i = 0;
		int selectedIndex = -1;
		for (TypeSource type : TypeSource.values()) {
			items.add(new ListItem(type.toString(), type.getType()));

			if (source.getType() == type) {
				selectedIndex = i;
			}
			i++;
		}

		if (Source.FOLDER_AVAILABLE) {
			PropertiesListBox lstType = addList(null, items, WidgetWidth.SMALL_PX, changeTypeSource, null);
			if (selectedIndex != -1) {
				lstType.setSelectedIndex(selectedIndex);
			}
		}

		txtUrlProtocol = addVariableText(LabelsCommon.lblCnst.URLOrProtocol(), source.getUrlVS(), WidgetWidth.SMALL_PX, null);
		txtPort = addVariableText(LabelsCommon.lblCnst.Port(), source.getPortVS(), WidgetWidth.SMALL_PX, null);
		
//		lstScanners = addList(Labels.lblCnst.Scanner(), new ArrayList<ListItem>(), WidgetWidth.SMALL_PX, null, null);
		
		txtFolderPath = addVariableText(Labels.lblCnst.FolderPath(), source.getFolderPathVS(), WidgetWidth.SMALL_PX, null);
		txtFilter = addVariableText(Labels.lblCnst.Filter(), source.getFilterVS(), WidgetWidth.SMALL_PX, null);
		txtAttachmentFilter = addVariableText(LabelsCommon.lblCnst.AttachmentFilter(), source.getAttachmentFilterVS(), WidgetWidth.SMALL_PX, null);
		checkNetwork = addCheckbox(LabelsCommon.lblCnst.IsNetworkFolder(), source.isNetworkFolder(), networkHandler);
		checkSubfolder = addCheckbox(Labels.lblCnst.IncludeSubfolder(), source.includeSubfolder(), subfolderHandler);

		checkSecured = addCheckbox(LabelsCommon.lblCnst.Secured(), source.isSecured(), securedHandler);
		
		txtLogin = addText(LabelsCommon.lblCnst.Login(), source.getLogin(), WidgetWidth.SMALL_PX, false);
		txtPassword = addText(LabelsCommon.lblCnst.Password(), source.getPassword(), WidgetWidth.SMALL_PX, true);

		txtOutputName = addVariableText(LabelsCommon.lblCnst.OutputFileName(), source.getOutputNameVS(), WidgetWidth.SMALL_PX, null);
		checkOutputName = addCheckbox(LabelsCommon.lblCnst.UseOutputName(), source.useOutputName(), useOutputHandler);
		txtWebServiceUrl = addVariableText(LabelsCommon.lblCnst.URL(), source.getUrlVS(), WidgetWidth.SMALL_PX, webServiceHandler);
		lstMethods = addList(null, new ArrayList<ListItem>(), WidgetWidth.SMALL_PX, methodHandler, null);

		panelParameters = addSimplePanel(true);
		
		lblMessageManageFolder = addLabel();
		lblMessageManageFolder.setText(Labels.lblCnst.ManageOptionFilesMessage());
		txtTreatedFolderPath = addVariableText(Labels.lblCnst.TreatedFolderPath(), source.getTreatedFolderPathVS(), WidgetWidth.SMALL_PX, null);
		checkRejectNonMatchingMail = addCheckbox(Labels.lblCnst.RejectNonMatchingFile(), source.isRejectNonMatchingMail(), rejectNonMatchingHandler);
		txtRejectedFolderPath = addVariableText(Labels.lblCnst.RejectedFolderPath(), source.getRejectedFolderPathVS(), WidgetWidth.SMALL_PX, null);
		checkCopy = addCheckbox(Labels.lblCnst.CheckToCopyTheFileInsteadOfMovingIt(), source.isCopyFile(), copyHandler);
		
		checkName(getTxtName(), source.getName());
		updateUi(source.getType());

		if (source.getMethod() != null) {
			methods = new ArrayList<WebServiceMethodDefinition>();
			methods.add(source.getMethod());

			lstMethods.addItem(source.getMethod().getName());
			loadParameters(source.getMethod());
		}
	}

	private void updateUi(TypeSource type) {
		txtFolderPath.setVisible(type == TypeSource.FOLDER || type == TypeSource.FTP || type == TypeSource.SFTP || type == TypeSource.MAIL);
		txtFilter.setVisible(type == TypeSource.FOLDER || type == TypeSource.FTP || type == TypeSource.SFTP || type == TypeSource.MAIL);
		checkNetwork.setVisible(type == TypeSource.FOLDER);
		checkSubfolder.setVisible(type == TypeSource.FOLDER || type == TypeSource.SFTP);

		txtUrlProtocol.setVisible(type == TypeSource.FTP || type == TypeSource.SFTP || type == TypeSource.MAIL);
		txtAttachmentFilter.setVisible(type == TypeSource.MAIL);
		checkSecured.setVisible(type == TypeSource.FTP || type == TypeSource.SFTP);

		txtPort.setVisible(type == TypeSource.FTP || type == TypeSource.SFTP);
		txtWebServiceUrl.setVisible(type == TypeSource.WEB_SERVICE);
		lstMethods.setVisible(type == TypeSource.WEB_SERVICE);
		panelParameters.setVisible(false);

		txtOutputName.setVisible(type == TypeSource.FOLDER || type == TypeSource.WEB_SERVICE || type == TypeSource.FTP || type == TypeSource.SFTP || type == TypeSource.D4C || type == TypeSource.MAIL);
		checkOutputName.setVisible(type == TypeSource.FOLDER || type == TypeSource.WEB_SERVICE || type == TypeSource.FTP || type == TypeSource.SFTP || type == TypeSource.D4C || type == TypeSource.MAIL);
		
		txtLogin.setVisible((source.isNetworkFolder() && type == TypeSource.FOLDER) || (source.isSecured() && (type == TypeSource.FTP || type == TypeSource.SFTP)) || type == TypeSource.MAIL);
		txtPassword.setVisible((source.isNetworkFolder() && type == TypeSource.FOLDER) || (source.isSecured() && (type == TypeSource.FTP || type == TypeSource.SFTP)) || type == TypeSource.MAIL);

		lblMessageManageFolder.setVisible(type == TypeSource.MAIL || type == TypeSource.FOLDER || type == TypeSource.SFTP);
		txtTreatedFolderPath.setVisible(type == TypeSource.MAIL || type == TypeSource.FOLDER || type == TypeSource.SFTP);
		checkRejectNonMatchingMail.setVisible(type == TypeSource.MAIL || type == TypeSource.FOLDER || type == TypeSource.SFTP);
		txtRejectedFolderPath.setVisible(source.isRejectNonMatchingMail() && (type == TypeSource.MAIL || type == TypeSource.FOLDER || type == TypeSource.SFTP));
		checkCopy.setVisible(type == TypeSource.MAIL || type == TypeSource.FOLDER || type == TypeSource.SFTP);
		
		if (type == TypeSource.WEB_SERVICE) {
			panelParameters.clear();
			panelParameters.setVisible(true);
			panelParameters.addStyleName(getStyle().simplePanelSize());
		}
		else if (type == TypeSource.D4C) {
			if (ckanProperties == null) {
				
				String url = source != null ? source.getUrlDisplay() : null;
				String org = source != null ? source.getLogin() : null;
				String apiKey = source != null ? source.getPassword() : null;
				CkanPackage pack = source != null ? source.getCkanPackage() : null;
				
				ckanProperties = new CkanProperties(this, pack, url, org, apiKey, false);
			}
			panelParameters.setWidget(ckanProperties);
			panelParameters.setVisible(true);
			panelParameters.removeStyleName(getStyle().simplePanelSize());
		}
	}

	private WebServiceMethodDefinition findSelectedMethod(String value) {
		if (methods != null && value != null && !value.isEmpty()) {
			for (WebServiceMethodDefinition method : methods) {
				if (method.getName().equals(value)) {
					return method;
				}
			}
		}
		return null;
	}

	private ValueChangeHandler<Boolean> networkHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			source.setNetworkFolder(event.getValue());
			updateUi(source.getType());
		}
	};

	private ValueChangeHandler<Boolean> rejectNonMatchingHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			source.setRejectNonMatchingMail(event.getValue());
			updateUi(source.getType());
		}
	};

	private ValueChangeHandler<Boolean> copyHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			source.setCopyFile(event.getValue());
		}
	};

	private ValueChangeHandler<Boolean> useOutputHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			source.setUseOutputName(event.getValue());
		}
	};

	private ValueChangeHandler<Boolean> subfolderHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			source.setIncludeSubfolder(event.getValue());
		}
	};

	private ValueChangeHandler<Boolean> securedHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			source.setSecured(event.getValue());
			updateUi(source.getType());
		}
	};

	private ChangeHandler changeTypeSource = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			int type = Integer.parseInt(lst.getValue(lst.getSelectedIndex()));

			source.setType(TypeSource.valueOf(type));
			updateUi(source.getType());
		}
	};

	private ClickHandler webServiceHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			VariableString value = txtWebServiceUrl.getVariableText();
			if (value.getStringForTextbox().isEmpty()) {
				txtWebServiceUrl.setTxtError(Labels.lblCnst.WebServiceUrlEmpty());
				return;
			}

			showWaitPart(true);

			lstMethods.clear();
			panelParameters.clear();

			ResourcesService.Connect.getInstance().getWebServiceMethods(value, new GwtCallbackWrapper<List<WebServiceMethodDefinition>>(SourceResourceProperties.this, true) {

				@Override
				public void onSuccess(List<WebServiceMethodDefinition> methods) {
					SourceResourceProperties.this.methods = methods;

					if (methods != null) {
						int i = 0;
						for (WebServiceMethodDefinition method : methods) {
							lstMethods.addItem(method.getName());

							if (source != null && source.getMethod() != null && source.getMethod().getName().equals(method.getName())) {
								lstMethods.setSelectedIndex(i);

								// setting previously set parameters
								if (method.getParameters() != null && source.getMethod().getParameters() != null) {
									for (WebServiceParameter param : method.getParameters()) {
										for (WebServiceParameter tmp : source.getMethod().getParameters()) {
											if (param.getName().equals(tmp.getName())) {
												param.setValue(tmp.getParameterValue());
												break;
											}
										}
									}
								}
							}
							i++;
						}

						NativeEvent event = Document.get().createChangeEvent();
						ChangeEvent.fireNativeEvent(event, lstMethods.getListBox());
					}
				}
			}.getAsyncCallback());
		}
	};

	private ChangeHandler methodHandler = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox source = (ListBox) event.getSource();
			String value = source.getValue(source.getSelectedIndex());

			WebServiceMethodDefinition selectedMethod = findSelectedMethod(value);
			if (selectedMethod != null) {
				loadParameters(selectedMethod);
			}
		}
	};

	private void loadParameters(WebServiceMethodDefinition selectedMethod) {
		parameters = new WebServiceParametersPanel(resourceManager, selectedMethod);
		panelParameters.setWidget(parameters);
	}

	@Override
	public boolean isValid() {
		boolean isValid = true;
		if (source.getType() == TypeSource.WEB_SERVICE) {
			if (txtOutputName.getText().isEmpty()) {
				isValid = false;
				txtOutputName.setTxtError(Labels.lblCnst.OutputNameIsNeeded());
			}
			else {
				txtOutputName.setTxtError("");
			}

			if (txtWebServiceUrl.getText().isEmpty()) {
				isValid = false;
				txtWebServiceUrl.setTxtError(Labels.lblCnst.WebServiceUrlIsNeeded());
			}
			else if (lstMethods.getSelectedIndex() == -1) {
				isValid = false;
				txtWebServiceUrl.setTxtError(Labels.lblCnst.WebServiceMethodIsNeeded());
			}
			else {
				txtWebServiceUrl.setTxtError("");
			}
		}
		else if (source.getType() == TypeSource.FOLDER) {
			if (txtFolderPath.getText().isEmpty()) {
				isValid = false;
				txtFolderPath.setTxtError(LabelsCommon.lblCnst.FolderPathIsNeeded());
			}
			else {
				txtFolderPath.setTxtError("");
			}
		}
		else if (source.getType() == TypeSource.FTP || source.getType() == TypeSource.SFTP) {
			if (txtUrlProtocol.getText().isEmpty()) {
				isValid = false;
				txtUrlProtocol.setTxtError(Labels.lblCnst.WebServiceUrlIsNeeded());
			}
			else {
				txtUrlProtocol.setTxtError("");
			}
			
			if (txtFolderPath.getText().isEmpty()) {
				isValid = false;
				txtFolderPath.setTxtError(LabelsCommon.lblCnst.FolderPathIsNeeded());
			}
			else {
				txtFolderPath.setTxtError("");
			}
		}
		else if (source.getType() == TypeSource.D4C) {
			if (ckanProperties == null || !ckanProperties.isValid()) {
				isValid = false;
			}
		}
		else if (source.getType() == TypeSource.MAIL) {
			if (txtUrlProtocol.getText().isEmpty()) {
				isValid = false;
				txtUrlProtocol.setTxtError(LabelsCommon.lblCnst.ProtocolUrlIsNeeded());
			}
			else {
				txtUrlProtocol.setTxtError("");
			}
		}
		return isNameValid && isValid;
	}

	@Override
	public Resource buildItem() {
		if (source.getType() == TypeSource.FOLDER) {
			VariableString folderPath = txtFolderPath.getVariableText();
			VariableString filter = txtFilter.getVariableText();
			VariableString outputName = txtOutputName.getVariableText();

			String login = txtLogin.getText();
			String password = txtPassword.getText();

			source.setFolderPath(folderPath);
			source.setFilter(filter);
			source.setLogin(login);
			source.setPassword(password);
			source.setOutputName(outputName);
		}
		else if (source.getType() == TypeSource.WEB_SERVICE) {
			VariableString outputName = txtOutputName.getVariableText();
			VariableString webServiceUrl = txtWebServiceUrl.getVariableText();
			String method = lstMethods.getValue(lstMethods.getSelectedIndex());
			WebServiceMethodDefinition selectedMethod = findSelectedMethod(method);
			if (selectedMethod != null && parameters != null) {
				selectedMethod.setParameters(parameters.getParameters());
			}

			source.setOutputName(outputName);
			source.setUrl(webServiceUrl);
			source.setMethod(selectedMethod);
		}
		else if (source.getType() == TypeSource.FTP || source.getType() == TypeSource.SFTP) {
			VariableString ftpUrl = txtUrlProtocol.getVariableText();
			VariableString port = txtPort.getVariableText();
			VariableString folderPath = txtFolderPath.getVariableText();
			VariableString filter = txtFilter.getVariableText();
			VariableString outputName = txtOutputName.getVariableText();

			String login = txtLogin.getText();
			String password = txtPassword.getText();

			source.setUrl(ftpUrl);
			source.setPort(port);
			source.setFolderPath(folderPath);
			source.setFilter(filter);

			source.setOutputName(outputName);
			source.setLogin(login);
			source.setPassword(password);
		}
		else if (source.getType() == TypeSource.D4C) {
			VariableString url = txtUrlProtocol.getVariableText();
			VariableString outputName = txtOutputName.getVariableText();
			
			url.setString((ckanProperties != null ? ckanProperties.getUrl() : ""), null, null);
			
			String login = ckanProperties != null ? ckanProperties.getOrg() : "";
			String password = ckanProperties != null ? ckanProperties.getApiKey() : "";
			CkanPackage pack = ckanProperties != null ? ckanProperties.getCkanPackage() : null;
			
			source.setUrl(url);
			
			source.setLogin(login);
			source.setPassword(password);

			source.setOutputName(outputName);
			source.setCkanPackage(pack);
		}
		else if (source.getType() == TypeSource.MAIL) {
			VariableString protocolUrl = txtUrlProtocol.getVariableText();
			VariableString port = txtPort.getVariableText();
			VariableString folderPath = txtFolderPath.getVariableText();
			VariableString treatedFolderPath = txtTreatedFolderPath.getVariableText();
			VariableString rejectedFolderPath = txtRejectedFolderPath.getVariableText();
			VariableString filter = txtFilter.getVariableText();
			VariableString attachmentFilter = txtAttachmentFilter.getVariableText();
			VariableString outputName = txtOutputName.getVariableText();

			String login = txtLogin.getText();
			String password = txtPassword.getText();

			source.setUrl(protocolUrl);
			source.setPort(port);
			source.setFolderPath(folderPath);
			source.setTreatedFolderPath(treatedFolderPath);
			source.setRejectedFolderPath(rejectedFolderPath);
			source.setFilter(filter);
			source.setAttachmentFilter(attachmentFilter);

			source.setOutputName(outputName);
			source.setLogin(login);
			source.setPassword(password);
		}
		return source;
	}

	@Override
	public void changeName(String value, boolean isValid) {
		this.isNameValid = isValid;
		source.setName(value);
	}
}
