package bpm.fwr.client.widgets;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.HyperlinkColumn;
import bpm.fwr.api.beans.components.GridComponent;
import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.DataSource;
import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.fwr.api.beans.dataset.FwrRelationStrategy;
import bpm.fwr.api.beans.dataset.GroupPrompt;
import bpm.fwr.api.beans.dataset.IResource;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.action.ActionAddColumnList;
import bpm.fwr.client.action.ActionMoveColumnInReportWidget;
import bpm.fwr.client.action.ActionTrashColumnList;
import bpm.fwr.client.action.ActionType;
import bpm.fwr.client.dialogs.HyperlinkDialog;
import bpm.fwr.client.dialogs.JoinDatasetDialogBox;
import bpm.fwr.client.draggable.HasBin;
import bpm.fwr.client.draggable.dropcontrollers.ListPanelDropController;
import bpm.fwr.client.draggable.dropcontrollers.ListPanelGroupDropController;
import bpm.fwr.client.draggable.dropcontrollers.ListWidgetHorizontalPanelDropController;
import bpm.fwr.client.draggable.dropcontrollers.ListWidgetVerticalPanelDropController;
import bpm.fwr.client.draggable.widgets.DraggableColumnHTML;
import bpm.fwr.client.draggable.widgets.DraggableHTMLPanel;
import bpm.fwr.client.images.WysiwygImage;
import bpm.fwr.client.panels.ReportSheet;
import bpm.fwr.client.utils.ColumnType;
import bpm.fwr.client.utils.SizeComponentConstants;
import bpm.fwr.client.utils.WidgetType;
import bpm.gwt.commons.client.listeners.FinishListener;
import bpm.gwt.commons.shared.InfoUser;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListWidget extends ReportWidget implements HasMouseDownHandlers, HasBin {
	private static final String CSS_LIST_PANEL = "listPanel";
	private static final String CSS_CELL_GROUP = "cellGroup";
	private static final String CSS_COLUMN_HEAD = "columnHead";
	public static final String CSS_COLUMN = "column";
	public static final String CSS_COLUMN_BORDER_RIGHT = "columnBorderRight";

	private PickupDragController dataDragController;
	private DropController dropControllerNorth, dropControllerWest;

	private PickupDragController detailDragController;
	private DropController columnDropController;

	private PickupDragController groupDragController;
	private DropController groupDropController;

	private ListCell north;
	private HorizontalPanel detailPanel;
	private VerticalPanel groupPanel;
	
	private boolean automaticGroupingList;

	private int headHeight = SizeComponentConstants.SIZE_GROUP_LIST;

	private List<DraggableHTMLPanel> columnsDetails = new ArrayList<DraggableHTMLPanel>();

	private DataSet dataset;
	private List<Column> detailColumns = new ArrayList<Column>();
	private List<Column> groupColumns = new ArrayList<Column>();

	private int widthComponent;

	private String selectedLanguage;
//	private HashMap<Integer, String> defaultLanguages = new HashMap<Integer, String>();
	private ReportSheet reportSheetParent;

	public ListWidget(PickupDragController groupDragController, PickupDragController dataDragController, PickupDragController detailDragController, ReportSheet reportSheetParent, boolean automaticGroupingList) {
		super(reportSheetParent, WidgetType.LIST, 0);
//		this.widthComponent = width;
		this.reportSheetParent = reportSheetParent;

		this.dataDragController = dataDragController;
		this.groupDragController = groupDragController;
		this.detailDragController = detailDragController;
		
		this.automaticGroupingList = automaticGroupingList;

		VerticalPanel listPanel = new VerticalPanel();
		listPanel.addStyleName(CSS_LIST_PANEL);
		this.add(listPanel);

		north = new ListCell(this, ColumnType.GROUP);
		north.addStyleName(CSS_CELL_GROUP);
		north.setSize("100%", SizeComponentConstants.SIZE_GROUP_LIST + "px");

		groupPanel = new VerticalPanel();
		groupPanel.setSize("100%", "100%");
		north.add(groupPanel);

		SimplePanel test = new SimplePanel();
		test.setSize("100%", "30px");
		groupPanel.add(test);

		ListCell west = new ListCell(this, ColumnType.DETAIL);
		west.setSize("100%", SizeComponentConstants.SIZE_LIST + "px");

		detailPanel = new HorizontalPanel();
		detailPanel.setSize("100%", "100%");
		west.add(detailPanel);

		// initialize our column drop controller
		groupDropController = new ListWidgetVerticalPanelDropController(north, groupPanel);
		columnDropController = new ListWidgetHorizontalPanelDropController(west, detailPanel);

		// initialize our column drop controller
		dropControllerNorth = new ListPanelGroupDropController(groupPanel, north, automaticGroupingList);
		dropControllerWest = new ListPanelDropController(detailPanel, west, automaticGroupingList);

		listPanel.add(north);
		listPanel.add(west);
	}

	@Override
	protected void onAttach() {
		groupDragController.registerDropController(groupDropController);
		dataDragController.registerDropController(dropControllerNorth);
		detailDragController.registerDropController(columnDropController);
		dataDragController.registerDropController(dropControllerWest);
		super.onAttach();
	}

	@Override
	protected void onDetach() {
		dataDragController.unregisterDropController(dropControllerNorth);
		dataDragController.unregisterDropController(dropControllerWest);
		groupDragController.unregisterDropController(groupDropController);
		detailDragController.unregisterDropController(columnDropController);
		super.onDetach();
	}

	public void manageColumns(Column column, ColumnType type, int index, boolean needToCheckDB, ActionType actionType) {
		if (selectedLanguage == null || selectedLanguage.isEmpty()) {
			selectedLanguage = reportSheetParent.getPanelParent().getPanelparent().getLanguageDefaultForMetadataWithId();
		}

		String defautlLanguage = selectedLanguage;

		if (type == ColumnType.GROUP) {
			if (automaticGroupingList && !(column.isDimension())) {
				return;
			}
			
			headHeight = headHeight + 15;
			north.setHeight(headHeight + "px");

			DraggableColumnHTML columnName = null;
			if (column.getCustomColumnName() != null) {
				columnName = new DraggableColumnHTML(column.getCustomColumnName(), column, ColumnType.GROUP, WidgetType.COLUMN, this, true);
			}
			else if (defautlLanguage == null) {
				columnName = new DraggableColumnHTML(column.getName(), column, ColumnType.GROUP, WidgetType.COLUMN, this, true);
			}
			else {
				columnName = new DraggableColumnHTML(column.getTitle(defautlLanguage), column, ColumnType.GROUP, WidgetType.COLUMN, this, true);
			}

			DraggableHTMLPanel columnPanel = new DraggableHTMLPanel("", columnName);
			columnPanel.setWidth("100%");
			columnPanel.add(columnName);

			if (groupColumns.size() < index) {
				index = index - 1;
				this.groupPanel.insert(columnPanel, index);
				this.groupColumns.add(index, column);
			}
			else {
				this.groupPanel.insert(columnPanel, index);
				this.groupColumns.add(index, column);
			}

			groupDragController.makeDraggable(columnPanel, columnName);

			if (needToCheckDB) {
				checkOrCreateDataset(column);
			}

			if (actionType == ActionType.DEFAULT) {
				ActionAddColumnList action = new ActionAddColumnList(ActionType.ADD_COLUMN_GROUP, this, columnPanel, index);
				reportSheetParent.getPanelParent().addActionToUndoAndClearRedo(action);
			}
			else if (actionType == ActionType.REDO) {
				ActionAddColumnList action = new ActionAddColumnList(ActionType.ADD_COLUMN_GROUP, this, columnPanel, index);
				reportSheetParent.getPanelParent().replaceLastActionToUndo(action);
			}
			else if (actionType == ActionType.UNDO) {
				ActionTrashColumnList action = new ActionTrashColumnList(ActionType.TRASH_COLUMN_GROUP, this, columnPanel, index);
				reportSheetParent.getPanelParent().replaceFirstActionToRedo(action);
			}
		}
		else if (type == ColumnType.DETAIL) {
			if (automaticGroupingList && !(column.isMeasure())) {
				return;
			}
			
//			int parentWidth = widthComponent - 2;
//			int width = parentWidth / (columnsDetails.size() + 1);

			for (int i = 0; i < columnsDetails.size(); i++) {
				if (i != columnsDetails.size() - 1 || index == columnsDetails.size()) {
					columnsDetails.get(i).setStyleName(CSS_COLUMN_BORDER_RIGHT);
//					columnsDetails.get(i).setWidth((width - 1) + "px");
//					columnsDetails.get(i).setWidth("100%");
				}
				else {
					columnsDetails.get(i).setStyleName(CSS_COLUMN);
//					columnsDetails.get(i).setWidth(width + "px");
//					columnsDetails.get(i).setWidth("100%");
				}
			}

//			if (parentWidth - ((columnsDetails.size() + 1) * width) != 0) {
//				width = width + (parentWidth - (columnsDetails.size() + 1) * width);
//			}

			DraggableColumnHTML columnName = null;
			if (column.getCustomColumnName() != null) {
				columnName = new DraggableColumnHTML(column.getCustomColumnName(), column, ColumnType.DETAIL, WidgetType.COLUMN, this, true);
			}
			else if (defautlLanguage == null) {
				columnName = new DraggableColumnHTML(column.getName(), column, ColumnType.DETAIL, WidgetType.COLUMN, this, true);
			}
			else {
				columnName = new DraggableColumnHTML(column.getTitle(defautlLanguage), column, ColumnType.DETAIL, WidgetType.COLUMN, this, true);
			}
			columnName.addStyleName(CSS_COLUMN_HEAD);

			DraggableHTMLPanel columnPanel = new DraggableHTMLPanel("", columnName);
			columnPanel.setHeight(SizeComponentConstants.SIZE_LIST + "px");

			if (index == columnsDetails.size()) {
//				columnPanel.setWidth(width + "px");
//				columnPanel.setWidth("100%");
				columnPanel.setStyleName(CSS_COLUMN);
			}
			else {
				columnPanel.setStyleName(CSS_COLUMN_BORDER_RIGHT);
//				columnPanel.setWidth((width - 1) + "px");
//				columnPanel.setWidth("100%");
			}
			columnPanel.add(columnName);

			this.columnsDetails.add(index, columnPanel);
			this.detailPanel.insert(columnPanel, index);
			this.detailColumns.add(index, column);

			detailDragController.makeDraggable(columnPanel, columnName);

			if (needToCheckDB) {
				checkOrCreateDataset(column);
			}

			if (actionType == ActionType.DEFAULT) {
				ActionAddColumnList action = new ActionAddColumnList(ActionType.ADD_COLUMN_DETAIL, this, columnPanel, index);
				reportSheetParent.getPanelParent().addActionToUndoAndClearRedo(action);
			}
			else if (actionType == ActionType.REDO) {
				ActionAddColumnList action = new ActionAddColumnList(ActionType.ADD_COLUMN_DETAIL, this, columnPanel, index);
				reportSheetParent.getPanelParent().replaceLastActionToUndo(action);
			}
			else if (actionType == ActionType.UNDO) {
				ActionTrashColumnList action = new ActionTrashColumnList(ActionType.TRASH_COLUMN_DETAIL, this, columnPanel, index);
				reportSheetParent.getPanelParent().replaceFirstActionToRedo(action);
			}
		}
	}

	public void manageHyperlink(HyperlinkColumn column, final int indexCell, boolean needToCheckDB, boolean suppressTheColumnFirst, DraggableHTMLPanel htmlPanel, ActionType actionType) {
		if (suppressTheColumnFirst) {
			widgetToTrash(htmlPanel);
		}

		if (selectedLanguage == null || selectedLanguage.isEmpty()) {
			selectedLanguage = reportSheetParent.getPanelParent().getPanelparent().getLanguageDefaultForMetadataWithId();
		}

		String defautlLanguage = selectedLanguage;

		int parentWidth = widthComponent - 2;
		int width = parentWidth / (columnsDetails.size() + 1);

		for (int i = 0; i < columnsDetails.size(); i++) {
			if (i != columnsDetails.size() - 1 || indexCell == columnsDetails.size()) {
				columnsDetails.get(i).setStyleName(CSS_COLUMN_BORDER_RIGHT);
//				columnsDetails.get(i).setWidth((width - 1) + "px");
			}
			else {
				columnsDetails.get(i).setStyleName(CSS_COLUMN);
//				columnsDetails.get(i).setWidth(width + "px");
			}
		}

//		if (parentWidth - ((columnsDetails.size() + 1) * width) != 0) {
//			width = width + (parentWidth - (columnsDetails.size() + 1) * width);
//		}

		DraggableColumnHTML columnName = null;
		if (column.getCustomColumnName() != null) {
			columnName = new DraggableColumnHTML(column.getCustomColumnName(), column, ColumnType.DETAIL, WidgetType.COLUMN, this, true);
		}
		else if (defautlLanguage == null) {
			columnName = new DraggableColumnHTML(column.getName(), column, ColumnType.DETAIL, WidgetType.COLUMN, this, true);
		}
		else {
			columnName = new DraggableColumnHTML(column.getTitle(defautlLanguage), column, ColumnType.DETAIL, WidgetType.COLUMN, this, true);
		}
		columnName.addStyleName(CSS_COLUMN_HEAD);

		final DraggableHTMLPanel columnPanel = new DraggableHTMLPanel("", columnName);
		columnPanel.setHeight(SizeComponentConstants.SIZE_LIST + "px");
		if (indexCell == columnsDetails.size()) {
			columnPanel.setWidth(width + "px");
			columnPanel.setStyleName(CSS_COLUMN);
		}
		else {
			columnPanel.setStyleName(CSS_COLUMN_BORDER_RIGHT);
			columnPanel.setWidth((width - 1) + "px");
		}
		columnPanel.add(columnName);

		Image hyperlink = new Image(WysiwygImage.INSTANCE.FWR_Hyperlink());
		hyperlink.addStyleName("styleImgHyperlink");
		hyperlink.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				List<DataSet> dsAvailable = reportSheetParent.getAvailableDatasets();
				HyperlinkDialog dial = new HyperlinkDialog(ListWidget.this, indexCell, reportSheetParent.getPanelParent().getPanelparent().getMetadatas(), dsAvailable, columnPanel);
				dial.center();
			}
		});

		columnPanel.add(hyperlink);

		this.columnsDetails.add(indexCell, columnPanel);
		this.detailPanel.insert(columnPanel, indexCell);
		this.detailColumns.add(indexCell, column);

		detailDragController.makeDraggable(columnPanel, columnName);

		if (needToCheckDB) {
			checkOrCreateDataset(column);
		}

		if (actionType == ActionType.DEFAULT) {
			ActionAddColumnList action = new ActionAddColumnList(ActionType.ADD_COLUMN_DETAIL, this, columnPanel, indexCell);
			reportSheetParent.getPanelParent().addActionToUndoAndClearRedo(action);
		}
		else if (actionType == ActionType.REDO) {
			ActionAddColumnList action = new ActionAddColumnList(ActionType.ADD_COLUMN_DETAIL, this, columnPanel, indexCell);
			reportSheetParent.getPanelParent().replaceLastActionToUndo(action);
		}
		else if (actionType == ActionType.UNDO) {
			ActionTrashColumnList action = new ActionTrashColumnList(ActionType.TRASH_COLUMN_DETAIL, this, columnPanel, indexCell);
			reportSheetParent.getPanelParent().replaceFirstActionToRedo(action);
		}
	}

	public ReportSheet getReportSheetParent() {
		return reportSheetParent;
	}

	private void checkOrCreateDataset(Column column) {
		if (dataset == null) {
			dataset = createDataSet(column);
			dataset.addColumn(column);
			column.setDatasetParent(dataset);
		}
		else {
			if (!dataset.getColumns().isEmpty()) {
				if (!column.getBusinessPackageParent().equals(dataset.getColumns().get(0).getBusinessPackageParent())) {
					DataSet dataset1 = dataset;
					DataSet dataset2 = createDataSet(column);
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

	private DataSet createDataSet(Column column) {

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

	private void checkOrDeleteDataset() {
		if (detailColumns.isEmpty() && groupColumns.isEmpty()) {
			dataset = null;
		}
	}

	public void loadComp(GridComponent component) {
		if (component != null) {
			this.dataset = component.getDataset();
			for (int i = 0; i < component.getGroups().size(); i++) {
				manageColumns(component.getGroups().get(i), ColumnType.GROUP, i, false, ActionType.NONE);
			}

			for (int i = 0; i < component.getColumns().size(); i++) {
				manageColumns(component.getColumns().get(i), ColumnType.DETAIL, i, false, ActionType.NONE);
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
			
			setShowHeader(component.showHeader());
		}
	}

	@Override
	public void widgetToTrash(Object widget) {
		if (widget instanceof DraggableHTMLPanel) {
			DraggableHTMLPanel columnPanel = (DraggableHTMLPanel) widget;
			removeWidget(columnPanel, false, ActionType.DEFAULT, false);
		}
		else if (widget instanceof ListWidget) {
			reportSheetParent.widgetToTrash(widget);
		}
	}

	public void removeWidget(DraggableHTMLPanel columnPanel, boolean removeFromParent, ActionType actionType, boolean deleteFromPanel) {
		int indexInWidget = 0;
		ActionType typeForTrash = null;
		ActionType typeForAdd = null;
		if (columnPanel.getColumn().getColumnType() == ColumnType.GROUP) {
			indexInWidget = groupColumns.indexOf(columnPanel.getColumn().getColumn());
			typeForTrash = ActionType.TRASH_COLUMN_GROUP;
			typeForAdd = ActionType.ADD_COLUMN_GROUP;

			headHeight = headHeight - 15;
			north.setHeight(headHeight + "px");

			groupColumns.remove(columnPanel.getColumn().getColumn());

			if (deleteFromPanel) {
				groupPanel.remove(indexInWidget);
			}
		}
		else if (columnPanel.getColumn().getColumnType() == ColumnType.DETAIL) {
			indexInWidget = detailColumns.indexOf(columnPanel.getColumn().getColumn());
			typeForTrash = ActionType.TRASH_COLUMN_DETAIL;
			typeForAdd = ActionType.ADD_COLUMN_DETAIL;

			detailColumns.remove(columnPanel.getColumn().getColumn());

			if (deleteFromPanel) {
				if (actionType == ActionType.UNDO || actionType == ActionType.REDO) {
					columnsDetails.remove(indexInWidget);
				}
				detailPanel.remove(indexInWidget);
			}
			else {
				columnsDetails.remove(columnPanel);
			}

			if (columnsDetails.size() != 0) {
				// Parent width without margins
				int parentWidth = this.getOffsetWidth() - 1;
				int width = parentWidth / columnsDetails.size();

				for (int i = 0; i < columnsDetails.size(); i++) {
					if (i != columnsDetails.size() - 1) {
						columnsDetails.get(i).setStyleName(CSS_COLUMN_BORDER_RIGHT);
//						columnsDetails.get(i).setWidth((width - 1) + "px");
					}
					else {
						if (parentWidth - (columnsDetails.size() * width) != 0) {
							width = width + (parentWidth - columnsDetails.size() * width);
						}

						columnsDetails.get(i).setStyleName(CSS_COLUMN);
//						columnsDetails.get(i).setWidth(width + "px");
					}
				}
			}
		}

		if (actionType == ActionType.DEFAULT) {
			ActionTrashColumnList action = new ActionTrashColumnList(typeForTrash, this, columnPanel, indexInWidget);
			reportSheetParent.getPanelParent().addActionToUndoAndClearRedo(action);
		}
		else if (actionType == ActionType.REDO) {
			ActionTrashColumnList action = new ActionTrashColumnList(typeForTrash, this, columnPanel, indexInWidget);
			reportSheetParent.getPanelParent().replaceLastActionToUndo(action);
		}
		else if (actionType == ActionType.UNDO) {
			ActionAddColumnList action = new ActionAddColumnList(typeForAdd, this, columnPanel, indexInWidget);
			reportSheetParent.getPanelParent().replaceFirstActionToRedo(action);
		}

		checkOrDeleteDataset();
	}

	public void switchWidget(DraggableColumnHTML dragColumn, ColumnType type, int index) {
		if (type == ColumnType.DETAIL) {
			int oldIndex = 0;
			for (int i = 0; i < detailColumns.size(); i++) {
				if (detailColumns.get(i).equals(dragColumn.getColumn())) {
					oldIndex = i;
					break;
				}
			}

			detailColumns.remove(dragColumn.getColumn());
			detailColumns.add(index, dragColumn.getColumn());

			ActionMoveColumnInReportWidget action = new ActionMoveColumnInReportWidget(ActionType.MOVE_COLUMN_DETAIL, this, oldIndex, index);
			getReportSheetParent().getPanelParent().addActionToUndoAndClearRedo(action);
		}
		else if (type == ColumnType.GROUP) {
			int oldIndex = 0;
			for (int i = 0; i < groupColumns.size(); i++) {
				if (groupColumns.get(i).equals(dragColumn.getColumn())) {
					oldIndex = i;
					break;
				}
			}

			groupColumns.remove(dragColumn.getColumn());
			groupColumns.add(index, dragColumn.getColumn());

			ActionMoveColumnInReportWidget action = new ActionMoveColumnInReportWidget(ActionType.MOVE_COLUMN_DETAIL, this, oldIndex, index);
			getReportSheetParent().getPanelParent().addActionToUndoAndClearRedo(action);
		}
	}

	public void switchWidget(ColumnType type, int oldIndex, int newIndex) {
		if (type == ColumnType.DETAIL) {
			Widget widg = detailPanel.getWidget(oldIndex);
			Column column = detailColumns.get(oldIndex);

			detailPanel.remove(oldIndex);
			detailPanel.insert(widg, newIndex);
			detailColumns.remove(oldIndex);
			detailColumns.add(newIndex, column);
		}
		else if (type == ColumnType.GROUP) {
			Widget widg = groupPanel.getWidget(oldIndex);
			Column column = groupColumns.get(oldIndex);

			groupPanel.remove(oldIndex);
			groupPanel.insert(widg, newIndex);
			groupColumns.remove(oldIndex);
			groupColumns.add(newIndex, column);
		}
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	public List<Column> getDetailColumns() {
		return detailColumns;
	}

	public List<Column> getGroupColumns() {
		return groupColumns;
	}

	public DataSet getDataset() {
		return dataset;
	}

	private FinishListener finishListener = new FinishListener() {

		@Override
		public void onFinish(Object result, Object source, String result1) {
			if (result instanceof DataSet) {
				dataset = (DataSet) result;
			}
		}
	};

	public void updateColumnContent(boolean needToUpdate) {
		for (int i = 0; i < detailPanel.getWidgetCount(); i++) {
			if (detailPanel.getWidget(i) instanceof DraggableHTMLPanel) {
				DraggableHTMLPanel column = ((DraggableHTMLPanel) detailPanel.getWidget(i));

				String columnName = "";
				if (needToUpdate || column.getColumn().getColumn().getCustomColumnName() == null) {
					StringBuffer beginBuf = new StringBuffer("");
					StringBuffer endBuf = new StringBuffer("");
					if (column.getColumn().getColumn().isUnderline()) {
						beginBuf.insert(0, "<u>");
						endBuf.append("</u>");
					}
					if (column.getColumn().getColumn().isBold()) {
						beginBuf.insert(0, "<b>");
						endBuf.append("</b>");
					}
					if (column.getColumn().getColumn().isItalic()) {
						beginBuf.insert(0, "<i>");
						endBuf.append("</i>");
					}

					if (column.getColumn().getColumn().getTextAlign().equals("left")) {
						column.getColumn().setTextAlignLeft();
					}
					else if (column.getColumn().getColumn().getTextAlign().equals("right")) {
						column.getColumn().setTextAlignRigh();
					}
					else {
						column.getColumn().setTextAlignCenter();
					}

					String colName = column.getColumn().getColumn().getCustomColumnName() != null ? column.getColumn().getColumn().getCustomColumnName() : column.getColumn().getColumn().getTitle(selectedLanguage);
					columnName = beginBuf.toString() + colName + endBuf.toString();

//					column.getColumn().getColumn().setCustomColumnName("", false);
//					column.getColumn().setCustom(false);
				}
				else {
					columnName = column.getColumn().getColumn().getCustomColumnName();
				}

				String sort = "";
				if (column.getColumn().getColumn().isSorted()) {
					sort += " (Sort " + column.getColumn().getColumn().getSortType().toUpperCase() + ")";
				}

				if (column.getColumn().getColumn().getGroupAggregation() != null) {
					sort += " *";
				}

				column.getColumn().setText(columnName + sort);
			}
		}

		for (int i = 0; i < groupPanel.getWidgetCount(); i++) {
			if (groupPanel.getWidget(i) instanceof DraggableHTMLPanel) {
				DraggableHTMLPanel column = ((DraggableHTMLPanel) groupPanel.getWidget(i));

				String columnName = "";
				if (needToUpdate || column.getColumn().getColumn().getCustomColumnName() == null) {
					StringBuffer beginBuf = new StringBuffer("");
					StringBuffer endBuf = new StringBuffer("");
					if (column.getColumn().getColumn().isUnderline()) {
						beginBuf.insert(0, "<u>");
						endBuf.append("</u>");
					}
					if (column.getColumn().getColumn().isBold()) {
						beginBuf.insert(0, "<b>");
						endBuf.append("</b>");
					}
					if (column.getColumn().getColumn().isItalic()) {
						beginBuf.insert(0, "<i>");
						endBuf.append("</i>");
					}

					if (column.getColumn().getColumn().getTextAlign().equals("left")) {
						column.getColumn().setTextAlignLeft();
					}
					else if (column.getColumn().getColumn().getTextAlign().equals("right")) {
						column.getColumn().setTextAlignRigh();
					}
					else {
						column.getColumn().setTextAlignCenter();
					}

					String colName = column.getColumn().getColumn().getCustomColumnName() != null ? column.getColumn().getColumn().getCustomColumnName() : column.getColumn().getColumn().getTitle(selectedLanguage);
					columnName = beginBuf.toString() + colName + endBuf.toString();

					column.getColumn().getColumn().setCustomColumnName("", false);
					column.getColumn().setCustom(false);
				}
				else {
					columnName = column.getColumn().getColumn().getCustomColumnName();
				}

				String sort = "";
				if (column.getColumn().getColumn().isSorted()) {
					sort += " (Sort " + column.getColumn().getColumn().getSortType().toUpperCase() + ")";
				}

				if (column.getColumn().getColumn().getGroupAggregation() != null) {
					sort += " *";
				}

				column.getColumn().setText(columnName + sort);
			}
		}
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.selectedLanguage = defaultLanguage;
		updateColumnContent(false);
	}

	@Override
	public int getWidgetHeight() {
		return getOffsetHeight();
	}

	public boolean isAutomaticGroupingList() {
		return automaticGroupingList;
	}
}
