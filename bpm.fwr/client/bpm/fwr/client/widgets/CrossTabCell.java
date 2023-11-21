package bpm.fwr.client.widgets;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.client.action.ActionType;
import bpm.fwr.client.draggable.widgets.DraggableColumn;
import bpm.fwr.client.draggable.widgets.DraggableColumnHTML;
import bpm.fwr.client.draggable.widgets.DraggableHTMLPanel;
import bpm.fwr.client.utils.ColumnType;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class CrossTabCell extends AbsolutePanel{
	
	private CrossTabWidget parent;
	private ColumnType type;
	
	public CrossTabCell(CrossTabWidget parent, ColumnType type) {
		super();
		this.parent = parent;
		this.type = type;
	}
	
	public void manageWidget(Widget widget, int index){
		if(widget instanceof DraggableColumn){
			Column column = ((DraggableColumn)widget).getColumn().getClone();
			parent.manageColumns(column, type, index, true, ActionType.DEFAULT);
		}
	}
	
	public void switchWidget(Widget widget, int index){
		if(widget instanceof DraggableHTMLPanel){
			DraggableColumnHTML dragColumn = ((DraggableHTMLPanel)widget).getColumn();
			parent.switchWidget(dragColumn, type, index);
		}
	}
	
	public ColumnType getType(){
		return type;
	}
}
