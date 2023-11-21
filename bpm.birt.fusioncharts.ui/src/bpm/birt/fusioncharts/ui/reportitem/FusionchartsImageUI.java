package bpm.birt.fusioncharts.ui.reportitem;

import org.eclipse.birt.report.designer.ui.extensions.IReportItemImageProvider;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.ExtendedElementException;
import org.eclipse.birt.report.model.api.extension.IReportItem;
import org.eclipse.swt.graphics.Image;

import bpm.birt.fusioncharts.core.reportitem.FusionchartsItem;
import bpm.birt.fusioncharts.ui.Activator;
import bpm.birt.fusioncharts.ui.icons.Icons;

public class FusionchartsImageUI implements IReportItemImageProvider{

	@Override
	public Image getImage(ExtendedItemHandle handle) {
		try {
			IReportItem item = handle.getReportItem();

			if (item instanceof FusionchartsItem) {
				((FusionchartsItem)item).getXml();
			}
		} catch (ExtendedElementException e) {
			e.printStackTrace( );
		}
		return Activator.getDefault().getImageRegistry().get(Icons.VANILLA_CHART);
	}
	
	@Override
	public void disposeImage(ExtendedItemHandle handle, Image image) {
//		if ( image != null && !image.isDisposed( ) )
//		{
//			image.dispose( );
//		}		
	}

}
