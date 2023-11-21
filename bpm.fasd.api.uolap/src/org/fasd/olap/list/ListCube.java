package org.fasd.olap.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.fasd.olap.OLAPCube;

public class ListCube extends Observable{

	public void setChanged() {
		
		super.setChanged();
		notifyObservers();
	}

	private List<OLAPCube> list = new ArrayList<OLAPCube>();
	
	public ListCube(){
		
	}

	public List<OLAPCube> getList(){
		return new ArrayList<OLAPCube>(list);
	}
	
	public void add(OLAPCube m){
		list.add(m);
	}
	
	public void remove(OLAPCube m){
		list.remove(m);
	}
	
	public boolean contains(OLAPCube m){
		return list.contains(m);
	}
}
