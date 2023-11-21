package bpm.es.sessionmanager.views.composite;

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
import org.jfree.chart.plot.Plot;
import org.jfree.experimental.chart.swt.ChartComposite;

import bpm.es.sessionmanager.Messages;
import bpm.es.sessionmanager.api.SessionManager;
import bpm.es.sessionmanager.views.charts.ChartHelper;
import bpm.vanilla.platform.core.beans.VanillaLogs;

public class ActionsByDateChartComposite extends AChartComposite  {

	public static String timeToday = Messages.ActionsByDateChartComposite_0;
	public static String timeLastWeek = Messages.ActionsByDateChartComposite_1;
	public static String timeLastMonth = Messages.ActionsByDateChartComposite_2;
	public static String timeAll = Messages.ActionsByDateChartComposite_3;
	
	public String[] dateFilters = {timeAll, timeToday, timeLastWeek, timeLastMonth};
	
	public String dateSelection = timeAll;
	
	private SessionManager manager = null;
	private int userId = -1;
	
	public ActionsByDateChartComposite(Composite parent, int style, String title,
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
				if (inputElement instanceof String[]) {
					return (String[])inputElement;
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
					if (ss.getFirstElement() instanceof String) {
						try {
							showChart((String)ss.getFirstElement());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
	}
	
	protected void showChart(String date) throws Exception {
		compositeChart.dispose();
		
		compositeChart = new ChartComposite(generalComposite, SWT.NONE);
		compositeChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		compositeChart.setChart(createChart(manager, userId, date));

		generalComposite.layout();
	}
	
	@Override
	public void setInput(SessionManager manager, int userId) {
		this.manager = manager;
		this.userId = userId;
		
		comboObject.setInput(dateFilters);
		
		try {
			showChart("");
		} catch (Exception e) {
			e.printStackTrace();
		} //show with no filter as a start //$NON-NLS-1$
	}
	
	private JFreeChart createChart(SessionManager manager, int userId, 
			String date) throws Exception{
		
		JFreeChart chart = ChartHelper.createActionTimeChart(manager, userId, date);
        //Plo
		Plot plot = chart.getPlot();
		plot.setNoDataMessage("No data available"); //$NON-NLS-1$
		//plot.set
		
        return chart;
	}
	
	private class ObjectComboLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			if (element instanceof VanillaLogs) {
				String app = ((VanillaLogs)element).getApplication();
				app = app.substring(app.lastIndexOf(".") + 1, app.length());
				return app;
			}
			else if (element instanceof String) {
				return (String) element;
			}
			else {
				return "unknown class"; //$NON-NLS-1$
			}
		}
	}
}
