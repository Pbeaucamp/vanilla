package bpm.norparena.mapmanager.viewers;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.vanilla.map.core.design.ICell;

public class TableCellContentProvider implements IStructuredContentProvider{

	@Override
	public Object[] getElements(Object inputElement) {
		List<ICell> cells = (List<ICell>)inputElement;
		return cells.toArray(new ICell[cells.size()]);
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
