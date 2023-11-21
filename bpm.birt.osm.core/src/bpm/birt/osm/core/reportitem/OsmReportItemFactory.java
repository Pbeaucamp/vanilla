package bpm.birt.osm.core.reportitem;

import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.IMessages;
import org.eclipse.birt.report.model.api.extension.IReportItem;
import org.eclipse.birt.report.model.api.extension.ReportItemFactory;

public class OsmReportItemFactory extends ReportItemFactory {
	
	public IReportItem newReportItem( DesignElementHandle modelHanlde ) {
		if ( modelHanlde instanceof ExtendedItemHandle
				&& OsmReportItem.EXTENSION_NAME.equals( ( (ExtendedItemHandle) modelHanlde ).getExtensionName( ) ) )
		{
			return new OsmReportItem( (ExtendedItemHandle) modelHanlde );
		}
		return null;
	}

	public IMessages getMessages( ) {
		return null;
	}

}
