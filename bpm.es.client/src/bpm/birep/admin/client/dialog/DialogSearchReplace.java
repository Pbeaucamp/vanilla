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

abstract class DialogSearchReplace extends Dialog {

	private Composite composite ;
	protected Text searchString, newString;
			
	public DialogSearchReplace(Shell parentShell) {
		super(parentShell);	
	}
	
	
	protected void initializeBounds() {
		getShell().setSize(500, 270);
	}

	protected Control createDialogArea(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));
		getShell().setText(Messages.DialogSearchReplace_0);
		
		Label pl = new Label(composite, SWT.NONE);
		pl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		pl.setText(Messages.DialogSearchReplace_1);
		
		searchString  = new Text(composite, SWT.BORDER);
		searchString.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label cl = new Label(composite, SWT.NONE);
		cl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		cl.setText(Messages.DialogSearchReplace_2);
		
		newString  = new Text(composite, SWT.BORDER);
		newString.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
		return composite;
		
	}
	
	
	@Override
	protected void okPressed() {
		super.okPressed();
	}
	
}