package bpm.fwr.client.widgets;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.components.ChartComponent;
import bpm.fwr.api.beans.components.IChart;
import bpm.fwr.api.beans.components.ImageComponent;
import bpm.fwr.api.beans.components.TextHTMLComponent;
import bpm.fwr.api.beans.components.VanillaChartComponent;
import bpm.fwr.api.beans.components.VanillaMapComponent;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.client.action.Action;
import bpm.fwr.client.action.ActionAddWidgetToGrid;
import bpm.fwr.client.action.ActionTrashWidgetToGrid;
import bpm.fwr.client.action.ActionType;
import bpm.fwr.client.draggable.dropcontrollers.TableCellDropController;
import bpm.fwr.client.draggable.widgets.DraggablePaletteItem;
import bpm.fwr.client.panels.ReportSheet;
import bpm.fwr.client.utils.ChartUtils;
import bpm.fwr.client.utils.SizeComponentConstants;
import bpm.fwr.client.utils.WidgetType;
import bpm.fwr.client.wizard.chart.ChartCreationWizard;
import bpm.fwr.client.wizard.map.VanillaMapsWizard;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.listeners.FinishListener;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;

public class GridWidget extends ReportWidget implements HasMouseDownHandlers {
	private static final String CSS_CROSSTAB = "crosstabPostion";
	private static final String CSS_WIDGET = "widgetMargin";
	private static final String CSS_REPORT_WIDGET_ROW = "reportWidgetRow";
	private static final String CSS_CELL_TABLE = "cellTable";

	private int SIZE_GRID_COMPONENT_DEFAULT = 20;

	// private int cellWidth;
	private int cellHeight;

	private List<List<GridCell>> grid = new ArrayList<List<GridCell>>();
	private GridCell selectedCell;

	private PickupDragController dataDragController, thirdBinDragC;
	private PickupDragController groupDragController, detailDragController, paletteDragController;
	private List<DropController> cellDropControllers = new ArrayList<DropController>();
	private ReportSheet reportSheetParent;

	private boolean first = true;

	public GridWidget(PickupDragController paletteDragController, PickupDragController dataDragController, PickupDragController groupDragController, PickupDragController detailDragController, PickupDragController thirdBinDragC, GridOptions options, int width, int height, ReportSheet reportSheetParent) throws Exception {
		super(reportSheetParent, WidgetType.GRID, width);

		this.reportSheetParent = reportSheetParent;

		this.dataDragController = dataDragController;
		this.groupDragController = groupDragController;
		this.detailDragController = detailDragController;
		this.paletteDragController = paletteDragController;
		this.thirdBinDragC = thirdBinDragC;

		// this.cellWidth = ((width * 98 / 100)) / options.getNbCols();
		this.cellHeight = (height - 5) / options.getNbRows();
		if (cellHeight > SIZE_GRID_COMPONENT_DEFAULT) {
			this.cellHeight = SIZE_GRID_COMPONENT_DEFAULT;
		}
		else {
			throw new Exception("Not enough place to put the grid.");
		}

		for (int i = 0; i < options.getNbRows(); i++) {
			HorizontalPanel panel = new HorizontalPanel();
			panel.setStyleName(CSS_REPORT_WIDGET_ROW);

			List<GridCell> cols = new ArrayList<GridCell>();
			for (int j = 0; j < options.getNbCols(); j++) {

				GridCell cell = new GridCell(this, j, i);
				cell.setStyleName(CSS_CELL_TABLE);
				cell.setSize("100%", cellHeight + "px");

				DropController cellDropController = new TableCellDropController(cell);
				paletteDragController.registerDropController(cellDropController);
				cellDropControllers.add(cellDropController);

				panel.add(cell);
				cols.add(cell);
			}

			grid.add(cols);
			this.add(panel);
		}
	}

	public List<List<GridCell>> getGridComponent() {
		return grid;
	}

	public void refreshGridSize() {
		for (List<GridCell> row : grid) {
			int maxSizeComponent = SIZE_GRID_COMPONENT_DEFAULT;
			for (GridCell tableCell : row) {
				if (tableCell.getWidget() != null && maxSizeComponent < tableCell.getWidget().getWidgetHeight()) {
					maxSizeComponent = tableCell.getWidget().getWidgetHeight();
				}
			}

			for (GridCell tableCell : row) {
				tableCell.setHeight(maxSizeComponent + 10 + "px");
			}
		}
	}

	public void manageWidget(DraggablePaletteItem dragHtml, int col, int row) {
		selectedCell = grid.get(row).get(col);
		switch (dragHtml.getType()) {
		case CHART:
			showChartCreationWizard();
			break;
		case LIST:
			showDialogList();
			break;
		case CROSSTAB:
			addCrossTab();
			break;
		case IMAGE:
			addImage(null);
			break;
		case LABEL:
			addLabel(null);
			break;
		case VANILLA_MAP:
			showVanillaMapCreationWizard();
			break;
		default:
			break;
		}
	}

	private void showChartCreationWizard() {
		List<DataSet> dsAvailable = reportSheetParent.getAvailableDatasets();

		ChartCreationWizard chartCreationWizard = new ChartCreationWizard(reportSheetParent, null, dsAvailable);
		chartCreationWizard.addFinishListener(finishListener);
		chartCreationWizard.center();
	}

	private void showDialogList() {
		final InformationsDialog dial = new InformationsDialog("Type of List", "Normal", "Automatic Grouping", "Please, choose the type of list you want.", true);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				addList(!dial.isConfirm());
			}
		});
		dial.center();
	}

	private void showVanillaMapCreationWizard() {
		List<DataSet> dsAvailable = reportSheetParent.getAvailableDatasets();

		VanillaMapsWizard mapCreationWizard = new VanillaMapsWizard(reportSheetParent, null, dsAvailable);
		mapCreationWizard.addFinishListener(finishListener);
		mapCreationWizard.center();
	}

	private void addList(boolean automaticGroupingList) {
		ListWidget list = new ListWidget(groupDragController, dataDragController, detailDragController, reportSheetParent, automaticGroupingList);
		list.setSelectedCell(selectedCell);

		this.selectedCell.setWidget(list);
		this.thirdBinDragC.makeDraggable(list);

		ActionAddWidgetToGrid action = new ActionAddWidgetToGrid(ActionType.ADD_LIST, this, list, selectedCell.getCol(), selectedCell.getRow());
		reportSheetParent.getPanelParent().addActionToUndoAndClearRedo(action);

		refreshGridSize();
	}

	private void addCrossTab() {
		CrossTabWidget crosstab = new CrossTabWidget(dataDragController, groupDragController, detailDragController, thirdBinDragC, SizeComponentConstants.WIDTH_WORKING_AREA, reportSheetParent, this);
		crosstab.addStyleName(CSS_CROSSTAB);
		crosstab.addStyleName(CSS_WIDGET);
		crosstab.setSelectedCell(selectedCell);

		this.selectedCell.setWidget(crosstab);
		this.thirdBinDragC.makeDraggable(crosstab);

		ActionAddWidgetToGrid action = new ActionAddWidgetToGrid(ActionType.ADD_CROSSTAB, this, crosstab, selectedCell.getCol(), selectedCell.getRow());
		reportSheetParent.getPanelParent().addActionToUndoAndClearRedo(action);

		refreshGridSize();
	}

	private void addImage(ImageComponent image) {
		ImageWidget imgWidget = new ImageWidget(reportSheetParent, SizeComponentConstants.WIDTH_WORKING_AREA, finishListener, this);
		imgWidget.addStyleName(CSS_WIDGET);
		imgWidget.setSelectedCell(selectedCell);

		if (image != null) {
			imgWidget.setUrlImg(image.getUrl());
		}
		else {
			imgWidget.showDialogUrl(false);
		}

		this.selectedCell.setWidget(imgWidget);
		this.thirdBinDragC.makeDraggable(imgWidget);

		ActionAddWidgetToGrid action = new ActionAddWidgetToGrid(ActionType.ADD_IMAGE, this, imgWidget, selectedCell.getCol(), selectedCell.getRow());
		reportSheetParent.getPanelParent().addActionToUndoAndClearRedo(action);

		refreshGridSize();
	}

	private void addLabel(TextHTMLComponent label) {
		LabelWidget lblWidget = new LabelWidget(reportSheetParent, SizeComponentConstants.WIDTH_WORKING_AREA, this);
		lblWidget.addStyleName(CSS_WIDGET);
		lblWidget.setSelectedCell(selectedCell);

		if (label != null) {
			lblWidget.setText(label.getTextContent());
		}
		else {
			lblWidget.showDialogText();
		}

		this.selectedCell.setWidget(lblWidget);
		this.thirdBinDragC.makeDraggable(lblWidget);

		ActionAddWidgetToGrid action = new ActionAddWidgetToGrid(ActionType.ADD_LABEL, this, lblWidget, selectedCell.getCol(), selectedCell.getRow());
		reportSheetParent.getPanelParent().addActionToUndoAndClearRedo(action);

		refreshGridSize();
	}

	private void addChart(IChart comp) {
		ChartWidget chartWidget = null;
		if (comp instanceof VanillaChartComponent) {
			chartWidget = new ChartWidget(reportSheetParent, comp, ChartUtils.getImageForChart(((VanillaChartComponent) comp).getChartType().getType(), ((VanillaChartComponent) comp).getOptions()), SizeComponentConstants.WIDTH_WORKING_AREA, this);
		}
		else {
			chartWidget = new ChartWidget(reportSheetParent, comp, ChartUtils.getImageForChart(((ChartComponent) comp).getChartType()), SizeComponentConstants.WIDTH_WORKING_AREA, this);
		}
		chartWidget.addStyleName(CSS_WIDGET);
		chartWidget.setSelectedCell(selectedCell);
		
		this.selectedCell.setWidget(chartWidget);
		this.thirdBinDragC.makeDraggable(chartWidget);

		ActionAddWidgetToGrid action = new ActionAddWidgetToGrid(ActionType.ADD_CHART, this, chartWidget, selectedCell.getCol(), selectedCell.getRow());
		reportSheetParent.getPanelParent().addActionToUndoAndClearRedo(action);
	}

	private void addVanillaMap(VanillaMapComponent map) {
		MapWidget mapWidget = new MapWidget(reportSheetParent, map, SizeComponentConstants.WIDTH_WORKING_AREA, this);
		mapWidget.addStyleName(CSS_WIDGET);
		mapWidget.setSelectedCell(selectedCell);

		this.selectedCell.setWidget(mapWidget);
		this.thirdBinDragC.makeDraggable(mapWidget);

		ActionAddWidgetToGrid action = new ActionAddWidgetToGrid(ActionType.ADD_VANILLA_MAP, this, mapWidget, selectedCell.getCol(), selectedCell.getRow());
		reportSheetParent.getPanelParent().addActionToUndoAndClearRedo(action);

		refreshGridSize();
	}

	private FinishListener finishListener = new FinishListener() {

		@Override
		public void onFinish(Object result, Object source, String result1) {
			if (source instanceof ChartCreationWizard) {
				if (result instanceof IChart) {
					addChart((IChart) result);
				}
			}
			else if (source instanceof VanillaMapsWizard) {
				if (result instanceof VanillaMapComponent) {
					addVanillaMap((VanillaMapComponent) result);
				}
			}

			refreshGridSize();

		}
	};

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	@Override
	protected void onDetach() {
		for (DropController dropController : cellDropControllers) {
			paletteDragController.unregisterDropController(dropController);
		}
		super.onDetach();
	}

	@Override
	protected void onAttach() {
		if (!first) {
			for (DropController dropController : cellDropControllers) {
				paletteDragController.registerDropController(dropController);
			}
		}
		else {
			first = false;
		}
		super.onAttach();
	}

	@Override
	public int getWidgetHeight() {
		return getOffsetHeight();
	}

	@Override
	public void widgetToTrash(Object widget) {
		if (widget instanceof ReportWidget) {
			GridCell selectedCell = ((ReportWidget) widget).getSelectedCell();
			if (selectedCell != null) {
				selectedCell.removeWidget((ReportWidget) widget);
			}
			else {
				reportSheetParent.widgetToTrash(widget);
			}
		}
	}

	public void restoreReportWidget(ActionType type, ReportWidget widget, int col, int row, boolean addAction) {
		GridCell selectedCell = grid.get(row).get(col);
		selectedCell.setWidget(widget);

		this.thirdBinDragC.makeDraggable(widget);

		if (addAction) {
			ActionAddWidgetToGrid action = new ActionAddWidgetToGrid(type, this, widget, col, row);
			reportSheetParent.getPanelParent().replaceLastActionToUndo(action);
		}
	}

	public void deleteReportWidget(ReportWidget widget, int col, int row, boolean addAction) {
		if (addAction) {
			Action action = new ActionTrashWidgetToGrid(ActionType.TRASH_WIDGET, this, widget, col, row);
			reportSheetParent.getPanelParent().replaceLastActionToUndo(action);
		}

		GridCell selectedCell = grid.get(row).get(col);
		selectedCell.removeWidget(widget);
	}
}
