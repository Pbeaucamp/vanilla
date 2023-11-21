package bpm.birt.fusioncharts.ui.builder;

import org.eclipse.birt.report.designer.ui.extensions.ReportItemBuilderUI;
import org.eclipse.birt.report.model.api.DataSetHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.IReportItem;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import bpm.birt.fusioncharts.core.reportitem.FusionchartsItem;
import bpm.birt.fusioncharts.ui.wizardeditor.WizardEditor;

public class FusionmapsBuilder extends ReportItemBuilderUI{
	
	public int open( ExtendedItemHandle handle )
	{
		try
		{
			IReportItem item = handle.getReportItem( );
			DataSetHandle dataSet = handle.getDataSet();
			
			if ( item instanceof FusionchartsItem )
			{	
				WizardEditor wiz = new WizardEditor((FusionchartsItem)item, dataSet, handle);
				WizardDialog dial = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
				dial.setTitle("Vanilla Chart");
				dial.setMinimumPageSize(700, 600);
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
