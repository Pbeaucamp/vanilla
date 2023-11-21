package bpm.birep.admin.client.trees;

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
	
	public String toString(){
		return it.getItemName();
	}


}
