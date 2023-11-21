package bpm.gwt.commons.client.viewer.fmdtdriller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bpm.gwt.commons.client.dataset.DatasetCubeDesignerPanel;
import bpm.vanilla.platform.core.beans.fmdt.FmdtData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;

public class Fmdtlevel extends Composite {

	private static FmdtlevelUiBinder uiBinder = GWT.create(FmdtlevelUiBinder.class);

	interface FmdtlevelUiBinder extends UiBinder<Widget, Fmdtlevel> {
	}

	@UiField
	Label labelLevel;

	@UiField
	SimplePanel txtOperation;

	@UiField
	FocusPanel dragLevel;

	@UiField
	Image imgLevel;

	private ValueListBox<String> operations = new ValueListBox<String>(new Renderer<String>() {

		@Override
		public String render(String object) {
			return object != null ? object : "";
		}

		@Override
		public void render(String object, Appendable appendable) throws IOException {
			appendable.append(object);
		}
	});

	private FmdtData level;
	private Widget parent;
	private final List<String> aggregList = new ArrayList<String>(Arrays.asList("avg", "min", "max", "sum", "count", "distinct-count", "last", "first"));
	public static final String VARIABLE_ID = "variableId";

	public Fmdtlevel(FmdtData level, Widget parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.level = level;
		this.parent = parent;

		labelLevel.setText(level.getLabel());

		init();
	}

	public Fmdtlevel(FmdtData level) {
		initWidget(uiBinder.createAndBindUi(this));
		this.level = level;
		labelLevel.setText(level.getLabel());

		init();
	}

	private void init() {
		if (parent instanceof CubeDesignerPanel) {
			operations.setValue(aggregList.get(0));
			operations.setAcceptableValues(aggregList);
			txtOperation.add(operations);
			imgLevel.setVisible(false);
		} else if (parent instanceof DatasetCubeDesignerPanel) { /*modif kmo 01/10*/
			operations.setValue(aggregList.get(0));
			operations.setAcceptableValues(aggregList);
			txtOperation.add(operations);
			imgLevel.setVisible(false);
		}
		else
			txtOperation.setVisible(false);

		dragLevel.getElement().setDraggable(Element.DRAGGABLE_TRUE);
		dragLevel.addDragStartHandler(dragStartHandler);
		dragLevel.addDropHandler(dropHandler);
		dragLevel.addDragOverHandler(dragOverHandler);
		dragLevel.addDragLeaveHandler(dragLeaveHandler);
	}

	public FmdtData getLevel() {
		level.setMeasOp((String) operations.getValue());
		return level;
	}

	private DragStartHandler dragStartHandler = new DragStartHandler() {
		@Override
		public void onDragStart(DragStartEvent event) {
			if (level != null) {
				if (parent instanceof CubeDesignerPanel) {
					((CubeDesignerPanel) parent).setSelected(Fmdtlevel.this);
				} else if (parent instanceof DatasetCubeDesignerPanel) {
					((DatasetCubeDesignerPanel) parent).setSelected(Fmdtlevel.this);
				} else if (parent instanceof ObjectFmdtDimension)
					if(((ObjectFmdtDimension) parent).getDesigner() instanceof CubeDesignerPanel){
						((CubeDesignerPanel)((ObjectFmdtDimension) parent).getDesigner()).setSelected(Fmdtlevel.this); /*modif kmo 30/09*/
					} else if(((ObjectFmdtDimension) parent).getDesigner() instanceof DatasetCubeDesignerPanel){
						((DatasetCubeDesignerPanel)((ObjectFmdtDimension) parent).getDesigner()).setSelected(Fmdtlevel.this); /*modif kmo 30/09*/
					}
					

				event.setData(VARIABLE_ID, String.valueOf(level.getId()));
				event.getDataTransfer().setDragImage(dragLevel.getElement(), 10, 10);
			}
		}
	};

	public void removeLevel() {
		if (parent instanceof CubeDesignerPanel) {
			((CubeDesignerPanel) parent).getMeasures().remove(level);
		} else if (parent instanceof DatasetCubeDesignerPanel) {
			((DatasetCubeDesignerPanel) parent).getMeasures().remove(level);
		} else if (parent instanceof ObjectFmdtDimension)
			((ObjectFmdtDimension) parent).getLevels().remove(level);
		Fmdtlevel.this.removeFromParent();
	}

	private DropHandler dropHandler = new DropHandler() {

		@Override
		public void onDrop(DropEvent event) {
			event.stopPropagation();
			event.preventDefault();
			String data = event.getData(ColumnDraggable.VARIABLE_ID);

			if (parent instanceof CubeDesignerPanel) {
				((CubeDesignerPanel) parent).addMeasures(data);
			} else if (parent instanceof DatasetCubeDesignerPanel) {
				((DatasetCubeDesignerPanel) parent).addMeasures(data);
			} else if (parent instanceof ObjectFmdtDimension) {
				if(((ObjectFmdtDimension) parent).getDesigner() instanceof CubeDesignerPanel){
					FmdtData column = ((CubeDesignerPanel)((ObjectFmdtDimension) parent).getDesigner()).getData(data); /*modif kmo 30/09*/
					((ObjectFmdtDimension) parent).addNextLevel(level, column);
				} else if(((ObjectFmdtDimension) parent).getDesigner() instanceof DatasetCubeDesignerPanel){
					FmdtData column = ((DatasetCubeDesignerPanel)((ObjectFmdtDimension) parent).getDesigner()).getData(data); /*modif kmo 30/09*/
					((ObjectFmdtDimension) parent).addNextLevel(level, column);
				}
				
			}
		}
	};

	private DragOverHandler dragOverHandler = new DragOverHandler() {

		@Override
		public void onDragOver(DragOverEvent event) {
		}
	};

	private DragLeaveHandler dragLeaveHandler = new DragLeaveHandler() {

		@Override
		public void onDragLeave(DragLeaveEvent event) {
		}
	};

	public ValueListBox<String> getOperations() {
		return operations;
	}

	
}
