package bpm.gateway.ui.resource.server.wizard;


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.resource.composite.connection.CompositeConnectionSql;



public class ConnectionPage extends WizardPage {

	protected CompositeConnectionSql compositeConnection;
	protected Listener listener;
	
	protected Group freeMetricComposite;
	protected Text freemetricsLogin, freemetricsPassword;
	
	private Composite mainComposite;
	
	protected ConnectionPage(String pageName) {
		super(pageName);
		
	}


	public void createControl(Composite parent) {
		//create main composite & set layout
		mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl( mainComposite );
		setPageComplete(true);

	}
	
	private void createPageContent(Composite composite){
		compositeConnection = new CompositeConnectionSql(composite, SWT.NONE);
		compositeConnection.setLayoutData(new GridData(GridData.FILL_BOTH));
		listener = new Listener(){

			public void handleEvent(Event event) {
				if (event.widget == compositeConnection){
					getContainer().updateButtons();
				}
				
			}
			
		};
	
		compositeConnection.addListener(SWT.SELECTED, listener);
		compositeConnection.addListener(SWT.Selection, listener);
	}

	
	public void cleanComposite(){
		if (freeMetricComposite != null && !freeMetricComposite.isDisposed()){
			freeMetricComposite.dispose();
		}
	}

	public void createFreemetricsInfo(){
		freeMetricComposite = new Group(mainComposite, SWT.NONE);
		freeMetricComposite.setLayout(new GridLayout(2, false));
		freeMetricComposite.setText(Messages.ConnectionPage_0);
		
		freeMetricComposite.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l = new Label(freeMetricComposite, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.ConnectionPage_1);
		
		freemetricsLogin = new Text(freeMetricComposite, SWT.BORDER);
		freemetricsLogin.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		Label l2 = new Label(freeMetricComposite, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.ConnectionPage_2);
		
		freemetricsPassword = new Text(freeMetricComposite, SWT.BORDER | SWT.PASSWORD);
		freemetricsPassword.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		mainComposite.layout();
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
//		String type = ((ServerTypePage)getWizard().getPage(ServerWizard.GENERAL_PAGE_NAME)).getValues().getProperty("type");
//		
//		if (type.equals(Server.DATABASE_TYPE)){
			return compositeConnection.isFilled();
//		}
//		else{
//			return true;
//		}
		
		
	}

	
	public String getFmLogin(){
		return freemetricsLogin.getText();
	}
	public String getFmPassword(){
		return freemetricsPassword.getText();
	}
	
	protected DataBaseConnection getDataBaseConnection(){
		compositeConnection.performChanges();
		return compositeConnection.getConnection();
	}


	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	

	
}
