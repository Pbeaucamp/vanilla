package bpm.vanilla.portal.client.Listeners;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.vanilla.portal.client.dialog.CreateDirectoryDialog;
import bpm.vanilla.portal.client.dialog.DisconnectedDialog;
import bpm.vanilla.portal.client.dialog.properties.PropertyDialog;
import bpm.vanilla.portal.client.panels.ContentDisplayPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DirectoryMenu extends PopupPanel {

	private static DirectoryMenuUiBinder uiBinder = GWT.create(DirectoryMenuUiBinder.class);

	interface DirectoryMenuUiBinder extends UiBinder<Widget, DirectoryMenu> {
	}
	
	@UiField
	HTMLPanel panelMenu;
	
	@UiField
	MenuItem btnAddPersoDirectory, btnProperties, btnDisco;

	private ContentDisplayPanel mainPanel;
	private PortailRepositoryDirectory dir;
	
	public DirectoryMenu(ContentDisplayPanel mainPanel, PortailRepositoryDirectory dir) {
		setWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.dir = dir;
		
		btnAddPersoDirectory.setScheduledCommand(addPersoCommand);
		btnProperties.setScheduledCommand(propertiesCommand);
		btnDisco.setScheduledCommand(discoCommand);
		
//		if(!ToolsGWT.checkItemsToPack(dir.getItems())) {
			btnDisco.setVisible(false);
//		}

		if (!(dir.getId() == 1 || dir.getDirectory().isPerso())) {
			btnAddPersoDirectory.setVisible(false);
		}

		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);
	}

	private Command addPersoCommand = new Command() {
		
		@Override
		public void execute() {
			hide();

			CreateDirectoryDialog dial = new CreateDirectoryDialog(mainPanel, dir.getId());
			dial.center();
		}
	};
	
	private Command propertiesCommand = new Command() {

		@Override
		public void execute() {
			hide();

			PropertyDialog dial = new PropertyDialog(dir);
			dial.center();
		}
	};
	
	private Command discoCommand = new Command() {

		@Override
		public void execute() {
			hide();

			DisconnectedDialog dial = new DisconnectedDialog(dir);
			dial.center();
		}
	};
}
