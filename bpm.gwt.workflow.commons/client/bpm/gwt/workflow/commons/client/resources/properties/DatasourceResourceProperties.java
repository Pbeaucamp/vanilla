package bpm.gwt.workflow.commons.client.resources.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.NameChanger;
import bpm.gwt.workflow.commons.client.NameChecker;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.service.WorkflowsService;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesButton;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesText;
import bpm.gwt.workflow.commons.client.workflow.properties.VariablePropertiesText;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.VariableString;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ListBox;

public class DatasourceResourceProperties extends PropertiesPanel<Resource> implements NameChanger {

	private DatabaseServer datasource;

	private PropertiesListBox lstDrivers;
	private VariablePropertiesText txtDatabaseUrl, txtQuery;
	private PropertiesText txtLogin, txtPassword;
	private PropertiesButton buttonTest;

	private boolean edit;
	private boolean isNameValid = true;

	public DatasourceResourceProperties(NameChecker dialog, IResourceManager resourceManager, DatabaseServer myDatasource) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.LARGE_PX, myDatasource != null ? myDatasource.getId() : 0, myDatasource != null ? myDatasource.getName() : "", false, true);
		this.edit = myDatasource != null;
		this.datasource = myDatasource != null ? myDatasource : new DatabaseServer();

		setNameChecker(dialog);
		setNameChanger(this);

		lstDrivers = addList(null, new ArrayList<ListItem>(), WidgetWidth.LARGE_PX, changeDriverHandler, null);

		txtDatabaseUrl = addVariableText(LabelsCommon.lblCnst.DatabaseUrl(), datasource.getDatabaseUrlVS(), WidgetWidth.LARGE_PX, null);

		txtLogin = addText(LabelsCommon.lblCnst.Login(), datasource.getLogin(), WidgetWidth.LARGE_PX, false);
		txtPassword = addText(LabelsCommon.lblCnst.Password(), datasource.getPassword(), WidgetWidth.LARGE_PX, true);

		txtQuery = addVariableText(LabelsCommon.lblCnst.Query(), datasource.getQueryVS(), WidgetWidth.LARGE_PX, null);

		buttonTest = addButton(LabelsCommon.lblCnst.TestConnection(), connectionHandler);

		checkName(getTxtName(), datasource.getName());
		loadDrivers(datasource.getDriverJdbc());
	}

	@Override
	public boolean isValid() {
		boolean isValid = true;
		if (!edit) {
			if (txtDatabaseUrl.getText().isEmpty()) {
				isValid = false;
				txtDatabaseUrl.setTxtError(LabelsCommon.lblCnst.DatabaseUrlIsNeeded());
			}
			else {
				txtDatabaseUrl.setTxtError("");
			}
		}
		return isNameValid && isValid;
	}

	@Override
	public void changeName(String value, boolean isValid) {
		this.isNameValid = isValid;
		datasource.setName(value);
	}

	@Override
	public Resource buildItem() {
		VariableString databaseUrl = txtDatabaseUrl.getVariableText();

		String login = txtLogin.getText();
		String password = txtPassword.getText();
		
		VariableString query = txtQuery.getVariableText();

		datasource.setDatabaseUrl(databaseUrl);
		datasource.setLogin(login);
		datasource.setPassword(password);
		datasource.setQuery(query);

		return datasource;
	}

	private void loadDrivers(final String selectedDriver) {
		showWaitPart(true);

		lstDrivers.clear();

		WorkflowsService.Connect.getInstance().getJdbcDrivers(new GwtCallbackWrapper<List<String>>(DatasourceResourceProperties.this, true) {

			@Override
			public void onSuccess(List<String> drivers) {
				if (drivers != null) {
					int i = 0;
					for (String driver : drivers) {
						lstDrivers.addItem(driver);

						if (selectedDriver != null && !selectedDriver.isEmpty() && selectedDriver.equals(driver)) {
							lstDrivers.setSelectedIndex(i);
						}
						i++;
					}

					NativeEvent event = Document.get().createChangeEvent();
					ChangeEvent.fireNativeEvent(event, lstDrivers.getListBox());
				}
			}
		}.getAsyncCallback());
	}

	private ChangeHandler changeDriverHandler = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			String driverName = lst.getValue(lst.getSelectedIndex());

			datasource.setDriverJdbc(driverName);
		}
	};

	private ClickHandler connectionHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (isValid()) {
				showWaitPart(true);

				DatabaseServer databaseServer = (DatabaseServer) buildItem();

				WorkflowsService.Connect.getInstance().testConnection(databaseServer, new GwtCallbackWrapper<String>(DatasourceResourceProperties.this, true) {

					@Override
					public void onSuccess(String error) {
						if (error == null || error.isEmpty()) {
							buttonTest.setTxtResult(LabelsCommon.lblCnst.ConnectionIsValid());
						}
						else {
							buttonTest.setTxtError(error);
						}
					}
				}.getAsyncCallback());
			}
			else {
				buttonTest.setTxtError(LabelsCommon.lblCnst.DefinitionNotValid());
			}
		}
	};
}
