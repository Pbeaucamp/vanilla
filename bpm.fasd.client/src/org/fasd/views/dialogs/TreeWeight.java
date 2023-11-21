package org.fasd.views.dialogs;

import org.fasd.utils.trees.TreeParent;

public class TreeWeight extends TreeParent {
	private int weight;
	public TreeWeight(int weight) {
		super(""); //$NON-NLS-1$
		this.weight = weight;
	}
	
	public int getWeight(){
		return weight;
	}

	public int getMaxWeight(){
		int r = weight;
		
		for(Object o : getChildren()){
			int t = ((TreeWeight)o).getMaxWeight();
			if (t>r)
				r=t;
		}
		
		return r;
	}
}
