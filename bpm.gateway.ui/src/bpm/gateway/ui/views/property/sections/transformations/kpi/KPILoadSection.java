package bpm.gateway.ui.views.property.sections.transformations.kpi;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.freemetrics.KPIOutput;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class KPILoadSection extends AbstractPropertySection {

	private Button isMetricName, isApplicationName;
	
	private CCombo dateFormat;
	private CCombo inputValueIndex;
	private CCombo inputDateIndex;
	private CCombo inputMetricIndex;
	private CCombo inputApplicationIndex;
	private CCombo inputAssocIndex;
	private Button performUpdate;
	
	
	private Node node;
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite main = getWidgetFactory().createComposite(parent);
		main.setLayout(new GridLayout());
		
		Group g = getWidgetFactory().createGroup(main, Messages.KPILoadSection_0);
		g.setLayout(new GridLayout(2, false));
		g.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l = getWidgetFactory().createLabel(g, Messages.KPILoadSection_1, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		inputDateIndex = getWidgetFactory().createCCombo(g, SWT.READ_ONLY | SWT.BORDER);
		inputDateIndex.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		inputDateIndex.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				((KPIOutput)node.getGatewayModel()).setInputDateIndex(inputDateIndex.getSelectionIndex());
			}
			
		});
		
		Label l5 = getWidgetFactory().createLabel(g, Messages.KPILoadSection_2, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		dateFormat = getWidgetFactory().createCCombo(g, SWT.BORDER);
		dateFormat.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dateFormat.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				((KPIOutput)node.getGatewayModel()).setDateFormat(dateFormat.getText());
			}
			
		});
		dateFormat.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				((KPIOutput)node.getGatewayModel()).setDateFormat(dateFormat.getText());
				
			}

			
		});
		
		
		Label l2 = getWidgetFactory().createLabel(main, Messages.KPILoadSection_3, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		inputValueIndex = getWidgetFactory().createCCombo(main, SWT.READ_ONLY| SWT.BORDER);
		inputValueIndex.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		inputValueIndex.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				((KPIOutput)node.getGatewayModel()).setInputValueIndex(inputValueIndex.getSelectionIndex());
			}
			
		});
		
		
		Group g1 = getWidgetFactory().createGroup(main, Messages.KPILoadSection_4);
		g1.setLayout(new GridLayout(2, false));
		g1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		isMetricName = getWidgetFactory().createButton(g1, Messages.KPILoadSection_5, SWT.CHECK);
		isMetricName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		isMetricName.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				((KPIOutput)node.getGatewayModel()).setMetricName(isMetricName.getSelection());
			}
		});
		Label l3 = getWidgetFactory().createLabel(g1, Messages.KPILoadSection_6, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		inputMetricIndex = getWidgetFactory().createCCombo(g1, SWT.READ_ONLY| SWT.BORDER);
		inputMetricIndex.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		inputMetricIndex.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				((KPIOutput)node.getGatewayModel()).setInputMetricIndex(inputMetricIndex.getSelectionIndex());
			}
			
		});
		
		
		Group g2 = getWidgetFactory().createGroup(main, Messages.KPILoadSection_7);
		g2.setLayout(new GridLayout(2, false));
		g2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
		isApplicationName= getWidgetFactory().createButton(g2, Messages.KPILoadSection_8, SWT.CHECK);
		isApplicationName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		isApplicationName.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				((KPIOutput)node.getGatewayModel()).setApplicationName(isApplicationName.getSelection());
			}
		});
		
		Label l4 = getWidgetFactory().createLabel(g2, Messages.KPILoadSection_9, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		
		inputApplicationIndex = getWidgetFactory().createCCombo(g2, SWT.READ_ONLY| SWT.BORDER);
		inputApplicationIndex.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		inputApplicationIndex.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				((KPIOutput)node.getGatewayModel()).setInputApplicationIndex(inputApplicationIndex.getSelectionIndex());
			}
			
		});
		
		Group g3 = getWidgetFactory().createGroup(main, Messages.KPILoadSection_10);
		g3.setLayout(new GridLayout(2, false));
		g3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
		
		
		Label l6 = getWidgetFactory().createLabel(g3, Messages.KPILoadSection_11, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		
		inputAssocIndex = getWidgetFactory().createCCombo(g3, SWT.READ_ONLY| SWT.BORDER);
		inputAssocIndex.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		inputAssocIndex.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				((KPIOutput)node.getGatewayModel()).setInputAssocIndex(inputAssocIndex.getSelectionIndex());
			}
			
		});
		
		performUpdate = getWidgetFactory().createButton(main, Messages.KPILoadSection_12, SWT.CHECK);
		performUpdate.setLayoutData(new GridData(GridData.FILL_BOTH));
		performUpdate.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				((KPIOutput)node.getGatewayModel()).setPerformUdpateOnOldValues(performUpdate.getSelection());
			}
			
		});

	}
	@Override
	public void refresh() {
		KPIOutput transfo = (KPIOutput)node.getGatewayModel();
		
		List<String> l = new ArrayList<String>();
		
		try{
			for(Transformation t : transfo.getInputs()){
				for(StreamElement e : t.getDescriptor(t).getStreamElements()){
					l.add(e.name);
				}
			}
		}catch(Exception e){
			
		}
		
		isApplicationName.setSelection(transfo.isApplicationName());
		isMetricName.setSelection(transfo.isMetricName());
		
		inputApplicationIndex.setItems(l.toArray(new String[l.size()]));
		if (transfo.getInputApplicationIndex() != null){
			try{
				inputApplicationIndex.select(transfo.getInputApplicationIndex());
			}catch(IndexOutOfBoundsException e){
				inputApplicationIndex.select(-1);
			}
		}
		
		inputDateIndex.setItems(l.toArray(new String[l.size()]));
		if (transfo.getInputDateIndex() != null){
			try{
				inputDateIndex.select(transfo.getInputDateIndex());
			}catch(IndexOutOfBoundsException e){
				inputDateIndex.select(-1);
			}
		}
		
		inputMetricIndex.setItems(l.toArray(new String[l.size()]));
		if (transfo.getInputMetricIndex()!= null){
			try{
				inputMetricIndex.select(transfo.getInputMetricIndex());
			}catch(IndexOutOfBoundsException e){
				inputMetricIndex.select(-1);
			}
		}
		
		inputValueIndex.setItems(l.toArray(new String[l.size()]));
		if (transfo.getInputValueIndex() != null){
			try{
				inputValueIndex.select(transfo.getInputValueIndex());
			}catch(IndexOutOfBoundsException e){
				inputValueIndex.select(-1);
			}
		}
		inputAssocIndex.setItems(l.toArray(new String[l.size()]));
		if (transfo.getInputAssocIndex() != null){
			try{
				inputAssocIndex.select(transfo.getInputAssocIndex());
			}catch(IndexOutOfBoundsException e){
				inputAssocIndex.select(-1);
			}
		}
		
		performUpdate.setSelection(transfo.isPerformUpdateOnOldValues());
		
		dateFormat.setItems(KPIOutput.DATE_FORMATS);
		dateFormat.setText(transfo.getDateFormat());
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
       
	}
}
