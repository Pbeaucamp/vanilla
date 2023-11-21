package bpm.gateway.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.forms.Form;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.viewer.TreeObject;

class ModelContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

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
//		else if (parent == ModelViewHelper.variables){
//			return getVariables();
//		}
//		else if (parent == ModelViewHelper.parameters){
//			return getParameters();
//		}
		else if (parent == ModelViewHelper.forms){
			return getForms();
		}
			
		
		
		return null;
	}

	
	private Object[] getVariables(){
		IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part == null){
			return null;
		}
		
		IEditorInput in = part.getEditorInput();
		
		Assert.isTrue(in instanceof GatewayEditorInput);
		List<Variable> l = ((GatewayEditorInput)in).getDocumentGateway().getVariables();
		
		return l.toArray(new Variable[l.size()]);
	}
	
	private Object[] getTransformations(){
		IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part == null){
			return null;
		}
		
		IEditorInput in = part.getEditorInput();

		
		if (in  instanceof GatewayEditorInput){
			List l = ((GatewayEditorInput)in).getDocumentGateway().getTransformations();
			return l.toArray(new Transformation[l.size()]);
		}
		
		return null;
	}
	
	
	private Object[] getParameters(){
		IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part == null){
			return null;
		}
		
		IEditorInput in = part.getEditorInput();
		
		
		if (in  instanceof GatewayEditorInput){
			List l = ((GatewayEditorInput)in).getDocumentGateway().getParameters();
			return l.toArray(new Parameter[l.size()]);
		}
		
		return null;
	}
	
	
	
	private Object[] getForms(){
		IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part == null){
			return null;
		}
		
		IEditorInput in = part.getEditorInput();
		
		
		if (in  instanceof GatewayEditorInput){
			List l = ((GatewayEditorInput)in).getDocumentGateway().getForms();
			return l.toArray(new Form[l.size()]);
		}
		
		return null;
	}
	
	
	private Object[] getLinks(){
		IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part == null){
			return null;
		}
		
		IEditorInput in = part.getEditorInput();
		
		
		if (in  instanceof GatewayEditorInput){
			List<Transformation> l = ((GatewayEditorInput)in).getDocumentGateway().getTransformations();
			List<String> links = new ArrayList<String>();
			
			for(Transformation t : l){
				for(Transformation o : t.getOutputs()){
					links.add(t.getName() + " ----> " + o.getName()); //$NON-NLS-1$
				}
				
			}
			
			return links.toArray(new String[links.size()]);
		}
		
		return null;
	}
	
	public Object getParent(Object element) {
		if (element instanceof Transformation){
			return ModelViewHelper.transformations;
		}
		
		else if (element == ModelViewHelper.root){
			return null;
		}
		
//		else if (element instanceof Variable){
//			return ModelViewHelper.variables;
//		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof Transformation){
			return false;
		}
		if (element instanceof String){
			return false;
		}
		if (element instanceof Variable){
			return false;
		}
		if (element instanceof Parameter){
			return false;
		}
		if (element instanceof Form){
			return false;
		}
		
		return true;
	}
	
}