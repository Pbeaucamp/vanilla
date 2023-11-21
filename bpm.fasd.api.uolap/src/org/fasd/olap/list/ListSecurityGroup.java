package org.fasd.olap.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.fasd.security.SecurityGroup;

public class ListSecurityGroup extends Observable{

	public void setChanged() {
		
		super.setChanged();
		notifyObservers();
	}

	private List<SecurityGroup> list = new ArrayList<SecurityGroup>();
	
	public ListSecurityGroup(){
		
	}

	public List<SecurityGroup> getList(){
		return new ArrayList<SecurityGroup>(list);
	}
	
	public void add(SecurityGroup m){
		list.add(m);
	}
	
	public void remove(SecurityGroup m){
		list.remove(m);
	}
	
	public boolean contains(SecurityGroup m){
		return list.contains(m);
	}
}
