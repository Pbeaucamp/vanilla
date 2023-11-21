package bpm.birep.admin.client.dialog;

import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
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
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.utils.MD5Helper;

public class DialogUpdatePass extends Dialog {

	private Composite composite ;
	private Text passwordText, confirmText;
	private User user;
	
	public DialogUpdatePass(Shell parentShell, User user) {
		super(parentShell);
		this.user = user;		
	}
	
	
	protected void initializeBounds() {
		getShell().setSize(500, 270);
	}

	protected Control createDialogArea(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));
		getShell().setText(Messages.DialogUpdatePass_0);
		
		Label pl = new Label(composite, SWT.NONE);
		pl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		pl.setText(Messages.DialogUpdatePass_1);
		
		passwordText  = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		passwordText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label cl = new Label(composite, SWT.NONE);
		cl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		cl.setText(Messages.DialogUpdatePass_2);
		
		confirmText  = new Text(composite, SWT.BORDER| SWT.PASSWORD);
		confirmText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		return composite;
		
	}

	public User getUserUpdate(){
		user.setPasswordEncrypted(true);
		return user;
	}
	
	
	@Override
	protected void okPressed() {
		
	if (passwordText.getText().equals(confirmText.getText())){
		user.setPassword(MD5Helper.encode(passwordText.getText()));
		user.setDatePasswordModification(new Date());
	}
	else{
		MessageDialog.openError(getShell(), Messages.DialogUpdatePass_3, Messages.DialogUpdatePass_4);
	}
		
		super.okPressed();
	}
	
}
