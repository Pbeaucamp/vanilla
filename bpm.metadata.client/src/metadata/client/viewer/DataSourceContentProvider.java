package metadata.client.viewer;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.sql.SQLDataSource;

public class DataSourceContentProvider implements ITreeContentProvider{

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IDataStream){
			Collection<?> l = ((IDataStream)parentElement).getElements();
			return l.toArray(new Object[l.size()]);
		}
		return null;
	}

	public Object getParent(Object element) {
		if (element instanceof IDataStreamElement){
			return ((IDataStreamElement)element).getDataStream();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IDataStream){
			return !((IDataStream)element).getElements().isEmpty();
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		SQLDataSource dataSource = (SQLDataSource)inputElement;
		List<?> l = dataSource.getDataStreams();
		return l.toArray(new Object[l.size()]);
	}

	public void dispose() {
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

}
