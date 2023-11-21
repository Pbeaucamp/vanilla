package bpm.vanilla.platform.core.repository;

import java.io.Serializable;

public interface IRepositoryObject extends Serializable {

	public String getName();
	
	public int getId();
}
