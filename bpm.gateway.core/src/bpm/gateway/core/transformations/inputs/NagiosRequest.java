package bpm.gateway.core.transformations.inputs;

public class NagiosRequest {
	
	private String name;
	private String request;
	
	public NagiosRequest() { }

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getRequest() {
		return request;
	}
}
