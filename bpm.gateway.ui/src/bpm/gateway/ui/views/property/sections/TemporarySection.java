package bpm.gateway.ui.views.property.sections;

import java.util.HashMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.Server;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogPickupConstant;

public class TemporarySection extends AbstractPropertySection {

	
	private Node node;
	private Text filePath, separator;
	
	private HashMap<String, Server> map = new HashMap<String, Server>();
	
	public TemporarySection() {
		
		
		
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
				
		Composite file = getWidgetFactory().createFlatFormComposite(composite);
		file.setLayout(new GridLayout(4, false));
		file.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		Label l2 = getWidgetFactory().createLabel(file, Messages.TemporarySection_0);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		filePath = getWidgetFactory().createText(file, ""); //$NON-NLS-1$
		filePath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		filePath.addModifyListener(listener);
		
		
		Button variable = getWidgetFactory().createButton(file, Messages.TemporarySection_2, SWT.PUSH);
		variable.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		variable.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickupConstant d = new DialogPickupConstant(getPart().getSite().getShell());
				
				if (d.open() == Dialog.OK){
					
					filePath.setText(d.getVariable().getOuputName() );
					
					Event ev = new Event();
					ev.widget= filePath;
					listener.modifyText(new ModifyEvent(ev));
				}
				
			}
		});
		
		
		Button browse = getWidgetFactory().createButton(file, Messages.TemporarySection_3, SWT.PUSH);
		browse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browse.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getPart().getSite().getShell());
				
				String varName = null;
				Variable v = null;
				String filterPath = null;
				try {

					if (filePath.getText().startsWith("{$")){ //$NON-NLS-1$
						
						varName = filePath.getText().substring(0, filePath.getText().indexOf("}" ) + 1); //$NON-NLS-1$
						
						v = ResourceManager.getInstance().getVariableFromOutputName(varName);
						String h = v.getValueAsString();
						fd.setFilterPath(h.startsWith("/") && h.contains(":") ?  h.substring(1) : h); //$NON-NLS-1$ //$NON-NLS-2$
						filterPath = fd.getFilterPath();
					}
					
					
				} catch (Exception e1) {
					
				}
				String path = fd.open();
				
							
				
				if (path != null){
					
					if (varName != null){
						path = v.getOuputName() + path.substring(filterPath.length() ); 
					}

					
					filePath.setText(path);
					Event ev = new Event();
					ev.widget= filePath;
					listener.modifyText(new ModifyEvent(ev));
				}
				
			}
			
		});
	
	
		Label l3 = getWidgetFactory().createLabel(file, Messages.TemporarySection_8);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		separator = getWidgetFactory().createText(file, ""); //$NON-NLS-1$
		separator.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		separator.addModifyListener(listener);
		
	}
	
	
	private ModifyListener listener = new ModifyListener() {
	    
        public void modifyText(ModifyEvent evt) {
        	if (evt.widget == filePath){
        		node.getGatewayModel().setTemporaryFilename(filePath.getText());
        		
        	}
        	else if (evt.widget == separator){
        		node.getGatewayModel().setTemporarySpliterChar(separator.getText().charAt(0));
        	}
            
        }
    };
    
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
		filePath.removeModifyListener(listener);
		
		filePath.setText(node.getGatewayModel().getTemporaryFilename() + ""); //$NON-NLS-1$
		filePath.addModifyListener(listener);
		
		
		separator.removeModifyListener(listener);
		separator.setText(node.getGatewayModel().getTemporarySpliterChar() + ""); //$NON-NLS-1$
		
		separator.addModifyListener(listener);
		

	}
    
	@Override
	public void aboutToBeShown() {
		if (separator !=  null || ! separator.isDisposed()){
			separator.addModifyListener(listener);
		}
		
		
		if (filePath !=  null || ! filePath.isDisposed()){
			filePath.addModifyListener(listener);
		}
		
		
		super.aboutToBeShown();
	}


	@Override
	public void aboutToBeHidden() {
		
		if (separator !=  null || ! separator.isDisposed()){
			separator.removeModifyListener(listener);
		}
		
		
		if (filePath !=  null || ! filePath.isDisposed()){
			filePath.removeModifyListener(listener);
		}
		
		super.aboutToBeHidden();
	}
    
}
