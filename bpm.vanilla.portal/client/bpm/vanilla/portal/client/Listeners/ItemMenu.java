package bpm.vanilla.portal.client.Listeners;

import bpm.gwt.commons.client.dialog.PublicUrlDialog;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.viewer.comments.DefineValidationDialog;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.PublicUrl.TypeURL;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.dialog.DisconnectedDialog;
import bpm.vanilla.portal.client.dialog.RequestAccessDialog;
import bpm.vanilla.portal.client.panels.ContentDisplayPanel;
import bpm.vanilla.portal.client.panels.navigation.TypeViewer;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ItemMenu extends PopupPanel {

	private enum TypeAction {
		RUN, ACCESS_REQUEST, VIEW, HISTORY, RUN_FA_WEB, BIRT_VIEWER, PROPERTIES, DELETE, SUBSCRIBE, REMOVE_WATCHLIST, ADD_WATCHLIST, REMOVE_ON_OPEN, ADD_ON_OPEN, DISCO, PUBLIC_URL, VALIDATION
	}

	private static ItemMenuUiBinder uiBinder = GWT.create(ItemMenuUiBinder.class);

	interface ItemMenuUiBinder extends UiBinder<Widget, ItemMenu> {
	}

	@UiField
	MenuBar subMenuBar;

	@UiField
	MenuItem btnRun, btnAccessRequest, btnView, btnHistory, btnRunFaWeb, btnBirtViewer, btnProperties, btnDelete, btnSubscribe, btnDisco, btnPublicUrl, subMenu;

	@UiField
	MenuItem btnValidation, btnRemoveWatchlist, btnAddWatchlist, btnRemoveOnOpen, btnAddOnOpen;

	@UiField
	HTMLPanel panelMenu;

	private ContentDisplayPanel mainPanel;

	public ItemMenu(ContentDisplayPanel mainPanel, PortailRepositoryItem item, TypeViewer typeViewer) {
		setWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;

		InfoUser infoUser = biPortal.get().getInfoUser();

		btnRun.setScheduledCommand(new CommandRun(item, TypeAction.RUN));
		btnAccessRequest.setScheduledCommand(new CommandRun(item, TypeAction.ACCESS_REQUEST));
		btnView.setScheduledCommand(new CommandRun(item, TypeAction.VIEW));
		btnHistory.setScheduledCommand(new CommandRun(item, TypeAction.HISTORY));
		btnRunFaWeb.setScheduledCommand(new CommandRun(item, TypeAction.RUN_FA_WEB));
		btnBirtViewer.setScheduledCommand(new CommandRun(item, TypeAction.BIRT_VIEWER));
		btnProperties.setScheduledCommand(new CommandRun(item, TypeAction.PROPERTIES));
		btnSubscribe.setScheduledCommand(new CommandRun(item, TypeAction.SUBSCRIBE));
		btnDelete.setScheduledCommand(new CommandRun(item, TypeAction.DELETE));
		btnRemoveWatchlist.setScheduledCommand(new CommandRun(item, TypeAction.REMOVE_WATCHLIST));
		btnAddWatchlist.setScheduledCommand(new CommandRun(item, TypeAction.ADD_WATCHLIST));
		btnRemoveOnOpen.setScheduledCommand(new CommandRun(item, TypeAction.REMOVE_ON_OPEN));
		btnAddOnOpen.setScheduledCommand(new CommandRun(item, TypeAction.ADD_ON_OPEN));
		btnDisco.setScheduledCommand(new CommandRun(item, TypeAction.DISCO));
		btnPublicUrl.setScheduledCommand(new CommandRun(item, TypeAction.PUBLIC_URL));
		btnValidation.setScheduledCommand(new CommandRun(item, TypeAction.VALIDATION));

		if (ToolsGWT.isRunnable(item)) {
			if ((item.getItem().canRun() && item.getItem().isOn()) || item.getType() == IRepositoryApi.URL || item.getType() == IRepositoryApi.FA_CUBE_TYPE) {
				btnAccessRequest.setVisible(false);
			}
			else if (!item.getItem().canRun() && item.getType() == IRepositoryApi.REPORTS_GROUP) {
				btnRun.setVisible(false);
				btnAccessRequest.setVisible(false);
			}
			else if (!item.getItem().canRun()) {
				btnRun.setVisible(false);
			}
			else {
				btnRun.setVisible(false);
				btnAccessRequest.setVisible(false);
			}
		}
		else {
			btnRun.setVisible(false);
			btnAccessRequest.setVisible(false);
		}

		if (item.isViewable() && item.getType() == IRepositoryApi.REPORTS_GROUP) {
			btnHistory.setVisible(false);
		}
		else if (!(item.isViewable() && item.isReport())) {
			btnView.setVisible(false);
			btnHistory.setVisible(false);
		}

		if (!ToolsGWT.isCube(item)) {
			btnRunFaWeb.setVisible(false);
		}

		if (!(ToolsGWT.isBirt(item) && biPortal.get().getInfoUser().isConnected(IRepositoryApi.BIRT_VIEWER))) {
			btnBirtViewer.setVisible(false);
		}

		if (!((item.getType() == IRepositoryApi.FAV_TYPE || item.getType() == IRepositoryApi.FWR_TYPE || item.getType() == IRepositoryApi.PROJECTION_TYPE || item.getType() == IRepositoryApi.FMDT_DRILLER_TYPE || item.getType() == IRepositoryApi.FMDT_CHART_TYPE) && item.isOwned())) {
			btnDelete.setVisible(false);
		}

		if (!ToolsGWT.checkItemToPack(item)) {
			btnDisco.setVisible(false);
		}
		
		if (!ToolsGWT.isPublicUrl(item)) {
			btnPublicUrl.setVisible(false);
		}

		if (!(item.isReport()/* || item.getType() == IRepositoryApi.GTW_TYPE) */)) {
			btnSubscribe.setVisible(false);
		}
		
		if (!(item.isReport() || item.getType() == IRepositoryApi.FD_TYPE)) {
			btnValidation.setVisible(false);
		}

		if (typeViewer == TypeViewer.WATCH_LIST || infoUser.isOnWatchlist(item.getItem())) {
			btnAddWatchlist.setVisible(false);
		}
		else {
			btnRemoveWatchlist.setVisible(false);
		}

		if (typeViewer == TypeViewer.OPEN_ON_STARTUP || infoUser.isOpenOnStartup(item.getItem())) {
			btnAddOnOpen.setVisible(false);
		}
		else if (item.isReport() || item.getType() == IRepositoryApi.FD_TYPE || item.getType() == IRepositoryApi.FMDT_TYPE) {
			btnRemoveOnOpen.setVisible(false);
		}
		else {
			btnAddOnOpen.setVisible(false);
			btnRemoveOnOpen.setVisible(false);
		}

		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);
		subMenuBar.addStyleName(VanillaCSS.POPUP_PANEL);
	}

	private class CommandRun implements Command {

		private PortailRepositoryItem item;
		private TypeAction action;

		public CommandRun(PortailRepositoryItem item, TypeAction action) {
			this.item = item;
			this.action = action;
		}

		@Override
		public void execute() {
			hide();

			switch (action) {
			case RUN:
				mainPanel.openViewer(item);
				break;
			case ACCESS_REQUEST:
				RequestAccessDialog dialog = new RequestAccessDialog(item);
				dialog.center();
				break;
			case VIEW:
				mainPanel.openView(biPortal.get(), item);
				break;
			case ADD_ON_OPEN:
				mainPanel.addToOnOpen(biPortal.get(), item, null);
				break;
			case ADD_WATCHLIST:
				mainPanel.addToWatchlist(biPortal.get(), item, null);
				break;
			case BIRT_VIEWER:
				if (item.getItem().isOn()) {
					mainPanel.openBirtViewer(biPortal.get(), item);
				}
				else {
					MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Error(), ToolsGWT.lblCnst.BiObject() + " " + item.getName() + " " + ToolsGWT.lblCnst.NotOn());
				}
				break;
			case HISTORY:
				mainPanel.openItemHistory(item);
				break;
			case PROPERTIES:
				mainPanel.showProperties(biPortal.get(), item);
				break;
			case DELETE:
				mainPanel.deleteItem(biPortal.get(), item);
				break;
			case REMOVE_ON_OPEN:
				mainPanel.removeFromOnOpen(biPortal.get(), item, null);
				break;
			case REMOVE_WATCHLIST:
				mainPanel.removeFromWatchlist(biPortal.get(), item, null);
				break;
			case RUN_FA_WEB:
				mainPanel.openFasd(biPortal.get(), item);
				break;
			case SUBSCRIBE:
				mainPanel.subscribe(biPortal.get(), item);
				break;
			case DISCO:
				DisconnectedDialog discoDial = new DisconnectedDialog(item);
				discoDial.center();
				break;
			case PUBLIC_URL:
				PublicUrlDialog publicDial = new PublicUrlDialog(biPortal.get().getInfoUser(), item.getId(), item.getItem(), TypeURL.REPOSITORY_ITEM);
				publicDial.center();
				break;
			case VALIDATION:
				DefineValidationDialog dial = new DefineValidationDialog(biPortal.get().getInfoUser(), item.getItem());
				dial.center();
				break;
			default:
				break;
			}
		}
	};
}
