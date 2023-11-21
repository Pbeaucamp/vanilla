package bpm.gwt.commons.shared.fmdt;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FmdtPackage extends FmdtObject implements IsSerializable {

	private List<String> connections;
	
	public FmdtPackage() {
		super();
	}

	public FmdtPackage(String name) {
		super(name);
	}

	public List<String> getConnections() {
		return connections;
	}

	public void setConnections(List<String> connections) {
		this.connections = connections;
	}
	
	
	
}
