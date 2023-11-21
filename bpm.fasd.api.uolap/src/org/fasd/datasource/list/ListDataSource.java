package org.fasd.datasource.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.fasd.datasource.DataSource;

public class ListDataSource extends Observable {

	public void setChanged() {
		
		super.setChanged();
		notifyObservers();
	}

	private List<DataSource> list = new ArrayList<DataSource>();
	
	public ListDataSource(){
		
	}

	public List<DataSource> getList(){
		return list;
	}
	
	public void add(DataSource m){
		list.add(m);
	}
	
	public void remove(DataSource m){
		list.remove(m);
	}
	
	public boolean contains(DataSource m){
		return list.contains(m);
	}
	
	public void renameDataSource(DataSource m, String name){
		for(DataSource s : list){
			if (s == m){
				m.setDSName(name);
				setChanged();
				break;
			}
		}
	}
}
