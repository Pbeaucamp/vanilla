package bpm.fasd.ui.measure.definition.tools;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.fasd.utils.trees.TreeObject;
import org.fasd.utils.trees.TreeParent;

public class TreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {

    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
    
	public void dispose() {
	}
    
	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}
    
	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject)child).getParent();
		}
		return null;
	}
    
	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			return ((TreeParent)parent).getChildren();
		}
		return new Object[0];
	}

    public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent)
			return ((TreeParent)parent).hasChildren();
		return false;
	}
}
