package bpm.document.management.core.model.aklademat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Classification implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int key;
	private String name;
	
	private Classification parent;
	private List<Classification> childs;
	
	public Classification() { }
	
	public Classification(int key, String name) {
		this.key = key;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Classification getParent() {
		return parent;
	}
	
	public void setParent(Classification parent) {
		this.parent = parent;
	}
	
	public List<Classification> getChilds() {
		return childs;
	}
	
	public void addChild(Classification child) {
		if (this.childs == null) {
			this.childs = new ArrayList<Classification>();
		}
		child.setParent(this);
		this.childs.add(child);
	}
	
//	public String getKey() {
//		return getParent() != null ? getParent().getKey() + "." + key : String.valueOf(key);
//	}
	
	public int getKey() {
		return key;
	}
}
