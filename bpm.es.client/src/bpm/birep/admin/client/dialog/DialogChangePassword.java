package bpm.birep.admin.client.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import adminbirep.Messages;

public class DialogChangePassword extends Dialog {
	
	private String explaination; 
	
	private String password;
	
	private Text password1;
	private Text password2;
	
	public DialogChangePassword(Shell parentShell, String explaination) {
		super(parentShell);
		
		this.explaination = explaination;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label reason = new Label(main, SWT.NONE);
		reason.setText(explaination);
		
		Composite container = new Composite(main, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l1 = new Label(container, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		l1.setText(Messages.DialogChangePassword_0);
		
		password1 = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		//password1.setEchoChar('*');
		
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		l2.setText(Messages.DialogChangePassword_1);
		
		password2 = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		//password2.setEchoChar('*');
		
		return container;
	}

	
//	@Override
//	protected void initializeBounds() {
//		getShell().setSize(800, 600);
//		getShell().setText("Licence expire " + Activator.expirationDate);
//	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Ok", true); //$NON-NLS-1$
	}
	
	@Override
	protected void okPressed() {
		if (password1.getText().equals("") || password2.getText().equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
			MessageDialog.openError(getShell(), Messages.DialogChangePassword_5, Messages.DialogChangePassword_6);
			return;
		}
		else if (!password1.getText().equals(password2.getText())) {
			MessageDialog.openError(getShell(), Messages.DialogChangePassword_7, Messages.DialogChangePassword_8);
			return;
		}
		
		password = password1.getText();
		
		super.okPressed();
	}
	
	public String getPassword() {
		return password;
	}

	
	
}
