package metadata.client.model.dialog;

import metadata.client.i18n.Messages;
import metadata.client.model.composites.CompositeLoV;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.resource.ListOfValue;

public class DialogLov extends Dialog {

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogLov_0); //$NON-NLS-1$
	}

	private CompositeLoV composite;
	private ListOfValue lov;
	
	public DialogLov(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.RESIZE | SWT.TITLE);
	}
	
	public DialogLov(Shell parentShell, ListOfValue lov) {
		super(parentShell);
		this.lov = lov;
		setShellStyle(SWT.RESIZE | SWT.TITLE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new CompositeLoV(parent, SWT.NONE, null, false, lov);
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
		composite.setData();
		lov = composite.getLov();
		super.okPressed();
	}

	public ListOfValue getLov(){
		return lov;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		if (lov == null){
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}
		
	}
}
