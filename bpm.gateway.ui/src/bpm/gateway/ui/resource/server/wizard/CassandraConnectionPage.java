package bpm.gateway.ui.resource.server.wizard;


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.gateway.core.server.database.nosql.cassandra.CassandraConnection;
import bpm.gateway.ui.resource.composite.connection.CassandraConnectionComposite;



public class CassandraConnectionPage extends WizardPage {

	private Composite mainComposite;
	
	private CassandraConnectionComposite connectionComposite;

	protected CassandraConnectionPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		//create main composite & set layout
		mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(3,false));
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		connectionComposite = new CassandraConnectionComposite(mainComposite, SWT.NONE, this);
		connectionComposite.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		// page setting
		setControl( mainComposite );
		setPageComplete(true);

	}
	
	public CassandraConnection getConnection() {
		return connectionComposite.getConnection();
	}

	@Override
	public boolean isPageComplete() {
		CassandraConnection connection = connectionComposite.getConnection();
		if(connection != null){
			return connection.getName() != null && !connection.getName().isEmpty() 
					&& connection.getHost() != null && !connection.getHost().isEmpty() 
					&& connection.getPort() != null && !connection.getPort().isEmpty() 
					&& connection.getKeyspace() != null && !connection.getKeyspace().isEmpty();
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
