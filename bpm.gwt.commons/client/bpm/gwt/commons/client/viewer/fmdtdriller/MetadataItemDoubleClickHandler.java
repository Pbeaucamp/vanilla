package bpm.gwt.commons.client.viewer.fmdtdriller;

import bpm.vanilla.platform.core.beans.fmdt.FmdtData;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;

public class MetadataItemDoubleClickHandler implements DoubleClickHandler {
	
	private DesignerPanel parent;
	private FmdtData item;
	
	public MetadataItemDoubleClickHandler(DesignerPanel parent, FmdtData item) {
		super();
		this.parent = parent;
		this.item = item;
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		parent.add(item);
	}
}
