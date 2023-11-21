package bpm.fd.design.ui.properties.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.model.PropertyGroup;

public class PropertiesContentProvider implements ITreeContentProvider{

	@Override
	public Object[] getChildren(Object parentElement) {
		
		List l = new ArrayList();
		
		if (parentElement instanceof PropertyGroup){
			l.addAll(((PropertyGroup)parentElement).getProperties());
		}
		return l.toArray(new Object[l.size()]);
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof PropertyGroup){
			return ((PropertyGroup)element).getParent();
		}
		else if (element instanceof Property){
			return ((Property)element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof PropertyGroup){
			return ((PropertyGroup)element).getProperties().size() > 0;
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List l = (List)inputElement;
		return l.toArray(new Object[l.size()]);
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
