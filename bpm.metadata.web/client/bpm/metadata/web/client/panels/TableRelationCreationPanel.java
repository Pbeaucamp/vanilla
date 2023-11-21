package bpm.metadata.web.client.panels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataRelation;
import bpm.gwt.commons.shared.fmdt.metadata.TableRelation;
import bpm.metadata.web.client.I18N.Labels;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class TableRelationCreationPanel extends Composite implements Handler {

	private static TableRelationCreationPanelUiBinder uiBinder = GWT.create(TableRelationCreationPanelUiBinder.class);

	interface TableRelationCreationPanelUiBinder extends UiBinder<Widget, TableRelationCreationPanel> {
	}

	@UiField
	HTMLPanel panelTablesRelations;
	
	@UiField
	SimplePanel panelRelationDefinitionContent;

//	@UiField(provided = true)
//	TableRelationPanel panelRelationDefinition;

	private CustomDatagrid<TableRelation> gridRelations;
	private SingleSelectionModel<TableRelation> selectionRelation;

	private HashMap<TableRelation, TableRelationPanel> relations;

	public TableRelationCreationPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		this.selectionRelation = new SingleSelectionModel<>();
		selectionRelation.addSelectionChangeHandler(this);

		this.gridRelations = buildTableRelation();
		panelTablesRelations.add(gridRelations);
	}

	private CustomDatagrid<TableRelation> buildTableRelation() {
		Column<TableRelation, String> relationsColumn = new Column<TableRelation, String>(new TextCell()) {

			@Override
			public String getValue(TableRelation object) {
				TableRelationPanel relationPanel = relations.get(object);
				if (relationPanel != null && relationPanel.getJoins() != null) {
					return String.valueOf(relationPanel.getJoins().size());
				}
				else if (relationPanel == null && object.getJoins() != null) {
					return String.valueOf(object.getJoins().size());
				}
				return "0";
			}
		};

		CustomDatagrid<TableRelation> gridRelations = new CustomDatagrid<TableRelation>(selectionRelation, 530, Labels.lblCnst.NoRelation(), Labels.lblCnst.TableRelations());
		gridRelations.addColumn(relationsColumn, Labels.lblCnst.Relations(), "120px");

		return gridRelations;
	}

	public void loadTables(List<DatabaseTable> tables, MetadataRelation relation) {
		this.relations = buildRelations(tables, relation);
		
		loadGridRelations();
	}

	private HashMap<TableRelation, TableRelationPanel> buildRelations(List<DatabaseTable> tables, MetadataRelation relation) {
		HashMap<TableRelation, TableRelationPanel> relations = new HashMap<>();
		if (tables != null) {
			for (int i = 0; i < tables.size(); i++) {
				for (int j = i + 1; j < tables.size(); j++) {
					TableRelation tableRelation = findTableRelation(tables.get(i), tables.get(j), relation);
					if (tableRelation == null) {
						tableRelation = new TableRelation(tables.get(i), tables.get(j));
					}
					relations.put(tableRelation, null);
				}
			}
		}

		return relations;
	}

	private TableRelation findTableRelation(DatabaseTable leftTable, DatabaseTable rightTable, MetadataRelation relation) {
		if (relation != null && relation.getRelations() != null && !relation.getRelations().isEmpty()) {
			for (TableRelation tableRelation : relation.getRelations()) {
				if (tableRelation.getLeftTable().getName().equals(leftTable.getName()) && tableRelation.getRightTable().getName().equals(rightTable.getName())) {
					return tableRelation;
				}
				else if (tableRelation.getLeftTable().getName().equals(rightTable.getName()) && tableRelation.getRightTable().getName().equals(leftTable.getName())) {
					return tableRelation;
				}
			}
		}
		return null;
	}

	@Override
	public void onSelectionChange(SelectionChangeEvent event) {
		TableRelation selectedRelation = selectionRelation.getSelectedObject();
		if (selectedRelation != null) {
			TableRelationPanel relationPanel = relations.get(selectedRelation);
			if (relationPanel == null) {
				relationPanel = new TableRelationPanel(this);
				relationPanel.buildTable(selectedRelation);
				
				relations.put(selectedRelation, relationPanel);
			}
			
			panelRelationDefinitionContent.setWidget(relationPanel);
		}
	}

//	public void refreshUi() {
//		loadGridRelations();
//
//		TableRelation relation = selectionRelation.getSelectedObject();
//		if (relation != null) {
//			selectionRelation.setSelected(relation, true);
//		}
//	}
	
	public void refreshUi() {
		gridRelations.refresh();
	}
	
	private void loadGridRelations() {
		List<TableRelation> relations = new ArrayList<>();
		relations.addAll(this.relations.keySet());
		gridRelations.loadItems(relations);
	}

	public List<TableRelation> getRelations() {
		List<TableRelation> relationsWithJoins = new ArrayList<>();
		if (relations != null) {
			for (TableRelation relation : relations.keySet()) {
				TableRelationPanel relationPanel = relations.get(relation);
				if (relationPanel != null && relationPanel.getJoins() != null && !relationPanel.getJoins().isEmpty()) {
					relation.setJoins(relationPanel.getJoins());
					relationsWithJoins.add(relation);
				}
				else if (relation.getJoins() != null && !relation.getJoins().isEmpty()){
					relationsWithJoins.add(relation);
				}
			}
		}
		 
		return relationsWithJoins;
	}
}
