package metadata.client.viewer;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TableContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {
		List l = (List)inputElement;
		
		return l.toArray(new List[l.size()]) ;
	}

	public void dispose() {}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
		

}
