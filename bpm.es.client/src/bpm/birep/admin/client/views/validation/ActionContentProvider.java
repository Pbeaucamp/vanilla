package bpm.birep.admin.client.views.validation;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ActionContentProvider implements IStructuredContentProvider{

	@Override
	public Object[] getElements(Object inputElement) {
		return (String[])inputElement;
	}

	@Override
	public void dispose() { }

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

}
