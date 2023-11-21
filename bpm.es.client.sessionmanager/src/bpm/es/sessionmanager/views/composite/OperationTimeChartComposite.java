package bpm.es.sessionmanager.views.composite;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.experimental.chart.swt.ChartComposite;

import bpm.es.sessionmanager.api.IndexedApplication;
import bpm.es.sessionmanager.api.SessionManager;
import bpm.es.sessionmanager.views.charts.ChartHelper;
import bpm.vanilla.platform.core.beans.VanillaLogs;

public class OperationTimeChartComposite extends AChartComposite  {

	private SessionManager manager = null;
	private int userId = -1;
	
	public OperationTimeChartComposite(Composite parent, int style, String title,
			String selectionLabel, FormToolkit toolkit) {
		super(parent, style, title, selectionLabel, toolkit);
	}

	@Override
	protected void initProviders() {
		comboObject.setContentProvider(new IStructuredContentProvider() {
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			
			public void dispose() {
			}
			
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof IndexedApplication) {
					List<VanillaLogs> keys = ((IndexedApplication)inputElement).getUniqueApplicationLogs();
					return keys.toArray();
				}
				else 
					return new Object[0];
			}
		});
		comboObject.setLabelProvider(new ObjectComboLabelProvider());
		comboObject.getCombo().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection ss = (StructuredSelection) comboObject.getSelection();
				
				if (!ss.isEmpty()) {
					if (ss.getFirstElement() instanceof VanillaLogs) {
						String applicationId = ((VanillaLogs)ss.getFirstElement()).getApplication();
						
						showChart(applicationId);
					}
				}
			}
		});
	}
	
	protected void showChart(String applicationId) {
		compositeChart.dispose();
		
		compositeChart = new ChartComposite(generalComposite, SWT.NONE);
		compositeChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		compositeChart.setChart(createChart(manager, userId, applicationId));

		generalComposite.layout();		
	}
	
	@Override
	public void setInput(SessionManager manager, int userId) {
		this.manager = manager;
		this.userId = userId;
		
		comboObject.setInput(manager.getVanillaLogsByApplication());
		
		showChart(""); //show with no filter as a start //$NON-NLS-1$
	}
	
	private JFreeChart createChart(SessionManager manager, int userId, 
			String applicationId){
		
		JFreeChart chart = ChartHelper.createApplicationBarChart(manager, userId, applicationId);
        //Plo
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setNoDataMessage("No data available"); //$NON-NLS-1$
		//plot.set
		
        return chart;
	}
	
	private class ObjectComboLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			if (element instanceof VanillaLogs) {
				return ((VanillaLogs)element).getObjectType();
			}
//			else if (element instanceof String) {
//				return (String) element;
//			}
			else {
				return "unknown class"; //$NON-NLS-1$
			}
		}
	}
}
