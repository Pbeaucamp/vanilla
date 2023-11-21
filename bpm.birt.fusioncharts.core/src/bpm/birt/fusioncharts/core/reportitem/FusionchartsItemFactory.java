package bpm.birt.fusioncharts.core.reportitem;

import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.IMessages;
import org.eclipse.birt.report.model.api.extension.IReportItem;
import org.eclipse.birt.report.model.api.extension.ReportItemFactory;

/**
 * Author : Sebastien Vigroux
 * Company : BPM-Conseil
 */
public class FusionchartsItemFactory extends ReportItemFactory {

	public IReportItem newReportItem( DesignElementHandle modelHanlde ) {
		if ( modelHanlde instanceof ExtendedItemHandle
				&& FusionchartsItem.EXTENSION_NAME.equals( ( (ExtendedItemHandle) modelHanlde ).getExtensionName( ) ) )
		{
			return new FusionchartsItem( (ExtendedItemHandle) modelHanlde );
		}
		return null;
	}

	public IMessages getMessages( ) {
		return null;
	}

}
