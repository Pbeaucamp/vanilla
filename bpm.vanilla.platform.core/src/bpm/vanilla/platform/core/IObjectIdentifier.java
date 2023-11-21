package bpm.vanilla.platform.core;

import java.io.Serializable;

public interface IObjectIdentifier extends Serializable {
	public int getRepositoryId();
	public int getDirectoryItemId();
}	
