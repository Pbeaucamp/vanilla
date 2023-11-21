package bpm.gateway.ui.viewer;

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
