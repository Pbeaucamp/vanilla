package bpm.gateway.ui.composites.labelproviders;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class StepRuntimeContentProvider implements ITreeContentProvider  {

	public Object[] getChildren(Object parentElement) {
//		if (parentElement instanceof TransformationRuntime){
//			List<TransformationLog>  l =  ((TransformationRuntime)parentElement).getLogs();
//			
//			return l.toArray(new TransformationLog[l.size()]);
//		}
//		else if (parentElement instanceof TreeLog){
//			List l = ((TreeLog)parentElement).getChilds();
//			return l.toArray(new Object[l.size()]);
//		}
//		else{
			return null;
//		}
		
		

	}

	public Object getParent(Object element) {
//		if (element instanceof TransformationLog){
//			return ((TransformationLog)element).parent;
//		}
		return null;
	}

	public boolean hasChildren(Object element) {
//		if (element instanceof TreeLog){
//			return ((TreeLog)element).getChilds().size() > 0;
//		}
//		else if (element instanceof TransformationRuntime){
//			return ((TransformationRuntime)element).getLogs().size() > 0;
//		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		
		if (inputElement == null){
			return new Object[]{};
		}
		List l = ((List)inputElement);
		return l.toArray(new Object[l.size()]);
//		return getChildren(inputElement);
	}

	public void dispose() {
		
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
