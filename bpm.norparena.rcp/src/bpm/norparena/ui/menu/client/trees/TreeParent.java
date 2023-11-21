package bpm.norparena.ui.menu.client.trees;

import java.util.ArrayList;
import java.util.List;


public class TreeParent<T> extends TreeObject<T>{

	private ArrayList<Object> children;
	
	public TreeParent(String name, T data){
		super(name, data);
		children = new ArrayList<Object>();
	}
	
	public TreeParent(String name) {
			super(name);
			children = new ArrayList<Object>();
		}
	@SuppressWarnings("unchecked")
	public void addChild(TreeObject<?> child) {
		if (child == null)
			return;
		children.add(child);
		child.setParent(this);
		child.fullName = fullName + "/" + child.getName(); //$NON-NLS-1$
	}
	
	public void removeAllChild(){
		for(Object o : getChildren()){
			if (o instanceof TreeObject<?>){
				((TreeObject<?>)o).setParent(null);
			}
			
		}
		children.clear();
	}
	
	public void removeChild(TreeObject<?> child) {
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
