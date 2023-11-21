package bpm.faweb.client.reporter.widgets;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.dnd.DraggableTreeItem;
import bpm.faweb.client.gwt.dnd.client.PickupDragController;
import bpm.faweb.client.gwt.dnd.client.drop.DropController;
import bpm.faweb.client.reporter.ColumnType;
import bpm.faweb.client.reporter.CrossTabCell;
import bpm.faweb.client.reporter.DraggableColumnHTML;
import bpm.faweb.client.reporter.DraggableHTMLPanel;
import bpm.faweb.client.reporter.controllers.CustomVerticalPanelDropController;
import bpm.faweb.client.reporter.controllers.ReporterVerticalPanelDropController;

import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CrossTabWidget extends AbsolutePanel implements HasMouseDownHandlers {
	public static final int SIZE_CROSSTAB_HEIGHT = 40;
	public static final int SIZE_CROSSTAB_WIDTH = 300;
	public static final int SIZE_CROSSTAB_INITIAL = 82;
	
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

	private PickupDragController dragCtr;

	public CrossTabCell cols, rows, cells;
	private VerticalPanel cellsPanel, rowsPanel, colsPanel;

	// We add to those panel the cell which we can't interact with
	private VerticalPanel contentRows, contentCells, contentCols;
	private SimplePanel nonePanel;

	private List<DraggableTreeItem> selectedCols = new ArrayList<DraggableTreeItem>();
	private List<DraggableTreeItem> selectedRows = new ArrayList<DraggableTreeItem>();
	private List<DraggableTreeItem> selectedCells = new ArrayList<DraggableTreeItem>();

	private boolean firstTime = true;

	public CrossTabWidget(PickupDragController dragCtr) {
		this.dragCtr = dragCtr;
		this.setHeight(SIZE_CROSSTAB_INITIAL + "px");

		nonePanel = new SimplePanel();
		nonePanel.addStyleName(CSS_NONE);
		nonePanel.setSize(SIZE_CROSSTAB_WIDTH + "px", SIZE_CROSSTAB_HEIGHT + "px");

		colsPanel = new VerticalPanel();
		colsPanel.addStyleName("test");
		colsPanel.setSize("100%", "100%");

		cols = new CrossTabCell(this, ColumnType.COLS);
		cols.setSize(SIZE_CROSSTAB_WIDTH + "px", SIZE_CROSSTAB_HEIGHT + "px");
		cols.add(colsPanel);

		contentCols = new VerticalPanel();
		contentCols.addStyleName(CSS_COLS);
		contentCols.add(cols);

		rowsPanel = new VerticalPanel();
		rowsPanel.setSize("100%", SIZE_CROSSTAB_HEIGHT + "px");

		rows = new CrossTabCell(this, ColumnType.ROWS);
		rows.setSize(SIZE_CROSSTAB_WIDTH + "px", SIZE_CROSSTAB_HEIGHT - 1 + "px");
		rows.add(rowsPanel);

		contentRows = new VerticalPanel();
		contentRows.addStyleName(CSS_ROWS);
		contentRows.add(rows);

		cellsPanel = new VerticalPanel();
		cellsPanel.setSize("100%", SIZE_CROSSTAB_HEIGHT + "px");

		cells = new CrossTabCell(this, ColumnType.CELLS);
		cells.setSize(SIZE_CROSSTAB_WIDTH + "px", SIZE_CROSSTAB_HEIGHT - 1 + "px");
		cells.add(cellsPanel);

		contentCells = new VerticalPanel();
		contentCells.addStyleName(CSS_CELLS);
		contentCells.add(cells);

		rowsDropController = new CustomVerticalPanelDropController(rows, rowsPanel);
		colsDropController = new CustomVerticalPanelDropController(cols, colsPanel);
		cellsDropController = new CustomVerticalPanelDropController(cells, cellsPanel);

		dropControllerRows = new ReporterVerticalPanelDropController(rows);
		dropControllerCells = new ReporterVerticalPanelDropController(cells);
		dropControllerCols = new ReporterVerticalPanelDropController(cols);

		
		this.add(nonePanel);
		this.add(contentCols);
		this.add(contentRows);
		this.add(contentCells);
	}

	@Override
	protected void onAttach() {
		dragCtr.registerDropController(dropControllerRows);
		dragCtr.registerDropController(dropControllerCells);
		dragCtr.registerDropController(dropControllerCols);

//		dragCtr.registerDropController(rowsDropController);
//		dragCtr.registerDropController(colsDropController);
//		dragCtr.registerDropController(cellsDropController);
		super.onAttach();
	}

	@Override
	protected void onDetach() {
		dragCtr.unregisterDropController(dropControllerRows);
		dragCtr.unregisterDropController(dropControllerCells);
		dragCtr.unregisterDropController(dropControllerCols);

		dragCtr.unregisterDropController(rowsDropController);
		dragCtr.unregisterDropController(colsDropController);
		dragCtr.unregisterDropController(cellsDropController);
		super.onDetach();
	}

	private boolean widgetContainColumn(DraggableTreeItem column) {
		for (DraggableTreeItem col : selectedCols) {
			if (col.getUname().equals(column.getUname())) {
				return true;
			}
		}

		for (DraggableTreeItem col : selectedRows) {
			if (col.getUname().equals(column.getUname())) {
				return true;
			}
		}

		for (DraggableTreeItem col : selectedCells) {
			if (col.getUname().equals(column.getUname())) {
				return true;
			}
		}

		return false;
	}

	public void manageColumns(DraggableTreeItem column, ColumnType type, int index, boolean needToCheckDB) {
		if (!widgetContainColumn(column)) {

			if (type == ColumnType.ROWS && !column.getUname().contains("[Measures]")) {
				this.selectedRows.add(column);

				int globalHeight = this.getOffsetHeight() + SIZE_CROSSTAB_HEIGHT + 2;
				int rowsHeight = rows.getOffsetHeight() + SIZE_CROSSTAB_HEIGHT;
				int cellsHeight = cells.getOffsetHeight() + SIZE_CROSSTAB_HEIGHT;
				if (this.getOffsetHeight() == 0) {
					int rowsSize = selectedRows.isEmpty() ? 1 : selectedRows.size();
					int cellsSize = selectedCells.isEmpty() ? 1 : selectedCells.size();
					int colsSize = selectedCols.isEmpty() ? 1 : selectedCols.size();

					globalHeight = (rowsSize + cellsSize + colsSize) * SIZE_CROSSTAB_HEIGHT + (rowsSize + cellsSize + colsSize) * 2;

					if (selectedRows.isEmpty() && selectedCells.isEmpty()) {
						rowsHeight = SIZE_CROSSTAB_HEIGHT;
						cellsHeight = SIZE_CROSSTAB_HEIGHT;
					}
					else {
						rowsHeight = (rowsSize + cellsSize) * SIZE_CROSSTAB_HEIGHT;
						cellsHeight = (rowsSize + cellsSize) * SIZE_CROSSTAB_HEIGHT;
					}
				}
				this.setHeight(globalHeight + "px");

				rows.setHeight(rowsHeight + "px");
				cells.setHeight(cellsHeight + "px");

				DraggableColumnHTML columnName = new DraggableColumnHTML(column.getUname(), column, ColumnType.ROWS, this);
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
				dragCtr.makeDraggable(columnPanel, columnName);
			}
			else if (type == ColumnType.CELLS && column.getUname().contains("[Measures]")) {
				this.selectedCells.add(column);

				int globalHeight = this.getOffsetHeight() + SIZE_CROSSTAB_HEIGHT + 2;
				int contentCellsHeight = contentCells.getOffsetHeight() + SIZE_CROSSTAB_HEIGHT;
				int cellsHeight = cells.getOffsetHeight() + SIZE_CROSSTAB_HEIGHT + 1;
				int rowsHeight = rows.getOffsetHeight() + 1;
				if (this.getOffsetHeight() == 0) {
					int rowsSize = selectedRows.isEmpty() ? 1 : selectedRows.size();
					int cellsSize = selectedCells.isEmpty() ? 1 : selectedCells.size();
					int colsSize = selectedCols.isEmpty() ? 1 : selectedCols.size();

					globalHeight = (rowsSize + cellsSize + colsSize) * SIZE_CROSSTAB_HEIGHT + (rowsSize + cellsSize + colsSize) * 2;

					if (selectedRows.isEmpty() && selectedCells.isEmpty()) {
						contentCellsHeight = SIZE_CROSSTAB_HEIGHT;
						cellsHeight = SIZE_CROSSTAB_HEIGHT;
					}
					else {
						contentCellsHeight = (rowsSize + cellsSize) * SIZE_CROSSTAB_HEIGHT;
						cellsHeight = (rowsSize + cellsSize) * SIZE_CROSSTAB_HEIGHT + 1;
					}

					if (selectedRows.size() > 0) {
						rowsHeight = SIZE_CROSSTAB_HEIGHT * rowsSize;
					}
					else {
						rowsHeight = SIZE_CROSSTAB_HEIGHT;
					}
				}

				this.setHeight(globalHeight + "px");
				
				contentCells.setHeight(contentCellsHeight + "px");
				if (firstTime) {
					cellsHeight = cellsHeight + 1;
					firstTime = false;
				}
				cells.setHeight(cellsHeight + "px");
				rows.setHeight(rowsHeight + "px");

				DraggableColumnHTML columnName = new DraggableColumnHTML(column.getUname(), column, ColumnType.CELLS, this);
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
				dragCtr.makeDraggable(columnPanel, columnName);

				// We add a empty panel on the rowsPanel for layout
				SimplePanel nonePanel = new SimplePanel();
				nonePanel.addStyleName(CSS_DIMENSION_NONE);
				nonePanel.setSize(SIZE_CROSSTAB_WIDTH + "px", SIZE_CROSSTAB_HEIGHT + "px");

				contentRows.insert(nonePanel, 0);
			}
			else if (type == ColumnType.COLS && !column.getUname().contains("[Measures]")) {
				this.selectedCols.add(column);

				int height = this.getOffsetHeight();
				int contentCellsHeight = contentCols.getOffsetHeight() + SIZE_CROSSTAB_HEIGHT;
				int cellsHeight = cols.getOffsetHeight() + SIZE_CROSSTAB_HEIGHT;
				if (this.getOffsetHeight() == 0) {
					int rowsSize = selectedRows.isEmpty() ? 1 : selectedRows.size();
					int cellsSize = selectedCells.isEmpty() ? 1 : selectedCells.size();
					int colsSize = selectedCols.isEmpty() ? 1 : selectedCols.size();

					height = colsSize * SIZE_CROSSTAB_HEIGHT;

					if (selectedRows.isEmpty() && selectedCells.isEmpty()) {
						contentCellsHeight = SIZE_CROSSTAB_HEIGHT;
						cellsHeight = SIZE_CROSSTAB_HEIGHT;
					}
					else {
						contentCellsHeight = (rowsSize + cellsSize) * SIZE_CROSSTAB_HEIGHT;
						cellsHeight = (rowsSize + cellsSize) * SIZE_CROSSTAB_HEIGHT;
					}
				}

				this.setHeight(height + SIZE_CROSSTAB_HEIGHT + "px");

				this.setWidgetPosition(contentCells, SIZE_CROSSTAB_WIDTH, selectedCols.size() * SIZE_CROSSTAB_HEIGHT);
				this.setWidgetPosition(contentRows, 0, selectedCols.size() * SIZE_CROSSTAB_HEIGHT);

				if (selectedCols.size() != 1) {
					contentCols.setHeight(contentCellsHeight + "px");
					cols.setHeight(cellsHeight + "px");
					nonePanel.setHeight(cellsHeight + "px");
				}

				DraggableColumnHTML columnName = new DraggableColumnHTML(column.getUname(), column, ColumnType.COLS, this);
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
				dragCtr.makeDraggable(columnPanel, columnName);
			}
		}
	}

	public void switchWidget(DraggableColumnHTML dragColumn, ColumnType type, int index) {
		if (type == ColumnType.ROWS) {
			for (int i = 0; i < selectedRows.size(); i++) {
				if (selectedRows.get(i).equals(dragColumn.getColumn())) {
					break;
				}
			}

			selectedRows.remove(dragColumn.getColumn());
			selectedRows.add(index, dragColumn.getColumn());
		}
		else if (type == ColumnType.COLS) {
			for (int i = 0; i < selectedCols.size(); i++) {
				if (selectedCols.get(i).equals(dragColumn.getColumn())) {
					break;
				}
			}

			selectedCols.remove(dragColumn.getColumn());
			selectedCols.add(index, dragColumn.getColumn());
		}
		else if (type == ColumnType.CELLS) {
			for (int i = 0; i < selectedCells.size(); i++) {
				if (selectedCells.get(i).equals(dragColumn.getColumn())) {
					break;
				}
			}

			selectedCells.remove(dragColumn.getColumn());
			selectedCells.add(index, dragColumn.getColumn());
		}
	}

	public void switchWidget(ColumnType type, int oldIndex, int newIndex) {
		if (type == ColumnType.ROWS) {
			Widget widg = rowsPanel.getWidget(oldIndex);

			rowsPanel.remove(oldIndex);
			rowsPanel.insert(widg, newIndex);
			selectedRows.remove(oldIndex);
		}
		else if (type == ColumnType.COLS) {
			Widget widg = colsPanel.getWidget(oldIndex);
			DraggableTreeItem column = selectedCols.get(oldIndex);

			colsPanel.remove(oldIndex);
			colsPanel.insert(widg, newIndex);
			selectedCols.remove(oldIndex);
			selectedCols.add(newIndex, column);
		}
		else if (type == ColumnType.CELLS) {
			Widget widg = cellsPanel.getWidget(oldIndex);
			DraggableTreeItem column = selectedCells.get(oldIndex);

			cellsPanel.remove(oldIndex);
			cellsPanel.insert(widg, newIndex);
			selectedCells.remove(oldIndex);
			selectedCells.add(newIndex, column);
		}
	}

	public void removeWidget(DraggableHTMLPanel columnPanel, boolean removeFromParent, boolean deleteFromPanel) {
		int indexInWidget = 0;
		if (columnPanel.getColumn().getColumnType() == ColumnType.ROWS) {
			indexInWidget = selectedRows.indexOf(columnPanel.getColumn().getColumn());

			int globalHeight = this.getOffsetHeight() - SIZE_CROSSTAB_HEIGHT;
			int rowsHeight = rows.getOffsetHeight() - SIZE_CROSSTAB_HEIGHT;
			int cellsHeight = cells.getOffsetHeight() - SIZE_CROSSTAB_HEIGHT;
			int contentCellsHeight = contentCells.getOffsetHeight() - SIZE_CROSSTAB_HEIGHT;

			this.setHeight(globalHeight + "px");
			rows.setHeight(rowsHeight + "px");
			contentCells.setHeight(contentCellsHeight + "px");
			cells.setHeight(cellsHeight + "px");

			selectedRows.remove(columnPanel.getColumn().getColumn());
			if (deleteFromPanel) {
				rowsPanel.remove(indexInWidget);
			}
			 rowsPanel.remove(columnPanel);
		}
		else if (columnPanel.getColumn().getColumnType() == ColumnType.CELLS) {
			indexInWidget = selectedCells.indexOf(columnPanel.getColumn().getColumn());

			selectedCells.remove(columnPanel.getColumn().getColumn());

			int globalHeight = this.getOffsetHeight() - SIZE_CROSSTAB_HEIGHT;
			int contentCellsHeight = contentCells.getOffsetHeight() - SIZE_CROSSTAB_HEIGHT;
			int cellsHeight = cells.getOffsetHeight() - SIZE_CROSSTAB_HEIGHT;

			if (selectedCells.size() == 0) {
				contentCellsHeight = contentCellsHeight - 1;
				cellsHeight = cellsHeight - 1;
			}

			this.setHeight(globalHeight + "px");
			contentCells.setHeight(contentCellsHeight + "px");
			cells.setHeight(cellsHeight + "px");

			contentRows.remove(0);

			 cellsPanel.remove(columnPanel);
			if (deleteFromPanel) {
				cellsPanel.remove(indexInWidget);
			}
		}
		else if (columnPanel.getColumn().getColumnType() == ColumnType.COLS) {
			indexInWidget = selectedCols.indexOf(columnPanel.getColumn().getColumn());

			selectedCols.remove(columnPanel.getColumn().getColumn());

			int height = this.getOffsetHeight();
			int contentColsHeight = contentCols.getOffsetHeight();
			int colsHeight = cols.getOffsetHeight();
			if (selectedCols.size() > 0) {
				height = height - SIZE_CROSSTAB_HEIGHT;
				contentColsHeight = contentColsHeight - SIZE_CROSSTAB_HEIGHT;
				colsHeight = colsHeight - SIZE_CROSSTAB_HEIGHT;

				this.setWidgetPosition(contentCells, SIZE_CROSSTAB_WIDTH, selectedCols.size() * SIZE_CROSSTAB_HEIGHT);
				this.setWidgetPosition(contentRows, 0, selectedCols.size() * SIZE_CROSSTAB_HEIGHT);
			}

			this.setHeight(height + "px");
			this.contentCols.setHeight(contentColsHeight + "px");
			this.cols.setHeight(colsHeight + "px");
			this.nonePanel.setHeight(colsHeight + "px");

			 colsPanel.remove(columnPanel);
			if (deleteFromPanel) {
				colsPanel.remove(indexInWidget);
			}
		}
	}

	public List<DraggableTreeItem> getSelectedCols() {
		return selectedCols;
	}

	public List<DraggableTreeItem> getSelectedRows() {
		return selectedRows;
	}

	public List<DraggableTreeItem> getSelectedCells() {
		return selectedCells;
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	public void widgetToTrash(Object widget) {
		if (widget instanceof DraggableHTMLPanel) {
			DraggableHTMLPanel columnPanel = (DraggableHTMLPanel) widget;
			removeWidget(columnPanel, false, false);
		}
	}
	
	public void refreshGrid(){
		nonePanel.getElement().setAttribute("style", "height: 40px !important;");
		contentCols.getElement().setAttribute("style", "height: 40px !important;");
		contentRows.getElement().setAttribute("style", "height: 40px !important;");
		contentCells.getElement().setAttribute("style", "height: 40px !important;");
		 cols.getElement().setAttribute("style", "height: 40px !important;");
		 rows.getElement().setAttribute("style", "height: 40px !important;");
		rowsPanel.clear();
		colsPanel.clear();
		cellsPanel.clear();
	}
	
}
