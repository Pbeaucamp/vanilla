package bpm.fd.api.core.model.structure.exceptions;

import bpm.fd.api.core.model.structure.IStructureElement;

public class StructureException extends Exception{

	private IStructureElement element;
	public StructureException(IStructureElement element) {
		super();
		this.element = element;
	}

	public StructureException(IStructureElement element, String message, Throwable cause) {
		super(message, cause);
		this.element = element;
	}

	public StructureException(IStructureElement element, String message) {
		super(message);
		this.element = element;
	}

	public StructureException(IStructureElement element, Throwable cause) {
		super(cause);
		this.element = element;
	}
	
	public IStructureElement getStructureElement(){
		return element;
	}

}
