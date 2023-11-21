package bpm.gateway.ui.resource.server.wizard;

import java.util.Properties;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bpm.gateway.ui.resource.composite.connection.LdapConnectionComposite;

public class LdapPage extends WizardPage{

	private LdapConnectionComposite ldapcomposite;
	
	protected LdapPage(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
		//create main composite & set layout
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl( mainComposite );
		setPageComplete(true);

		
	}
	private void createPageContent(Composite composite){
		ldapcomposite = new LdapConnectionComposite(composite, SWT.NONE);
		ldapcomposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ldapcomposite.addListener(SWT.Modify, new Listener(){

			public void handleEvent(Event event) {
				getContainer().updateButtons();
				
			}
			
		});
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		 
		return ldapcomposite.isFilled();
	}


	public Properties getValues() {
		
		return ldapcomposite.getProperties();
	}

	
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
}
