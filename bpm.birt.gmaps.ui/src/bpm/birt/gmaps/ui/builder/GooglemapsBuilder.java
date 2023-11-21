package bpm.birt.gmaps.ui.builder;

import org.eclipse.birt.report.designer.ui.extensions.ReportItemBuilderUI;
import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.IReportItem;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import bpm.birt.gmaps.core.reportitem.GooglemapsItem;
import bpm.birt.gmaps.ui.wizardeditor.WizardEditor;

public class GooglemapsBuilder extends ReportItemBuilderUI{
	
	public int open( ExtendedItemHandle handle )
	{
		try
		{
			IReportItem item = handle.getReportItem( );
			DataSetHandle dataSet = handle.getDataSet();
			
			if ( item instanceof GooglemapsItem )
			{	
				WizardEditor wiz = new WizardEditor((GooglemapsItem)item, dataSet, handle);
				WizardDialog dial = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
				dial.setTitle("Vanilla GMap");
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
