package bpm.gateway.ui.views.property.sections.googleanalytics;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.googleanalytics.IMetricsDimensions;
import bpm.gateway.core.transformations.googleanalytics.VanillaAnalyticsGoogle;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class MetricsDimensionsSection extends AbstractPropertySection {

	private VanillaAnalyticsGoogle model;
	
	private CheckboxTableViewer tableDim;
	private CheckboxTableViewer tableMetrics;
	
	public MetricsDimensionsSection() {
		
	}
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		final Shell shell = parent.getShell();
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		Label lblInfos = getWidgetFactory().createLabel(composite, Messages.MetricsDimensionsSection_0 + "\n" + Messages.MetricsDimensionsSection_5); //$NON-NLS-1$
		lblInfos.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

		Label lblDim = getWidgetFactory().createLabel(composite, Messages.MetricsDimensionsSection_6);
		lblDim.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		Label lblMetrics = getWidgetFactory().createLabel(composite, Messages.MetricsDimensionsSection_7);
		lblMetrics.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		
		tableDim = new CheckboxTableViewer(getWidgetFactory().createTable(composite, SWT.BORDER 
				| SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.CHECK));
		tableDim.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		tableDim.setContentProvider(new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
			@Override
			public void dispose() { }
			
			@Override
			public Object[] getElements(Object inputElement) {
				return (String[])inputElement;
			}
		});
		tableDim.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return (String)element;
			}
			
		});
		tableDim.addCheckStateListener(new ICheckStateListener(){
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				String dimension = (String)event.getElement();
				if (event.getChecked()){
					if(model.getDimensions() == null || model.getDimensions().size()<7){
						model.addDimension(dimension);
					}
					else {
						tableDim.setChecked(event.getElement(), false);
						MessageDialog.openInformation(shell, Messages.MetricsDimensionsSection_1, Messages.MetricsDimensionsSection_2);
					}
				}
				else{
					model.removeDimension(dimension);
				}
				
				model.refreshDescriptor();
			}
			
		});
		
		tableMetrics = new CheckboxTableViewer(getWidgetFactory().createTable(composite, SWT.BORDER 
				| SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.CHECK));
		tableMetrics.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		tableMetrics.setContentProvider(new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
			@Override
			public void dispose() { }
			
			@Override
			public Object[] getElements(Object inputElement) {
				return (String[])inputElement;
			}
		});
		tableMetrics.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return (String)element;
			}
			
		});
		tableMetrics.addCheckStateListener(new ICheckStateListener(){

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				String metric = (String)event.getElement();
				if (event.getChecked()){
					if(model.getMetrics() == null || model.getMetrics().size()<10){
						model.addMetric(metric);
					}
					else {
						tableMetrics.setChecked(event.getElement(), false);
						MessageDialog.openInformation(shell, Messages.MetricsDimensionsSection_3, Messages.MetricsDimensionsSection_4);
					}
				}
				else{
					model.removeMetric(metric);
				}
				
				model.refreshDescriptor();
			}
		});
		
		tableDim.setInput(IMetricsDimensions.DIMENSIONS);
		tableMetrics.setInput(IMetricsDimensions.METRICS);
	}
	
	@Override
	public void refresh() {
		setDimensionsChecked(model.getDimensions());
		setMetricsChecked(model.getMetrics());
		
		model.refreshDescriptor();
	}
	
	private void setDimensionsChecked(List<String> dims){
		if(dims != null){
			for(String dim : dims){
				tableDim.setChecked(dim, true);
			}
		}
		tableDim.refresh();
	}
	
	private void setMetricsChecked(List<String> metrics){
		if(metrics != null){
			for(String met : metrics){
				tableMetrics.setChecked(met, true);
			}
		}
		tableMetrics.refresh();
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.model = (VanillaAnalyticsGoogle)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
}
