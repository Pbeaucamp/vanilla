package bpm.document.management.core.model;

import java.io.Serializable;

public interface IAdminDematObject extends Serializable {
	
	public void setParent(AkladematAdminEntity<? extends IAdminDematObject> entity);
}
