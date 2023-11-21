package org.fasd.olap.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.fasd.olap.OLAPDimension;

public class ListDimension extends Observable{

	private List<OLAPDimension> list = new ArrayList<OLAPDimension>();
	
	public ListDimension(){
		
	}

	public void setChanged() {
		super.setChanged();
		notifyObservers();
	}
	
	public List<OLAPDimension> getList(){
		return new ArrayList<OLAPDimension>(list);
	}
	
	public void add(OLAPDimension m){
		list.add(m);
	}
	
	public void clear() {
		list.clear();
	}
	
	public void remove(OLAPDimension m){
		list.remove(m);
	}
	
	public boolean contains(OLAPDimension m){
		return list.contains(m);
	}

	public void swap(OLAPDimension d1, OLAPDimension d2) {
		int i1 = list.indexOf(d1);
		int i2 = list.indexOf(d2);
		
		list.set(i1, d2);
		list.set(i2, d1);
		
	}
}
