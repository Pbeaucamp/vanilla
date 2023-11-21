package bpm.vanilla.api.core;

import bpm.vanilla.api.core.IAPIManager.IAPIType;
import bpm.vanilla.api.core.IAPIManager.IMethod;

public class APIAction {

	private IAPIType type;
	private IMethod method;
	private Object[] parameters;
	
	public APIAction(IAPIType type, IMethod method, Object... parameters) {
		this.type = type;
		this.method = method;
		this.parameters = parameters;
	}
	
	public IAPIType getType() {
		return type;
	}
	
	public IMethod getMethod() {
		return method;
	}
	
	public Object[] getParameters() {
		return parameters;
	}
}
