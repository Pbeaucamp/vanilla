package bpm.oda.driver.reader.viewer;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ListContentProvider<E> implements IStructuredContentProvider{

	public Object[] getElements(Object inputElement) {
		Collection<E> l = (Collection<E>)inputElement;
		return l.toArray(new Object[l.size()]);
	}

	public void dispose() {
		
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
