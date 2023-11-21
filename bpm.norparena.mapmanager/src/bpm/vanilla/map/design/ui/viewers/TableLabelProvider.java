package bpm.vanilla.map.design.ui.viewers;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.vanilla.map.core.design.IAddress;

public class TableLabelProvider extends LabelProvider{

	@Override
	public Image getImage(Object obj) {
		return null;		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if(element instanceof IAddress)
			return ((IAddress)element).getLabel();
		return super.getText(element);
	}
}
