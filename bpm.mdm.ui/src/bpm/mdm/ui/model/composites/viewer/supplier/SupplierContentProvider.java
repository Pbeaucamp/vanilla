package bpm.mdm.ui.model.composites.viewer.supplier;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class SupplierContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		return((List)inputElement).toArray();
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
