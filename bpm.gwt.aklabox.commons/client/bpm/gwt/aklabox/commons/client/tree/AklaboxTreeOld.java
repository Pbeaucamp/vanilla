package bpm.gwt.aklabox.commons.client.tree;

import bpm.document.management.core.model.IObject.ItemTreeType;
import bpm.gwt.aklabox.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.aklabox.commons.client.utils.EnterpriseTreeItem;
import bpm.gwt.aklabox.commons.client.utils.IsLoaded;
import bpm.gwt.aklabox.commons.shared.AklaboxConnection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class AklaboxTreeOld extends CompositeWaitPanel {

	private static AklaboxTreeUiBinder uiBinder = GWT.create(AklaboxTreeUiBinder.class);

	interface AklaboxTreeUiBinder extends UiBinder<Widget, AklaboxTreeOld> {
	}

	@UiField(provided=true)
	Tree tree;

	private int targetKey;

	public AklaboxTreeOld(AklaboxConnection server, String login, ItemTreeType type, Integer selectedItem, SelectionHandler<TreeItem> selectionHandler) {
		tree = new Tree() {
			@Override
			public void setFocus(boolean focus) {
				// Don't do anything
			}
		};
		initWidget(uiBinder.createAndBindUi(this));

		switch (type) {
		case ENTERPRISE:
			targetKey = AklaboxTreeManager.loadEnterprise(server, login, tree);
			break;
		// TODO: REFACTOR RIGHT - LATER - (Mail Management) Change someday with private or redo Mail
		// case MAIL_MANAGEMENT:
		case MY_DOCUMENTS:
			targetKey = AklaboxTreeManager.loadMail(server, 0, tree, null);
			break;
		default:
			break;
		}

		if (selectedItem != null && selectedItem > 0) {
			selectTreeItem(targetKey, tree, selectedItem, 0);
		}

		if (selectionHandler != null) {
			tree.addSelectionHandler(selectionHandler);
		}
	}

	public bpm.document.management.core.model.Tree getSelectedFolder() {
		TreeItem selectedItem = tree.getSelectedItem();
		return selectedItem instanceof DirectoryTreeItem ? ((DirectoryTreeItem) selectedItem).getDirectory() : null;
	}

	private void selectTreeItem(final Integer key, final Tree tree, final int folderTargetId, final int nbTry) {
		showWaitPart(true);

		IsLoaded isLoaded = AklaboxTreeManager.getLoaded(key);
		if (nbTry < 5 && !isLoaded.isLoaded()) {
			Timer t = new Timer() {
				@Override
				public void run() {
					int nbTryIncrement = nbTry + 1;
					selectTreeItem(key, tree, folderTargetId, nbTryIncrement);
				}
			};
			t.schedule(5000);
		}
		else {
			selectTreeItem(tree, folderTargetId);
		}
	}

	protected void selectTreeItem(Tree tree, int folderTargetId) {
		for (int i = 0; i < tree.getItemCount(); i++) {
			if (tree.getItem(i) instanceof DirectoryTreeItem) {
				DirectoryTreeItem dit = (DirectoryTreeItem) tree.getItem(i);
				if (dit.getDirectory().getId() == folderTargetId) {
					tree.setSelectedItem(dit);
					break;
				}
				if (selectTreeItem(dit, folderTargetId))
					dit.setState(true);
				break;
			}
			else if (tree.getItem(i) instanceof EnterpriseTreeItem) {
				EnterpriseTreeItem dit = (EnterpriseTreeItem) tree.getItem(i);
				if (selectTreeItem(dit, folderTargetId)) {
					dit.setState(true);
					break;
				}
			}
		}

		showWaitPart(false);
	}

	protected boolean selectTreeItem(TreeItem tree, int folderTargetId) {
		for (int i = 0; i < tree.getChildCount(); i++) {
			if (tree.getChild(i) instanceof DirectoryTreeItem) {
				DirectoryTreeItem dit = (DirectoryTreeItem) tree.getChild(i);
				if (dit.getDirectory().getId() == folderTargetId) {
					dit.setSelected(true);
					return true;
				}
				if (selectTreeItem(dit, folderTargetId)) {
					tree.setState(true);
					return true;
				}
			}
		}
		return false;
	}

}
