package org.fasd.datasource.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.fasd.olap.OLAPRelation;

public class ListRelation extends Observable {

	public void setChanged() {
		
		super.setChanged();
		notifyObservers();
	}

	private List<OLAPRelation> list = new ArrayList<OLAPRelation>();
	
	public ListRelation(){
		
	}

	public List<OLAPRelation> getList(){
		return list;
	}
	
	public void add(OLAPRelation m){
		list.add(m);
	}
	
	public void remove(OLAPRelation m){
		list.remove(m);
	}
	
	public boolean contains(OLAPRelation m){
		return list.contains(m);
	}
}
