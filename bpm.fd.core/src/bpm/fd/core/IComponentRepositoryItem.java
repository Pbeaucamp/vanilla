package bpm.fd.core;

import java.io.Serializable;

public interface IComponentRepositoryItem extends Serializable {

	public int getItemId();
	
	public void setItemId(int itemId);
	
	public int getItemType();
	
}
