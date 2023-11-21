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

import bpm.metadata.scripting.Script;

public class DialogScript extends Dialog{
	
	private CompositeScript composite;
	private Script script;
	
	public DialogScript(Shell parentShell) {
		super(parentShell);
		this.setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	public DialogScript(Shell parentShell, Script script) {
		this(parentShell);
		this.script = script;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		if (script == null){
			composite = new CompositeScript(parent,SWT.NONE);
		}
		else{
			composite = new CompositeScript(parent,SWT.NONE, script);
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
		getShell().setText(Messages.DialogScript_0);
		getShell().setSize(400, 300);
	}
	
	public Script getScript(){
		return script;
	}
	
	@Override
	protected void okPressed() {
		script = composite.getScript();
		super.okPressed();
	}
}
