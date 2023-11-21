package bpm.vanilla.api.controller.commons;

public class APIResponse {
	
	private String status;
	private Object result;
	
	public APIResponse(Object result) {
		this.status = "success";
		this.result = result;
	}
	
	public APIResponse(String status) {
		this.status = status;
	}
	
	public APIResponse(String status, Object result) {
		this.status = status;
		this.result = result;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
	
	
	
	
}
