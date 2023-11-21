package bpm.gwt.commons.client.tree;

import bpm.gwt.commons.client.tree.TreeObjectWidget.IDragListener;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.cmis.CmisFolder;
import bpm.gwt.commons.shared.cmis.CmisItem;
import bpm.mdm.model.supplier.MdmDirectory;

import com.google.gwt.user.client.ui.TreeItem;

public class MdmTreeItem<T> extends TreeItem {

	private IDragListener dragListener;
	private TreeObjectWidget<T> objectWidget;
	
	private boolean loaded = false;
	private boolean canOnlySelectFolder;

	public MdmTreeItem(IDragListener dragListener, TreeObjectWidget<T> objectWidget, boolean canOnlySelectFolder) {
		super(objectWidget);
		this.dragListener = dragListener;
		this.canOnlySelectFolder = canOnlySelectFolder;
		
		buildItem(objectWidget);
	}
	
	public void buildItem(TreeObjectWidget<T> objectWidget) {
		this.objectWidget = objectWidget;
		
		removeItems();
		
		T item = objectWidget.getItem();
		if (item instanceof MdmDirectory) {
			MdmDirectory folder = (MdmDirectory) item;
			this.loaded = true;
			for (MdmDirectory object : folder.getChilds()) {
				this.addItem(new MdmTreeItem<MdmDirectory>(dragListener, new TreeObjectWidget<MdmDirectory>(dragListener, object), canOnlySelectFolder));
			}

			if (loaded) {
				setState(true);
			}
			else {
				this.addItem(new WaitTreeItem());
			}
		}
	}
	
	public void rebuildItem(CmisFolder folder) {
		removeItems();
		
		if (folder.isLoaded()) {
			this.loaded = true;
			for (CmisItem object : folder.getChilds()) {
				this.addItem(new MdmTreeItem<CmisItem>(dragListener, new TreeObjectWidget<CmisItem>(dragListener, object), canOnlySelectFolder));
			}
		}

		if (loaded) {
			setState(true);
		}
		else {
			this.addItem(new WaitTreeItem());
		}
	}

	@Override
	public void setSelected(boolean selected) {
		T item = objectWidget.getItem();
		if (!canOnlySelectFolder || item instanceof CmisFolder) {
			if (!selected) {
				this.objectWidget.removeStyleName(VanillaCSS.TREE_ITEM_SELECTED);
			}
			else {
				this.objectWidget.addStyleName(VanillaCSS.TREE_ITEM_SELECTED);
			}
		}
		super.setSelected(selected);
	}

	public T getItem() {
		return objectWidget.getItem();
	}
	
	public boolean isLoaded() {
		return loaded;
	}
}
