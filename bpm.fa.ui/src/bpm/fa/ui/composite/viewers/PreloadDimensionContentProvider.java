package bpm.fa.ui.composite.viewers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Schema;

public class PreloadDimensionContentProvider implements ITreeContentProvider{

	@Override
	public Object[] getChildren(Object parentElement) {
		List l = new ArrayList();
		if (parentElement instanceof Dimension){
			l.addAll(((Dimension)parentElement).getHierarchies());
		}
		return l.toArray(new Object[l.size()]);
	}

	@Override
	public Object getParent(Object element) {
		
		return ((Hierarchy)element).getParentDimension();
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Dimension){
			return !((Dimension)element).getHierarchies().isEmpty();
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List l = new ArrayList();
		if (inputElement instanceof Schema){
			l = ((Schema)inputElement).getDimensions();
		}
		else if (inputElement instanceof Hierarchy){
			l = ((Hierarchy)inputElement).getLevels();
		}
		
		return l.toArray(new Object[l.size()]);
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
