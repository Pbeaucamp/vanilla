package metadata.client.model.dialog;

import metadata.client.i18n.Messages;
import metadata.client.model.composites.CompositeFilter;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.resource.Filter;

public class DialogFilter extends Dialog {

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogFilter_0); //$NON-NLS-1$
	}

	private CompositeFilter composite;
	private Filter filter;
	private boolean isOnOlap;
	
	public DialogFilter(Shell parentShell, boolean isOnOlap) {
		super(parentShell);
		this.isOnOlap = isOnOlap;
		setShellStyle(SWT.RESIZE | SWT.TITLE);
	}
	
	public DialogFilter(Shell parentShell, Filter filter, boolean isOnOlap) {
		super(parentShell);
		this.isOnOlap = isOnOlap;
		this.filter = filter;
		setShellStyle(SWT.RESIZE | SWT.TITLE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new CompositeFilter(parent, SWT.NONE, null, false, filter, isOnOlap);
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
		filter = composite.getFilter();
		super.okPressed();
	}

	public Filter getFilter(){
		return filter;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		if (filter == null){
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}
		
	}
}
