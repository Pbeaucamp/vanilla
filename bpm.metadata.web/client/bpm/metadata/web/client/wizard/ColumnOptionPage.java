package bpm.metadata.web.client.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.tree.DatabaseTree;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataResource;
import bpm.metadata.web.client.panels.ColumnPropertiesPanel;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.IDatabaseObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class ColumnOptionPage extends Composite implements IGwtPage, SelectionHandler<TreeItem> {

	private static ColumnOptionPageUiBinder uiBinder = GWT.create(ColumnOptionPageUiBinder.class);

	interface ColumnOptionPageUiBinder extends UiBinder<Widget, ColumnOptionPage> {
	}

	@UiField
	SimplePanel panelLogicalTables, panelProperties;

	private Datasource datasource;
	
	private DatabaseTree treeLogicalTables;
	private List<DatabaseTable> logicalTables;
	
	private HashMap<DatabaseColumn, ColumnPropertiesPanel> columnsProperties = new HashMap<>();

	public ColumnOptionPage(IGwtWizard parent, Datasource datasource) {
		initWidget(uiBinder.createAndBindUi(this));
		this.datasource = datasource;
		
		this.treeLogicalTables = new DatabaseTree();
		treeLogicalTables.addSelectionHandler(this);
		this.panelLogicalTables.setWidget(treeLogicalTables);
	}

	public void buildLogicalTree(List<DatabaseTable> logicalTables) {
		this.logicalTables = logicalTables;
		refreshLogicalTree();
	}

	private void refreshLogicalTree() {
		treeLogicalTables.buildTree(logicalTables != null ? logicalTables : new ArrayList<DatabaseTable>());
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return false;
	}

	@Override
	public int getIndex() {
		return 4;
	}
	
	public List<MetadataResource> getResourcesAndApplyOptions() {
		List<MetadataResource> myResources = new ArrayList<>();
		if (columnsProperties != null) {
			for (DatabaseColumn column : columnsProperties.keySet()) {
				ColumnPropertiesPanel columnPropertiesPanel = columnsProperties.get(column);
				if (columnPropertiesPanel != null && columnPropertiesPanel.getResources() != null) {
					columnPropertiesPanel.apply();
					
					myResources.addAll(columnPropertiesPanel.getResources());
				}
			}
		}
		 
		return myResources;
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		IDatabaseObject item = treeLogicalTables.getSelectedItem();
		if (item instanceof DatabaseColumn) {
			DatabaseColumn column = (DatabaseColumn) item;
			
			ColumnPropertiesPanel columnPanel = columnsProperties.get(item);
			if (columnPanel == null) {
				columnPanel = new ColumnPropertiesPanel(datasource, column, null);
				
				columnsProperties.put(column, columnPanel);
			}
			
			panelProperties.setWidget(columnPanel);
		}
	}
}
