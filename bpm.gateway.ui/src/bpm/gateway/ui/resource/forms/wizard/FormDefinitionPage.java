package bpm.gateway.ui.resource.forms.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogRepositoryObject;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FormDefinitionPage extends WizardPage {


	private Text repositoryObject, name;
	
	protected RepositoryItem dirIt;

	
	protected FormDefinitionPage(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
		
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl( mainComposite );
		setPageComplete(true);
		
		
		
	}

	
	private Control createPageContent(Composite parent){
		
		Composite  serverC = new Composite(parent, SWT.NONE);
		serverC.setLayout(new GridLayout(3, false));
		serverC.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label l0 = new Label(serverC, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l0.setText(Messages.FormDefinitionPage_0);
		
		name = new Text(serverC, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		


		Label l2 = new Label(serverC, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.FormDefinitionPage_2);
		
		
		repositoryObject = new Text(serverC, SWT.BORDER | SWT.READ_ONLY);
		repositoryObject.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Button bBrowse = new Button(serverC, SWT.PUSH );
		bBrowse.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		bBrowse.setText(Messages.FormDefinitionPage_3);
		bBrowse.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				DialogRepositoryObject d = new DialogRepositoryObject(getShell(), IRepositoryApi.CUST_TYPE, IRepositoryApi.ORBEON_XFORMS);
				
				if (d.open() == DialogRepositoryObject.OK){
					dirIt = d.getRepositoryItem();
					
					repositoryObject.setText(dirIt.getItemName());
				}
				getWizard().getContainer().updateButtons();
			}
			
		});
		
		return serverC;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return (dirIt != null);
	}

	public String getFormName() {
		return name.getText();
	}


	
	
}
