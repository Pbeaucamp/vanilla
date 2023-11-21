package bpm.faweb.client.reporter.widgets;

import bpm.faweb.client.reporter.DraggableHTMLPanel;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class BinWidget extends Image{
	
	public BinWidget(){ }
	
	public BinWidget(ImageResource resource) { 
		super(resource);
	}
	
	public void manageWidget(Widget widget){
		if(widget instanceof DraggableHTMLPanel){
			((DraggableHTMLPanel) widget).getColumn().getCrossWidget().widgetToTrash(widget);
		}
	}
}
