package bpm.gateway.ui.views.property.sections.transformations.scd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.outputs.SlowChangingDimension1;
import bpm.gateway.core.transformations.outputs.SlowChangingDimension2;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class SCDVersionSection extends AbstractPropertySection {

	private Node node;
	private CCombo versionField;
	
	public SCDVersionSection() {
		
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
		Group g3 = getWidgetFactory().createGroup(parent, Messages.SCDVersionSection_0);
		g3.setLayout(new GridLayout(2, false));
		g3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l5 = getWidgetFactory().createLabel(g3, Messages.SCDVersionSection_1);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		versionField = getWidgetFactory().createCCombo(g3, SWT.READ_ONLY | SWT.BORDER | SWT.FLAT);
		versionField.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		versionField.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (node.getGatewayModel() instanceof SlowChangingDimension2){
					((SlowChangingDimension2)node.getGatewayModel()).setTargetVersionIndex(versionField.getSelectionIndex());		
				}
				else if (node.getGatewayModel() instanceof SlowChangingDimension1){
					((SlowChangingDimension1)node.getGatewayModel()).setTargetVersionIndex(versionField.getSelectionIndex());		
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
		
		/*
		 * fill the Combos
		 */
		List<String> scdFieldsNames = new ArrayList<String>();
		Integer index = null;
		if (node.getGatewayModel() instanceof SlowChangingDimension2){
			SlowChangingDimension2 scd = (SlowChangingDimension2)node.getGatewayModel();
			
			index = scd.getTargetVersionIndex();
			
			try {
				for(StreamElement e : scd.getDescriptor(scd).getStreamElements()){
					scdFieldsNames.add(e.name);
				}
			} catch (ServerException e1) {
				
				e1.printStackTrace();
			}		
		}
		else if (node.getGatewayModel() instanceof SlowChangingDimension1){
			SlowChangingDimension1 scd = (SlowChangingDimension1)node.getGatewayModel();
			
			
			index = scd.getTargetVersionIndex();
			try {
				for(StreamElement e : scd.getDescriptor(scd).getStreamElements()){
					scdFieldsNames.add(e.name);
				}
			} catch (ServerException e1) {
				
				e1.printStackTrace();
			}		
		}
		
		
		versionField.setItems(scdFieldsNames.toArray(new String[scdFieldsNames.size()]));
		
		if (index != null){
			versionField.select(index);
		}
	}

}
