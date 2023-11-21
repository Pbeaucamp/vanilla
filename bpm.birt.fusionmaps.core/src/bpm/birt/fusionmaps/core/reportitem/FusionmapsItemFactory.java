package bpm.birt.fusionmaps.core.reportitem;

import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.IMessages;
import org.eclipse.birt.report.model.api.extension.IReportItem;
import org.eclipse.birt.report.model.api.extension.ReportItemFactory;

/**
 * Author : Charles Martin
 * Company : BPM-Conseil
 */
public class FusionmapsItemFactory extends ReportItemFactory {

	public IReportItem newReportItem( DesignElementHandle modelHanlde ) {
		if ( modelHanlde instanceof ExtendedItemHandle
				&& FusionmapsItem.EXTENSION_NAME.equals( ( (ExtendedItemHandle) modelHanlde ).getExtensionName( ) ) )
		{
			return new FusionmapsItem( (ExtendedItemHandle) modelHanlde );
		}
		return null;
	}

	public IMessages getMessages( ) {
		return null;
	}

}
