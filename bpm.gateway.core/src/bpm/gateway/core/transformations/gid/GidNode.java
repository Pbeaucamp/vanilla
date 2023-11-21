package bpm.gateway.core.transformations.gid;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.Transformation;

public abstract class GidNode<T extends Transformation> {
	private T transformation;
	private GidNode<?> parent;
	private List<GidNode<?>> childs = new ArrayList<GidNode<?>>();
	
	public GidNode(T transformation){
		this.transformation = transformation;
	}
		
	public GidNode<?> getParent(){
		return parent;
	}
	
	public List<GidNode<?>> getChilds(){
		return childs;
	}
	
	public void addChild(GidNode<?> child){
		childs.add(child);
		child.setParent(this);
	}
	
	public void setParent(GidNode<?> parent){
		this.parent = parent;
	}
	
	public T getTransformation(){
		return transformation;
	}
	
	abstract public Query evaluteQuery();
	
	public String dump(String offset) {
		StringBuffer buf = new StringBuffer();
		buf.append(getTransformation().getName());
		buf.append("\n");
		for(GidNode n : getChilds()){
			
			buf.append(offset + "    |_ ");
			buf.append(n.dump( offset + "    |"));
			
		}
		return buf.toString();
	}
	
	
}
