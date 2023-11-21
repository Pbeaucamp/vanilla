package bpm.profiling.ui.trees;

import java.util.ArrayList;
import java.util.List;


public class TreeParent extends TreeObject{

	private ArrayList<TreeObject> children;

	public TreeParent(String name) {
			super(name);
			children = new ArrayList<TreeObject>();
		}
	@SuppressWarnings("unchecked") //$NON-NLS-1$
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
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public List<TreeObject> getChildren() {
		return children;
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


	@Override
	public String toString() {
		return getName();
	}
	
	public TreeObject getChildNamed(String s){
		for(TreeObject o : children){
			if (o.getName().equals(s)){
				return o;
			}
		}
		
		return null;
	}
	
	public void removeAll(){
		children.clear();
	}
}
