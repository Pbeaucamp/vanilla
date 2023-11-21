package bpm.gateway.ui.views.property.sections.files.vcl;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.server.file.FileVCL;
import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.gateway.core.transformations.outputs.FileOutputCSV;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.model.properties.FileCSVProperties;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class FileInputVCLSection extends AbstractPropertySection {

	private Node node;
	private Text skippedRows;

	
	public FileInputVCLSection() {
		
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		Label l = getWidgetFactory().createLabel(composite, Messages.FileInputVCLSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		skippedRows = getWidgetFactory().createText(composite, "0"); //$NON-NLS-1$
		skippedRows.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		skippedRows.addModifyListener(listener);
		
		
		
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
		FileVCL tr = (FileVCL) node.getGatewayModel();

		skippedRows.removeModifyListener(listener);
		skippedRows.setText("" + tr.getNumberOfRowToSkip()); //$NON-NLS-1$
		skippedRows.addModifyListener(listener);
		
		

	}
	
	
	private ModifyListener listener = new ModifyListener() {
	    
        public void modifyText(ModifyEvent evt) {
        	FileVCL tr = (FileVCL) node.getGatewayModel();
        	
        	String last = skippedRows.getText().substring(skippedRows.getText().length() - 1);
        	if (Character.isDigit(last.charAt(0))){
        		if (evt.widget == skippedRows){
            		tr.setNumberOfRowToSkip(skippedRows.getText());
            		
            	}
        	}
        	else{
        		skippedRows.setText("" + tr.getNumberOfRowToSkip()); //$NON-NLS-1$
        	}

            
        }
    };
}
