package bpm.gwt.commons.client.tree;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.IDatabaseObject;

import com.google.gwt.user.client.ui.TreeItem;

public class DatabaseTreeItem extends TreeItem {

	private DatabaseObjectWidget databaseObjectWidget;
	private IDatabaseObject databaseObject;

	public DatabaseTreeItem(DatabaseObjectWidget databaseObjectWidget, boolean dragAndDrop) {
		super(databaseObjectWidget);
		this.databaseObjectWidget = databaseObjectWidget;
		this.databaseObject = databaseObjectWidget.getDatabaseObject();

		if (databaseObject instanceof DatabaseTable) {
			DatabaseTable table = (DatabaseTable) databaseObject;
			if (table.getColumns() != null) {
				for (DatabaseColumn object : table.getColumns() ) {
					this.addItem(new DatabaseTreeItem(new DatabaseObjectWidget(object, dragAndDrop), dragAndDrop));
				}
			}
		}
	}

	@Override
	public void setSelected(boolean selected) {
		if (!selected) {
			this.databaseObjectWidget.removeStyleName(VanillaCSS.TREE_ITEM_SELECTED);
		}
		else {
			this.databaseObjectWidget.addStyleName(VanillaCSS.TREE_ITEM_SELECTED);
		}
		super.setSelected(selected);
	}

	public IDatabaseObject getDatabaseObject() {
		return databaseObject;
	}
}
