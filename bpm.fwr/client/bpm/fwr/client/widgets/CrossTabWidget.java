package bpm.fwr.client.widgets;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.components.CrossComponent;
import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.DataSource;
import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.fwr.api.beans.dataset.FwrRelationStrategy;
import bpm.fwr.api.beans.dataset.GroupPrompt;
import bpm.fwr.api.beans.dataset.IResource;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.action.ActionAddColumnCrosstab;
import bpm.fwr.client.action.ActionMoveColumnInReportWidget;
import bpm.fwr.client.action.ActionTrashColumnCrosstab;
import bpm.fwr.client.action.ActionType;
import bpm.fwr.client.dialogs.JoinDatasetDialogBox;
import bpm.fwr.client.draggable.HasBin;
import bpm.fwr.client.draggable.dropcontrollers.CrossTabVerticalPanelDropController;
import bpm.fwr.client.draggable.dropcontrollers.CustomVerticalPanelDropController;
import bpm.fwr.client.draggable.widgets.DraggableColumnHTML;
import bpm.fwr.client.draggable.widgets.DraggableHTMLPanel;
import bpm.fwr.client.panels.ReportSheet;
import bpm.fwr.client.utils.ColumnType;
import bpm.fwr.client.utils.DataStreamType;
import bpm.fwr.client.utils.SizeComponentConstants;
import bpm.fwr.client.utils.WidgetType;
import bpm.gwt.commons.client.listeners.FinishListener;
import bpm.gwt.commons.shared.InfoUser;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetCollection;

public class CrossTabWidget extends ReportWidget implements HasMouseDownHandlers, HasBin {
	private static final String CSS_COLS = "crossTabCols";
	private static final String CSS_ROWS = "crossTabRows";
	private static final String CSS_CELLS = "crossTabCells";
	private static final String CSS_NONE = "crossTabNone";

	private static final String CSS_DIMENSION_NONE = "crossTabRowNone";
	private static final String CSS_COLUMN_DIMENSION = "columnDimension";

	/*
	 * Drop controllers which receive data from Metadata on the palette
	 */
	private DropController dropControllerRows, dropControllerCells, dropControllerCols;

	/*
	 * Drop controllers which allows to exchange two colomn on the same cell
	 */
	private DropController rowsDropController, cellsDropController, colsDropController;

	private PickupDragController dataDragController, colsDragController, measureDragController, rowsDragController;

	private CrossTabCell cols, rows, cells;
	private VerticalPanel cellsPanel, rowsPanel, colsPanel;

	// We add to those panel the cell which we can't interact with
	private VerticalPanel contentRows, contentCells, contentCols;
	private SimplePanel nonePanel;

	private List<Column> selectedCols = new ArrayList<Column>();
	private List<Column> selectedRows = new ArrayList<Column>();
	private List<Column> selectedCells = new ArrayList<Column>();

	private DataSet dataset;

//	private HashMap<Integer, String> defaultLanguages = new HashMap<Integer, String>();
	private String defaultLanguage;
	private ReportSheet reportSheetParent;
	private GridWidget gridWidget;

	private boolean firstTime = true;

	public CrossTabWidget(PickupDragController dataDragController, PickupDragController measureDragController, PickupDragController rowsDragController, PickupDragController thirdBinDragC, int width, ReportSheet reportSheetParent, GridWidget gridWidget) {
		super(reportSheetParent, WidgetType.CROSSTAB, width);
		this.setHeight(SizeComponentConstants.SIZE_CROSSTAB_INITIAL + "px");

		this.reportSheetParent = reportSheetParent;
		this.dataDragController = dataDragController;
		this.colsDragController = thirdBinDragC;
		this.rowsDragController = rowsDragController;
		this.measureDragController = measureDragController;
		this.gridWidget = gridWidget;

		nonePanel = new SimplePanel();
		nonePanel.addStyleName(CSS_NONE);
		nonePanel.setSize(SizeComponentConstants.SIZE_CROSSTAB_WIDTH + "px", SizeComponentConstants.SIZE_CROSSTAB_HEIGHT + "px");

		colsPanel = new VerticalPanel();
		colsPanel.addStyleName("test");
		colsPanel.setSize("100%", "100%");

		cols = new CrossTabCell(this, ColumnType.COLS);
		cols.setSize(SizeComponentConstants.SIZE_CROSSTAB_WIDTH + "px", SizeComponentConstants.SIZE_CROSSTAB_HEIGHT + "px");
		cols.add(colsPanel);

		contentCols = new VerticalPanel();
		contentCols.addStyleName(CSS_COLS);
		contentCols.add(cols);

		rowsPanel = new VerticalPanel();
		rowsPanel.setSize("100%", SizeComponentConstants.SIZE_CROSSTAB_HEIGHT + "px");

		rows = new CrossTabCell(this, ColumnType.ROWS);
		rows.setSize(SizeComponentConstants.SIZE_CROSSTAB_WIDTH + "px", SizeComponentConstants.SIZE_CROSSTAB_HEIGHT - 1 + "px");
		rows.add(rowsPanel);

		contentRows = new VerticalPanel();
		contentRows.addStyleName(CSS_ROWS);
		contentRows.add(rows);

		cellsPanel = new VerticalPanel();
		cellsPanel.setSize("100%", SizeComponentConstants.SIZE_CROSSTAB_HEIGHT + "px");

		cells = new CrossTabCell(this, ColumnType.CELLS);
		cells.setSize(SizeComponentConstants.SIZE_CROSSTAB_WIDTH + "px", SizeComponentConstants.SIZE_CROSSTAB_HEIGHT - 1 + "px");
		cells.add(cellsPanel);

		contentCells = new VerticalPanel();
		contentCells.addStyleName(CSS_CELLS);
		contentCells.add(cells);

		rowsDropController = new CustomVerticalPanelDropController(rows, rowsPanel);
		colsDropController = new CustomVerticalPanelDropController(cols, colsPanel);
		cellsDropController = new CustomVerticalPanelDropController(cells, cellsPanel);

		dropControllerRows = new CrossTabVerticalPanelDropController(rows);
		dropControllerCells = new CrossTabVerticalPanelDropController(cells);
		dropControllerCols = new CrossTabVerticalPanelDropController(cols);

		this.add(nonePanel);
		this.add(contentCols);
		this.add(contentRows);
		this.add(contentCells);
	}

	@Override
	protected void onAttach() {
		dataDragController.registerDropController(dropControllerRows);
		dataDragController.registerDropController(dropControllerCells);
		dataDragController.registerDropController(dropControllerCols);

		rowsDragController.registerDropController(rowsDropController);
		colsDragController.registerDropController(colsDropController);
		measureDragController.registerDropController(cellsDropController);
		super.onAttach();
	}

	@Override
	protected void onDetach() {
		dataDragController.unregisterDropController(dropControllerRows);
		dataDragController.unregisterDropController(dropControllerCells);
		dataDragController.unregisterDropController(dropControllerCols);

		rowsDragController.unregisterDropController(rowsDropController);
		colsDragController.unregisterDropController(colsDropController);
		measureDragController.unregisterDropController(cellsDropController);
		super.onDetach();
	}

	private boolean widgetContainColumn(Column column) {
		for (Column col : selectedCols) {
			if (col.getName().equals(column.getName()) && col.getMetadataId() == column.getMetadataId() && col.getMetadataParent().equals(column.getMetadataParent()) && col.getBusinessModelParent().equals(column.getBusinessModelParent()) && col.getBusinessPackageParent().equals(column.getBusinessPackageParent())) {
				return true;
			}
		}

		for (Column col : selectedRows) {
			if (col.getName().equals(column.getName()) && col.getMetadataId() == column.getMetadataId() && col.getMetadataParent().equals(column.getMetadataParent()) && col.getBusinessModelParent().equals(column.getBusinessModelParent()) && col.getBusinessPackageParent().equals(column.getBusinessPackageParent())) {
				return true;
			}
		}

		for (Column col : selectedCells) {
			if (col.getName().equals(column.getName()) && col.getMetadataId() == column.getMetadataId() && col.getMetadataParent().equals(column.getMetadataParent()) && col.getBusinessModelParent().equals(column.getBusinessModelParent()) && col.getBusinessPackageParent().equals(column.getBusinessPackageParent())) {
				return true;
			}
		}

		return false;
	}

	public void manageColumns(Column column, ColumnType type, int index, boolean needToCheckDB, ActionType actionType) {
		if (!widgetContainColumn(column)) {
			if (defaultLanguage == null || defaultLanguage.isEmpty()) {
				defaultLanguage = reportSheetParent.getPanelParent().getPanelparent().getLanguageDefaultForMetadataWithId();
			}

			String defautlLanguage = this.defaultLanguage;

			if (type == ColumnType.ROWS && column.isDimension()) {
				this.selectedRows.add(column);

				int globalHeight = this.getOffsetHeight() + SizeComponentConstants.SIZE_CROSSTAB_HEIGHT + 2;
				int rowsHeight = rows.getOffsetHeight() + SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
				int cellsHeight = cells.getOffsetHeight() + SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
				if (this.getOffsetHeight() == 0) {
					int rowsSize = selectedRows.isEmpty() ? 1 : selectedRows.size();
					int cellsSize = selectedCells.isEmpty() ? 1 : selectedCells.size();
					int colsSize = selectedCols.isEmpty() ? 1 : selectedCols.size();

					globalHeight = (rowsSize + cellsSize + colsSize) * SizeComponentConstants.SIZE_CROSSTAB_HEIGHT + (rowsSize + cellsSize + colsSize) * 2;

					if (selectedRows.isEmpty() && selectedCells.isEmpty()) {
						rowsHeight = SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
						cellsHeight = SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
					}
					else {
						rowsHeight = (rowsSize + cellsSize) * SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
						cellsHeight = (rowsSize + cellsSize) * SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
					}
				}
				this.setHeight(globalHeight + "px");
				System.out.println("Global height: " + globalHeight + "px");

				rows.setHeight(rowsHeight + "px");
				System.out.println("Rows height: " + rowsHeight + "px");

				cells.setHeight(cellsHeight + "px");
				System.out.println("Cells height: " + cellsHeight + "px");

				DraggableColumnHTML columnName = null;
				if (defautlLanguage == null) {
					columnName = new DraggableColumnHTML(column.getName(), column, ColumnType.ROWS, WidgetType.COLUMN, this, false);
				}
				else {
					columnName = new DraggableColumnHTML(column.getTitle(defautlLanguage), column, ColumnType.ROWS, WidgetType.COLUMN, this, false);
				}
				columnName.addStyleName(CSS_COLUMN_DIMENSION);

				DraggableHTMLPanel columnPanel = new DraggableHTMLPanel("", columnName);
				columnPanel.setWidth("100%");
				columnPanel.add(columnName);

				if (rowsPanel.getWidgetCount() < index) {
					rowsPanel.insert(columnPanel, index - 1);
				}
				else {
					rowsPanel.insert(columnPanel, index);
				}
				rowsDragController.makeDraggable(columnPanel, columnName);

				if (needToCheckDB) {
					checkOrCreateDataset(column);
				}

				if (actionType == ActionType.DEFAULT) {
					ActionAddColumnCrosstab action = new ActionAddColumnCrosstab(ActionType.ADD_COLUMN_ROW, this, columnPanel, index);
					reportSheetParent.getPanelParent().addActionToUndoAndClearRedo(action);
				}
				else if (actionType == ActionType.REDO) {
					ActionAddColumnCrosstab action = new ActionAddColumnCrosstab(ActionType.ADD_COLUMN_ROW, this, columnPanel, index);
					reportSheetParent.getPanelParent().replaceLastActionToUndo(action);
				}
				else if (actionType == ActionType.UNDO) {
					ActionTrashColumnCrosstab action = new ActionTrashColumnCrosstab(ActionType.TRASH_COLUMN_GROUP, this, columnPanel, index);
					reportSheetParent.getPanelParent().replaceFirstActionToRedo(action);
				}
			}
			else if (type == ColumnType.CELLS && column.isMeasure()) {
				this.selectedCells.add(column);

				int globalHeight = this.getOffsetHeight() + SizeComponentConstants.SIZE_CROSSTAB_HEIGHT + 2;
				int contentCellsHeight = contentCells.getOffsetHeight() + SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
				int cellsHeight = cells.getOffsetHeight() + SizeComponentConstants.SIZE_CROSSTAB_HEIGHT + 1;
				int rowsHeight = rows.getOffsetHeight() + 1;
				if (this.getOffsetHeight() == 0) {
					int rowsSize = selectedRows.isEmpty() ? 1 : selectedRows.size();
					int cellsSize = selectedCells.isEmpty() ? 1 : selectedCells.size();
					int colsSize = selectedCols.isEmpty() ? 1 : selectedCols.size();

					globalHeight = (rowsSize + cellsSize + colsSize) * SizeComponentConstants.SIZE_CROSSTAB_HEIGHT + (rowsSize + cellsSize + colsSize) * 2;

					if (selectedRows.isEmpty() && selectedCells.isEmpty()) {
						contentCellsHeight = SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
						cellsHeight = SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
					}
					else {
						contentCellsHeight = (rowsSize + cellsSize) * SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
						cellsHeight = (rowsSize + cellsSize) * SizeComponentConstants.SIZE_CROSSTAB_HEIGHT + 1;
					}

					if (selectedRows.size() > 0) {
						rowsHeight = SizeComponentConstants.SIZE_CROSSTAB_HEIGHT * rowsSize;
					}
					else {
						rowsHeight = SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
					}
				}

				this.setHeight(globalHeight + "px");
				System.out.println("Global height: " + globalHeight + "px");
				contentCells.setHeight(contentCellsHeight + "px");
				System.out.println("Content Cells height: " + contentCellsHeight + "px");
				if (firstTime) {
					cellsHeight = cellsHeight + 1;
					firstTime = false;
				}
				cells.setHeight(cellsHeight + "px");
				System.out.println("Cells height: " + cellsHeight + "px");
				rows.setHeight(rowsHeight + "px");
				System.out.println("Rows height: " + rowsHeight + "px");

				DraggableColumnHTML columnName = null;
				if (defautlLanguage == null) {
					columnName = new DraggableColumnHTML(column.getName(), column, ColumnType.CELLS, WidgetType.COLUMN, this, false);
				}
				else {
					columnName = new DraggableColumnHTML(column.getTitle(defautlLanguage), column, ColumnType.CELLS, WidgetType.COLUMN, this, false);
				}
				columnName.addStyleName(CSS_COLUMN_DIMENSION);

				DraggableHTMLPanel columnPanel = new DraggableHTMLPanel("", columnName);
				columnPanel.setWidth("100%");
				columnPanel.add(columnName);

				if (cellsPanel.getWidgetCount() < index) {
					cellsPanel.insert(columnPanel, index - 1);
				}
				else {
					cellsPanel.insert(columnPanel, index);
				}
				measureDragController.makeDraggable(columnPanel, columnName);

				// We add a empty panel on the rowsPanel for layout
				SimplePanel nonePanel = new SimplePanel();
				nonePanel.addStyleName(CSS_DIMENSION_NONE);
				nonePanel.setSize(SizeComponentConstants.SIZE_CROSSTAB_WIDTH + "px", SizeComponentConstants.SIZE_CROSSTAB_HEIGHT + "px");

				contentRows.insert(nonePanel, 0);

				if (needToCheckDB) {
					checkOrCreateDataset(column);
				}

				if (actionType == ActionType.DEFAULT) {
					ActionAddColumnCrosstab action = new ActionAddColumnCrosstab(ActionType.ADD_COLUMN_CELL, this, columnPanel, index);
					reportSheetParent.getPanelParent().addActionToUndoAndClearRedo(action);
				}
				else if (actionType == ActionType.REDO) {
					ActionAddColumnCrosstab action = new ActionAddColumnCrosstab(ActionType.ADD_COLUMN_CELL, this, columnPanel, index);
					reportSheetParent.getPanelParent().replaceLastActionToUndo(action);
				}
				else if (actionType == ActionType.UNDO) {
					ActionTrashColumnCrosstab action = new ActionTrashColumnCrosstab(ActionType.TRASH_COLUMN_CELL, this, columnPanel, index);
					reportSheetParent.getPanelParent().replaceFirstActionToRedo(action);
				}
			}
			else if (type == ColumnType.COLS && column.isDimension()) {
				this.selectedCols.add(column);

				int height = this.getOffsetHeight();
				int contentCellsHeight = contentCols.getOffsetHeight() + SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
				int cellsHeight = cols.getOffsetHeight() + SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
				if (this.getOffsetHeight() == 0) {
					int rowsSize = selectedRows.isEmpty() ? 1 : selectedRows.size();
					int cellsSize = selectedCells.isEmpty() ? 1 : selectedCells.size();
					int colsSize = selectedCols.isEmpty() ? 1 : selectedCols.size();

					height = colsSize * SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;

					if (selectedRows.isEmpty() && selectedCells.isEmpty()) {
						contentCellsHeight = SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
						cellsHeight = SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
					}
					else {
						contentCellsHeight = (rowsSize + cellsSize) * SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
						cellsHeight = (rowsSize + cellsSize) * SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
					}
				}

				this.setHeight(height + SizeComponentConstants.SIZE_CROSSTAB_HEIGHT + "px");

				this.setWidgetPosition(contentCells, SizeComponentConstants.SIZE_CROSSTAB_WIDTH, selectedCols.size() * SizeComponentConstants.SIZE_CROSSTAB_HEIGHT);
				this.setWidgetPosition(contentRows, 0, selectedCols.size() * SizeComponentConstants.SIZE_CROSSTAB_HEIGHT);

				if (selectedCols.size() != 1) {
					contentCols.setHeight(contentCellsHeight + "px");
					System.out.println("Content Cols height: " + contentCellsHeight + "px");

					cols.setHeight(cellsHeight + "px");
					System.out.println("Cols height: " + cellsHeight + "px");

					nonePanel.setHeight(cellsHeight + "px");
				}

				DraggableColumnHTML columnName = null;
				if (defautlLanguage == null) {
					columnName = new DraggableColumnHTML(column.getName(), column, ColumnType.COLS, WidgetType.COLUMN, this, false);
				}
				else {
					columnName = new DraggableColumnHTML(column.getTitle(defautlLanguage), column, ColumnType.COLS, WidgetType.COLUMN, this, false);
				}
				columnName.addStyleName(CSS_COLUMN_DIMENSION);

				DraggableHTMLPanel columnPanel = new DraggableHTMLPanel("", columnName);
				columnPanel.setWidth("100%");
				columnPanel.add(columnName);

				if (colsPanel.getWidgetCount() < index) {
					colsPanel.insert(columnPanel, index - 1);
				}
				else {
					colsPanel.insert(columnPanel, index);
				}
				colsDragController.makeDraggable(columnPanel, columnName);

				if (needToCheckDB) {
					checkOrCreateDataset(column);
				}

				if (actionType == ActionType.DEFAULT) {
					ActionAddColumnCrosstab action = new ActionAddColumnCrosstab(ActionType.ADD_COLUMN_COL, this, columnPanel, index);
					reportSheetParent.getPanelParent().addActionToUndoAndClearRedo(action);
				}
				else if (actionType == ActionType.REDO) {
					ActionAddColumnCrosstab action = new ActionAddColumnCrosstab(ActionType.ADD_COLUMN_COL, this, columnPanel, index);
					reportSheetParent.getPanelParent().replaceLastActionToUndo(action);
				}
				else if (actionType == ActionType.UNDO) {
					ActionTrashColumnCrosstab action = new ActionTrashColumnCrosstab(ActionType.TRASH_COLUMN_COL, this, columnPanel, index);
					reportSheetParent.getPanelParent().replaceFirstActionToRedo(action);
				}
			}
		}
	}

	public void switchWidget(DraggableColumnHTML dragColumn, ColumnType type, int index) {
		if (type == ColumnType.ROWS) {
			int oldIndex = 0;
			for (int i = 0; i < selectedRows.size(); i++) {
				if (selectedRows.get(i).equals(dragColumn.getColumn())) {
					oldIndex = i;
					break;
				}
			}

			selectedRows.remove(dragColumn.getColumn());
			selectedRows.add(index, dragColumn.getColumn());

			ActionMoveColumnInReportWidget action = new ActionMoveColumnInReportWidget(ActionType.MOVE_COLUMN_ROW, this, oldIndex, index);
			getReportSheetParent().getPanelParent().addActionToUndoAndClearRedo(action);
		}
		else if (type == ColumnType.COLS) {
			int oldIndex = 0;
			for (int i = 0; i < selectedCols.size(); i++) {
				if (selectedCols.get(i).equals(dragColumn.getColumn())) {
					oldIndex = i;
					break;
				}
			}

			selectedCols.remove(dragColumn.getColumn());
			selectedCols.add(index, dragColumn.getColumn());

			ActionMoveColumnInReportWidget action = new ActionMoveColumnInReportWidget(ActionType.MOVE_COLUMN_COL, this, oldIndex, index);
			getReportSheetParent().getPanelParent().addActionToUndoAndClearRedo(action);
		}
		else if (type == ColumnType.CELLS) {
			int oldIndex = 0;
			for (int i = 0; i < selectedCells.size(); i++) {
				if (selectedCells.get(i).equals(dragColumn.getColumn())) {
					oldIndex = i;
					break;
				}
			}

			selectedCells.remove(dragColumn.getColumn());
			selectedCells.add(index, dragColumn.getColumn());

			ActionMoveColumnInReportWidget action = new ActionMoveColumnInReportWidget(ActionType.MOVE_COLUMN_CELL, this, oldIndex, index);
			getReportSheetParent().getPanelParent().addActionToUndoAndClearRedo(action);
		}
	}

	public void switchWidget(ColumnType type, int oldIndex, int newIndex) {
		if (type == ColumnType.ROWS) {
			Widget widg = rowsPanel.getWidget(oldIndex);
			Column column = selectedRows.get(oldIndex);

			rowsPanel.remove(oldIndex);
			rowsPanel.insert(widg, newIndex);
			selectedRows.remove(oldIndex);
			selectedRows.add(newIndex, column);
		}
		else if (type == ColumnType.COLS) {
			Widget widg = colsPanel.getWidget(oldIndex);
			Column column = selectedCols.get(oldIndex);

			colsPanel.remove(oldIndex);
			colsPanel.insert(widg, newIndex);
			selectedCols.remove(oldIndex);
			selectedCols.add(newIndex, column);
		}
		else if (type == ColumnType.CELLS) {
			Widget widg = cellsPanel.getWidget(oldIndex);
			Column column = selectedCells.get(oldIndex);

			cellsPanel.remove(oldIndex);
			cellsPanel.insert(widg, newIndex);
			selectedCells.remove(oldIndex);
			selectedCells.add(newIndex, column);
		}
	}

	public void loadComp(CrossComponent component) {
		if (component != null) {
			this.dataset = component.getDataset();

			for (int i = 0; i < component.getCrossCols().size(); i++) {
//				component.getCrossCols().get(i).setType(DataStreamType.DIMENSION_TYPE);
				manageColumns(component.getCrossCols().get(i), ColumnType.COLS, i, false, ActionType.NONE);
			}

			for (int i = 0; i < component.getCrossRows().size(); i++) {
//				component.getCrossRows().get(i).setType(DataStreamType.DIMENSION_TYPE);
				manageColumns(component.getCrossRows().get(i), ColumnType.ROWS, i, false, ActionType.NONE);
			}

			for (int i = 0; i < component.getCrossCells().size(); i++) {
//				component.getCrossCells().get(i).setType(DataStreamType.MEASURE_TYPE);
				manageColumns(component.getCrossCells().get(i), ColumnType.CELLS, i, false, ActionType.NONE);
			}

			if (component.getPrompts() != null) {
				for (IResource prompt : component.getPrompts()) {

					if (prompt instanceof FwrPrompt) {
						((FwrPrompt) prompt).setMetadataId(dataset.getDatasource().getItemId());
						((FwrPrompt) prompt).setModelParent(dataset.getDatasource().getBusinessModel());
						((FwrPrompt) prompt).setPackageParent(dataset.getDatasource().getBusinessPackage());
					}
					else if (prompt instanceof GroupPrompt) {
						((GroupPrompt) prompt).setDatasetInfo(dataset.getDatasource().getItemId(), dataset.getDatasource().getBusinessModel(), dataset.getDatasource().getBusinessPackage());
					}

					addPromptResource(prompt);
				}
			}

			if (component.getFwrFilters() != null) {
				for (FWRFilter filter : component.getFwrFilters()) {
					filter.setMetadataId(dataset.getDatasource().getItemId());
					filter.setModelParent(dataset.getDatasource().getBusinessModel());
					filter.setPackageParent(dataset.getDatasource().getBusinessPackage());

					addFilter(filter);
				}
			}

			if (component.getRelations() != null) {
				for (FwrRelationStrategy relation : component.getRelations()) {
					relation.setMetadataId(dataset.getDatasource().getItemId());
					relation.setModelParent(dataset.getDatasource().getBusinessModel());

					addRelation(relation);
				}
			}
		}
	}

	public void removeWidget(DraggableHTMLPanel columnPanel, boolean removeFromParent, ActionType actionType, boolean deleteFromPanel) {
		int indexInWidget = 0;
		ActionType typeForTrash = null;
		ActionType typeForAdd = null;
		if (columnPanel.getColumn().getColumnType() == ColumnType.ROWS) {
			indexInWidget = selectedRows.indexOf(columnPanel.getColumn().getColumn());
			typeForTrash = ActionType.TRASH_COLUMN_ROW;
			typeForAdd = ActionType.ADD_COLUMN_ROW;

			int globalHeight = this.getOffsetHeight() - SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
			int rowsHeight = rows.getOffsetHeight() - SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
			int cellsHeight = cells.getOffsetHeight() - SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
			int contentCellsHeight = contentCells.getOffsetHeight() - SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;

			this.setHeight(globalHeight + "px");
			System.out.println("Global height: " + globalHeight + "px");
			rows.setHeight(rowsHeight + "px");
			System.out.println("Rows height: " + rowsHeight + "px");
			contentCells.setHeight(contentCellsHeight + "px");
			System.out.println("Content Cells height: " + contentCellsHeight + "px");
			cells.setHeight(cellsHeight + "px");
			System.out.println("Cells height: " + cellsHeight + "px");

			selectedRows.remove(columnPanel.getColumn().getColumn());
			if (deleteFromPanel) {
				rowsPanel.remove(indexInWidget);
			}
			// rowsPanel.remove(columnPanel);
		}
		else if (columnPanel.getColumn().getColumnType() == ColumnType.CELLS) {
			indexInWidget = selectedCells.indexOf(columnPanel.getColumn().getColumn());
			typeForTrash = ActionType.TRASH_COLUMN_CELL;
			typeForAdd = ActionType.ADD_COLUMN_CELL;

			selectedCells.remove(columnPanel.getColumn().getColumn());

			int globalHeight = this.getOffsetHeight() - SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
			int contentCellsHeight = contentCells.getOffsetHeight() - SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
			int cellsHeight = cells.getOffsetHeight() - SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;

			if (selectedCells.size() == 0) {
				contentCellsHeight = contentCellsHeight - 1;
				cellsHeight = cellsHeight - 1;
			}

			this.setHeight(globalHeight + "px");
			System.out.println("Global height: " + globalHeight + "px");
			contentCells.setHeight(contentCellsHeight + "px");
			System.out.println("Content Cells height: " + contentCellsHeight + "px");
			cells.setHeight(cellsHeight + "px");
			System.out.println("Cells height: " + cellsHeight + "px");

			contentRows.remove(0);

			// cellsPanel.remove(columnPanel);
			if (deleteFromPanel) {
				cellsPanel.remove(indexInWidget);
			}
		}
		else if (columnPanel.getColumn().getColumnType() == ColumnType.COLS) {
			indexInWidget = selectedCols.indexOf(columnPanel.getColumn().getColumn());
			typeForTrash = ActionType.TRASH_COLUMN_COL;
			typeForAdd = ActionType.ADD_COLUMN_COL;

			selectedCols.remove(columnPanel.getColumn().getColumn());

			int height = this.getOffsetHeight();
			int contentColsHeight = contentCols.getOffsetHeight();
			int colsHeight = cols.getOffsetHeight();
			if (selectedCols.size() > 0) {
				height = height - SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
				contentColsHeight = contentColsHeight - SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;
				colsHeight = colsHeight - SizeComponentConstants.SIZE_CROSSTAB_HEIGHT;

				this.setWidgetPosition(contentCells, SizeComponentConstants.SIZE_CROSSTAB_WIDTH, selectedCols.size() * SizeComponentConstants.SIZE_CROSSTAB_HEIGHT);
				this.setWidgetPosition(contentRows, 0, selectedCols.size() * SizeComponentConstants.SIZE_CROSSTAB_HEIGHT);
			}

			this.setHeight(height + "px");
			this.contentCols.setHeight(contentColsHeight + "px");
			System.out.println("Content Cols height: " + contentColsHeight + "px");
			this.cols.setHeight(colsHeight + "px");
			System.out.println("Cols height: " + colsHeight + "px");
			this.nonePanel.setHeight(colsHeight + "px");

			// colsPanel.remove(columnPanel);
			if (deleteFromPanel) {
				colsPanel.remove(indexInWidget);
			}
		}

		if (actionType == ActionType.DEFAULT) {
			ActionTrashColumnCrosstab action = new ActionTrashColumnCrosstab(typeForTrash, this, columnPanel, indexInWidget);
			reportSheetParent.getPanelParent().addActionToUndoAndClearRedo(action);
		}
		else if (actionType == ActionType.REDO) {
			ActionTrashColumnCrosstab action = new ActionTrashColumnCrosstab(typeForTrash, this, columnPanel, indexInWidget);
			reportSheetParent.getPanelParent().replaceLastActionToUndo(action);
		}
		else if (actionType == ActionType.UNDO) {
			ActionAddColumnCrosstab action = new ActionAddColumnCrosstab(typeForAdd, this, columnPanel, indexInWidget);
			reportSheetParent.getPanelParent().replaceFirstActionToRedo(action);
		}

		checkOrDeleteDataset();
	}

	public List<Column> getSelectedCols() {
		return selectedCols;
	}

	public List<Column> getSelectedRows() {
		return selectedRows;
	}

	public List<Column> getSelectedCells() {
		return selectedCells;
	}

	public DataSet getDataset() {
		return dataset;
	}

	private void updateColumnContent() {
		WidgetCollection widgetCol = this.getChildren();
		for (Widget widg : widgetCol) {
			if (widg instanceof DraggableHTMLPanel) {
				DraggableHTMLPanel column = (DraggableHTMLPanel) widg;
				column.getColumn().setText(column.getColumn().getColumn().getTitle(defaultLanguage));
			}
		}
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
		updateColumnContent();
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	private void checkOrCreateDataset(Column column) {
		if (dataset == null) {
			dataset = createFwrDataSet(column);
			dataset.addColumn(column);
			column.setDatasetParent(dataset);
		}
		else {
			if (!dataset.getColumns().isEmpty()) {
				if (!column.getBusinessPackageParent().equals(dataset.getColumns().get(0).getBusinessPackageParent())) {
					DataSet dataset1 = dataset;
					DataSet dataset2 = createFwrDataSet(column);
					dataset2.addColumn(column);
					column.setDatasetParent(dataset2);

					createJoinDataset(dataset1, dataset2);
				}
				else {
					dataset.addColumn(column);
					column.setDatasetParent(dataset);
				}
			}
			else {
				dataset.addColumn(column);
				column.setDatasetParent(dataset);
			}
		}
	}

	private DataSet createFwrDataSet(Column column) {

		InfoUser infoUser = Bpm_fwr.getInstance().getInfoUser();

		// Datasource creation
		DataSource dataS = new DataSource();
		dataS.setBusinessModel(column.getBusinessModelParent());
		dataS.setBusinessPackage(column.getBusinessPackageParent());
		dataS.setConnectionName("Default");
		dataS.setGroup(infoUser.getGroup().getName());
		dataS.setItemId(column.getMetadataId());
		dataS.setRepositoryId(infoUser.getRepository().getId());
		dataS.setName("datasource_" + System.currentTimeMillis());
		dataS.setPassword(infoUser.getUser().getPassword());
		dataS.setUrl(infoUser.getRepository().getUrl());
		dataS.setUser(infoUser.getUser().getLogin());
		dataS.setOnOlap(getReportSheetParent().getPanelParent().getPanelparent().searchedModel(column.getBusinessModelParent()).isOnOlap());

		DataSet dataset = new DataSet();
		dataset.setLanguage("fr");
		dataset.setDatasource(dataS);
		dataset.setName("dataset_" + System.currentTimeMillis());
		return dataset;
	}

	private void createJoinDataset(DataSet dataset1, DataSet dataset2) {
		JoinDatasetDialogBox dial = new JoinDatasetDialogBox(dataset1, dataset2);
		dial.addFinishListener(finishListener);
		dial.center();
	}

	private FinishListener finishListener = new FinishListener() {

		@Override
		public void onFinish(Object result, Object source, String result1) {
			if (result instanceof DataSet) {
				dataset = (DataSet) result;
			}

		}
	};

	private void checkOrDeleteDataset() {
		if (selectedCells.isEmpty() && selectedCols.isEmpty() && selectedRows.isEmpty()) {
			dataset = null;
		}
	}

	@Override
	public int getWidgetHeight() {
		return getOffsetHeight();
	}

	@Override
	public void widgetToTrash(Object widget) {
		if (widget instanceof DraggableHTMLPanel) {
			DraggableHTMLPanel columnPanel = (DraggableHTMLPanel) widget;
			removeWidget(columnPanel, false, ActionType.DEFAULT, false);
		}
		else if (widget instanceof ReportWidget) {
			if (gridWidget != null) {
				gridWidget.widgetToTrash(widget);
			}
			else {
				reportSheetParent.widgetToTrash(widget);
			}
		}
	}
}
