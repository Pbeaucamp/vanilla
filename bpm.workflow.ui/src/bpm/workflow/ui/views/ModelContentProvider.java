package bpm.workflow.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.editors.WorkflowEditorInput;
import bpm.workflow.ui.viewer.TreeObject;


/**
 * Provider which draws the model definition (activities and links)
 * @author CAMUS, MARTIN
 *
 */
public class ModelContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	public void dispose() {}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	public Object[] getChildren(Object parent) {
		if (parent == ModelViewHelper.root){
			return ModelViewHelper.root.getChildren().toArray(new TreeObject[ModelViewHelper.root.getChildren().size()]);
			
		}
		else if (parent == ModelViewHelper.transformations){
			return getTransformations();
		}
		else if (parent == ModelViewHelper.links){
			return getLinks();
		}

					
		return null;
	}

	
	private Object[] getTransformations(){
		IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part == null){
			return null;
		}
		
		IEditorInput in = part.getEditorInput();

		
		if (in  instanceof WorkflowEditorInput){
			return ((WorkflowEditorInput)in).getWorkflowModel().getActivities().values().toArray();

		}
		
		return null;
	}
	
	

	
	
	private Object[] getLinks(){
		IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part == null){
			return null;
		}
		
		IEditorInput in = part.getEditorInput();
		
		
		if (in  instanceof WorkflowEditorInput){
			Collection<IActivity> l = ((WorkflowEditorInput)in).getWorkflowModel().getActivities().values();
			List<String> links = new ArrayList<String>();
			
			for(IActivity t : l){
				
				for(IActivity o : t.getTargets()){
					links.add(t.getName() + " ----> " + o.getName()); //$NON-NLS-1$
				}
				
			}
			
			return links.toArray(new String[links.size()]);
		}
		
		return null;
	}
	
	public Object getParent(Object element) {
		if (element instanceof IActivity){
			return ModelViewHelper.transformations;
		}
		
		else if (element == ModelViewHelper.root){
			return null;
		}
		
		else if (element instanceof Variable){
			return ModelViewHelper.variables;
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IActivity){
			return false;
		}
		if (element instanceof String){
			return false;
		}
		if (element instanceof Variable){
			return false;
		}

		
		return true;
	}
	
}