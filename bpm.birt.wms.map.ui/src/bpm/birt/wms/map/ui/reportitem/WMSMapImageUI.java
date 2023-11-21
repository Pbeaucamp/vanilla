package bpm.birt.wms.map.ui.reportitem;

import org.eclipse.birt.report.designer.ui.extensions.IReportItemImageProvider;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.swt.graphics.Image;

import bpm.birt.wms.map.ui.Activator;
import bpm.birt.wms.map.ui.Icons;

public class WMSMapImageUI implements IReportItemImageProvider {

	@Override
	public void disposeImage(ExtendedItemHandle handle, Image image) {
		//nop
	}

	@Override
	public Image getImage(ExtendedItemHandle handle) {
		return Activator.getDefault().getImageRegistry().get(Icons.ICON_WMS_BIG);
	}

}
