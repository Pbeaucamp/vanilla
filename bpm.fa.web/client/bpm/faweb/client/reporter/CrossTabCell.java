package bpm.faweb.client.reporter;


import bpm.faweb.client.dnd.DraggableTreeItem;
import bpm.faweb.client.reporter.widgets.CrossTabWidget;

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
		if(widget instanceof DraggableTreeItem){
			DraggableTreeItem column = ((DraggableTreeItem)widget).cloneItem();
			parent.manageColumns(column, type, index, true);
		}
	}
	
	public void switchWidget(Widget widget, int index){
		if(widget instanceof DraggableHTMLPanel){
			DraggableColumnHTML dragColumn = ((DraggableHTMLPanel)widget).getColumn();
			parent.switchWidget(dragColumn, type, index);
		}else if(widget instanceof DraggableTreeItem){
			
			DraggableTreeItem column = ((DraggableTreeItem)widget).cloneItem();
			
			parent.manageColumns(column, type, index, false);
			
		}
	}
	
	public ColumnType getType(){
		return type;
	}
}
