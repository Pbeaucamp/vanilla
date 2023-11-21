package bpm.fd.design.ui.properties.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PropertyFinder {
	private List<Property> properties ;
	
	private List<Property> matching = new ArrayList<Property>();
	private Iterator<Property> iterator;
	
	public PropertyFinder(List<Property> props){
		this.properties = props;
	}
	
	public Property getNext(){
		if (!iterator.hasNext()){
			return null;
		}
		return iterator.next();
	}
	
	public boolean find(String text){
		iterator = null;
		matching.clear();
		lookup(text, properties);
		iterator = matching.iterator();
		return iterator.hasNext();
	}
	
	private void lookup(String text, List<Property> props){
		for(Property p : props){
			if (p.getName().toLowerCase().contains(text.toLowerCase())){
				matching.add(p);
			}
			
			if (p instanceof PropertyGroup){
				lookup(text, ((PropertyGroup)p).getProperties());
			}
		}
	}
}
