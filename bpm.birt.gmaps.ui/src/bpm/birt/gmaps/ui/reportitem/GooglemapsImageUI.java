package bpm.birt.gmaps.ui.reportitem;

import org.eclipse.birt.report.designer.ui.extensions.IReportItemImageProvider;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.ExtendedElementException;
import org.eclipse.birt.report.model.api.extension.IReportItem;
import org.eclipse.swt.graphics.Image;

import bpm.birt.gmaps.core.reportitem.GooglemapsItem;
import bpm.birt.gmaps.ui.Activator;
import bpm.birt.gmaps.ui.icons.Icons;

public class GooglemapsImageUI implements IReportItemImageProvider{

	@Override
	public Image getImage(ExtendedItemHandle handle) {
		try {
			IReportItem item = handle.getReportItem();

			if (item instanceof GooglemapsItem) {
				((GooglemapsItem)item).getMapID();
			}
		} catch (ExtendedElementException e) {
			e.printStackTrace( );
		}
		return Activator.getDefault().getImageRegistry().get(Icons.VANILLA_GMAP);
	}
	
	@Override
	public void disposeImage(ExtendedItemHandle handle, Image image) {
//		if ( image != null && !image.isDisposed( ) )
//		{
//			image.dispose( );
//		}		
	}

}
