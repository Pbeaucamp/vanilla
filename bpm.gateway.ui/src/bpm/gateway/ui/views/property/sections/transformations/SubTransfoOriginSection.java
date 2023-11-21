package bpm.gateway.ui.views.property.sections.transformations;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.transformations.SubTransformation;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.model.GatewayObjectProperties;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.model.properties.DataStreamProperties;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogPickupConstant;
import bpm.gateway.ui.tools.dialogs.DialogRepositoryObject;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class SubTransfoOriginSection extends AbstractPropertySection {

	private Node node;
	
	private Text definition;
	private Button b, v;
	
	private RepositoryItem item;
	
	
	public SubTransfoOriginSection() {
		
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
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayout(new GridLayout());;
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		CLabel l = getWidgetFactory().createCLabel(composite, Messages.SubTransfoOriginSection_0, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		definition = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		definition.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		definition.setToolTipText(Messages.SubTransfoOriginSection_2);
		definition.setEnabled(false);
		
		v = getWidgetFactory().createButton(composite, Messages.SubTransfoOriginSection_1, SWT.PUSH);
		v.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		v.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickupConstant d = new DialogPickupConstant(getPart().getSite().getShell());
				
				if (d.open() == Dialog.OK){
					
					definition.setText(d.getVariable().getOuputName() );
					
					Event ev = new Event();
					ev.widget= definition;
					ev.data = d.getVariable().getOuputName() ;
					listener.modifyText(new ModifyEvent(ev));
				}
			}
			
		});
		
		b = getWidgetFactory().createButton(composite, "...", SWT.PUSH); //$NON-NLS-1$
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if (Activator.getDefault().getRepositoryContext() == null){
					FileDialog fd = new FileDialog(getPart().getSite().getShell());
					
					String varName = null;
					Variable v = null;
					String filterPath = null;
					try {

						if (definition.getText().startsWith("{$")){ //$NON-NLS-1$
							
							varName = definition.getText().substring(0, definition.getText().indexOf("}" ) + 1); //$NON-NLS-1$
							
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

						
						definition.setText(path);
						Event ev = new Event();
						ev.widget= definition;
						ev.data = path;
						listener.modifyText(new ModifyEvent(ev));
					}

				}
				else {
					SubTransformation tr = (SubTransformation)node.getGatewayModel();
					DialogRepositoryObject dial = new DialogRepositoryObject(getPart().getSite().getShell(), IRepositoryApi.GTW_TYPE);
					
					if (dial.open() == Dialog.OK){
						
						item = dial.getRepositoryItem();
						definition.setText(item.getItemName());
						((SubTransformation)node.getGatewayModel()).setDefinition(item.getId() + ""); //$NON-NLS-1$
					}
					
					
				}
				
				
				
								
			}
			
		});
	}
	
	
	
	@Override
	public void aboutToBeHidden() {
		if (definition != null && !definition.isDisposed()){
			definition.removeModifyListener(listener);
		}
		
		super.aboutToBeHidden();
	}

	
	
	
	
	@Override
	public void aboutToBeShown() {
		if (definition != null && !definition.isDisposed()){
			definition.addModifyListener(listener);
		}
		
		super.aboutToBeShown();
	}
	
	
	@Override
	public void refresh() {
		GatewayObjectProperties properties = (GatewayObjectProperties) node.getAdapter(IPropertySource.class);
		definition.removeModifyListener(listener);
		SubTransformation tr = (SubTransformation)node.getGatewayModel();
		if (tr.getDefinition() != null){
			
			if (Activator.getDefault().getRepositoryContext()== null){
				item = null;
				b.setEnabled(true);
				v.setEnabled(true);
				definition.setText(tr.getDefinition() != null ? tr.getDefinition() : ""); //$NON-NLS-1$
			}
			else {
				v.setEnabled(false);
				b.setEnabled(true);
				if (tr.getDefinition() != null && (item == null || !tr.getDefinition().equals(item.getId() + ""))){ //$NON-NLS-1$
					IRepositoryApi sock = Activator.getDefault().getRepositoryConnection();
					try {
						item = sock.getRepositoryService().getDirectoryItem(Integer.parseInt(tr.getDefinition()));
					} catch (NumberFormatException e) {
						item = null;
						e.printStackTrace();
					} catch (Exception e) {
						item = null;
						e.printStackTrace();
					}
					
				}
				if (item != null){
					definition.setText(item.getItemName());
				}
				else{
					definition.setText(""); //$NON-NLS-1$
				}
				
				
			}
			
				
		}
		else{
			definition.setText(""); //$NON-NLS-1$
			b.setEnabled(true);
			v.setEnabled(true);
		}
		
		definition.addModifyListener(listener);
		

	}
	
	
	
	
	
	private ModifyListener listener = new ModifyListener() {
	    
        public void modifyText(ModifyEvent evt) {
        	GatewayObjectProperties properties = (GatewayObjectProperties) node.getAdapter(IPropertySource.class);

        	if (evt.widget == definition){
        		((SubTransformation)node.getGatewayModel()).setDefinition((String)evt.data);
        		
        		
        		
        	}
            
        }
    };
}
