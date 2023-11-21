package bpm.vanilla.oda.commons.trees;

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
}
