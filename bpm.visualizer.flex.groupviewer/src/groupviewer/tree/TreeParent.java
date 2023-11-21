package groupviewer.tree;

import java.util.ArrayList;
import java.util.List;

public class TreeParent extends TreeObject {

	private List<ITreeObject> children = new ArrayList<ITreeObject>(2);

	public TreeParent(String name, Object data) {
		super(name, data);
	}

	public TreeParent(String name) {
		super(name);
	}

	public void addChild(ITreeObject child) {
		if (child == null)
			return;

		if (this.children == null)
			this.children = new ArrayList<ITreeObject>(2);

		for (ITreeObject p = this; p != null; p = p.getParent())
			if (child == p)
				throw new IllegalArgumentException(
						"Figure being added introduces cycle"); //$NON-NLS-1$

		if (child.getParent() != null)
			child.getParent().removeChild(child);

		this.children.add(child);
		child.setParent(this);
		((TreeObject) child).setFullName(getFullName() + "."+ child.getName());
	}
	
	public void addAllChild(List<ITreeObject> childList){
		if (childList != null){
			for (ITreeObject child : childList){
				addChild(child);
			}
		}
	}

	public void removeAllChild() {
		if (getChildren() != null){
			for (ITreeObject o : getChildren()) {
				if (o instanceof TreeParent)
					((TreeParent) o).removeAllChild();
				o.setParent(null);
			}
			children.clear();
		}	
	}

	public void removeChild(ITreeObject child) {
		if (getChildren() != null){
			children.remove(child);
			child.setParent(null);
		}	
	}

	public List<ITreeObject> getChildren() {
		return (children != null) ? children : null;
	}

	public Object[] getChildrenArray() {
		return (children != null) ? children.toArray() : null;
	}

	public boolean hasChildren() {
		return (getChildren() != null)? true : false;
	}

	public boolean isChild(Object obj) {
		return getChildren().contains(obj);
	}
	
	public int getChildrenLength(){
		return (getChildren()!=null)? children.size(): 0;
	}
}
