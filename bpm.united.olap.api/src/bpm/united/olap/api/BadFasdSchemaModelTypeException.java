package bpm.united.olap.api;

import org.fasd.olap.FAModel;

import bpm.vanilla.platform.core.IObjectIdentifier;

public class BadFasdSchemaModelTypeException extends Exception{
	private FAModel model;
	private IObjectIdentifier identifier;
	
	public BadFasdSchemaModelTypeException(FAModel model, IObjectIdentifier identifier){
		super("The FASD Model is not an UnitedOlap one");
		this.model = model;
		this.identifier = identifier;
	}

	/**
	 * @return the model
	 */
	public FAModel getModel() {
		return model;
	}

	/**
	 * @return the identifier
	 */
	public IObjectIdentifier getIdentifier() {
		return identifier;
	}
	
	
}
