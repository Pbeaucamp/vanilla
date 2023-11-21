package bpm.vanilla.server.commons.pool;

import bpm.vanilla.platform.core.IRepositoryContext;


/**
 * Represent an identifier for a model inside the pool
 * @author ludo
 *
 */
public class VanillaItemKey {
	private int directoryItemId = -1;
	private IRepositoryContext repositoryContext;
	
	
	/**
	 * @param directoryItemId
	 * @param repCtx
	 */
	public VanillaItemKey(IRepositoryContext repCtx, int directoryItemId) {
		this.directoryItemId = directoryItemId;
		this.repositoryContext = repCtx;
	}

	

	/**
	 * @return the directoryItemId
	 */
	public int getDirectoryItemId() {
		return directoryItemId;
	}


	public IRepositoryContext getRepositoryContext(){
		return repositoryContext;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null){
			return false;
		}
		
		if (obj.getClass() != VanillaItemKey.class){
			return false;
		}
		VanillaItemKey k = (VanillaItemKey)obj;
		if (directoryItemId != k.directoryItemId){
			return false;
		}
		if (!repositoryContext.equals(k.getRepositoryContext())){
			return false;
		}
		
		
		return true;
	}
	
	
	
}
