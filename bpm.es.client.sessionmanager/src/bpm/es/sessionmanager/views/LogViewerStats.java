package bpm.es.sessionmanager.views;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.es.sessionmanager.Messages;
import bpm.es.sessionmanager.api.SessionManager;
import bpm.es.sessionmanager.views.composite.AChartComposite;
import bpm.es.sessionmanager.views.composite.ActionsByDateChartComposite;
import bpm.es.sessionmanager.views.composite.ObjectChartComposite;
import bpm.es.sessionmanager.views.composite.OperationTimeChartComposite;

public class LogViewerStats {
	
	private Composite general;
	
	private FormToolkit toolkit;
	
	private AChartComposite objectByCountChart;
	private AChartComposite operationTimeChart;
	private AChartComposite actionByDayChart;
	
	public LogViewerStats(FormToolkit toolkit, Composite parent, LogViewer daddy) {
		this.toolkit = toolkit;
		
		createComposite(parent);
	}
	
	public Composite getComposite() {
		return general;
	}

	private void createComposite(Composite parent) {
		general = toolkit.createComposite(parent, SWT.NO_SCROLL);
		general.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		//general.setBackground(ColorConstants.blue);
		general.setLayout(new GridLayout(2, true));
		
		objectByCountChart = new ObjectChartComposite(general, SWT.NONE, 
				Messages.LogViewerStats_0, Messages.LogViewerStats_1, toolkit);
		objectByCountChart.setLayout(new GridLayout());
		objectByCountChart.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		operationTimeChart = new OperationTimeChartComposite(general, SWT.NONE, 
				Messages.LogViewerStats_2, Messages.LogViewerStats_3, toolkit);
		operationTimeChart.setLayout(new GridLayout());
		operationTimeChart.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		actionByDayChart = new ActionsByDateChartComposite(general, SWT.NONE, 
				Messages.LogViewerStats_4, Messages.LogViewerStats_5, toolkit);
		actionByDayChart.setLayout(new GridLayout());
		actionByDayChart.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
//		objectByCountChart2 = new ObjectChartComposite(general, SWT.NONE, 
//				Messages.LogViewerStats_6, Messages.LogViewerStats_7, toolkit);
//		objectByCountChart2.setLayout(new GridLayout());
//		objectByCountChart2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
	}
	
	public void showData(SessionManager manager, int userId) {

		try {
			objectByCountChart.setInput(manager, userId);
			operationTimeChart.setInput(manager, userId);
			actionByDayChart.setInput(manager, userId);
//			objectByCountChart2.setInput(manager, userId);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(general.getShell(), "Error", "Error opening visual " + //$NON-NLS-1$ //$NON-NLS-2$
					"viewer :" + e.getMessage()); //$NON-NLS-1$
		}
	}
}

