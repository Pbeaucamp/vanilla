package bpm.gwt.commons.client.datasource;

import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.IDatasourceObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class DatasourceJdbcPage extends Composite implements IDatasourceObjectPage {

	private static DatasourceJdbcPageUiBinder uiBinder = GWT.create(DatasourceJdbcPageUiBinder.class);

	interface DatasourceJdbcPageUiBinder extends UiBinder<Widget, DatasourceJdbcPage> {
	}

	@UiField
	LabelTextBox txtUrl, txtHost, txtPort, txtBase, txtUser, txtPass;

	@UiField
	ListBox lstDriver;

	@UiField
	CheckBox chkFullUrl;

	@UiField
	Button btnTestConnection;

	private Datasource datasource;
	private DatasourceWizard parent;

	public DatasourceJdbcPage(DatasourceWizard parent, Datasource datasource) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;

		lstDriver.addStyleName(VanillaCSS.COMMONS_LISTBOX);
		btnTestConnection.addStyleName(VanillaCSS.COMMONS_BUTTON);
		
		txtUrl.setEnabled(false);
		
		loadJdbcDrivers();
		loadItem(datasource);
	}

	private void loadJdbcDrivers() {
		parent.showWaitPart(true);
		CommonService.Connect.getInstance().getJdbcDrivers(new GwtCallbackWrapper<List<String>>(parent, true) {

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
	}
	
	public void loadItem(Datasource datasource) {
		this.datasource = datasource;
		if (datasource != null && datasource.getObject() != null && datasource.getObject() instanceof DatasourceJdbc) {
			DatasourceJdbc dsJdbc = (DatasourceJdbc) datasource.getObject();

			txtUser.setText(dsJdbc.getUser());
			txtPass.setText(dsJdbc.getPassword());
 			if (dsJdbc.getFullUrl()) {
				txtUrl.setText(dsJdbc.getUrl());
				txtHost.clear();
				txtPort.clear();
				txtBase.clear();
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

	@Override
	public IDatasourceObject getDatasourceObject() {
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

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return false;
	}

	@Override
	public int getIndex() {
		return 1;
	}

	@UiHandler("btnTestConnection")
	public void onTestConnection(ClickEvent e) {
		DatasourceJdbc jdbc = (DatasourceJdbc) getDatasourceObject();
		parent.showWaitPart(true);
		CommonService.Connect.getInstance().testJdbcDatasource(jdbc, new GwtCallbackWrapper<String>(parent, true) {
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
}
