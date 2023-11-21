package bpm.es.web.client.panels;

import java.util.List;

import org.realityforge.gwt.keycloak.Keycloak;

import bpm.es.web.client.EsWeb.Layout;
import bpm.es.web.client.dialogs.LastConnectionDialog;
import bpm.es.web.client.menu.MenuPanel;
import bpm.es.web.client.panels.documents.DocumentsManagerPanel;
import bpm.es.web.client.services.AdminService;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.login.AdminPasswordChangeDemandsDialog;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.beans.PasswordBackup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel extends CompositeWaitPanel {

	private static MainPanelUiBinder uiBinder = GWT.create(MainPanelUiBinder.class);

	interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {
	}

	interface MyStyle extends CssResource {
	}

	@UiField
	MyStyle style;

	@UiField(provided=true)
	MenuPanel menu;

	@UiField(provided = true)
	TopToolbar toolbar;

	@UiField
	SimplePanel content;

	private HomePanel homePanel;
	private DossierPanel dossierPanel;
	private DocumentsManagerPanel documentsPanel;
	private StatisticReportsPanel statReportsPanel;
	private UsersPanel usersPanel;
	private LogsPanel logsPanel;
	private SettingsPanel settingsPanel;

	public MainPanel(InfoUser infoUser, Keycloak keycloak) {
		toolbar = new TopToolbar(this, infoUser, keycloak);
		menu = new MenuPanel(this);
		initWidget(uiBinder.createAndBindUi(this));

		if (infoUser.getUser().isSuperUser()) {
			checkRights();
		}
		else {
			menu.updateMenu(false);
		}
		
		displayHome();
		LastConnectionDialog dial = new LastConnectionDialog(infoUser);
		dial.center();
	}

	private void checkRights() {
		showWaitPart(true);

		AdminService.Connect.getInstance().canAccessAdministration(new GwtCallbackWrapper<Boolean>(this, true) {

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					menu.updateMenu(true);
					checkPasswordDemands();
				}
				else {
					menu.updateMenu(false);
				}
			}
		}.getAsyncCallback());
	}

	private void checkPasswordDemands() {
		LoginService.Connect.getInstance().getPendingPasswordChangeDemands(new GwtCallbackWrapper<List<PasswordBackup>>(null, false) {

			@Override
			public void onSuccess(List<PasswordBackup> result) {
				if (result != null && !result.isEmpty()) {
					AdminPasswordChangeDemandsDialog dial = new AdminPasswordChangeDemandsDialog(MainPanel.this, result);
					dial.center();
				}
			}
		}.getAsyncCallback());
	}

	public void manageLayout(Layout layout) {
		if (usersPanel != null)
			usersPanel.manageLayout(layout);
	}

//	public void displaySettings() {
//		if (settingsPanel == null) {
//			settingsPanel = new SettingsPanel();
//		}
//		content.setWidget(settingsPanel);
//	}

	public void displayHome() {
		if (homePanel == null) {
			homePanel = new HomePanel();
		}
		content.setWidget(homePanel);
		menu.deselectAll();
	}

	public void displayDossier() {
		if (dossierPanel == null) {
			dossierPanel = new DossierPanel();
		}
		content.setWidget(dossierPanel);
	}

	public void displayBillsPanel() {
		if (documentsPanel == null) {
			documentsPanel = new DocumentsManagerPanel();
		}
		content.setWidget(documentsPanel);
	}

	public void displayStatReportsPanel() {
		if (statReportsPanel == null) {
			statReportsPanel = new StatisticReportsPanel();
		}
		content.setWidget(statReportsPanel);
	}

	public void displayUsers() {
		if (usersPanel == null) {
			usersPanel = new UsersPanel();
		}
		content.setWidget(usersPanel);
	}

	public void displayLogs() {
		if (logsPanel == null) {
			logsPanel = new LogsPanel();
		}
		content.setWidget(logsPanel);
	}

	public void displaySettings() {
		if (settingsPanel == null) {
			settingsPanel = new SettingsPanel();
		}
		content.setWidget(settingsPanel);
	}
}
