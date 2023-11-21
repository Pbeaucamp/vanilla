package org.fasd.utils.trees;

import org.fasd.security.View;

public class TreeView extends TreeObject {
	private View view;
	
	public TreeView(View d) {
		super(d.getName());
		this.view =d;
	}

	public View getView(){
		return view;
	}
	

}
