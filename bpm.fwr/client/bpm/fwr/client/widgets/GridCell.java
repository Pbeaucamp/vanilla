package bpm.fwr.client.widgets;

import bpm.fwr.client.draggable.HasBin;
import bpm.fwr.client.draggable.widgets.DraggableImageHTML;
import bpm.fwr.client.draggable.widgets.DraggablePaletteItem;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class GridCell extends AbsolutePanel implements HasBin{

	private GridWidget parent;
	private int col, row;
	private ReportWidget widget;
	
	public GridCell(GridWidget parent, 
			int col, int row) {
		super();
		this.parent = parent;
		this.col = col;
		this.row = row;
	}
	
	public void manageWidget(Widget widget){
		if(widget instanceof DraggablePaletteItem){
			parent.manageWidget((DraggablePaletteItem)widget, col, row);
		}
	}
	
	public void setWidget(ReportWidget widget){
		this.widget = widget;
		this.add(widget);
	}
	
	public ReportWidget getWidget(){
		return widget;
	}

	@Override
	public void widgetToTrash(Object widget) {
		removeWidget((ReportWidget) widget);
	}

	public void removeWidget(ReportWidget widget) {
		this.widget = null;
		this.remove(widget);
	}
	
	public int getCol(){
		return col;
	}
	
	public int getRow(){
		return row;
	}
}
