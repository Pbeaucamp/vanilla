package bpm.metadata.web.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.shared.fmdt.metadata.ColumnJoin;
import bpm.gwt.commons.shared.fmdt.metadata.TableRelation;
import bpm.gwt.commons.shared.fmdt.metadata.ColumnJoin.Outer;
import bpm.metadata.web.client.I18N.Labels;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

public class TableRelationPanel extends Composite {

	private static TableDefinitionPanelUiBinder uiBinder = GWT.create(TableDefinitionPanelUiBinder.class);

	interface TableDefinitionPanelUiBinder extends UiBinder<Widget, TableRelationPanel> {
	}

	@UiField
	SimplePanel panelGridLeft, panelGridRight;

	@UiField
	CheckBox checkLeftOuter, checkRightOuter;

	@UiField
	Button btnAdd, btnDelete;

	@UiField
	SimplePanel panelRelation;
	
	private TableRelationCreationPanel parent;

	private CustomDatagrid<DatabaseColumn> gridLeft, gridRight;
	private SingleSelectionModel<DatabaseColumn> selectionLeftColumn, selectionRightColumn;
	
	private CustomDatagrid<ColumnJoin> gridJoins;
	private SingleSelectionModel<ColumnJoin> selectionJoin;
	
	private List<ColumnJoin> joins;

	@UiConstructor
	public TableRelationPanel(TableRelationCreationPanel parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;

		this.selectionLeftColumn = new SingleSelectionModel<>();
		this.gridLeft = new CustomDatagrid<DatabaseColumn>(selectionLeftColumn, 220, Labels.lblCnst.NoColumn(), Labels.lblCnst.LeftColumns());
		panelGridLeft.add(gridLeft);
		
		this.selectionRightColumn = new SingleSelectionModel<>();
		this.gridRight = new CustomDatagrid<DatabaseColumn>(selectionRightColumn, 220, Labels.lblCnst.NoColumn(), Labels.lblCnst.RightColumns());
		panelGridRight.add(gridRight);

		this.selectionJoin = new SingleSelectionModel<>();
		this.gridJoins = new CustomDatagrid<>(selectionJoin, 200, Labels.lblCnst.NoJoinRelation(), Labels.lblCnst.Relations());
		panelRelation.add(gridJoins);
	}

	public void buildTable(TableRelation relation) {
		this.joins = new ArrayList<>(relation.getJoins() != null ? relation.getJoins() : new ArrayList<ColumnJoin>());
		
		gridLeft.loadItems(relation.getLeftTable().getColumns());
		gridRight.loadItems(relation.getRightTable().getColumns());
		
		refreshUi();
	}
	
	private void refreshUi() {
		gridJoins.loadItems(joins != null ? joins : new ArrayList<ColumnJoin>());
		parent.refreshUi();
	}

	@UiHandler("btnAdd")
	public void onAddClick(ClickEvent e) {
		DatabaseColumn leftColumn = selectionLeftColumn.getSelectedObject();
		DatabaseColumn rightColumn = selectionRightColumn.getSelectedObject();
		
		boolean leftOuter = checkLeftOuter.getValue();
		boolean rightOuter = checkRightOuter.getValue();
		
		Outer outer = leftOuter && rightOuter ? Outer.FULL_OUTER : leftOuter && !rightOuter ? Outer.LEFT_OUTER : !leftOuter && rightOuter ? Outer.RIGHT_OUTER : Outer.INNER;
		
		joins.add(new ColumnJoin(leftColumn, rightColumn, outer));
		refreshUi();
	}

	@UiHandler("btnDelete")
	public void onDeleteClick(ClickEvent e) {
		ColumnJoin join = selectionJoin.getSelectedObject();
		if (join != null) {
			joins.remove(join);
			refreshUi();
		}
	}

	public List<ColumnJoin> getJoins() {
		return joins;
	}
}
