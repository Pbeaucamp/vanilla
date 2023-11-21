package bpm.fd.repository.ui.wizard.pages;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.repository.ui.Activator;
import bpm.fd.repository.ui.Messages;
import bpm.fd.repository.ui.tools.RepositoryConnectionLoader;
import bpm.vanilla.platform.core.IRepositoryApi;


public class PageConnectionDefinition extends WizardPage {

	private Text conName, host, port, login, password, desc;
	private Combo methodAccess;
	private Button test, save;
	
	private IStatus status;
	
	private IRepositoryApi connection;
	
	public PageConnectionDefinition(String pageName) {
		super(pageName);
		status = Status.OK_STATUS;
		
	}
	
	public void setRepositoryConnection(IRepositoryApi connection){
		this.connection = connection;
	}
	public IRepositoryApi getRepositoryConnection(){
		return connection;
	}
	
	public void createControl(Composite parent){
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FillLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl( mainComposite );
		setPageComplete(false);
	}

	public void createPageContent(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.PageConnectionDefinition_0);
		
		conName = new Text(container, SWT.BORDER);
		conName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		conName.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
			
		});
		
		Label l0 = new Label(container, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 2));
		l0.setText(Messages.PageConnectionDefinition_1);
		
		desc = new Text(container, SWT.BORDER | SWT.MULTI);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		
		
			
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.PageConnectionDefinition_2);
		
		methodAccess= new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		methodAccess.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		Label l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.PageConnectionDefinition_3);
		
		host = new Text(container, SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		host.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				connection.setHost(host.getText());
				getContainer().updateButtons();
				
			}
			
		});


		
		Label l4 = new Label(container, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.PageConnectionDefinition_4);
		
		port = new Text(container, SWT.BORDER);
		port.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		port.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				connection.setPort(port.getText());
				getContainer().updateButtons();
				
			}
			
		});


		
		

		Label l6 = new Label(container, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l6.setText(Messages.PageConnectionDefinition_5);
		
		login= new Text(container, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		login.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				connection.setUsername(login.getText());
				getContainer().updateButtons();
				
			}
			
		});
		
		Label l7 = new Label(container, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.PageConnectionDefinition_6);
		
		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		password.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				connection.setPassword(password.getText());
				getContainer().updateButtons();
				
			}
			
		});
		
		Composite buttons = new Composite(container, SWT.NONE);
		buttons.setLayout(new GridLayout(2, true));
		buttons.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		
		test = new Button(buttons, SWT.PUSH);
		test.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		test.setText(Messages.PageConnectionDefinition_7);
		test.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				URL url;
				try {
					url = new URL(host.getText());
					HttpURLConnection sock = (HttpURLConnection) url.openConnection();

					sock.setDoInput(true);
					sock.setDoOutput(true);
					sock.setRequestMethod("GET"); //$NON-NLS-1$
					sock.setRequestProperty("Content-type", "text/xml"); //$NON-NLS-1$ //$NON-NLS-2$
					  //sock.setRequestProperty("data", prepareData());
					
					sock.connect();
					sock.disconnect();
					MessageDialog.openInformation(getShell(), Messages.PageConnectionDefinition_11, Messages.PageConnectionDefinition_12);
				} catch (MalformedURLException e1) {
					MessageDialog.openError(getShell(), Messages.PageConnectionDefinition_13, Messages.PageConnectionDefinition_14);
					e1.printStackTrace();
				} catch (IOException ex) {
					MessageDialog.openError(getShell(), Messages.PageConnectionDefinition_15, Messages.PageConnectionDefinition_16);
					ex.printStackTrace();
				}
				
			}
		});
		
		save = new Button(buttons, SWT.PUSH);
		save.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		save.setText(Messages.PageConnectionDefinition_17);
		save.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				RepositoryConnectionLoader.getConnections().remove(connection.getName());
				connection.setDesc(desc.getText());
				connection.setName(conName.getText());
				connection.setPassword(password.getText());
				connection.setPort(port.getText());
				connection.setUsername(login.getText());
				connection.setHost(host.getText());
				RepositoryConnectionLoader.getConnections().put(connection.getName(), connection);
				
				RepositoryConnectionLoader.save();	
			}
		});
		
	}

	
	/**
	 * fill the informations from the RepositoryConnection Object
	 *
	 */
	public void fillData(){
		conName.setText(connection.getName());
		host.setText(connection.getHost());
		port.setText(String.valueOf(connection.getPort()));
		login.setText(connection.getUsername());
		password.setText(connection.getPassword());
		methodAccess.setText(Messages.PageConnectionDefinition_18);
		
	}
	
	/**
	 * update the repositoryConnection object with the values contained in the page
	 *
	 */
	public void update(){
		connection.setHost(host.getText());
		connection.setName(conName.getText());
		connection.setPassword(password.getText());
		connection.setPort(Integer.valueOf(port.getText()));
		connection.setUsername(login.getText());
		
	}
	
	

	@Override
	public boolean isPageComplete() {
		status = new Status(IStatus.OK, Activator.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
		
		if (host.getText().trim().equals("")){ //$NON-NLS-1$
			//this.setErrorMessage("Host is required.");
			test.setEnabled(false);
			save.setEnabled(false);
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, Messages.PageConnectionDefinition_21, null);
		}
		if (conName.getText().trim().equals("")){ //$NON-NLS-1$
			//this.setErrorMessage("Name is required.");
			test.setEnabled(false);
			save.setEnabled(false);
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, Messages.PageConnectionDefinition_23, null);			
		}
		
		if (login.getText().trim().equals("")){ //$NON-NLS-1$
			//this.setErrorMessage("Login is required.");
			test.setEnabled(false);
			save.setEnabled(false);
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, Messages.PageConnectionDefinition_25, null);
		}
		
		try{
			Integer.valueOf(port.getText());
		}
		catch(NumberFormatException e){
			
			//this.setErrorMessage("Port must be an integer required.");
			test.setEnabled(false);
			save.setEnabled(false);
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, Messages.PageConnectionDefinition_26, null);
		}
		
		if (status.getSeverity() == IStatus.OK){
			test.setEnabled(true);
			save.setEnabled(true);
			setErrorMessage(null);
			return true;
		}
		
		else{
			setErrorMessage(status.getMessage());
			return false;
		}
	
	}


	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
		//return true;
	}

	@Override
	public IWizardPage getPreviousPage() {
//		if (super.getPreviousPage() instanceof PageConnection){
//			((PageConnection)super.getPreviousPage()).refresh();
//		}
		return super.getPreviousPage(); 
	}



		
}
