package bpm.birt.gmaps.core.reportitem;

import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.IMessages;
import org.eclipse.birt.report.model.api.extension.IReportItem;
import org.eclipse.birt.report.model.api.extension.ReportItemFactory;


public class GooglemapsItemFactory extends ReportItemFactory {
	
	public IReportItem newReportItem( DesignElementHandle modelHanlde ) {
		if ( modelHanlde instanceof ExtendedItemHandle
				&& GooglemapsItem.EXTENSION_NAME.equals( ( (ExtendedItemHandle) modelHanlde ).getExtensionName( ) ) )
		{
			return new GooglemapsItem( (ExtendedItemHandle) modelHanlde );
		}
		return null;
	}

	public IMessages getMessages( ) {
		return null;
	}
}
