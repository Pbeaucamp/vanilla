package bpm.gwt.commons.shared.fmdt;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FmdtObject implements IsSerializable {

	private String name;

	public FmdtObject() {
		super();
	}

	public FmdtObject(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
