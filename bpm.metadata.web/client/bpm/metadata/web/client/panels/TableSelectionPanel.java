package bpm.metadata.web.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.tree.DatabaseTree;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.IDatabaseObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class TableSelectionPanel extends Composite {

	private static TableSelectionPanelUiBinder uiBinder = GWT.create(TableSelectionPanelUiBinder.class);

	interface TableSelectionPanelUiBinder extends UiBinder<Widget, TableSelectionPanel> {
	}

	@UiField
	LabelTextBox lblSearch;

	@UiField
	SimplePanel panelPhysicalTables, panelLogicalTables;

	private DatabaseTree treePhysicalTables;
	private DatabaseTree treeLogicalTables;

	private List<DatabaseTable> physicalTables;
	private List<DatabaseTable> logicalTables;
	
	private IGwtWizard parent;

	public TableSelectionPanel(IGwtWizard parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;

		this.treePhysicalTables = new DatabaseTree();
		this.panelPhysicalTables.setWidget(treePhysicalTables);

		this.treeLogicalTables = new DatabaseTree();
		this.panelLogicalTables.setWidget(treeLogicalTables);
	}

	@UiHandler("btnAdd")
	public void onAddClick(ClickEvent event) {
		IDatabaseObject databaseObject = treePhysicalTables.getSelectedItem();
		if (databaseObject != null && databaseObject instanceof DatabaseTable) {
			DatabaseTable table = (DatabaseTable) databaseObject;
			if (!logicalContains(table)) {
				addLogicalTable(table);
				refreshLogicalTree();
				if (parent != null) {
					parent.updateBtn();
				}
			}
		}
	}

	@UiHandler("btnAddAll")
	public void onAddAllClick(ClickEvent event) {
		this.logicalTables = new ArrayList<>();
		if (physicalTables != null) {
			logicalTables.addAll(physicalTables);
		}
		refreshLogicalTree();
		if (parent != null) {
			parent.updateBtn();
		}
	}

	@UiHandler("btnDelete")
	public void onDeleteClick(ClickEvent event) {
		IDatabaseObject databaseObject = treeLogicalTables.getSelectedItem();
		if (databaseObject != null && databaseObject instanceof DatabaseTable) {
			DatabaseTable table = (DatabaseTable) databaseObject;
			if (logicalContains(table)) {
				removeLogicalTable(table);
				refreshLogicalTree();
				if (parent != null) {
					parent.updateBtn();
				}
			}
		}
	}

	@UiHandler("btnDeleteAll")
	public void onDeleteAllClick(ClickEvent event) {
		this.logicalTables = new ArrayList<>();
		refreshLogicalTree();
		if (parent != null) {
			parent.updateBtn();
		}
	}

	public void buildPhysicalTree(List<DatabaseTable> physicalTables) {
		this.physicalTables = physicalTables;
		treePhysicalTables.buildTree(physicalTables);
	}

	public void buildLogicalTree(List<DatabaseTable> logicalTables) {
		this.logicalTables = logicalTables;
		refreshLogicalTree();
	}

	private boolean logicalContains(DatabaseTable myTable) {
		if (logicalTables != null) {
			for (DatabaseTable table : logicalTables) {
				if (table.getName().equals(myTable.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	private void refreshLogicalTree() {
		treeLogicalTables.buildTree(logicalTables != null ? logicalTables : new ArrayList<DatabaseTable>());
	}
	
	public void addLogicalTable(DatabaseTable table) {
		if (logicalTables == null) {
			this.logicalTables = new ArrayList<>();
		}
		this.logicalTables.add(table);
	}
	
	public void removeLogicalTable(DatabaseTable table) {
		this.logicalTables.remove(table);
	}
	
	public List<DatabaseTable> getLogicalTables() {
		return logicalTables;
	}

	public List<DatabaseTable> getPhysicalTables() {
		return physicalTables;
	}

	public boolean isComplete() {
		return logicalTables != null && !logicalTables.isEmpty();
	}
}
