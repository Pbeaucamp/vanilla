package metadata.client.trees;

import bpm.vanilla.platform.core.repository.RepositoryItem;

public class TreeItem extends TreeParent {
	private RepositoryItem it;
	public TreeItem(RepositoryItem it){
		super(it.getItemName());
		this.it = it;
	}
	
	public RepositoryItem getItem(){
		return it;
	}
	@Override
	public Object getContainedModelObject() {
		return it;
	}
}
