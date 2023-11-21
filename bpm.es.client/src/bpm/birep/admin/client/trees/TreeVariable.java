package bpm.birep.admin.client.trees;

import bpm.vanilla.platform.core.beans.Variable;


public class TreeVariable extends TreeParent {
	
	private Variable variable;
	
	public TreeVariable(Variable variable) {
		super(variable.getName());
		this.variable = variable;
	}
	
	public Variable getVariable() {
		return this.variable;
	}
	
	public String toString() {
		return variable.getName();
	}
	

}
