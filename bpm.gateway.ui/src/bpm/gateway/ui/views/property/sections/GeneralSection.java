package bpm.gateway.ui.views.property.sections;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.ui.gef.model.GatewayObjectProperties;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;

public class GeneralSection extends AbstractPropertySection {

	private Text nameTxt;
	private Text descriptionTxt;
	
	private Node node;
	
	public GeneralSection() {
		
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);

		nameTxt = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		FormData data = new FormData();
        data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
        nameTxt.setLayoutData(data);
        nameTxt.addModifyListener(listener);
        
        
        CLabel labelLabel = getWidgetFactory().createCLabel(composite, "Name"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(nameTxt,ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(nameTxt, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);
		
		
		descriptionTxt= getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
        data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
        data.right = new FormAttachment(100, 0);
        data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
        descriptionTxt.setLayoutData(data);
        descriptionTxt.addModifyListener(listener);

        
        labelLabel = getWidgetFactory().createCLabel(composite, "Description"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(descriptionTxt,ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(descriptionTxt, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);
	
	}

	
	
	
	@Override
	public void refresh() {
		if (nameTxt != null && !nameTxt.isDisposed()){
			nameTxt.removeModifyListener(listener);
		}
		if (descriptionTxt != null && !descriptionTxt.isDisposed()){
			descriptionTxt.removeModifyListener(listener);
		}
		
		GatewayObjectProperties properties = (GatewayObjectProperties) node.getAdapter(IPropertySource.class);
		
		nameTxt.removeModifyListener(listener);
		nameTxt.setText((String)properties.getPropertyValue(GatewayObjectProperties.PROPERTY_NAME));
		nameTxt.addModifyListener(listener);

		descriptionTxt.removeModifyListener(listener);
		String desc = (String)properties.getPropertyValue(GatewayObjectProperties.PROPERTY_DESCRIPTION);
		
		descriptionTxt.setText(desc == null ? "" : desc); //$NON-NLS-1$
		descriptionTxt.addModifyListener(listener);
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
	public void aboutToBeShown() {
		
		super.aboutToBeShown();
	}


	@Override
	public void aboutToBeHidden() {
		
		
		super.aboutToBeHidden();
	}


	private ModifyListener listener = new ModifyListener() {
	    
        public void modifyText(ModifyEvent evt) {
        	GatewayObjectProperties properties = (GatewayObjectProperties) node.getAdapter(IPropertySource.class);
        	if (evt.widget == nameTxt){
        		properties.setPropertyValue(GatewayObjectProperties.PROPERTY_NAME, nameTxt.getText());
        		
        	}
        	
        	if (evt.widget == descriptionTxt){
        		properties.setPropertyValue(GatewayObjectProperties.PROPERTY_DESCRIPTION, descriptionTxt.getText());
        		
        	}
            
        }
    };
}

