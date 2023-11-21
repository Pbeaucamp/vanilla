package metadata.client.model.dialog;

import metadata.client.i18n.Messages;
import metadata.client.model.composites.CompositeSqlFilter;

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
import bpm.metadata.resource.SqlQueryFilter;

public class DialogSqlFilter2 extends DialogSqlFilter{
	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogFilter_0); //$NON-NLS-1$
	}

	private CompositeSqlFilter composite;
	
	public DialogSqlFilter2(Shell parentShell) {
		super(parentShell);
		
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	public DialogSqlFilter2(Shell parentShell, IDataStream table) {
		super(parentShell);
		this.table = table;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	public DialogSqlFilter2(Shell parentShell, SqlQueryFilter filter) {
		super(parentShell);
		this.filter = filter;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	public DialogSqlFilter2(Shell parentShell, IBusinessTable businessTable) {
		super(parentShell);
		this.businessTable = businessTable;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	

	@Override
	protected Control createDialogArea(Composite parent) {
		if (businessTable != null){
			composite = new CompositeSqlFilter(parent, SWT.NONE);
		}
		else if ( filter  != null){
			composite = new CompositeSqlFilter(parent, SWT.NONE, null, filter, false);
		}
		else if (table != null){
			composite = new CompositeSqlFilter(parent, SWT.NONE);
		}
		else{
			composite = new CompositeSqlFilter(parent, SWT.NONE, false);
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

	public SqlQueryFilter getFilter(){
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
