package bpm.workflow.ui.resources.server.wizard;

import java.util.Properties;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.workflow.runtime.resources.servers.FactoryServer;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.servers.ListServer;
import bpm.workflow.runtime.resources.servers.Server;
import bpm.workflow.runtime.resources.servers.ServerMail;
import bpm.workflow.ui.Messages;

/**
 * Page for the selection of the type of the new server to create
 * @author CHARBONNIER, MARTIN
 *
 */
public class ServerTypePage extends WizardPage {


	private Button bDataBase;
	private Button bMail;
	private Button bFileServer;
//	private Button bFreemetrics;
	
	private Text name, description;
	
	/**
	 * Create a page with the specified name
	 * @param pageName
	 */
	protected ServerTypePage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		createPageContent(mainComposite);
		setControl( mainComposite );
		setPageComplete(true);
	}

	
	private Control createPageContent(Composite parent){
		Group general = new Group(parent, SWT.NONE);
		general.setLayout(new GridLayout(2, false));
		general.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		general.setText(Messages.ServerTypePage_0);
		
		Label l = new Label(general, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.ServerTypePage_1);
		
		name = new Text(general, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent e) {
				ServerTypePage.this.setMessage(Messages.ServerTypePage_2);
				
			}

			public void focusLost(FocusEvent e) {
				ServerTypePage.this.setMessage(""); //$NON-NLS-1$
				
			}
			
		});
		name.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
				for(Server s : ListServer.getInstance().getServers(getSelectedServerTypeClass())){
					if (name.getText().equals(s.getName())){
						setPageComplete(false);
						setErrorMessage(Messages.ServerTypePage_4);
						break;
					}
					else{
						setErrorMessage(null);
					}
				}
			}
			
		});
		
		Label l2 = new Label(general, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		l2.setText(Messages.ServerTypePage_5);
		
		description = new Text(general, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		description.addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent e) {
				ServerTypePage.this.setMessage(Messages.ServerTypePage_6);
				
			}

			public void focusLost(FocusEvent e) {
				ServerTypePage.this.setMessage(""); //$NON-NLS-1$
				
			}
			
		});
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		
		Group g = new Group(parent, SWT.NONE);
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		g.setText(Messages.ServerTypePage_8);
		
		RadioListener buttonListener = new RadioListener();
		
		bDataBase = new Button(g, SWT.RADIO);
		bDataBase.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bDataBase.setText(Messages.ServerTypePage_9);
		bDataBase.addSelectionListener(buttonListener);
		
//		bFreemetrics = new Button(g, SWT.RADIO);
//		bFreemetrics.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		bFreemetrics.setText(Messages.ServerTypePage_3);
//		bFreemetrics.addSelectionListener(buttonListener);
		
		bMail = new Button(g, SWT.RADIO);
		bMail.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bMail.setText(Messages.ServerTypePage_10);
		bMail.addSelectionListener(buttonListener);
		
		bFileServer = new Button(g, SWT.RADIO);
		bFileServer.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bFileServer.setText(Messages.ServerTypePage_11);
		bFileServer.addSelectionListener(buttonListener);
		

		
		return g;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return !name.getText().equals("") && (bMail.getSelection()  //$NON-NLS-1$
				|| bDataBase.getSelection()
				|| bFileServer.getSelection());

	}


	/**
	 * 
	 * @return a properties object containing the values filled is that page
	 * the Keys are : 
	 *  - name
	 *  - description
	 *  - type
	 */
	protected Properties getValues(){
		Properties p =  new Properties ();
		
		p.setProperty("name", name.getText()); //$NON-NLS-1$
		p.setProperty("description", description.getText()); //$NON-NLS-1$
		

		p.setProperty("type", getType()+ ""); //$NON-NLS-1$ //$NON-NLS-2$
		
		
		return p;
	}


	
	
	
	@Override
	public IWizardPage getNextPage() {

		if (bMail.getSelection()) {
			return getWizard().getPage(ServerWizard.MAIL_SERVER_PAGE_NAME);
		}
		if (bFileServer.getSelection()) {
			return getWizard().getPage(ServerWizard.FILE_SERVER_PAGE_NAME);
		}
		if (bDataBase.getSelection()) {
			return getWizard().getPage(ServerWizard.SQL_CONNECTION_PAGE_NAME);
		}
//		if (bFreemetrics.getSelection()) {
//			return getWizard().getPage(ServerWizard.FREEMETRICS_SERVER_PAGE_NAME);
//		}
		return this;
	}



	protected int getType(){


		if ((bMail.getSelection())){
			return FactoryServer.MAIL_SERVER;
		}
		if ((bFileServer.getSelection())){
			return FactoryServer.FILE_SERVER;
		}

		if ((bDataBase.getSelection())) {
			return FactoryServer.DATABASE_SERVER;
		}
//		if ((bFreemetrics.getSelection())) {
//			return FactoryServer.FREEMETRICS_SERVER;
//		}
		return -1;
	}


	private class RadioListener extends SelectionAdapter{

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			ServerTypePage.this.getContainer().updateButtons();
		}
		
	}
	
	
	private Class getSelectedServerTypeClass(){


		if (bMail.getSelection()) {
			return ServerMail.class;
		}
		if (bFileServer.getSelection()) {
			return FileServer.class;
		}

		return null;
	}
}
