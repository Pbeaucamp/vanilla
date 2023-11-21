package bpm.gateway.runtime2.internal;

import bpm.gateway.runtime2.RuntimeStep;

public class TransformationLog {
	public RuntimeStep parent;
	
	public int priority;
	
	public TransformationLog(RuntimeStep parent, int priority){
		this.parent = parent;
		this.priority = priority;
	}
	
	public String message;
}
