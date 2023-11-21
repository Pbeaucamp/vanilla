package bpm.mdm.ui.model.composites.viewer;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.mdm.model.Entity;
import bpm.mdm.model.Model;

public class MdmContentProvider implements ITreeContentProvider{

	@Override
	public Object[] getChildren(Object parentElement) {
		
		if (parentElement instanceof Model){
			return((Model)parentElement).getEntities().toArray();
		}
		if (parentElement instanceof Entity){
			return((Entity)parentElement).getAttributes().toArray();
		}

		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof EObject){
			return ((EObject)element).eContainer();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		
		return true;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<?> l = ((Model)inputElement).getEntities();
		return l.toArray(new Object[l.size()]);
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

	
}
