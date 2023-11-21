package metadata.client.scripting;

import metadata.client.i18n.Messages;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.scripting.Variable;

public class DialogVariable extends Dialog{
	
	private CompositeVariable composite;
	private Variable variable;
	
	public DialogVariable(Shell parentShell) {
		super(parentShell);
		this.setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	public DialogVariable(Shell parentShell, Variable variable) {
		this(parentShell);
		this.variable = variable;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		if (variable == null){
			composite = new CompositeVariable(parent,SWT.NONE);
		}
		else{
			composite = new CompositeVariable(parent,SWT.NONE, variable);
		}
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		composite.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event) {
				getButton(IDialogConstants.OK_ID).setEnabled(composite.isFilled());
				
			}
		});
		
		return composite;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogVariable_0);
		getShell().setSize(400, 300);
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
