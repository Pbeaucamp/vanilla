package bpm.metadata.web.client.wizard;

import java.util.List;

import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.gwt.commons.shared.fmdt.metadata.TableRelation;
import bpm.metadata.web.client.panels.TableRelationCreationPanel;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class TableRelationPage extends Composite implements IGwtPage {

	private static DatasourceDefinitionPageUiBinder uiBinder = GWT.create(DatasourceDefinitionPageUiBinder.class);

	interface DatasourceDefinitionPageUiBinder extends UiBinder<Widget, TableRelationPage> {
	}

	@UiField
	HTMLPanel panelContent;


	private TableRelationCreationPanel tableRelationCreationPanel;

	public TableRelationPage(IGwtWizard parent) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.tableRelationCreationPanel = new TableRelationCreationPanel();
		panelContent.add(tableRelationCreationPanel);
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
		return isComplete();
	}

	@Override
	public int getIndex() {
		return 3;
	}

	public void loadTables(List<DatabaseTable> result) {
		tableRelationCreationPanel.loadTables(result, null);
	}

	public List<TableRelation> getRelations() {
		return tableRelationCreationPanel.getRelations();
	}
}
