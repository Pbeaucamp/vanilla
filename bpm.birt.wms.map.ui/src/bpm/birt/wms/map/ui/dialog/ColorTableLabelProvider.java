package bpm.birt.wms.map.ui.dialog;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.birt.wms.map.core.reportitem.ColorRange;

public class ColorTableLabelProvider extends LabelProvider{
	@Override
	public Image getImage(Object obj) {
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
