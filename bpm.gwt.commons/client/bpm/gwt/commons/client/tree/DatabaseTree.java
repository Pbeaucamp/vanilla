package bpm.gwt.commons.client.tree;

import java.util.List;

import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.IDatabaseObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class DatabaseTree extends Composite {

	private static DatabaseTreeUiBinder uiBinder = GWT.create(DatabaseTreeUiBinder.class);

	interface DatabaseTreeUiBinder extends UiBinder<Widget, DatabaseTree> {
	}

	@UiField
	HTMLPanel databasePanel;

	@UiField
	Tree databaseTree;

	public DatabaseTree() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void buildTree(List<DatabaseTable> result) {
		databaseTree.clear();

		if (result != null) {
			for (DatabaseTable table : result) {
				DatabaseTreeItem item = new DatabaseTreeItem(new DatabaseObjectWidget(table, false), false);
				databaseTree.addItem(item);
			}
		}
	}
	
	public IDatabaseObject getSelectedItem() {
		if (databaseTree.getSelectedItem() != null) {
			DatabaseTreeItem item = (DatabaseTreeItem) databaseTree.getSelectedItem();
			return item.getDatabaseObject();
		}
		
		return null;
	}
	
	public void addSelectionHandler(SelectionHandler<TreeItem> handler) {
		databaseTree.addSelectionHandler(handler);
	}
}
