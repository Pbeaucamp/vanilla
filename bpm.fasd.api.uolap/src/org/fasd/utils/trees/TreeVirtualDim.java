package org.fasd.utils.trees;

import org.fasd.olap.virtual.VirtualDimension;

public class TreeVirtualDim extends TreeParent {
	public VirtualDimension vDim;
	
	public TreeVirtualDim(VirtualDimension vDim){
		super(vDim.getName());
		this.vDim = vDim;
	}
	
	public VirtualDimension getVirtualDimension(){
		return vDim;
	}
}
