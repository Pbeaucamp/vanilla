package bpm.faweb.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;


public class TreeParentDTO extends TreeObjectDTO implements IsSerializable{
	protected List children;

	public TreeParentDTO() {
		super("");
		children = new ArrayList();
	}
	
	public TreeParentDTO(String name) {
			super(name);
			children = new ArrayList();
		}
	
	public void addChild(TreeObjectDTO child) {
		if (child == null)
			return;
		children.add(child);
		child.setParent(this);
	}
	public void removeChild(TreeObjectDTO child) {
		children.remove(child);
		child.setParent(null);
	}
	
	public Object[] getChildren() {
		return (Object[]) children.toArray(new Object[children.size()]);
	}
	public boolean hasChildren() {
		return children.size()>0;
	}

	public List childList(){
		return children;
	}

}
