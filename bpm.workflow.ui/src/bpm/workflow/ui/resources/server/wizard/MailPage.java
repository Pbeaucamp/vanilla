package bpm.workflow.ui.resources.server.wizard;

import java.util.Properties;

import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;

/**
 * Page for the creation of a new mail server
 * @author CHARBONNIER, MARTIN
 *
 */
public class MailPage extends WizardPage {
	private Text smtp;
	private Text login;
	private Text password;
	private Text port;
	private Button testServer;

	private ModifyListener listener = new ModifyListener(){

		public void modifyText(ModifyEvent e) {
			getContainer().updateButtons();
		}
	};

	protected MailPage(String pageName) {
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
		Composite container = new Composite(composite, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Label l0 = new Label(container, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l0.setText(Messages.MailPage_0);
		
		smtp = new Text(container, SWT.BORDER);
		smtp.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		smtp.addModifyListener(listener);
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.MailPage_1);
		
		login = new Text(container, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		login.addModifyListener(listener);
		
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.MailPage_2);
		
		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));		
		password.addModifyListener(listener);
		
		Label lblPort = new Label(container, SWT.NONE);
		lblPort.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblPort.setText(Messages.MailPage_3);
		
		port = new Text(container, SWT.BORDER);
		port.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		port.setText("25"); //$NON-NLS-1$
		port.addModifyListener(listener);
		
		testServer = new Button(composite, SWT.PUSH);
		testServer.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		testServer.setText(Messages.MailPage_5);
		testServer.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					SMTPClient client = new SMTPClient();
				      client.connect( smtp.getText(), Integer.parseInt(port.getText()) );
				      int reply = client.getReplyCode();
				      if( !SMTPReply.isPositiveCompletion( reply ) )
				      {
				        client.disconnect();
				        
				        MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.MailPage_6, Messages.MailPage_7);
						
				      }
				      else{
					MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.MailPage_8, Messages.MailPage_9);
				      }
				}
				catch(Exception eserver){
					MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.MailPage_10, Messages.MailPage_11);
					
				}
			}
			
		});
		
	}




	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		if (!smtp.getText().equals("") && !port.getText().equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		return false;
	}


	public Properties getValues() {
		Properties p = new Properties();
		p.setProperty("url", smtp.getText()); //$NON-NLS-1$
		p.setProperty("username", login.getText()); //$NON-NLS-1$
		p.setProperty("password", password.getText()); //$NON-NLS-1$
		p.setProperty("port", port.getText()); //$NON-NLS-1$
		return p;
	}

	
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	

	
}
