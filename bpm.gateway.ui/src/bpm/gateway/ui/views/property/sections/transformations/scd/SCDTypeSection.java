package bpm.gateway.ui.views.property.sections.transformations.scd;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.outputs.SlowChangingDimension2;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class SCDTypeSection extends AbstractPropertySection {

	private Node node;
	private Button type1, type2;
	
	public SCDTypeSection() {
		
	}
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());
		
		/*
		 * Version Field
		 */
		Group g3 = getWidgetFactory().createGroup(parent, Messages.SCDTypeSection_0);
		g3.setLayout(new GridLayout());
		g3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		type1 = getWidgetFactory().createButton(g3, Messages.SCDTypeSection_1, SWT.RADIO);
		type1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		type1.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (type1.getSelection()){
					((SlowChangingDimension2)node.getGatewayModel()).setScdType(SlowChangingDimension2.SCD_TYPE1);
				}
				else{
					((SlowChangingDimension2)node.getGatewayModel()).setScdType(SlowChangingDimension2.SCD_TYPE2);
				}
			}
			
		});
		type2 = getWidgetFactory().createButton(g3, Messages.SCDTypeSection_2, SWT.RADIO);
		type2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		type2.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (type1.getSelection()){
					((SlowChangingDimension2)node.getGatewayModel()).setScdType(SlowChangingDimension2.SCD_TYPE1);
				}
				else{
					((SlowChangingDimension2)node.getGatewayModel()).setScdType(SlowChangingDimension2.SCD_TYPE2);
				}
			}
		});
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
	}
	
	@Override
	public void refresh() {
		
		if (((SlowChangingDimension2)node.getGatewayModel()).getScdType() == SlowChangingDimension2.SCD_TYPE1){
			type1.setSelection(true);
			type2.setSelection(false);
		}
		else{
			type1.setSelection(false);
			type2.setSelection(true);
		}
	}

}
