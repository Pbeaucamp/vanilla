package bpm.vanilla.platform.core.impl;

import java.io.Serializable;

import bpm.vanilla.platform.core.IObjectIdentifier;

public class ObjectIdentifier implements IObjectIdentifier, Serializable{
	private int repositoryId;
	private int directoryItemId;
	public ObjectIdentifier(){}
	public ObjectIdentifier(int repositoryId, int directoryItemId){
		this.repositoryId = repositoryId;
		this.directoryItemId = directoryItemId;
	}

	/**
	 * @return the repositoryId
	 */
	public int getRepositoryId() {
		return repositoryId;
	}

	/**
	 * @return the directoryItemId
	 */
	public int getDirectoryItemId() {
		return directoryItemId;
	}
	
	@Override
	public boolean equals(Object arg0) {
		if (arg0 == null){
			return false;
		}
		if (!(arg0 instanceof IObjectIdentifier)){
			return false;
		}
		
		IObjectIdentifier id = (IObjectIdentifier)arg0;
		
		return id.getDirectoryItemId() == getDirectoryItemId() && id.getRepositoryId() == getRepositoryId();
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("{repositoryId=" + getRepositoryId() + ";directoryItemId=" + getDirectoryItemId() + "}");
		return b.toString();
	}
	
}
