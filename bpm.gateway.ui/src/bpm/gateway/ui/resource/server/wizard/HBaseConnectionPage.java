package bpm.gateway.ui.resource.server.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.gateway.core.server.database.nosql.hbase.HBaseConnection;
import bpm.gateway.ui.resource.composite.connection.HBaseConnectionComposite;

public class HBaseConnectionPage extends WizardPage {

	private Composite mainComposite;
	
	private HBaseConnectionComposite connectionComposite;
	
	public HBaseConnectionPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		//create main composite & set layout
		mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		// create contents
		connectionComposite = new HBaseConnectionComposite(mainComposite, SWT.NONE, this);
		connectionComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		// page setting
		setControl( mainComposite );
		setPageComplete(true);
	}
	


	public HBaseConnection getConnection() {
		return connectionComposite.getConnection();
	}
	
	@Override
	public boolean isPageComplete() {
		HBaseConnection connection = connectionComposite.getConnection();
		if(connectionComposite.getConnection() != null) {
			return connection.getName() != null && !connection.getName().isEmpty() 
					&& connection.getConfigurationFileUrl() != null && !connection.getConfigurationFileUrl().isEmpty();
		}
		
		return false;
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	public void updateWizardButtons() {
		getContainer().updateButtons();
		
	}
}
