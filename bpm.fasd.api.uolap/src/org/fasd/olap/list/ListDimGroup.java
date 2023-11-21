package org.fasd.olap.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.fasd.olap.OLAPDimensionGroup;

public class ListDimGroup extends Observable{

	public void setChanged() {
		
		super.setChanged();
		notifyObservers();
	}

	private List<OLAPDimensionGroup> list = new ArrayList<OLAPDimensionGroup>();
	
	public ListDimGroup(){
		
	}

	public List<OLAPDimensionGroup> getList(){
		return new ArrayList<OLAPDimensionGroup>(list);
	}
	
	public void add(OLAPDimensionGroup m){
		list.add(m);
	}
	
	public void remove(OLAPDimensionGroup m){
		list.remove(m);
	}
	
	public boolean contains(OLAPDimensionGroup m){
		return list.contains(m);
	}
}
