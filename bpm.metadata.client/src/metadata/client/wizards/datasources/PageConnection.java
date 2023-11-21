package metadata.client.wizards.datasources;

import metadata.client.i18n.Messages;
import metadata.client.model.composites.CompositeConnectionSql;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bpm.metadata.layer.logical.sql.FactorySQLDataSource;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.layer.physical.sql.SQLConnectionException;

public class PageConnection extends WizardPage {

	protected CompositeConnectionSql compositeConnection;
	protected Listener listener;
	
	protected PageConnection(String pageName) {
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
		compositeConnection = new CompositeConnectionSql(composite, SWT.NONE, true);
		compositeConnection.setLayoutData(new GridData(GridData.FILL_BOTH));
		compositeConnection.setLayout(new GridLayout());
		listener = new Listener(){


			public void handleEvent(Event event) {
				if (event.widget == compositeConnection){
					getContainer().updateButtons();
				}
				
			}
			
		};
		
		compositeConnection.addListener(SWT.SELECTED, listener);
	}


	@Override
	public boolean canFlipToNextPage() {
		return compositeConnection.isFilled();
	}

	@Override
	public IWizardPage getNextPage() {
		try {
			if (compositeConnection.isFilled()){
				compositeConnection.setConnection();
				compositeConnection.getConnection().setName("Default"); //$NON-NLS-1$
				compositeConnection.getConnection().test();
				if (((DataSourceWizard)getWizard()).dataSource == null){
					((DataSourceWizard)getWizard()).dataSource = FactorySQLDataSource.getInstance().createDataSource(compositeConnection.getConnection());
				}
				else{
					((SQLDataSource)((DataSourceWizard)getWizard()).dataSource).setConnection(0, compositeConnection.getConnection());
					
				}
				
				if (getWizard() instanceof DataSourceWizard){
					((SQLDataSource)((DataSourceWizard)getWizard()).dataSource).securizeConnection(compositeConnection.getConnection().getName(), ((DataSourceWizard)getWizard()).groups);	
				}
				
				
				((DataSourceWizard)getWizard()).dataSource.setName(compositeConnection.getConnectionName());
				((DataSourceWizard)getWizard()).selectionPage.createModel(((DataSourceWizard)getWizard()).dataSource);
				return super.getNextPage();
			}else{
				MessageDialog.openInformation(getShell(), Messages.PageConnection_1, Messages.PageConnection_2); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
		} catch (SQLConnectionException e) {
			Activator.getLogger().error(e.getMessage(), e);
			MessageDialog.openError(getShell(), Messages.PageConnection_3, e.getMessage()); //$NON-NLS-1$
			
		} catch (Exception e) {
			Activator.getLogger().error(e.getMessage(), e);
			MessageDialog.openError(getShell(), Messages.PageConnection_4, e.getMessage()); //$NON-NLS-1$
		}
		return this;
	}

	@Override
	protected boolean isCurrentPage() {
		return super.isCurrentPage();
	}


	/**
	 * fill the compiste with the given connection
	 * @param con
	 */
	protected void setConnection(SQLConnection con) {
		try {
			compositeConnection.setConnection(con);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	

}
