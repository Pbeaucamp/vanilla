package bpm.birt.wms.map.core.reportitem;

import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.IMessages;
import org.eclipse.birt.report.model.api.extension.IReportItem;
import org.eclipse.birt.report.model.api.extension.ReportItemFactory;

public class WMSMapItemFactory extends ReportItemFactory {

	@Override
	public IMessages getMessages() {
		return null;
	}

	@Override
	public IReportItem newReportItem(DesignElementHandle extendedItemHandle) {
		if(extendedItemHandle instanceof ExtendedItemHandle && WMSMapItem.EXTENSION_NAME.equals(((ExtendedItemHandle) extendedItemHandle).getExtensionName())) {
			return new WMSMapItem((ExtendedItemHandle) extendedItemHandle);
		}
		return null;
	}

}
