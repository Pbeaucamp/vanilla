package bpm.gwt.commons.client.tree;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class RepositoryTreeItem extends TreeItem {
	
	private RepositoryObjectWidget widget;
	private IRepositoryObject repositoryObject;
	
	public RepositoryTreeItem(RepositoryObjectWidget repositoryObjectWidget, boolean dragAndDrop) {
		super(repositoryObjectWidget);
		this.widget = repositoryObjectWidget;
		this.repositoryObject = repositoryObjectWidget.getRepositoryObject();
		
		if(repositoryObject instanceof RepositoryDirectory) {
			RepositoryDirectory dir = ((RepositoryDirectory) repositoryObject);
			if(dir.getChilds() != null) {
				for(IRepositoryObject object : dir.getChilds()) {
					if(object instanceof RepositoryItem) {
						if(!((RepositoryItem)object).isDisplay()) {
							continue;
						}
					}
					this.addItem(new RepositoryTreeItem(new RepositoryObjectWidget(object, dragAndDrop), dragAndDrop));
				}
			}
		}
	}
	
	public RepositoryTreeItem(RepositoryObjectWidget repositoryObjectWidget, boolean dragAndDrop, int selectedItem, Tree tree) {
		super(repositoryObjectWidget);
		this.widget = repositoryObjectWidget;
		this.repositoryObject = repositoryObjectWidget.getRepositoryObject();
		
		if(repositoryObject instanceof RepositoryDirectory) {
			RepositoryDirectory dir = ((RepositoryDirectory) repositoryObject);
			if(dir.getChilds() != null) {
				for(IRepositoryObject object : dir.getChilds()) {
					if(object instanceof RepositoryItem) {
						if(!((RepositoryItem)object).isDisplay()) {
							continue;
						}
					}
					RepositoryTreeItem item = new RepositoryTreeItem(new RepositoryObjectWidget(object, dragAndDrop), dragAndDrop, selectedItem, tree);
					this.addItem(item);
					if(item.getRepositoryObject().getId() == selectedItem) {
//						tree.setSelectedItem(item);
						item.setSelected(true);
					}
				}
			}
		}
		setState(true);
	}

	@Override
	public void setSelected(boolean selected){
		if(!selected){
			this.widget.removeStyleName(VanillaCSS.TREE_ITEM_SELECTED);
		}
		else {
			this.widget.addStyleName(VanillaCSS.TREE_ITEM_SELECTED);
		}
		super.setSelected(selected);
	}
	
	public IRepositoryObject getRepositoryObject() {
		return repositoryObject;
	}
}
