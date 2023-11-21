package bpm.vanilla.platform.core.remote.impl.components.internal;

import bpm.vanilla.platform.core.components.IRunIdentifier;

public class SimpleRunIdentifier implements IRunIdentifier{
	private String key;
	
	public SimpleRunIdentifier() {
	}
	
	public SimpleRunIdentifier(String key){
		this.key = key;
	}
	
	public String getKey(){
		return key;
	}
}
