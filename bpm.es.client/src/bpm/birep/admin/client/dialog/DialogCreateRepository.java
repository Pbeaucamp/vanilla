package bpm.birep.admin.client.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import adminbirep.Messages;
import bpm.vanilla.platform.core.beans.Repository;




public class DialogCreateRepository extends Dialog {

	private Composite composite ;
	private Text nametext;
	private Text urltext;
	private Text societetext;
	private Text keytext;
	private Repository repository;

	
	public DialogCreateRepository(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(500, 270);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));
		getShell().setText(Messages.DialogCreateRepository_0);
		
		
		Label nl = new Label(composite, SWT.NONE);
		nl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		nl.setText(Messages.DialogCreateRepository_1);
		
		nametext  = new Text(composite, SWT.BORDER);
		nametext.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			
		Label sl = new Label(composite, SWT.NONE);
		sl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		sl.setText(Messages.DialogCreateRepository_2);
		
		societetext  = new Text(composite, SWT.BORDER);
		societetext.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label ul = new Label(composite, SWT.NONE);
		ul.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		ul.setText(Messages.DialogCreateRepository_3);
		
		urltext  = new Text(composite, SWT.BORDER);
		urltext.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label kl = new Label(composite, SWT.NONE);
		kl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		kl.setText(Messages.DialogCreateRepository_4);
		
		keytext= new Text(composite, SWT.BORDER);
		keytext.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
	
		
		return composite;
		
	}
	
	public Repository getRepositoryDefinition(){
		return repository;
	}
	

	@Override
	protected void okPressed() {
		
		repository = new Repository();
		repository.setName(nametext.getText());
		repository.setSociete(societetext.getText());
		repository.setUrl(urltext.getText());
		repository.setKey(keytext.getText());
		super.okPressed();
	}
	
}
