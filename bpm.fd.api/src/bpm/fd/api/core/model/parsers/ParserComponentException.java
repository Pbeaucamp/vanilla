package bpm.fd.api.core.model.parsers;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;

public class ParserComponentException extends AbstractParserException {
	private IComponentDefinition component;
	
	public ParserComponentException(IComponentDefinition component, String message, Throwable cause) {
		super(message, cause);
		this.component = component;
	}

	public ParserComponentException(IComponentDefinition component, String message) {
		super(message);
		this.component = component;
	}
	
	public Object  getDatas(){
		return component;
	}
}
