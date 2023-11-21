package org.fasd.utils.trees;

import org.fasd.security.SecurityDim;

public class TreeDimView extends TreeParent {
	private SecurityDim dim;
	
	public TreeDimView(SecurityDim d) {
		super(d.getName());
		this.dim =d;
		
	}

	public SecurityDim getSecurityDim(){
		return dim;
	}
	
	
}
