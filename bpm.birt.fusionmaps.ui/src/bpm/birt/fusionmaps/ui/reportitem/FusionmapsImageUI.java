package bpm.birt.fusionmaps.ui.reportitem;

import org.eclipse.birt.report.designer.ui.extensions.IReportItemImageProvider;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.ExtendedElementException;
import org.eclipse.birt.report.model.api.extension.IReportItem;
import org.eclipse.swt.graphics.Image;

import bpm.birt.fusionmaps.core.reportitem.FusionmapsItem;
import bpm.birt.fusionmaps.ui.Activator;
import bpm.birt.fusionmaps.ui.icons.Icons;

public class FusionmapsImageUI implements IReportItemImageProvider{

	@Override
	public Image getImage(ExtendedItemHandle handle) {
		try {
			IReportItem item = handle.getReportItem();

			if (item instanceof FusionmapsItem) {
				((FusionmapsItem)item).getMapDataXML();
			}
		} catch (ExtendedElementException e) {
			e.printStackTrace( );
		}
		return Activator.getDefault().getImageRegistry().get(Icons.VANILLA_MAP);
	}
	
	@Override
	public void disposeImage(ExtendedItemHandle handle, Image image) {
//		if ( image != null && !image.isDisposed( ) )
//		{
//			image.dispose( );
//		}		
	}

}
