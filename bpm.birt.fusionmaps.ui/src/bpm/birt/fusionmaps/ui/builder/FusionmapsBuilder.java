package bpm.birt.fusionmaps.ui.builder;

import org.eclipse.birt.report.designer.ui.extensions.ReportItemBuilderUI;
import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.IReportItem;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import bpm.birt.fusionmaps.core.reportitem.FusionmapsItem;
import bpm.birt.fusionmaps.ui.wizardeditor.WizardEditor;

public class FusionmapsBuilder extends ReportItemBuilderUI{
	
	public int open( ExtendedItemHandle handle )
	{
		try
		{
			IReportItem item = handle.getReportItem( );
			DataSetHandle dataSet = handle.getDataSet();
			
			if ( item instanceof FusionmapsItem )
			{	
				WizardEditor wiz = new WizardEditor((FusionmapsItem)item, dataSet, handle);
				WizardDialog dial = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
				dial.setTitle("Vanilla Map");
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
