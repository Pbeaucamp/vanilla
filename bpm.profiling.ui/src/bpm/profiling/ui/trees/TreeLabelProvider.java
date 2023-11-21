package bpm.profiling.ui.trees;

import javax.swing.tree.TreeModel;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.profiling.ui.Activator;
import bpm.profiling.ui.repository.TreeDirectory;
import bpm.profiling.ui.repository.TreeItem;




public class TreeLabelProvider extends LabelProvider {
	
	private static final Image[] img = new Image[]{};
	
	@Override
	public String getText(Object obj) {
		return obj.toString();
	}
	@Override
	public Image getImage(Object obj) {
		 if (obj instanceof TreeTable){
			return Activator.getDefault().getImageRegistry().get("table"); //$NON-NLS-1$
		}
		
		else if (obj instanceof TreeColumn){
			return Activator.getDefault().getImageRegistry().get("column"); //$NON-NLS-1$
		}
		else if (obj instanceof TreeConnection){
			return Activator.getDefault().getImageRegistry().get("connection"); //$NON-NLS-1$
		}
		else if (obj instanceof TreeModel){
			return Activator.getDefault().getImageRegistry().get("model"); //$NON-NLS-1$
		}
		else if (obj instanceof TreeItem){
			return Activator.getDefault().getImageRegistry().get("fmdt");	
		}
		else if (obj instanceof TreeDirectory){
			return Activator.getDefault().getImageRegistry().get("directory");
		} 
		
		else if (obj instanceof TreeParent){
			return Activator.getDefault().getImageRegistry().get("object"); //$NON-NLS-1$
		}
		 
		return null;
	}

}
