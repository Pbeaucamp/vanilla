package bpm.fd.design.ui.properties.viewer;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.model.PropertyGroup;

public class PropertyLabelProvider extends ColumnLabelProvider{
@Override
	public String getText(Object element) {
	if (element instanceof PropertyGroup){
		return ((PropertyGroup)element).getName();
	}
	else if (element instanceof Property){
		return ((Property)element).getName();
	}
	return super.getText(element);
		
		
	}
}
