package bpm.gateway.ui.resource.server.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.gateway.core.server.database.nosql.mongodb.MongoDbConnection;
import bpm.gateway.ui.resource.composite.connection.MongoDbConnectionComposite;

public class MongoDbConnectionPage extends WizardPage{

	private Composite mainComposite;
	
	private MongoDbConnectionComposite connectionComposite;
	
	public MongoDbConnectionPage(String pageName){
		super(pageName);
	}
	public boolean isPageComplete() {
		MongoDbConnection connection = connectionComposite.getConnection();
		if(connectionComposite.getConnection() != null) {
			return  connection.getHost() != null && !connection.getHost().isEmpty() && connection.getPort() != null
					&& !connection.getHost().isEmpty();
		}
		
		return false;
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	@Override
	public void createControl(Composite parent) {
		//create main composite & set layout
		mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		// create contents
		connectionComposite = new MongoDbConnectionComposite(mainComposite, SWT.NONE, this);
		connectionComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		// page setting
		setControl( mainComposite );
		setPageComplete(true);		
	}

	public MongoDbConnection getConnection() {
		return connectionComposite.getConnection();
	}
	
	public void updateWizardButtons() {
		getContainer().updateButtons();
		
	}

}
