package bpm.vanilla.platform.core.exceptions;

public class LicenceExpiredException extends VanillaException {
	
	public LicenceExpiredException(){
		super("Licence has expired. Please contact an administrator.");
	}
}
