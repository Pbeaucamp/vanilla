package bpm.birep.admin.client.trees;

import bpm.vanilla.platform.core.beans.Repository;


public class TreeRepositoryDefinition extends TreeParent {

	private Repository repository;
	
	public TreeRepositoryDefinition(Repository repository){
		super(repository.getName());
		this.repository = repository;
	}
	
	public Repository getRepository(){
		return repository;
	}
	
	public String toString(){
		return repository.getName();
	}
	
}
