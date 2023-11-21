package bpm.workflow.runtime.model;

import bpm.workflow.runtime.resources.BiRepositoryObject;

public interface IRepositoryItem {

	public void setItem(BiRepositoryObject obj);
	
	public BiRepositoryObject getItem();
	
}
