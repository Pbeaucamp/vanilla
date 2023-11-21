package bpm.faweb.client.tree;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TreeItem;

public class TreeLevel extends TreeItem {
	private String caption;
	
	public TreeLevel() {
		super();
	}

	public TreeLevel(String text) {
		super(new HTML(text));
	}
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	
}
