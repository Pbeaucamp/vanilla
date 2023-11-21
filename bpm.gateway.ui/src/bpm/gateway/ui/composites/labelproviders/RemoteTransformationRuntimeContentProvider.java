package bpm.gateway.ui.composites.labelproviders;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.gateway.ui.viewer.TreeObject;
import bpm.gateway.ui.views.GatewayStepLog;

public class RemoteTransformationRuntimeContentProvider implements ITreeContentProvider  {

	private Object input;
	
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof GatewayStepLog){
			List<String> l = ((GatewayStepLog)parentElement).getMessage();
			return l.toArray(new String[l.size()]);
		}
		else{
			return null;
		}
		
		

	}

	public Object getParent(Object element) {
		if (element instanceof TreeObject){
			return ((TreeObject)element).getParent();
		}
		else if (element instanceof String){
			return input;
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof GatewayStepLog){
			return ((GatewayStepLog)element).getMessage().size() > 0;
		}
		
		return false;
	}

	public Object[] getElements(Object inputElement) {
		if (!(inputElement instanceof List)){
			return new Object[]{};
		}
		input = inputElement;
		List<GatewayStepLog> l = (List<GatewayStepLog>)inputElement;
		return l.toArray(new GatewayStepLog[l.size()]);
	}

	public void dispose() {
		
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
