package bpm.vanillahub.web.client.utils;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.user.client.ui.TreeItem;

public class RepositoryTreeItem extends TreeItem {

	private RepositoryObjectWidget widget;
	private IRepositoryObject repositoryObject;

	public RepositoryTreeItem(RepositoryObjectWidget repositoryObjectWidget, boolean dragAndDrop) {
		super(repositoryObjectWidget);
		this.widget = repositoryObjectWidget;
		this.repositoryObject = repositoryObjectWidget.getRepositoryObject();

		if (repositoryObject instanceof RepositoryDirectory) {
			RepositoryDirectory dir = ((RepositoryDirectory) repositoryObject);
			if (dir.getChilds() != null) {
				for (IRepositoryObject object : dir.getChilds()) {
					if (object instanceof RepositoryDirectory || isEtlOrWorkflow(object)) {
						this.addItem(new RepositoryTreeItem(new RepositoryObjectWidget(object, dragAndDrop), dragAndDrop));
					}
				}
			}
		}
	}
	
	private boolean isEtlOrWorkflow(IRepositoryObject item) {
		return item instanceof RepositoryItem && (((RepositoryItem) item).getType() == IRepositoryApi.BIW_TYPE || ((RepositoryItem) item).getType() == IRepositoryApi.GTW_TYPE);
	}

	@Override
	public void setSelected(boolean selected) {
		if (!selected) {
			this.widget.removeStyleName("treeItemSelected");
		}
		else {
			this.widget.addStyleName("treeItemSelected");
		}
		super.setSelected(selected);
	}

	public IRepositoryObject getRepositoryObject() {
		return repositoryObject;
	}
}
