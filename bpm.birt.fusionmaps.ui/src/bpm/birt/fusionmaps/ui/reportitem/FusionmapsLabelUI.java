package bpm.birt.fusionmaps.ui.reportitem;

import org.eclipse.birt.report.designer.ui.extensions.IReportItemLabelProvider;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.ExtendedElementException;
import org.eclipse.birt.report.model.api.extension.IReportItem;

import bpm.birt.fusionmaps.core.reportitem.FusionmapsItem;

/**
 * Author : Charles Martin
 * Company : BPM-Conseil
 */
public class FusionmapsLabelUI implements IReportItemLabelProvider {

	public String getLabel( ExtendedItemHandle handle ) {
		try {
			IReportItem item = handle.getReportItem( );

			if ( item instanceof FusionmapsItem ) {
				return ( (FusionmapsItem) item ).getMapID();
			}
		} catch ( ExtendedElementException e ) {
			e.printStackTrace( );
		}
		return null;
	}

}
