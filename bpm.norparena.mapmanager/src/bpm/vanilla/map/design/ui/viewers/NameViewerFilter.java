package bpm.vanilla.map.design.ui.viewers;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import bpm.vanilla.map.core.design.IAddress;
import bpm.vanilla.map.core.design.IMapDefinition;

public class NameViewerFilter extends ViewerFilter{

	private String name = ""; //$NON-NLS-1$
	
	public NameViewerFilter(String name){
		this.name = name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IAddress){
			return((IAddress)element).getLabel().startsWith(name);
		}
		else if (element instanceof IMapDefinition){
			return ((IMapDefinition)element).getLabel().startsWith(name);
		}
		return true;
	}
	
}
