package bpm.norparena.mapmanager.viewers;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.vanilla.map.core.design.IMapDefinition;

public class TableMapDefinitionContentProvider implements IStructuredContentProvider{

	@Override
	public Object[] getElements(Object inputElement) {
		List<IMapDefinition> mapDefinitions = (List<IMapDefinition>)inputElement;
		return mapDefinitions.toArray(new IMapDefinition[mapDefinitions.size()]);
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
