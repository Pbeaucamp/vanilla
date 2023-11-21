package bpm.es.datasource.analyzer.ui.gef.model;

import bpm.vanilla.platform.core.repository.RepositoryItem;



public class Link {
	private Object source;
	private RepositoryItem target;
	
	public Link(Object source, RepositoryItem target){
		this.source = source;
		this.target = target;
	}
	
	public Object getSource(){
		return source;
	}
	
	public RepositoryItem getTarget(){
		return target;
	}
}
