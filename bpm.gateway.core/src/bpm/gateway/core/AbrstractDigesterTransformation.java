package bpm.gateway.core;

import org.apache.commons.digester.Digester;


public abstract class AbrstractDigesterTransformation {
		
	public AbrstractDigesterTransformation() {
	}
	
	
	
	abstract public void createCallbacks(Digester digester, String root);
	
	
}
