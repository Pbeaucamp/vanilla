package bpm.sqldesigner.ui.view.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.procedure.SQLProcedure;
import bpm.sqldesigner.ui.internal.Workspace;

public class DatabaseTreeContentProvider implements ITreeContentProvider {

	
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Node) {
			return ((Node) parentElement).getChildren();
		}
		return new Object[] {};

	}

	
	public Object getParent(Object element) {
		if (element instanceof Node) {
			return ((Node) element).getParent();
		}
		return null;
	}

	
	public boolean hasChildren(Object element) {

		if (element instanceof Node) {
			Node node = (Node) element;

			if (node instanceof SQLProcedure)
				return false;

			if (node.isNotLoaded()) {
				return true;
			}
			if (node.getChildren() != null)
				return node.getChildren().length != 0;
			else
				return false;
		}
		return false;
	}

	
	public Object[] getElements(Object inputElement) {
		
		if (inputElement instanceof Workspace){
			
			return ((Workspace)inputElement).getOpenedClusters().toArray(new Object[((Workspace)inputElement).getOpenedClusters().size()]);
		}
		
		return getChildren(inputElement);
	}

	
	public void dispose() {

	}

	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
