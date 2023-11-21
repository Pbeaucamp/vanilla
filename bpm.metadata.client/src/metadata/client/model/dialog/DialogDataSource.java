package metadata.client.model.dialog;

import metadata.client.i18n.Messages;
import metadata.client.model.composites.CompositeDataSource;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.logical.IDataSource;

public class DialogDataSource extends Dialog {

	
	private IDataSource dataSource;
	private Composite composite;
	
	
	public DialogDataSource(Shell parentShell, IDataSource dataSource) {
		super(parentShell);
		this.dataSource = dataSource;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new CompositeDataSource(parent, SWT.NONE, dataSource);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return parent;
	}


	@Override
	protected void okPressed() {
		((CompositeDataSource)composite).setDataSource();
		super.okPressed();
	}


	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
		getShell().setText(Messages.DialogDataSource_0); //$NON-NLS-1$
	}
	
	
}
