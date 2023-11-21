package bpm.fd.design.ui.structure.gef.editparts.palette;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.graphics.Image;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.icons.Icons;

public class PaletteStackableCellPart extends AbstractTreeEditPart{
	@Override
	protected String getText() {
		return Messages.PaletteStackableCellPart_0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getImage()
	 */
	@Override
	protected Image getImage() {
		return Activator.getDefault().getImageRegistry().get(Icons.table);
	}

	@Override
	protected void addChild(EditPart child, int index) {
		
		super.addChild(child, index);
	}
	
	
}
