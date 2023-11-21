package bpm.workflow.ui.resources.server.wizard;

import java.io.File;
import java.util.Properties;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;

/**
 * Page for the creation of a file server
 * @author Charles MARTIN
 *
 */
public class FilesServerPage extends WizardPage {
	private Text url;
	private Text folder;
	private Text portNum;
	private Text login;
	private Text password;
	private Combo type;
	private ContentProposalAdapter adapter2;
	private Text keyPath;
	
	private Button testServer;

	private ModifyListener listener = new ModifyListener(){

		public void modifyText(ModifyEvent e) {
			getContainer().updateButtons();
		
		}
	};

	/**
	 * Create a page with the specified name
	 * @param pageName
	 */
	protected FilesServerPage(String pageName) {
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
		
		Label lblType = new Label(container, SWT.NONE);
		lblType.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblType.setText(Messages.FilesServerPage_0);

		
		type = new Combo(container, SWT.READ_ONLY);
		type.setLayoutData(new GridData(GridData.BEGINNING,GridData.CENTER,false, false));
		
		String[] comp = FileServer.SERVERS_TYPES;
 		
 		type.setItems(comp);
 		
 		type.addSelectionListener(adaptder);
		
		
		Label l0 = new Label(container, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l0.setText(Messages.FilesServerPage_1);
		
		
		url = new Text(container, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		url.addModifyListener(listener);
		
		Label lfolder = new Label(container, SWT.NONE);
		lfolder.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lfolder.setText(Messages.FilesServerPage_2);
		
		folder = new Text(container, SWT.BORDER);
		folder.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		folder.addModifyListener(listener);

		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.FilesServerPage_3);
		
		login = new Text(container, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		login.addModifyListener(listener);
		login.setEnabled(false);
		

		
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.FilesServerPage_4);
		
		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));		
		password.addModifyListener(listener);
		password.setEnabled(false);
		
 		
 		Label lblPortNum = new Label(container, SWT.NONE);
 		lblPortNum.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
 		lblPortNum.setText(Messages.FilesServerPage_5);
		
		portNum = new Text(container, SWT.BORDER);
		portNum.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		portNum.addModifyListener(listener);
		portNum.setEnabled(false);
		
		
		
		Label  lblKeyPath = new Label(container, SWT.NONE);
		lblKeyPath.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblKeyPath.setText(Messages.FilesServerPage_6);
		
		keyPath = new Text(container, SWT.BORDER);
		keyPath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		keyPath.addModifyListener(listener);
		keyPath.setEnabled(false);
		
		
		testServer = new Button(composite, SWT.PUSH);
		testServer.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		testServer.setText(Messages.FilesServerPage_7);
		testServer.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					if(type.getText().equalsIgnoreCase(Messages.FilesServerPage_8)){
						String url1 = url.getText();
						if(url1.contains("{$VANILLA_HOME}")){ //$NON-NLS-1$
							MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FilesServerPage_10, Messages.FilesServerPage_11);

						}
						if(url1.contains("{$VANILLA_FILES}")){ //$NON-NLS-1$
							MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FilesServerPage_13, Messages.FilesServerPage_14);


						}
						else{
							StringBuffer cheminbuf = new StringBuffer();
							cheminbuf.append(url1);


							if(folder.getText() != null){
								cheminbuf.append(folder.getText() +"\\"); //$NON-NLS-1$
							}
							String testchemin = cheminbuf.toString();
							File test = new File(testchemin);
							if(test.exists()){
								MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FilesServerPage_17, Messages.FilesServerPage_18);
							}

							else{
								MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FilesServerPage_19, Messages.FilesServerPage_20);

							}
						}

					}
					if(type.getText().equalsIgnoreCase(Messages.FilesServerPage_21)){

						FTPClient ftp = new FTPClient();
						ftp.connect(url.getText());
						ftp.login( login.getText(), password.getText() );
						ftp.setFileType(FTP.BINARY_FILE_TYPE);
						if(folder.getText() != null){
							ftp.changeWorkingDirectory( folder.getText() );
						}
						ftp.logout();
						ftp.disconnect();
						MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FilesServerPage_24, Messages.FilesServerPage_25);

					}
					if(type.getText().equalsIgnoreCase(Messages.FilesServerPage_26)){
						SMTPClient client = new SMTPClient();
						client.connect( url.getText(), Integer.parseInt(portNum.getText()) );
						int reply = client.getReplyCode();
						if(!SMTPReply.isPositiveCompletion(reply)) {
							client.disconnect();

							MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FilesServerPage_27, Messages.FilesServerPage_28);

						}
						else{
							MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FilesServerPage_29, Messages.FilesServerPage_30);
						}
					}




				}
				catch(Exception eserver){
					MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FilesServerPage_31, Messages.FilesServerPage_32);

				}
			}

		});

	}




	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		if (!url.getText().equalsIgnoreCase("") //$NON-NLS-1$
				&& !type.getText().equalsIgnoreCase("")) { //$NON-NLS-1$
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return the values of the new File server : url,username,password,typeserver,repertoiredef,port
	 */
	public Properties getValues() {
		Properties p = new Properties();
		p.setProperty("url", url.getText()); //$NON-NLS-1$
		p.setProperty("username", login.getText()); //$NON-NLS-1$
		p.setProperty("password", password.getText()); //$NON-NLS-1$
		
		
		p.setProperty("typeserver", type.getText()); //$NON-NLS-1$
		
		if(folder.getText() != null){
			p.setProperty("repertoiredef", folder.getText()); //$NON-NLS-1$
		}
		
		if(portNum.getText() != null){
			p.setProperty("port", portNum.getText()); //$NON-NLS-1$
		}
		
		if (keyPath.getText() != null && !keyPath.getText().equalsIgnoreCase("")) { //$NON-NLS-1$
			p.setProperty(FileServer.KEY_PATH, keyPath.getText());
		}

		return p;
	}

	
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	
	SelectionAdapter adaptder = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			if(type.getText().equalsIgnoreCase(FileServer.SERVER_FTP)){ //$NON-NLS-1$
				login.setEnabled(true);
				password.setEnabled(true);
				portNum.setEnabled(true);
				portNum.setText("21"); //$NON-NLS-1$
				adapter2.setContentProposalProvider(new SimpleContentProposalProvider(null));
			}
			else if(type.getText().equalsIgnoreCase(FileServer.SERVER_SFTP)){ 
				login.setEnabled(true);
				password.setEnabled(true);
				portNum.setEnabled(true);
				portNum.setText("22"); //$NON-NLS-1$
				keyPath.setEnabled(true);
				
				adapter2 = new ContentProposalAdapter(
						keyPath, 
						new TextContentAdapter(), 
						new SimpleContentProposalProvider(ListVariable.ENVIRONEMENT_VARIABLE),
						Activator.getDefault().getKeyStroke(), 
						Activator.getDefault().getAutoActivationCharacters());
			}
			else if (type.getText().equalsIgnoreCase(FileServer.SERVER_MAILS)) { //$NON-NLS-1$
				
				login.setEnabled(true);
				password.setEnabled(true);
				portNum.setEnabled(true);
				portNum.setText(""); //$NON-NLS-1$
				adapter2.setContentProposalProvider(new SimpleContentProposalProvider(null));

			}
			else{
				login.setText(""); //$NON-NLS-1$
				password.setText(""); //$NON-NLS-1$
				portNum.setText(""); //$NON-NLS-1$
				
				
				String[] listepro = new String[2];
				int i = 0;
				for(String stringini : ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel())){
					if(stringini.equalsIgnoreCase("{$VANILLA_HOME}")|| stringini.equalsIgnoreCase("{$VANILLA_FILES}")){ //$NON-NLS-1$ //$NON-NLS-2$
						listepro[i] = stringini;
						i++;
					}

				}
				
				adapter2 = new ContentProposalAdapter(
						url, 
						new TextContentAdapter(), 
						new SimpleContentProposalProvider(listepro),
						Activator.getDefault().getKeyStroke(), 
						Activator.getDefault().getAutoActivationCharacters());

				login.setEnabled(false);
				password.setEnabled(false);
				portNum.setEnabled(false);
			}
			
		}
	
	};

	
}
