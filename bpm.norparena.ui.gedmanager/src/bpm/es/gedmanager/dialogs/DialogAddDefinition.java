package bpm.es.gedmanager.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.es.gedmanager.Messages;

public class DialogAddDefinition extends Dialog {
	
	private Text tname;
	private String name;
	private Display display;
	
	
	public DialogAddDefinition(Shell parentShell) {
		super(parentShell);
		
		this.display = parentShell.getDisplay();
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));
		
		Label lname = new Label(main, SWT.NONE);
		lname.setText(Messages.DialogAddDefinition_0);
		
		tname = new Text(main, SWT.BORDER);
		tname.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));
		tname.setText(""); //$NON-NLS-1$
		
	
		return main;
	}
	
	protected void okPressed() {	
		name = tname.getText();
		
		super.okPressed();
	}


	public String getName() {
		return name;
	}
	
	
}

