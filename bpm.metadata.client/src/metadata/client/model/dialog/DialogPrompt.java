package metadata.client.model.dialog;

import metadata.client.i18n.Messages;
import metadata.client.model.composites.CompositePrompt;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.resource.IResource;
import bpm.metadata.resource.Prompt;

public class DialogPrompt extends Dialog {

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogPrompt_0); //$NON-NLS-1$
	}
	
	private CompositePrompt composite;
	private Prompt prompt;
	private boolean edit = false;
	
	private String oldName;
	
	public DialogPrompt(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	public DialogPrompt(Shell parentShell, Prompt prompt) {
		super(parentShell);
		if(prompt != null) {
			edit = true;
			oldName = prompt.getName();
		}
		this.prompt = prompt;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		if (prompt == null){
			composite = new CompositePrompt(parent, SWT.NONE, false);	
		}
		else{
			composite = new CompositePrompt(parent, SWT.NONE, null, prompt, false);	
		}
		
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.addListener(SWT.Selection, new Listener() {
			
			public void handleEvent(Event event) {
				getButton(IDialogConstants.OK_ID).setEnabled(composite.isFilled());
				
			}
		});
		
		return parent;
	}

	@Override
	protected void okPressed() {
		prompt = composite.getPrompt();
		
		if(edit) {
			try {
				IResource res = Activator.getDefault().getModel().getResource(oldName);
				Activator.getDefault().getModel().delResource(res);
				Activator.getDefault().getModel().addResource(prompt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		super.okPressed();
	}

	public Prompt getPrompt(){
		return prompt;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(prompt != null);
	}
}
