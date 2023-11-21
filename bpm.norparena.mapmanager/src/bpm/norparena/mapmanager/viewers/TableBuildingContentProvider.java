package bpm.norparena.mapmanager.viewers;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.vanilla.map.core.design.IBuilding;

public class TableBuildingContentProvider implements IStructuredContentProvider{

	@Override
	public Object[] getElements(Object inputElement) {
		List<IBuilding> buildings = (List<IBuilding>)inputElement;
		return buildings.toArray(new IBuilding[buildings.size()]);
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
