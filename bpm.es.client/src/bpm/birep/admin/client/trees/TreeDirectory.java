package bpm.birep.admin.client.trees;

import bpm.vanilla.platform.core.repository.RepositoryDirectory;



public class TreeDirectory extends TreeParent {
	private RepositoryDirectory dir;
	
	public TreeDirectory(RepositoryDirectory d){
		super(d.getName());
		this.dir = d;
	}
	
	public RepositoryDirectory getDirectory(){
		return dir;
	}
	
	public String toString(){
		return dir.getName();
	}
}
