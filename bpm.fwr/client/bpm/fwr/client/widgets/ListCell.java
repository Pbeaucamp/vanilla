package bpm.fwr.client.widgets;

import java.util.List;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.client.action.ActionType;
import bpm.fwr.client.dialogs.HyperlinkDialog;
import bpm.fwr.client.draggable.widgets.DraggableColumn;
import bpm.fwr.client.draggable.widgets.DraggableColumnHTML;
import bpm.fwr.client.draggable.widgets.DraggableHTMLPanel;
import bpm.fwr.client.draggable.widgets.DraggablePaletteItem;
import bpm.fwr.client.utils.ColumnType;
import bpm.fwr.client.utils.WidgetType;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class ListCell extends AbsolutePanel{
	
	private ListWidget parent;
	private ColumnType type;
	
	public ListCell(ListWidget parent, ColumnType type) {
		super();
		this.parent = parent;
		this.type = type;
	}
	
	public void manageWidget(Widget widget, int index){
		if(widget instanceof DraggableColumn){
			Column column = ((DraggableColumn)widget).getColumn().getClone();
			parent.manageColumns(column, type, index, true, ActionType.DEFAULT);
		}
		else if(widget instanceof DraggablePaletteItem && type == ColumnType.DETAIL){
			DraggablePaletteItem comp = (DraggablePaletteItem)widget;
			if(comp.getType() == WidgetType.HYPERLINK){
				List<DataSet> dsAvailable = parent.getReportSheetParent().getAvailableDatasets();
				HyperlinkDialog dial = new HyperlinkDialog(parent, index, parent.getReportSheetParent().getPanelParent().getPanelparent().getMetadatas(), dsAvailable, null);
				dial.center();
			}
		}
	}

	public void switchWidget(Widget widget, int index){
		if(widget instanceof DraggableHTMLPanel){
			DraggableColumnHTML dragColumn = ((DraggableHTMLPanel)widget).getColumn();
			parent.switchWidget(dragColumn, type, index);
		}
	}
}
