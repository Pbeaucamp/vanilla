package bpm.birt.wms.map.ui.reportitem;

import org.eclipse.birt.report.designer.ui.extensions.ReportItemBuilderUI;
import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.IReportItem;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import bpm.birt.wms.map.core.reportitem.WMSMapItem;
import bpm.birt.wms.map.ui.wizard.WMSMapWizard;

public class WMSMapBuilder extends ReportItemBuilderUI {

	@Override
	public int open(ExtendedItemHandle handle) {
		try
		{
			IReportItem item = handle.getReportItem( );
			DataSetHandle dataSet = handle.getDataSet();
			
			WMSMapWizard wiz = new WMSMapWizard((WMSMapItem)item, dataSet, handle);
			WizardDialog dial = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
			dial.setTitle("Vanilla GMap");
			dial.setMinimumPageSize(600, 400);
			dial.create();
		
			return dial.open();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return Window.CANCEL;
	}
	
}
