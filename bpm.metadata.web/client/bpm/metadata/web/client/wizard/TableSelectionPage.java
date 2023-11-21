package bpm.metadata.web.client.wizard;

import java.util.List;

import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.metadata.web.client.panels.TableSelectionPanel;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class TableSelectionPage extends Composite implements IGwtPage {

	private static TableSelectionPageUiBinder uiBinder = GWT.create(TableSelectionPageUiBinder.class);

	interface TableSelectionPageUiBinder extends UiBinder<Widget, TableSelectionPage> {
	}

	@UiField
	SimplePanel panelContent;

	private TableSelectionPanel tableSelectionPanel;

	public TableSelectionPage(IGwtWizard parent) {
		initWidget(uiBinder.createAndBindUi(this));

		this.tableSelectionPanel = new TableSelectionPanel(parent);
		this.panelContent.setWidget(tableSelectionPanel);
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return tableSelectionPanel.isComplete();
	}

	@Override
	public boolean canGoFurther() {
		return isComplete();
	}

	@Override
	public int getIndex() {
		return 2;
	}

	public void buildPhysicalTree(List<DatabaseTable> physicalTables) {
		this.tableSelectionPanel.buildPhysicalTree(physicalTables);
	}
	
	public List<DatabaseTable> getLogicalTables() {
		return tableSelectionPanel.getLogicalTables();
	}

	public List<DatabaseTable> getPhysicalTables() {
		return tableSelectionPanel.getPhysicalTables();
	}
}
