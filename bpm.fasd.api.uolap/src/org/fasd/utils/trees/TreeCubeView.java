package org.fasd.utils.trees;

import org.fasd.olap.CubeView;

public class TreeCubeView extends TreeObject {
	private CubeView view;
	
	public TreeCubeView(CubeView d) {
		super(d.getName());
		this.view =d;
	}

	public CubeView getCubeView(){
		return view;
	}
	

}
