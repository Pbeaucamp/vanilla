package bpm.vanilla.platform.core.exceptions;

import bpm.vanilla.platform.core.beans.VanillaSession;

public class VanillaSessionExpiredException extends VanillaException{
	private String sessionUuid;
	
	public VanillaSessionExpiredException(VanillaSession session){
		super("The session " + session.getUuid() + " has expired");
	}
	public VanillaSessionExpiredException(String mssg){
		super(mssg);
	}
			
	
}
