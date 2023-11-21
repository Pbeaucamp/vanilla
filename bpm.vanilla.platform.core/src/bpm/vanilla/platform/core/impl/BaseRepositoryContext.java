package bpm.vanilla.platform.core.impl;

import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;

public class BaseRepositoryContext implements IRepositoryContext{
	private Group group;
	private Repository repository;
	private IVanillaContext vanillaContext;
	
	public BaseRepositoryContext(IVanillaContext vanillaContext, Group group, Repository repository) {
		this.group = group;
		this.repository = repository;
		this.vanillaContext = vanillaContext;
	}

	/**
	 * @return the group
	 */
	public Group getGroup() {
		return group;
	}

	

	/**
	 * @return the vanillaContext
	 */
	public IVanillaContext getVanillaContext() {
		return vanillaContext;
	}

	/**
	 * @return the repository
	 */
	public Repository getRepository() {
		return repository;
	}

	
	

}
