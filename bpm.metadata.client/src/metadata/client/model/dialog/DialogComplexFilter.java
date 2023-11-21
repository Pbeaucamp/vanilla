package metadata.client.model.dialog;

import metadata.client.i18n.Messages;
import metadata.client.model.composites.CompositeComplexFilter;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.resource.ComplexFilter;

public class DialogComplexFilter extends Dialog {

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogComplexFilter_0); //$NON-NLS-1$
	}
	
	private CompositeComplexFilter composite;
	private ComplexFilter filter;
	private IDataStream table;
	private IBusinessTable businessTable;
	
	
	public DialogComplexFilter(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	public DialogComplexFilter(Shell parentShell, ComplexFilter filter) {
		super(parentShell);
		this.filter = filter;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	public DialogComplexFilter(Shell parentShell, IDataStream data) {
		super(parentShell);
		table = data;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	public DialogComplexFilter(Shell parentShell, IBusinessTable businessTable) {
		super(parentShell);
		this.businessTable = businessTable;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		if (table != null){
			composite = new CompositeComplexFilter(parent, SWT.NONE, table);
		}
		else if (businessTable != null){
			composite = new CompositeComplexFilter(parent, SWT.NONE, businessTable);
		}
		else if (filter != null){
			composite = new CompositeComplexFilter(parent, SWT.NONE, null, filter, false);
		}
		else{
			composite = new CompositeComplexFilter(parent, SWT.NONE, false);
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
		filter = composite.getFilter();
		super.okPressed();
	}

	public ComplexFilter getFilter(){
		return filter;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		if (filter == null){
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}
	}
	
	
}
