package bpm.birt.osm.ui.reportitem;

import org.eclipse.birt.report.designer.ui.extensions.ReportItemBuilderUI;
import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.IReportItem;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import bpm.birt.osm.core.reportitem.OsmReportItem;
import bpm.birt.osm.ui.wizard.WizardOsmMap;


public class OsmMapBuilder extends ReportItemBuilderUI{
	public int open( ExtendedItemHandle handle )
	{
		try
		{
			IReportItem item = handle.getReportItem( );
			DataSetHandle dataSet = handle.getDataSet();
			
			if ( item instanceof OsmReportItem )
			{	
				WizardOsmMap wiz = new WizardOsmMap((OsmReportItem)item, dataSet, handle);
				WizardDialog dial = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
				dial.setTitle("OSM Map");
				dial.setMinimumPageSize(600, 400);
				dial.create();
			
				return dial.open();
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		return Window.CANCEL;
	}
}
