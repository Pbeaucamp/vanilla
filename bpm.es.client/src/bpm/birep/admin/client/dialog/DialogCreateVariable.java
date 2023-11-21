package bpm.birep.admin.client.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import adminbirep.Messages;
import bpm.birep.admin.client.composites.CompositeVariable;
import bpm.vanilla.platform.core.beans.Variable;

public class DialogCreateVariable extends Dialog {

	private CompositeVariable composite ;
	
	private Variable variable;

	
	public DialogCreateVariable(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(500, 270);
		getShell().setText(Messages.DialogCreateVariable_0);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new CompositeVariable(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));
	
		return composite;
		
	}
	
	public Variable getVariable(){
		return variable;
	}
	

	@Override
	protected void okPressed() {
		variable = composite.getVariable();
		super.okPressed();
	}
	
}


