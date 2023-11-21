package bpm.gateway.ui.resource.server.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.gateway.core.server.d4c.D4CConnection;
import bpm.gateway.ui.resource.composite.connection.D4CConnectionComposite;

public class D4CConnectionPage extends WizardPage {

	private Composite mainComposite;

	private D4CConnectionComposite connectionComposite;

	public D4CConnectionPage(String pageName) {
		super(pageName);
	}

	public boolean isPageComplete() {
		D4CConnection connection = connectionComposite.getConnection();
		if (connectionComposite.getConnection() != null) {
			return connection.getUrl() != null && !connection.getUrl().isEmpty() 
					&& connection.getOrg() != null && !connection.getOrg().isEmpty() 
					&& connection.getLogin() != null && !connection.getLogin().isEmpty() 
					&& connection.getPassword() != null && !connection.getPassword().isEmpty();
		}

		return false;
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public void createControl(Composite parent) {
		// create main composite & set layout
		mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		// create contents
		connectionComposite = new D4CConnectionComposite(mainComposite, SWT.NONE, this);
		connectionComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		// page setting
		setControl(mainComposite);
		setPageComplete(true);
	}

	public D4CConnection getConnection() {
		return connectionComposite.getConnection();
	}

	public void updateWizardButtons() {
		getContainer().updateButtons();

	}

}
