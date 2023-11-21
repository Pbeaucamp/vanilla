package bpm.vanillahub.web.client.dialogs;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanillahub.core.beans.resources.VanillaServer;
import bpm.vanillahub.web.client.utils.RepositoryTree;
import bpm.vanillahub.web.client.utils.RepositoryTreeItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryDialog extends AbstractDialogBox {

	private static RepositoryDialogUiBinder uiBinder = GWT.create(RepositoryDialogUiBinder.class);

	interface RepositoryDialogUiBinder extends UiBinder<Widget, RepositoryDialog> {
	}
	
	@UiField
	HTMLPanel mainPanel;
	
	private RepositoryTree tree;
	
	private boolean confirm;
	
	private RepositoryItem selectedItem;

	public RepositoryDialog(VanillaServer vanillaServer) {
		super("", true, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsCommon.lblCnst.Confirmation(), confirmHandler, LabelsCommon.lblCnst.Cancel(), cancelHandler);
		
		tree = new RepositoryTree(new SelectionHandler<TreeItem>() {
			
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				RepositoryTreeItem item = (RepositoryTreeItem) event.getSelectedItem();
				if(item.getRepositoryObject() instanceof RepositoryItem) {
					selectedItem = (RepositoryItem) item.getRepositoryObject();
				}
			}
		});
		
		mainPanel.add(tree);
		tree.loadTree(vanillaServer);
	}

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
	
	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			confirm = true;			
			hide();
		}
	};
	
	public boolean isConfirm() {
		return confirm;
	}
	
	public RepositoryItem getSelectedItem() {
		return selectedItem;
	}
}
