package bpm.gwt.aklabox.commons.client.tree;

import bpm.document.management.core.model.Tree;
import bpm.gwt.aklabox.commons.client.customs.TreeItemPanel.ItemSize;
import bpm.gwt.aklabox.commons.client.images.CommonImages;

public class DirectoryTreeItem extends CustomTreeItem {

	private Tree directory;

	public DirectoryTreeItem(Tree directory, int type) {
		this.directory = directory;

		DocHTML html = new DocHTML(CommonImages.INSTANCE.ic_folder(), directory.getName(), directory, ItemSize.SMALL);
		this.setWidget(html);
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
	}
	
	public Tree getDirectory() {
		return directory;
	}

}
