package bpm.gwt.aklabox.commons.client.dialogs;

import java.util.ArrayList;
import java.util.List;

import bpm.document.management.core.model.MailServer;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.customs.EditPasswordCell;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class MailServerSettingDialog extends AbstractDialogBox {

	private static MailServerSettingDialogUiBinder uiBinder = GWT.create(MailServerSettingDialogUiBinder.class);

	interface MailServerSettingDialogUiBinder extends UiBinder<Widget, MailServerSettingDialog> {
	}

	@UiField
	SimplePanel gridPanel;

	private boolean confirm = false;

	private ListDataProvider<MailServer> dataprovider = new ListDataProvider<>();

	public MailServerSettingDialog() {
		super("Configuration serveurs mails", true, true);
		setWidget(uiBinder.createAndBindUi(this));

		buildDatagrid();

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		AklaCommonService.Connect.getService().getMailServers(new GwtCallbackWrapper<List<MailServer>>(null, false, false) {
			@Override
			public void onSuccess(List<MailServer> result) {
				dataprovider.setList(result);
			}
		}.getAsyncCallback());
	}

	public boolean isConfirm() {
		return confirm;
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			confirm = true;
			hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			confirm = false;
			hide();
		}
	};

	private void buildDatagrid() {
		Column<MailServer, String> colName = new Column<MailServer, String>(new EditTextCell()) {
			@Override
			public String getValue(MailServer object) {
				return object.getServerName();
			}
		};
		colName.setFieldUpdater(new FieldUpdater<MailServer, String>() {
			@Override
			public void update(int index, MailServer object, String value) {
				object.setServerName(value);
			}
		});

		Column<MailServer, String> colUrl = new Column<MailServer, String>(new EditTextCell()) {
			@Override
			public String getValue(MailServer object) {
				return object.getUrl();
			}
		};
		colUrl.setFieldUpdater(new FieldUpdater<MailServer, String>() {
			@Override
			public void update(int index, MailServer object, String value) {
				object.setUrl(value);
			}
		});
		Column<MailServer, String> colPort = new Column<MailServer, String>(new EditTextCell()) {
			@Override
			public String getValue(MailServer object) {
				return object.getPort() + "";
			}
		};
		colPort.setFieldUpdater(new FieldUpdater<MailServer, String>() {
			@Override
			public void update(int index, MailServer object, String value) {
				object.setPort(Integer.parseInt(value));
			}
		});
		Column<MailServer, String> colLogin = new Column<MailServer, String>(new EditTextCell()) {
			@Override
			public String getValue(MailServer object) {
				return object.getLogin();
			}
		};
		colLogin.setFieldUpdater(new FieldUpdater<MailServer, String>() {
			@Override
			public void update(int index, MailServer object, String value) {
				object.setLogin(value);
			}
		});
		Column<MailServer, String> colPassword = new Column<MailServer, String>(new EditPasswordCell()) {
			@Override
			public String getValue(MailServer object) {
				return object.getPassword();
			}
		};
		colPassword.setFieldUpdater(new FieldUpdater<MailServer, String>() {
			@Override
			public void update(int index, MailServer object, String value) {
				object.setPassword(value);
			}
		});

//		SelectionCell cell = new SelectionCell(MailServer.authTypes);
//		Column<MailServer, String> colAuth = new Column<MailServer, String>(cell) {
//			@Override
//			public String getValue(MailServer object) {
//				return object.getAuthType().toString();
//			}
//		};
//		colAuth.setFieldUpdater(new FieldUpdater<MailServer, String>() {
//			@Override
//			public void update(int index, MailServer object, String value) {
//				object.setAuthType(MailServer.AuthType.valueOf(value));
//			}
//		});
		
		Column<MailServer, String> colFolder = new Column<MailServer, String>(new TextCell()) {
			@Override
			public String getValue(MailServer object) {
				return object.getFolder();
			}
		};
		colFolder.setFieldUpdater(new FieldUpdater<MailServer, String>() {
			@Override
			public void update(int index, MailServer object, String value) {
				object.setFolder(value);
			}
		});

		CheckboxCell chkCell = new CheckboxCell();
		Column<MailServer, Boolean> colTls = new Column<MailServer, Boolean>(chkCell) {
			@Override
			public Boolean getValue(MailServer object) {
				return object.isTlsEnabled();
			}
		};
		colTls.setFieldUpdater(new FieldUpdater<MailServer, Boolean>() {
			@Override
			public void update(int index, MailServer object, Boolean value) {
				object.setTlsEnabled(value);
			}
		});

		DataGrid<MailServer> datagrid = new DataGrid<>();

		datagrid.setSize("100%", "100%");
		datagrid.addColumn(colName, "Nom");
		datagrid.addColumn(colUrl, "Url");
		datagrid.addColumn(colPort, "Port");
		datagrid.addColumn(colLogin, "Login");
		datagrid.addColumn(colPassword, "Mot de passe");
//		datagrid.addColumn(colAuth, "Authentification");
		datagrid.addColumn(colFolder, "Dossier");
		datagrid.addColumn(colTls, "Tls");
		
		datagrid.setColumnWidth(colPort, "50px");
		datagrid.setColumnWidth(colLogin, "180px");
		datagrid.setColumnWidth(colTls, "50px");

		dataprovider.addDataDisplay(datagrid);

		gridPanel.add(datagrid);
	}

	@UiHandler("btnAddServer")
	public void onAdd(ClickEvent event) {
		List<MailServer> servers = dataprovider.getList();
		servers.add(new MailServer());
		dataprovider.setList(servers);
	}

	@Override
	public int getThemeColor() {
		return 0;
	}

	public List<MailServer> getMailServers() {
		return new ArrayList<>(dataprovider.getList());
	}

}
