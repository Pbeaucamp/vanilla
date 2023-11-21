package org.fasd.olap.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPMeasure;

public class ListMeasure extends Observable{
	private List<OLAPMeasure> list = new ArrayList<OLAPMeasure>();
	
	public ListMeasure(){
		
	}

	public void setChanged() {
		
		super.setChanged();
		notifyObservers();
	}
	
	public List<OLAPMeasure> getList(){
		return new ArrayList<OLAPMeasure>(list);
	}
	
	public void add(OLAPMeasure m){
		list.add(m);
	}
	
	public void remove(OLAPMeasure m){
		list.remove(m);
	}
	
	public boolean contains(OLAPMeasure m){
		return list.contains(m);
	}
}
