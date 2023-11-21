package bpm.gwt.commons.client.viewer.fmdtdriller;

import bpm.gwt.commons.client.dataset.DatasetCubeDesignerPanel;
import bpm.vanilla.platform.core.beans.fmdt.FmdtColumn;
import bpm.vanilla.platform.core.beans.fmdt.FmdtData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ColumnDraggable extends Composite {

	private static ColumnDraggableUiBinder uiBinder = GWT.create(ColumnDraggableUiBinder.class);

	interface ColumnDraggableUiBinder extends UiBinder<Widget, ColumnDraggable> {
	}
	
	interface MyStyle extends CssResource {
		String colUndefined();
		String colDimension();
		String colMeasure();
		String colProperty();
	}
	
	@UiField
	MyStyle style;

	@UiField
	FocusPanel dragColumn;

	@UiField
	Label lblcolumn;

	@UiField
	Image imgColumn;

	private FmdtData column;
	public static final String VARIABLE_ID = "variableId";
	// private CubeDesignerPanel parent;
	private Composite parent;

	public ColumnDraggable(FmdtData column) {
		initWidget(uiBinder.createAndBindUi(this));
		this.column = column;

		buildContent();
	}

	public ColumnDraggable(FmdtData column, Composite parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.column = column;
		this.parent = parent;

		buildContent();
	}

	private void buildContent() {
		lblcolumn.setText(column.getLabel());
		lblcolumn.setTitle(column.getLabel());
		dragColumn.getElement().setDraggable(Element.DRAGGABLE_TRUE);
		dragColumn.addDragStartHandler(dragStartHandler);
		
		if (column instanceof FmdtColumn) {
			addStyleName(getColumnStyle((FmdtColumn) column));
		}
	}

	private String getColumnStyle(FmdtColumn column) {
		switch (column.getType()) {
		case UNDEFINED:
			return style.colUndefined();
		case DIMENSION:
			return style.colDimension();
		case COUNTRY:
			return style.colDimension();
		case CITY:
			return style.colDimension();
		case MEASURE:
			return style.colMeasure();
		case PROPERTY:
			return style.colProperty();
		default:
			break;
		}
		return style.colUndefined();
	}

	private DragStartHandler dragStartHandler = new DragStartHandler() {
		@Override
		public void onDragStart(DragStartEvent event) {
			if (column != null) {
				if (parent != null) {
					if (parent instanceof CubeDesignerPanel)
						((CubeDesignerPanel) parent).setSelected(ColumnDraggable.this);
					if (parent instanceof AnalysisDesignerPanel)
						((AnalysisDesignerPanel) parent).setSelected(ColumnDraggable.this);
					if (parent instanceof DatasetCubeDesignerPanel) /*modif kmo 30/09*/
						((DatasetCubeDesignerPanel) parent).setSelected(ColumnDraggable.this);

					event.setData(VARIABLE_ID, String.valueOf(column.getId()));
					event.getDataTransfer().setDragImage(dragColumn.getElement(), 10, 10);
				}
			}
		}
	};

	public FmdtData getColumn() {
		return column;
	}

	public Label getLblcolumn() {
		return lblcolumn;
	}

}
