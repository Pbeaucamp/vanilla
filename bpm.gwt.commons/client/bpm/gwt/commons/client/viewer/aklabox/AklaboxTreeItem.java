package bpm.gwt.commons.client.viewer.aklabox;

import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.Tree;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.user.client.ui.TreeItem;

public class AklaboxTreeItem extends TreeItem {

	private AklaboxFolderWidget widget;
	private Tree aklaboxFolder;

	public AklaboxTreeItem(AklaboxFolderWidget aklaboxWidget) {
		super(aklaboxWidget);
		this.widget = aklaboxWidget;
		this.aklaboxFolder = aklaboxWidget.getAklaboxFolder();

		if (aklaboxFolder.getChildren() != null) {
			for (IObject object : aklaboxFolder.getChildren()) {
				if (object instanceof Tree) {
					this.addItem(new AklaboxTreeItem(new AklaboxFolderWidget((Tree) object)));
				}
			}
		}
	}

	@Override
	public void setSelected(boolean selected) {
		if (!selected) {
			this.widget.removeStyleName(VanillaCSS.TREE_ITEM_SELECTED);
		}
		else {
			this.widget.addStyleName(VanillaCSS.TREE_ITEM_SELECTED);
		}
		super.setSelected(selected);
	}

	public Tree getAklaboxFolder() {
		return aklaboxFolder;
	}
}
