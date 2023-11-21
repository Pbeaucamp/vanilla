package bpm.vanilla.platform.core;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;


public interface IRepositoryContext {
	public Group getGroup();
	public Repository getRepository();
	public IVanillaContext getVanillaContext();
	
	
}
