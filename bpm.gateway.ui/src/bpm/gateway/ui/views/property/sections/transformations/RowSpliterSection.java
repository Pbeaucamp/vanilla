package bpm.gateway.ui.views.property.sections.transformations;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.RowsFieldSplitter;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class RowSpliterSection extends AbstractPropertySection {
	private Node node;
	
	private CCombo inputFields;
	private Text splitSequence;
	private Button trim, keepOriginalInOutput;
	
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		Label l = getWidgetFactory().createLabel(composite, Messages.RowSpliterSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		inputFields = getWidgetFactory().createCCombo(composite, SWT.READ_ONLY);
		inputFields.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		inputFields.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				((RowsFieldSplitter)node.getGatewayModel()).setInputFieldIndexToSplit(inputFields.getSelectionIndex());
			}
			
		});

		Label l2 = getWidgetFactory().createLabel(composite, Messages.RowSpliterSection_1);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		splitSequence = getWidgetFactory().createText(composite, "", SWT.FLAT); //$NON-NLS-1$
		splitSequence.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		splitSequence.addModifyListener(new ModifyListener(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			public void modifyText(ModifyEvent e) {
				((RowsFieldSplitter)node.getGatewayModel()).setSplitSequence(splitSequence.getText());
				
			}
			
			
		});
		trim = getWidgetFactory().createButton(composite, Messages.RowSpliterSection_3, SWT.CHECK);
		trim.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		trim.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				((RowsFieldSplitter)node.getGatewayModel()).setTrim(trim.getSelection());
			}
		});
		
		
		keepOriginalInOutput = getWidgetFactory().createButton(composite, Messages.RowSpliterSection_4, SWT.CHECK);
		keepOriginalInOutput.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		keepOriginalInOutput.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				((RowsFieldSplitter)node.getGatewayModel()).setKeepOriginalFieldInOuput(keepOriginalInOutput.getSelection());
			}
		});
	}
	
	@Override
	public void refresh() {
		try{
			List<String> l = new ArrayList<String>();
			for(StreamElement e : node.getGatewayModel().getInputs().get(0).getDescriptor(node.getGatewayModel()).getStreamElements()){
				l.add(e.name);
			}
			inputFields.setItems(l.toArray(new String[l.size()]));
			
			
			if (((RowsFieldSplitter)node.getGatewayModel()).getInputFieldIndexToSplit() != null){
				for(int i = 0; i < l.size(); i++){
					if (((RowsFieldSplitter)node.getGatewayModel()).getInputFieldIndexToSplit() == i){
						inputFields.select(i);
						break;
					}
				}
			}
			
			
		}catch(Exception e){
			inputFields.setItems(new String[]{});
			e.printStackTrace();
		}
	
		
		keepOriginalInOutput.setSelection(((RowsFieldSplitter)node.getGatewayModel()).isKeepOriginalFieldInOuput());
		trim.setSelection(((RowsFieldSplitter)node.getGatewayModel()).isTrim());
		splitSequence.setText(((RowsFieldSplitter)node.getGatewayModel()).getSplitSequence());
		
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
