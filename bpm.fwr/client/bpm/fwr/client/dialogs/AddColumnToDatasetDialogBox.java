package bpm.fwr.client.dialogs;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.draggable.HasBin;
import bpm.fwr.client.draggable.dragcontrollers.BinDragController;
import bpm.fwr.client.draggable.dragcontrollers.DataDragController;
import bpm.fwr.client.draggable.dropcontrollers.BinDropController;
import bpm.fwr.client.draggable.dropcontrollers.ListBoxDropController;
import bpm.fwr.client.draggable.widgets.DraggableColumn;
import bpm.fwr.client.draggable.widgets.DraggableColumnLabel;
import bpm.fwr.client.images.WysiwygImage;
import bpm.fwr.client.tree.DatasetAddColumnTree;
import bpm.fwr.client.widgets.BinWidget;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AddColumnToDatasetDialogBox extends AbstractDialogBox implements HasBin {

	private static AddColumnToDatasetDialogBoxUiBinder uiBinder = GWT.create(AddColumnToDatasetDialogBoxUiBinder.class);

	interface AddColumnToDatasetDialogBoxUiBinder extends UiBinder<Widget, AddColumnToDatasetDialogBox> {
	}
	
	@UiField
	HTMLPanel contentPanel;
	
	private static final String CSS_NAME = "lblNameAddColumn";
	private static final String CSS_LIST = "listAddColumn";
	private static final String CSS_TREE = "treeAddColumn";
	private static final String CSS_SCROLL = "scrollPanelAddColumn";
	private static final String CSS_EMPTY_BIN = "emptyBinAddColumn";

	private DatasetAddColumnTree datasetTree;
	private AbsolutePanel listColumn;

	private DataSet dataset;
	private List<Column> selectedColumns = new ArrayList<Column>();

	private DropController binDropController, listBoxDropController;
	private PickupDragController binDragController, dragController;
	
	public AddColumnToDatasetDialogBox(DataSet dataset) {
		super(Bpm_fwr.LBLW.AddColumn(), false, true);
		this.dataset = dataset;
		
		setWidget(uiBinder.createAndBindUi(this));

		AbsolutePanel rootPanel = new AbsolutePanel();

		Label lblDatasetName = new Label(Bpm_fwr.LBLW.DatasetName() + ": " + dataset.getName());
		lblDatasetName.addStyleName(CSS_NAME);

		dragController = new DataDragController(rootPanel, false);
		binDragController = new BinDragController(rootPanel, false);

		datasetTree = new DatasetAddColumnTree(dataset, dragController);
		datasetTree.addStyleName(CSS_TREE);
		datasetTree.setHeight("100%");

		listColumn = new AbsolutePanel();
		listColumn.addStyleName(CSS_LIST);
		for (Column column : dataset.getColumns()) {
			DraggableColumnLabel lblColumn = new DraggableColumnLabel(column.getTitle(dataset.getLanguage()), this, column);
			listColumn.add(lblColumn);
		}

		ScrollPanel scroolPanel = new ScrollPanel();
		scroolPanel.addStyleName(CSS_SCROLL);
		scroolPanel.add(datasetTree);

		HorizontalPanel middlePanel = new HorizontalPanel();
		middlePanel.add(scroolPanel);
		middlePanel.add(listColumn);

		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.add(lblDatasetName);
		mainPanel.add(middlePanel);

		BinWidget imgBin = new BinWidget(WysiwygImage.INSTANCE.empty_bin64());
		imgBin.setStyleName(CSS_EMPTY_BIN);

		rootPanel.add(mainPanel);
		rootPanel.add(imgBin);

		contentPanel.add(rootPanel);
		createButtonBar(Bpm_fwr.LBLW.Ok(), okHandler, Bpm_fwr.LBLW.Cancel(), cancelHandler);

		// add drop controller for trash bin
		binDropController = new BinDropController(imgBin, true);
		listBoxDropController = new ListBoxDropController(this, listColumn);

		dragController.registerDropController(listBoxDropController);
		binDragController.registerDropController(binDropController);
	}

	@Override
	public void onDetach() {
		dragController.unregisterDropController(listBoxDropController);
		binDragController.unregisterDropController(binDropController);
		super.onDetach();
	}

	public void manageWidget(Widget widget) {
		if (widget instanceof DraggableColumn) {
			DraggableColumn draggableColumn = (DraggableColumn) widget;
			DraggableColumnLabel lblColumn = new DraggableColumnLabel(draggableColumn.getColumn().getTitle(dataset.getLanguage()), this, draggableColumn.getColumn());
			draggableColumn.getColumn().setDatasetParent(dataset);
			binDragController.makeDraggable(lblColumn);
			listColumn.add(lblColumn);
			selectedColumns.add(draggableColumn.getColumn());
		}
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			AddColumnToDatasetDialogBox.this.hide();
		}
	};

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			for (Column column : selectedColumns) {
				dataset.addColumn(column);
			}
			finish(null, null, null);
			AddColumnToDatasetDialogBox.this.hide();
		}
	};

	@Override
	public void widgetToTrash(Object widget) {
		listColumn.remove((DraggableColumnLabel) widget);
		selectedColumns.remove(((DraggableColumnLabel) widget).getColumn());
	}
}