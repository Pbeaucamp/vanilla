package bpm.vanilla.oda.commons.trees;


import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.vanilla.oda.commons.Activator;
import bpm.vanilla.oda.commons.ui.icons.IconsNames;




public class TreeLabelProvider extends LabelProvider {
	
	private static final Image[] img = new Image[]{};
	
	@Override
	public String getText(Object obj) {
		return obj.toString();
	}
	@Override
	public Image getImage(Object obj) {
		if (obj instanceof TreeDirectory){
			return Activator.getDefault().getImageRegistry().get(IconsNames.DIRECTORY); //$NON-NLS-1$
		}
		if (obj instanceof TreeItem){
			return Activator.getDefault().getImageRegistry().get(IconsNames.DIRECTORY_ITEM); //$NON-NLS-1$
		}
		
		return null;
	}

}
