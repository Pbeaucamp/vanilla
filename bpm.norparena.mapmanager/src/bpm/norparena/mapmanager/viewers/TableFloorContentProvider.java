package bpm.norparena.mapmanager.viewers;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.vanilla.map.core.design.IBuildingFloor;

public class TableFloorContentProvider implements IStructuredContentProvider{

	@Override
	public Object[] getElements(Object inputElement) {
		List<IBuildingFloor> floors = (List<IBuildingFloor>)inputElement;
		return floors.toArray(new IBuildingFloor[floors.size()]);
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
