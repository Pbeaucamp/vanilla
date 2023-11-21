package bpm.gwt.commons.client.viewer.fmdtdriller;

import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TreeItem;

public class MetadataTreeItem extends TreeItem {

	private HTML html;
	
	public MetadataTreeItem(String label, ImageResource resource, Object item, DoubleClickHandler doubleClickHandler) {
		this.html = new HTML(new Image(resource) + label);
		setWidget(html);

		addDoubleClickHandler(doubleClickHandler);
		
		setUserObject(item);
	}
	
	public void addDoubleClickHandler(DoubleClickHandler handler){
		this.html.addDoubleClickHandler(handler);
	}
}
