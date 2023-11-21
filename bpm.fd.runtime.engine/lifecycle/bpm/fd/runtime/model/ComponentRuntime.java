package bpm.fd.runtime.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.fd.api.core.model.IBaseElement;
/**
 * This class represent the runtime side of the elements presents in the Dashboad at runtime.
 * It will store the dependancies between the elements 
 * 
 * @author ludo
 *
 */
public abstract class ComponentRuntime {
	private static class Sorter implements Comparator<ComponentRuntime>{

		@Override
		public int compare(ComponentRuntime o1, ComponentRuntime o2) {
			if (o1.getClass() == ComponentContainer.class){
				return -1;
			}
			else if (o2.getClass() == ComponentContainer.class){
				return 1;
			}
			return 0;
		}
		
	}
	
	private List<ComponentRuntime> target = new ArrayList<ComponentRuntime>();
	private List<ComponentRuntime> sources = new ArrayList<ComponentRuntime>();
	private IBaseElement element;
	
	public ComponentRuntime(IBaseElement element){
		this.element = element;
	
	}
	public List<ComponentRuntime> getTargets(boolean cascade){
		List<ComponentRuntime> l = new ArrayList<ComponentRuntime>(target);
//		Collections.sort(l, new Sorter());
		return l;
	}
	public String getName(){
		return element.getName();
	}
	public void addTarget(ComponentRuntime target){
		this.target.add(target);
		target.addSource(this);
	}
	
	private void addSource(ComponentRuntime source){
		if (!sources.contains(source) && source != this){
			sources.add(source);
		}
	}
	public List<ComponentRuntime> getSources(){
		return new ArrayList<ComponentRuntime>(sources);
	}
	
	final public IBaseElement getElement(){
		return element;
	}
}
