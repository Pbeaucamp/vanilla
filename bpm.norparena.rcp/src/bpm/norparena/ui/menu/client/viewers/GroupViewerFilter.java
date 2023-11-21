package bpm.norparena.ui.menu.client.viewers;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import bpm.norparena.ui.menu.client.trees.TreeGroup;
import bpm.norparena.ui.menu.client.trees.TreeUser;
import bpm.vanilla.platform.core.beans.Group;

public class GroupViewerFilter extends ViewerFilter {

	private String filterValue;
	private boolean applyOnStart = false;
	
	
	
	
	public GroupViewerFilter(String value) {
		super();
		
		
		setValue(value);
		
	}



	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof Group){
			return isValid(((Group)element).getName());
		}
		if (element instanceof TreeGroup){
			return isValid(((TreeGroup)element).getGroup().getName());
		}
		else if (element instanceof TreeUser){
			return isValid(((TreeUser)element).getUser().getName());
		}
		return false;
	}

	private boolean isValid(String value){
		
		if (applyOnStart){
			return value.toLowerCase().contains(filterValue.toLowerCase());
		}
		
		return value.toLowerCase().startsWith(filterValue.toLowerCase());
		
		
	}



	public void setValue(String text) {
		filterValue = text;
		if (text.trim().startsWith("%")){ //$NON-NLS-1$
			filterValue = filterValue.trim().substring(1);
			applyOnStart = true;
		}
	}
}
