package bpm.vanilla.platform.core.exceptions;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;

public class VanillaComponentDownException extends VanillaException{
	private IVanillaComponentIdentifier identifier;
	private IObjectIdentifier objectId;
	
	public VanillaComponentDownException(IVanillaComponentIdentifier identifier, IObjectIdentifier objectId){
		super("The VanillaComponent " + identifier.getComponentId() + " at " + identifier.getComponentUrl() + " does not respond");
		this.objectId = objectId;
		this.identifier = identifier;
	}
	
	public IVanillaComponentIdentifier getComponentIdentifier(){
		return identifier;
	}
	
	public IObjectIdentifier getItemIdentifier(){
		return objectId;
	}

}
