package groupviewer.views;

import java.util.ArrayList;

public class RootElement {
	
	ArrayList<Object> children;
	private String title;
	
	public RootElement(String title){
		this.title = title;
	}
	
	public String getName(){
		return this.title;
	}
	
	public void addChild(Object child){
		if (children == null)
			children = new ArrayList<Object>();
		if (child !=null)
			children.add(child);
	}
	public Object[] getChildren(){
		if (children != null)
			return children.toArray();
		return null;
	}

}
