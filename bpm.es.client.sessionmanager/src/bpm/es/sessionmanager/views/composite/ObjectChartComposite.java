package bpm.es.sessionmanager.views.composite;

import java.awt.Font;
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
import org.jfree.chart.plot.PiePlot;
import org.jfree.experimental.chart.swt.ChartComposite;

import bpm.es.sessionmanager.api.IndexedObjectId;
import bpm.es.sessionmanager.api.SessionManager;
import bpm.es.sessionmanager.views.charts.ChartHelper;
import bpm.vanilla.platform.core.beans.VanillaLogs;

public class ObjectChartComposite extends AChartComposite  {

	private SessionManager manager = null;
	private int userId = -1;
	
	public ObjectChartComposite(Composite parent, int style, String title,
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
				if (inputElement instanceof IndexedObjectId) {
					List<VanillaLogs> keys = ((IndexedObjectId)inputElement).getUniqueLogs();
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
						int objectId = ((VanillaLogs)ss.getFirstElement()).getDirectoryItemId();
						
						try {
							showChart(objectId);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
	}
	
	protected void showChart(int objectId) throws Exception {
		compositeChart.dispose();
		
		compositeChart = new ChartComposite(generalComposite, SWT.NONE);
		compositeChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		compositeChart.setChart(createActionByObjectChart(manager, userId, objectId));

		generalComposite.layout();		
	}
	
	@Override
	public void setInput(SessionManager manager, int userId) {
		this.manager = manager;
		this.userId = userId;
		
		try {
			comboObject.setInput(manager.getVanillaLogsByItems());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			showChart(-1);
		} catch (Exception e) {
			e.printStackTrace();
		} //show with no filter as a start
	}
	
	private JFreeChart createActionByObjectChart(SessionManager manager, int userId, 
			int objectId) throws Exception{
		
		JFreeChart chart = ChartHelper.createActionsPieSetChart(manager, userId, objectId);
        
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setSectionOutlinesVisible(false);
		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12)); //$NON-NLS-1$
		plot.setNoDataMessage("No data available"); //$NON-NLS-1$
		plot.setCircular(false);
		plot.setLabelGap(0.02);
		
        return chart;
	}
	
	private class ObjectComboLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			if (element instanceof VanillaLogs) {
				return ((VanillaLogs)element).getObjectName();
			}
			else {
				return "unknown class"; //$NON-NLS-1$
			}
		}
	}
}
