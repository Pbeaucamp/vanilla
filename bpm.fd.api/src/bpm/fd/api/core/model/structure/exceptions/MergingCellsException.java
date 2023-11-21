package bpm.fd.api.core.model.structure.exceptions;

import bpm.fd.api.core.model.structure.IStructureElement;

public class MergingCellsException extends StructureException{

	public MergingCellsException(IStructureElement element) {
		super(element);
		
	}

	public MergingCellsException(IStructureElement element, String message) {
		super(element, message);
		
	}

	
	
	

}
