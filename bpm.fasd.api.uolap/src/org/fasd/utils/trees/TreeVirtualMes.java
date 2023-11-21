package org.fasd.utils.trees;

import org.fasd.olap.virtual.VirtualMeasure;

public class TreeVirtualMes extends TreeParent {
	public VirtualMeasure vMes;
	
	public TreeVirtualMes(VirtualMeasure vMes){
		super(vMes.getName());
		this.vMes = vMes;
	}
	
	public VirtualMeasure getVirtualMeasure(){
		return vMes;
	}
}
