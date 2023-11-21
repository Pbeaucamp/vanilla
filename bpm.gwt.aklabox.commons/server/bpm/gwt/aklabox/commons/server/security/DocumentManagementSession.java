package bpm.gwt.aklabox.commons.server.security;

import bpm.document.management.core.model.InterfaceSetter;

public class DocumentManagementSession extends CommonSession {
	
	private InterfaceSetter setter;

	public String getApplicationId() {
		return null;
	}

	public InterfaceSetter getSetter() {
		return setter;
	}

	public void setSetter(InterfaceSetter setter) {
		this.setter = setter;
	}
}
