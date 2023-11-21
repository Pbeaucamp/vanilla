package org.fasd.utils.trees;

import java.util.ArrayList;
import java.util.List;


public class TreeParent extends TreeObject{

	private ArrayList<Object> children;

	public TreeParent(String name) {
			super(name);
			children = new ArrayList<Object>();
		}
	@SuppressWarnings("unchecked")
	public void addChild(TreeObject child) {
		if (child == null)
			return;
		children.add(child);
		child.setParent(this);
	}
	public void removeChild(TreeObject child) {
		children.remove(child);
		child.setParent(null);
	}
	@SuppressWarnings("unchecked")
	public Object[] getChildren() {
		return (Object[]) children.toArray(new Object[children.size()]);
	}
	public boolean hasChildren() {
		return children.size()>0;
	}
	public boolean isChild(Object obj) {
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i) instanceof TreeParent && obj instanceof TreeParent) {
				if (children.get(i).equals(obj)) {
					return true;
				}
			}
			if (children.get(i) instanceof TreeObject && obj instanceof TreeObject) {
				if (children.get(i).equals(obj)) {
					return true;
				}
			}
		}
		return false;
	}

	public List<Object> childList(){
		return children;
	}
}
