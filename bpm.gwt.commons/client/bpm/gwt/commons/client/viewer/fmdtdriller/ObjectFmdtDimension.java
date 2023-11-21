package bpm.gwt.commons.client.viewer.fmdtdriller;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.dataset.DatasetCubeDesignerPanel;
import bpm.gwt.commons.shared.fmdt.HtmlFocusPanel;
import bpm.vanilla.platform.core.beans.fmdt.FmdtData;
import bpm.vanilla.platform.core.beans.fmdt.FmdtDimension;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ObjectFmdtDimension extends HtmlFocusPanel {

	private static ObjectFmdtDimensionUiBinder uiBinder = GWT.create(ObjectFmdtDimensionUiBinder.class);

	interface ObjectFmdtDimensionUiBinder extends UiBinder<Widget, ObjectFmdtDimension> {
	}

	@UiField
	HTMLPanel levelsPanel;

	@UiField
	TextBox txtdimension, txtHiera;

	@UiField
	Image btnRemove;

	@UiField
	HtmlFocusPanel contentPanel;

	interface MyStyle extends CssResource {
		String select();
	}

	@UiField
	MyStyle style;

	private List<FmdtData> levels = new ArrayList<FmdtData>();
	private Composite designer;/*modif kmo 30/09*/

	public ObjectFmdtDimension(FmdtData data, Composite designer) { /*modif kmo 30/09*/
		super("");
		add(uiBinder.createAndBindUi(this));
		this.designer = designer;
		initPanel(data);
		addDropHandler(dropHandler);
	}

	private void initPanel(FmdtData data) {
		txtdimension.setText(data.getLabel());
		levelsPanel.add(new Fmdtlevel(data, this));
		levels.add(data);

	}

	public void removelevel(Fmdtlevel level) { /*modif kmo 30/09*/
		levels.remove(level.getLevel());
		if(designer instanceof CubeDesignerPanel)
			((CubeDesignerPanel)designer).addToColumns(level.getLevel());
		else if(designer instanceof DatasetCubeDesignerPanel)
			((DatasetCubeDesignerPanel)designer).addToColumns(level.getLevel());
		level.removeFromParent();
	}

	@UiHandler("btnRemove")
	public void onRemoveClick(ClickEvent event) { /*modif kmo 30/09*/
		if(designer instanceof CubeDesignerPanel)
			((CubeDesignerPanel)designer).addAllToColumns(levels);
		else if(designer instanceof DatasetCubeDesignerPanel)
			((DatasetCubeDesignerPanel)designer).addAllToColumns(levels);
		
		removeFromParent();
	}

	public void addLevelZero(FmdtData level) { /*modif kmo 30/09*/
		if(designer instanceof CubeDesignerPanel)
			((CubeDesignerPanel)designer).removeFromparent();
		else if(designer instanceof DatasetCubeDesignerPanel)
			((DatasetCubeDesignerPanel)designer).removeFromparent();
		
		levels.add(0, level);
		refreshLevels();
	}

	public void addNextLevel(FmdtData level, FmdtData nextLevel) { /*modif kmo 30/09*/
		if(designer instanceof CubeDesignerPanel)
			((CubeDesignerPanel)designer).removeFromparent();
		else if(designer instanceof DatasetCubeDesignerPanel)
			((DatasetCubeDesignerPanel)designer).removeFromparent();
		int index = levels.indexOf(level);
		levels.add(index + 1, nextLevel);
		refreshLevels();
	}

	public void refreshLevels() {
		levelsPanel.clear();
		for (FmdtData level : levels) {
			levelsPanel.add(new Fmdtlevel(level, getobjectDimension()));
		}
	}

	public FmdtDimension getDimension() {
		FmdtDimension dimension = new FmdtDimension();
		if (!txtdimension.getText().isEmpty() && checktxtName(txtdimension.getText()))
			dimension.setName(txtdimension.getText());
		else
			dimension.setName(levels.get(0).getLabel());

		if (!txtHiera.getText().isEmpty() && checktxtName(txtHiera.getText()))
			dimension.setHierarchyName(txtHiera.getText());

		dimension.setLevels(levels);

		return dimension;
	}

	public Boolean checktxtName(String name) {
		if (name.matches("\\s*"))
			return false;
		return true;
	}

	public ObjectFmdtDimension getobjectDimension() {
		return this;
	}

	private DropHandler dropHandler = new DropHandler() {

		@Override
		public void onDrop(DropEvent event) { /*modif kmo 30/09*/
			event.stopPropagation();
			event.preventDefault();
			String data = event.getData(ColumnDraggable.VARIABLE_ID);
			if(designer instanceof CubeDesignerPanel){
				FmdtData level = ((CubeDesignerPanel)designer).getData(data);
				addLevelZero(level);
				((CubeDesignerPanel)designer).removeFromparent();
			}
			else if(designer instanceof DatasetCubeDesignerPanel){
				FmdtData level = ((DatasetCubeDesignerPanel)designer).getData(data);
				addLevelZero(level);
				((DatasetCubeDesignerPanel)designer).removeFromparent();
			}
		}
	};

//	private DragOverHandler dragOverHandler = new DragOverHandler() {
//
//		@Override
//		public void onDragOver(DragOverEvent event) {
//		}
//	};
//
//	private DragLeaveHandler dragLeaveHandler = new DragLeaveHandler() {
//
//		@Override
//		public void onDragLeave(DragLeaveEvent event) {
//		}
//	};

	public List<FmdtData> getLevels() {
		return levels;
	}

	public Composite getDesigner() { /*modif kmo 30/09*/
		return designer;
	}

}
