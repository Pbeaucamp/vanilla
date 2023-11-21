package org.fasd.olap.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.fasd.olap.virtual.VirtualCube;

public class ListVirtualCube extends Observable{

	public void setChanged() {
		
		super.setChanged();
		notifyObservers();
	}

	private List<VirtualCube> list = new ArrayList<VirtualCube>();
	
	public ListVirtualCube(){
		
	}

	public List<VirtualCube> getList(){
		return new ArrayList<VirtualCube>(list);
	}
	
	public void add(VirtualCube m){
		list.add(m);
	}
	
	public void remove(VirtualCube m){
		list.remove(m);
	}
	
	public boolean contains(VirtualCube m){
		return list.contains(m);
	}
}
