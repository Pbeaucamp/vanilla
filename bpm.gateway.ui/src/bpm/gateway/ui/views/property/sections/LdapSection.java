package bpm.gateway.ui.views.property.sections;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.inputs.LdapInput;
import bpm.gateway.ui.dialogs.ldap.DialogLdapBrowser;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class LdapSection extends AbstractPropertySection{

	protected Node node;
	protected Text ldapDn;
	protected Button browse;
	
	protected SelectionAdapter browseListener = new SelectionAdapter(){

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			
			try {
				final DialogLdapBrowser dial = new DialogLdapBrowser(getPart().getSite().getShell(), false, (LdapInput)node.getGatewayModel());
				if (dial.open() == Dialog.OK){
					
					if (node.getGatewayModel() instanceof LdapInput){
						((LdapInput)node.getGatewayModel()).setAttributeMapping(dial.getStreamDescriptor());
					}
					else{
						BusyIndicator.showWhile(Display.getDefault(), new Runnable(){
							public void run(){
								ldapDn.setText(dial.getDn());
								((DataStream)node.getGatewayModel()).setDefinition(dial.getDn());
							}
						});
					}
					
					
					
					
				
					
				}
			}catch(Exception ex){
				
			}
			
		}
		
	};
	
	
	private ContentProposalAdapter proposalAdapter ;
	
	private ModifyListener modifyListener = new ModifyListener() {
		
		public void modifyText(ModifyEvent e) {
			((DataStream)node.getGatewayModel()).setDefinition(ldapDn.getText());
			
		}
	};
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite container = getWidgetFactory().createFlatFormComposite(parent);
		container.setLayout(new GridLayout(3, false));
		
		Label l = getWidgetFactory().createLabel(container, Messages.LdapSection_0);
		l.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		
		ldapDn = getWidgetFactory().createText(container, "", SWT.BORDER | SWT.FLAT); //$NON-NLS-1$
		ldapDn.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		ldapDn.setEnabled(true);
		
		proposalAdapter = new ContentProposalAdapter(
				ldapDn, 
				new TextContentAdapter(), 
				new SimpleContentProposalProvider(new String[]{}),
				null, 
				null);;

		browse = getWidgetFactory().createButton(container, "...", SWT.PUSH); //$NON-NLS-1$
		browse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browse.addSelectionListener(browseListener);
	}
	
	
	@Override
	public void refresh() {
		ldapDn.removeModifyListener(modifyListener);
		ldapDn.setEnabled(!node.getGatewayModel().getInputs().isEmpty());
		if (((DataStream)node.getGatewayModel()).getDefinition() != null){
			ldapDn.setText(((DataStream)node.getGatewayModel()).getDefinition());
		}
		/*
		 * content proposal for input stream
		 */
		
		List<String> l = new ArrayList<String>();
		
		if (!node.getGatewayModel().getInputs().isEmpty()){
			try{
				for(StreamElement e : node.getGatewayModel().getInputs().get(0).getDescriptor(node.getGatewayModel()).getStreamElements()){
					l.add("{$" + e.name + "}"); //$NON-NLS-1$ //$NON-NLS-2$
				
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
				
		}
		
		proposalAdapter.setContentProposalProvider(new SimpleContentProposalProvider(l.toArray(new String[l.size()])));
		ldapDn.addModifyListener(modifyListener);
		
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
