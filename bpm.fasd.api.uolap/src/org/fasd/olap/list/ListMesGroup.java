package org.fasd.olap.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.fasd.olap.OLAPDimensionGroup;
import org.fasd.olap.OLAPMeasureGroup;

public class ListMesGroup extends Observable{

	public void setChanged() {
		
		super.setChanged();
		notifyObservers();
	}

	private List<OLAPMeasureGroup> list = new ArrayList<OLAPMeasureGroup>();
	
	public ListMesGroup(){
		
	}

	public List<OLAPMeasureGroup> getList(){
		return new ArrayList<OLAPMeasureGroup>(list);
	}
	
	public void add(OLAPMeasureGroup m){
		list.add(m);
	}
	
	public void remove(OLAPMeasureGroup m){
		list.remove(m);
	}
	
	public boolean contains(OLAPMeasureGroup m){
		return list.contains(m);
	}
}
