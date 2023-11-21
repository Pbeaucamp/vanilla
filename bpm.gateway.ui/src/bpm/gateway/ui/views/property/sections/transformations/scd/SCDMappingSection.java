package bpm.gateway.ui.views.property.sections.transformations.scd;

import java.awt.Point;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
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

import bpm.gateway.core.ISCD;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.ui.composites.SCDMappingComposite;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogMatchingFields;

public class SCDMappingSection extends AbstractPropertySection {

	private SCDMappingComposite keysComposite, fieldsComposite;
	private Node node;
	
	public SCDMappingSection() {
		
	}
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout(2, true));

		Group g = getWidgetFactory().createGroup(parent, Messages.SCDMappingSection_0);
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true));
		
		
		Button b = getWidgetFactory().createButton(g, Messages.SCDMappingSection_1, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		b.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					ISCD scd = (ISCD)node.getGatewayModel();
					StreamDescriptor trDesc = ((Transformation)scd).getDescriptor((Transformation)scd);
					
					if (((Transformation)scd).getInputs().isEmpty()){
						return;
					}
					
					StreamDescriptor inDesc = ((Transformation)scd).getInputs().get(0).getDescriptor((Transformation)scd); 

					DialogMatchingFields d = new DialogMatchingFields(getPart().getSite().getShell(), 
							trDesc,
							inDesc);
					if(d.open() == Dialog.OK){
						for(Point p : d.getChecked()){
							scd.createKeyMapping(trDesc.getStreamElements().get(p.x), p.y);//+1);
						}
						keysComposite.fillDatas(scd);
						
					}
				}catch(Exception ex){
					
				}
			}
			
		});
		
		keysComposite = new SCDMappingComposite(g, SWT.NONE, false);
		keysComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group g2 = getWidgetFactory().createGroup(parent, Messages.SCDMappingSection_2);
		g2.setLayout(new GridLayout());
		g2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true));
		
		
		Button b2 = getWidgetFactory().createButton(g2, Messages.SCDMappingSection_3, SWT.PUSH);
		b2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		b2.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					ISCD scd = (ISCD)node.getGatewayModel();
					StreamDescriptor trDesc = ((Transformation)scd).getDescriptor((Transformation)scd);
					
					if (((Transformation)scd).getInputs().isEmpty()){
						return;
					}
					
					StreamDescriptor inDesc = ((Transformation)scd).getInputs().get(0).getDescriptor((Transformation)scd); 

					DialogMatchingFields d = new DialogMatchingFields(getPart().getSite().getShell(), 
							trDesc,
							inDesc);
					if(d.open() == Dialog.OK){
						for(Point p : d.getChecked()){
							scd.createFieldMapping(trDesc.getStreamElements().get(p.x), p.y);
						}
						fieldsComposite.fillDatas(scd);
						
					}
				}catch(Exception ex){
					
				}
			}
			
		});
		
		
		
		fieldsComposite = new SCDMappingComposite(g2, SWT.NONE, true);
		fieldsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		
	}

	
	@Override
	public void refresh() {
		ISCD scd = (ISCD)node.getGatewayModel();
		keysComposite.fillDatas(scd);
		fieldsComposite.fillDatas(scd);
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
