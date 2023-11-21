package bpm.gwt.commons.client.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.tree.RepositoryTree;
import bpm.gwt.commons.client.tree.RepositoryTreeItem;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

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

	private RepositoryDirectory selectedDirectory;
	private RepositoryItem selectedItem;

	public RepositoryDialog(int type) {
		super("", true, true);
		buildContent();
		tree.loadTree(type);
	}

	public RepositoryDialog(String url, String login, String pass, Group group, Repository repo, int type) {
		super("", true, true);
		buildContent();
		tree.loadTree(url, login, pass, group, repo, type);
	}
	
	private void buildContent() {
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		tree = new RepositoryTree(new SelectionHandler<TreeItem>() {

			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				RepositoryTreeItem item = (RepositoryTreeItem) event.getSelectedItem();
				if (item.getRepositoryObject() instanceof RepositoryItem) {
					selectedItem = (RepositoryItem) item.getRepositoryObject();
					selectedDirectory = null;
				}
				else if (item.getRepositoryObject() instanceof RepositoryDirectory) {
					selectedDirectory = (RepositoryDirectory) item.getRepositoryObject();
					selectedItem = null;
				}
			}
		});

		mainPanel.add(tree);
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

	public boolean folderSelected() {
		return selectedDirectory != null;
	}

	public RepositoryItem getSelectedItem() {
		return selectedItem;
	}
	
	public RepositoryDirectory getSelectedDirectory() {
		return selectedDirectory;
	}
}
