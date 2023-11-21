package bpm.fwr.client.widgets;

import bpm.fwr.client.draggable.HasBin;
import bpm.fwr.client.draggable.widgets.DraggableColumnLabel;
import bpm.fwr.client.draggable.widgets.DraggableGroupAggregation;
import bpm.fwr.client.draggable.widgets.DraggableHTMLPanel;
import bpm.fwr.client.draggable.widgets.DraggableResourceHTML;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class BinWidget extends Image{
	
	public BinWidget(){ }
	
	public BinWidget(ImageResource resource) {
		super(resource);
	}
	
	public void manageWidget(Widget widget){
		if(widget.getParent() instanceof HasBin){
			HasBin parent = (HasBin)widget.getParent();
			parent.widgetToTrash(widget);
		}
		else if(widget instanceof ReportWidget){
			((ReportWidget)widget).widgetToTrash(widget);
		}
		else if(widget instanceof DraggableHTMLPanel){
			((DraggableHTMLPanel) widget).getColumn().getReportWidgetParent().widgetToTrash(widget);
		}
		else if(widget instanceof DraggableColumnLabel){
			((DraggableColumnLabel) widget).getDialogParent().widgetToTrash(widget);
		}
		else if(widget instanceof DraggableResourceHTML){
			((DraggableResourceHTML) widget).getReportPanel().widgetToTrash(widget);
		}
		else if(widget instanceof DraggableGroupAggregation){
			((DraggableGroupAggregation) widget).getDialogParent().widgetToTrash(widget);
		}
	}
}
