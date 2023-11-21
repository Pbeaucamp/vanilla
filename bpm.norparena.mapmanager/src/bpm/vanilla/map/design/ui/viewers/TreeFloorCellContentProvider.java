package bpm.vanilla.map.design.ui.viewers;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.vanilla.map.core.design.ICell;
import bpm.vanilla.map.core.design.IBuildingFloor;

public class TreeFloorCellContentProvider implements ITreeContentProvider{

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IBuildingFloor){
			if(((IBuildingFloor)parentElement).getCells() != null){
				List<ICell> cells = ((IBuildingFloor)parentElement).getCells();
				return cells.toArray(new ICell[cells.size()]);
			}
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof IBuildingFloor){
			if(((IBuildingFloor)element).getCells() != null){
				return true;
			}
		}
		return false;
	}

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
