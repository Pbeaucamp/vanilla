package bpm.birt.gmaps.ui.viewers;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.birt.gmaps.ui.Activator;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class ColorTableLabelProvider extends LabelProvider{
	@Override
	public Image getImage(Object obj) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		return null;
				
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if(element instanceof ColorRange)
			return ((ColorRange)element).toString();
		return super.getText(element);
	}
}
