package bpm.vanilla.portal.client.Listeners;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.portal.client.dialog.RequestAccessDialog;
import bpm.vanilla.portal.client.panels.ContentDisplayPanel;
import bpm.vanilla.portal.client.tree.TreeDirectoryItem;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;

public class DirectoryItemDoubleClickHandler implements DoubleClickHandler {
	
	private ContentDisplayPanel contentDisplayPanel;
	private TreeDirectoryItem treeDir;
	
	public DirectoryItemDoubleClickHandler(ContentDisplayPanel contentDisplayPanel, TreeDirectoryItem treeDir) {
		super();
		this.contentDisplayPanel = contentDisplayPanel;
		this.treeDir = treeDir;
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		final PortailRepositoryItem item = treeDir.getItem();
		
		if(ToolsGWT.isRunnable(item) && item.getItem().canRun() && item.getItem().isOn()) {
			contentDisplayPanel.openViewer(item);
		}
		else if(ToolsGWT.isRunnable(item) && item.getItem().isOn() && !item.getItem().canRun()) {
			final InformationsDialog dial = new InformationsDialog(ToolsGWT.lblCnst.Access(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), "You don't have the right to access this item, would you like to ask for an access ?", true);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if(dial.isConfirm()) {
						RequestAccessDialog dialog = new RequestAccessDialog(item);
						dialog.center();
					}
				}
			});
			dial.center();
		}
	}
}
