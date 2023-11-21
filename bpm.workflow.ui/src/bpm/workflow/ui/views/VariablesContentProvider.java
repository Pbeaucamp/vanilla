package bpm.workflow.ui.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.viewer.TreeObject;

/**
 * Provider of the variables view
 * @author CHARBONNIER, MARTIN
 *
 */
public class VariablesContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	public void dispose() {
		
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

	public Object[] getChildren(Object parent) {
		if (parent == VariablesViewPart.root){
			return VariablesViewPart.root.getChildren().toArray(new TreeObject[VariablesViewPart.root.getChildren().size()]);
			
		}
		else if (parent == VariablesViewPart.variables) {
			return getVariables();
		}
		return null;
	}
	
	private Object[] getVariables() {
		return ListVariable.getInstance().getVariables().toArray();
	}

	public Object getParent(Object element) {
		if (element == VariablesViewPart.root){
			return VariablesViewPart.root;
		}
		else if (element == VariablesViewPart.variables){
			return VariablesViewPart.root;
		}
		else {
			return VariablesViewPart.variables;
		}
	}

	public boolean hasChildren(Object element) {
		if (element instanceof Variable){
			return false;
		}
		
		return true;
	}
	
}
