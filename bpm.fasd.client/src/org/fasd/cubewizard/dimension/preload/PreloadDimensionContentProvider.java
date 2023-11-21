package org.fasd.cubewizard.dimension.preload;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPSchema;

public class PreloadDimensionContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		List l = new ArrayList();
		if (parentElement instanceof OLAPDimension) {
			l.addAll(((OLAPDimension) parentElement).getHierarchies());
		}
		return l.toArray(new Object[l.size()]);
	}

	public Object getParent(Object element) {

		return ((OLAPHierarchy) element).getParent();
	}

	public boolean hasChildren(Object element) {
		if (element instanceof OLAPDimension) {
			return !((OLAPDimension) element).getHierarchies().isEmpty();
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		List l = new ArrayList();
		if (inputElement instanceof OLAPSchema) {
			l = ((OLAPSchema) inputElement).getDimensions();
		} else if (inputElement instanceof OLAPHierarchy) {
			l = ((OLAPHierarchy) inputElement).getLevels();
		}

		return l.toArray(new Object[l.size()]);
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
