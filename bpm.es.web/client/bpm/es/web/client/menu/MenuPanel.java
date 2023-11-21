package bpm.es.web.client.menu;

import bpm.es.web.client.panels.MainPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class MenuPanel extends Composite {

	private static MenuPanelUiBinder uiBinder = GWT.create(MenuPanelUiBinder.class);

	interface MenuPanelUiBinder extends UiBinder<Widget, MenuPanel> {
	}

	@UiField
	HTMLPanel menu;

	@UiField
	MenuItem menuSheet, menuManager, menuAlertes, menuListings;

	@UiField
	MenuItem menuDocumentManager, menuBills, menuLetters, menuEtiquettes, menuScan;
	
	@UiField
	MenuItem menuStatistics, menuReports, menuDashboards, menuCubes;

	@UiField
	MenuItem menuParentSettings, menuMySettings, menuUsers, menuLogs, menuSettings;

	private MainPanel mainPanel;

	private MenuItem selectedItem;
	private MenuItem selectedSubItem;

	public MenuPanel(MainPanel mainPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
	}

	public void updateMenu(boolean isSuperUser) {
		if (isSuperUser) {
			menuUsers.setVisible(true);
			menuLogs.setVisible(true);
			menuSettings.setVisible(true);
		}
		else {
			menuUsers.removeFromParent();
			menuLogs.removeFromParent();
			menuSettings.removeFromParent();
		}
	}

	@UiHandler("menuSheet")
	protected void onMenuSheet(ClickEvent e) {
		closeMenu();
		refreshMenu(menuSheet);
		
		mainPanel.displayDossier();
	}

	@UiHandler("menuDocumentManager")
	protected void onMenuDocumentManager(ClickEvent e) {
		closeMenu();
		refreshMenu(menuDocumentManager);

		onMenuBills(null);
	}

	@UiHandler("menuBills")
	protected void onMenuBills(ClickEvent e) {
		closeMenu();
		refreshMenu(menuBills);

		mainPanel.displayBillsPanel();
	}

	@UiHandler("menuScan")
	protected void onMenuScan(ClickEvent e) {
		closeMenu();
		refreshMenu(menuScan);

		mainPanel.displayBillsPanel();
	}

	@UiHandler("menuAlertes")
	protected void onmenuAlertes(ClickEvent e) {
		closeMenu();
		refreshMenu(menuAlertes);

		mainPanel.displayStatReportsPanel();
	}

	@UiHandler("menuStatistics")
	protected void onMenuStatistics(ClickEvent e) {
		closeMenu();
		refreshMenu(menuStatistics);

		onMenuReports(null);
	}

	@UiHandler("menuReports")
	protected void onMenuReports(ClickEvent e) {
		closeMenu();
		refreshMenu(menuReports);

		mainPanel.displayStatReportsPanel();
	}

	@UiHandler("menuDashboards")
	protected void onMenuDashboards(ClickEvent e) {
		closeMenu();
		refreshMenu(menuDashboards);

		mainPanel.displayStatReportsPanel();
	}

	@UiHandler("menuCubes")
	protected void onMenuCubes(ClickEvent e) {
		closeMenu();
		refreshMenu(menuCubes);

		mainPanel.displayStatReportsPanel();
	}

	@UiHandler("menuListings")
	protected void onMenuListings(ClickEvent e) {
		closeMenu();
		refreshMenu(menuListings);

		mainPanel.displayStatReportsPanel();
	}

	@UiHandler("menuParentSettings")
	protected void onMenuParentSettings(ClickEvent e) {
		closeMenu();
		refreshMenu(menuParentSettings);

		onMenuSettings(null);
	}

	@UiHandler("menuUsers")
	protected void onMenuUsers(ClickEvent e) {
		closeMenu();
		refreshMenu(menuUsers);

		mainPanel.displayUsers();
	}

	@UiHandler("menuLogs")
	protected void onMenuLogs(ClickEvent e) {
		closeMenu();
		refreshMenu(menuLogs);

		mainPanel.displayLogs();
	}

	@UiHandler("menuSettings")
	protected void onMenuSettings(ClickEvent e) {
		closeMenu();
		refreshMenu(menuSettings);

		mainPanel.displaySettings();
	}

	public void deselectAll() {
		refreshMenu(null);
	}

	private void refreshMenu(MenuItem item) {
		if (item == null) {
			if (selectedSubItem != null) {
				selectedSubItem.setSelected(false);
			}
			if (selectedItem != null) {
				selectedItem.setSelected(false);
			}
			selectedItem = null;
		}
		else if (item.isChild()) {
			if (selectedSubItem != null) {
				selectedSubItem.setSelected(false);
			}
			selectedSubItem = item;
			selectedSubItem.setSelected(true);
		}
		else {
			if (selectedItem != null) {
				selectedItem.setSelected(false);
			}
			selectedItem = item;
			selectedItem.setSelected(true);
		}
	}

	private void closeMenu() {
		// if (drawerPanel.getNarrow()) {
		// drawerPanel.closeDrawer();
		// }
	}
}