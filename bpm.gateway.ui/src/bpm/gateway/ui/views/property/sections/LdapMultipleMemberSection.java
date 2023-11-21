package bpm.gateway.ui.views.property.sections;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.transformations.inputs.LdapMultipleMembers;
import bpm.gateway.ui.dialogs.ldap.DialogLdapBrowser;
import bpm.gateway.ui.i18n.Messages;

public class LdapMultipleMemberSection extends LdapSection {
	private Text attributeName;
	
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite container = getWidgetFactory().createFlatFormComposite(parent);
		container.setLayout(new GridLayout(2, false));
		
		Label l = getWidgetFactory().createLabel(container, Messages.LdapMultipleMemberSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		attributeName = getWidgetFactory().createText(container, "", SWT.BORDER); //$NON-NLS-1$
		attributeName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		attributeName.setEnabled(false);
		
		// replacement listener
		browse.removeSelectionListener(browseListener);
	
		browse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				try {
					final DialogLdapBrowser dial = new DialogLdapBrowser(getPart().getSite().getShell(), true, (DataStream)node.getGatewayModel());
					if (dial.open() == Dialog.OK){
						
						BusyIndicator.showWhile(Display.getDefault(), new Runnable(){
							public void run(){
								ldapDn.setText(dial.getDn());
								attributeName.setText(dial.getSelectedAttribute());
								((LdapMultipleMembers)node.getGatewayModel()).setDefinition(dial.getDn());
								((LdapMultipleMembers)node.getGatewayModel()).setAttributeName(dial.getSelectedAttribute());
							}
						});
						
						
					
						
					}
				}catch(Exception ex){
					
				}
				
			}
		});
	
		
	}
	@Override
	public void refresh() {
		
		super.refresh();
		
		
		if (((LdapMultipleMembers)node.getGatewayModel()).getAttributeName() != null){
			attributeName.setText(((LdapMultipleMembers)node.getGatewayModel()).getAttributeName());
		}
		
		
		
	}
}
