package bpm.metadata.web.client.panels;

import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.beans.data.IDatasourceObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class DatasourcePanel extends Composite {

	private static DatasourcePanelUiBinder uiBinder = GWT.create(DatasourcePanelUiBinder.class);

	interface DatasourcePanelUiBinder extends UiBinder<Widget, DatasourcePanel> {
	}


	@UiField
	LabelTextBox txtName, txtUrl, txtHost, txtPort, txtBase, txtUser, txtPass;

	@UiField
	ListBox lstDriver;

	@UiField
	CheckBox chkFullUrl;

	@UiField
	Button btnTestConnection;
	
	private IWait waitPanel;
	
	private Datasource datasource;
	private int userId;
	
	@UiConstructor
	public DatasourcePanel(IWait waitPanel, int userId) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.userId = userId;
		
		lstDriver.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		btnTestConnection.addStyleName(VanillaCSS.COMMONS_BUTTON);

		txtUrl.setEnabled(false);

		waitPanel.showWaitPart(true);
		CommonService.Connect.getInstance().getJdbcDrivers(new GwtCallbackWrapper<List<String>>(waitPanel, true) {

			@Override
			public void onSuccess(List<String> result) {
				int i = 0;
				for (String driver : result) {
					String driverName = CommonConstants.jdbcLabels.get(driver);
					if (driverName == null || driverName.isEmpty()) {
						driverName = driver;
					}
					lstDriver.addItem(driverName, driver);
					if (datasource != null && datasource.getObject() != null && datasource.getObject() instanceof DatasourceJdbc) {
						if (((DatasourceJdbc) datasource.getObject()).getDriver().equals(driver)) {
							lstDriver.setSelectedIndex(i);
						}
					}
					i++;
				}
			}
		}.getAsyncCallback());
		
		//TODO: To delete
//		chkFullUrl.setValue(true);
//		refreshUi(true);
//		txtName.setText("MyDatasource");
//		txtUrl.setText("jdbc:mysql://localhost:3306/sampledata");
//		txtUser.setText("root");
//		txtPass.setText("root");
	}
	
	public void loadItem(Datasource datasource) {
		this.datasource = datasource;
		if (datasource != null && datasource.getObject() != null && datasource.getObject() instanceof DatasourceJdbc) {
			DatasourceJdbc dsJdbc = (DatasourceJdbc) datasource.getObject();

			txtName.setText(datasource.getName());
			
			txtUser.setText(dsJdbc.getUser());
			txtPass.setText(dsJdbc.getPassword());
 			if (dsJdbc.getFullUrl()) {
				txtUrl.setText(dsJdbc.getUrl());
				txtHost.clear();
				txtPort.clear();
				txtBase.clear();
			}
			else if (dsJdbc.getUrl() != null && !dsJdbc.getUrl().isEmpty()) {
				String res = dsJdbc.getUrl().split("//")[1];
				txtHost.setText(res.split(":")[0]);
				txtPort.setText(res.split(":")[1].split("/")[0]);
				txtBase.setText(res.split("/")[1]);
				txtUrl.setText("");
			}
			else {
				txtHost.setText(dsJdbc.getHost());
				txtPort.setText(dsJdbc.getPort());
				txtBase.setText(dsJdbc.getDatabaseName());
				txtUrl.setText("");
			}

			chkFullUrl.setValue(dsJdbc.getFullUrl());

			txtUrl.setEnabled(dsJdbc.getFullUrl());
			txtHost.setEnabled(!dsJdbc.getFullUrl());
			txtPort.setEnabled(!dsJdbc.getFullUrl());
			txtBase.setEnabled(!dsJdbc.getFullUrl());
		}
	}
	
	public void simplifyParameters() {
		txtUrl.setVisible(false);
		lstDriver.setVisible(false);
		chkFullUrl.setVisible(false);
	}

	@UiHandler("btnTestConnection")
	public void onTestConnection(ClickEvent e) {
		DatasourceJdbc jdbc = (DatasourceJdbc) getDatasourceObject();
		
		waitPanel.showWaitPart(true);
		CommonService.Connect.getInstance().testJdbcDatasource(jdbc, new GwtCallbackWrapper<String>(waitPanel, true) {
			@Override
			public void onSuccess(String result) {

				if (result.equals("")) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.TestConnection(), LabelsConstants.lblCnst.ConnectionSuccessful());
				}
				else {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.TestConnection(), result);
				}

			}
		}.getAsyncCallback());

	}

	@UiHandler("chkFullUrl")
	public void onFullUrlClick(ValueChangeEvent<Boolean> e) {
		boolean isFullUrl = e.getValue();
		refreshUi(isFullUrl);
	}
	
	private void refreshUi(boolean isFullUrl) {
		txtUrl.setEnabled(isFullUrl);
		txtHost.setEnabled(!isFullUrl);
		txtPort.setEnabled(!isFullUrl);
		txtBase.setEnabled(!isFullUrl);
	}

	public Datasource getDatasource() {
		applyProperties();
		return datasource;
	}
	
	public void applyProperties() {
		String datasourceName = txtName.getText();
		
		IDatasourceObject datasourceObject = getDatasourceObject();
		if (datasource == null) {
			datasource = new Datasource();
		}
		datasource.setName(datasourceName);
		datasource.setIdAuthor(userId);
		datasource.setType(DatasourceType.JDBC);
		datasource.setObject(datasourceObject);
	}
	
	private IDatasourceObject getDatasourceObject() {
		DatasourceJdbc object = null;
		if (datasource != null && datasource.getObject() != null && datasource.getObject() instanceof DatasourceJdbc) {
			object = (DatasourceJdbc) datasource.getObject();
		}
		else {
			object = new DatasourceJdbc();
		}
		String driverName = lstDriver.getValue(lstDriver.getSelectedIndex());
		object.setDriver(driverName);
		object.setPassword(txtPass.getText());

		object.setUrl(txtUrl.getText());
		object.setHost(txtHost.getText());
		object.setPort(txtPort.getText());
		object.setDatabaseName(txtBase.getText());
		object.setFullUrl(chkFullUrl.getValue());
		object.setUser(txtUser.getText());
		return object;
	}

	public boolean isComplete() {
//		boolean isFullUrl = chkFullUrl.getValue();
//		
//		String fullUrl = txtUrl.getText();
//		
//		String host = txtHost.getText();
//		String port = txtPort.getText();
//		String dbName = txtBase.getText();
		
//		return isFullUrl ? (!fullUrl.isEmpty()) : (!host.isEmpty() && !port.isEmpty() && !dbName.isEmpty());
		return true;
	}
}
