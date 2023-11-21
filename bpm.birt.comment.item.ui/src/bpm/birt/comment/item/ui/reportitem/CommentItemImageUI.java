package bpm.birt.comment.item.ui.reportitem;

import org.eclipse.birt.report.designer.ui.extensions.IReportItemImageProvider;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.swt.graphics.Image;

import bpm.birt.comment.item.ui.Activator;
import bpm.birt.comment.item.ui.icons.Icons;

public class CommentItemImageUI implements IReportItemImageProvider {

	@Override
	public void disposeImage(ExtendedItemHandle handle, Image arg1) {
		
	}

	@Override
	public Image getImage(ExtendedItemHandle arg0) {
		return Activator.getDefault().getImageRegistry().get(Icons.COMMENT_BIG);
	}

}
