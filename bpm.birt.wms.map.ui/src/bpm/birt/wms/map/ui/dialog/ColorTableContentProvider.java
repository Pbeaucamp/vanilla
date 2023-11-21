package bpm.birt.wms.map.ui.dialog;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.birt.wms.map.core.reportitem.ColorRange;

public class ColorTableContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		List<ColorRange> colorRanges = (List<ColorRange>)inputElement;
		return colorRanges.toArray(new ColorRange[colorRanges.size()]);
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
