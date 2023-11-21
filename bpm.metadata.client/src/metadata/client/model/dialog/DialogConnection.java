package metadata.client.model.dialog;

import metadata.client.i18n.Messages;
import metadata.client.model.composites.CompositeConnectionSql;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.layer.physical.sql.SQLConnectionException;

public class DialogConnection extends Dialog {
	
	private CompositeConnectionSql composite;
	private SQLConnection sock = null;
	private IDataSource dataSource;
	
	public DialogConnection(Shell parentShell, SQLConnection sock) {
		super(parentShell);
		this.sock = sock;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	public DialogConnection(Shell parentShell, IDataSource dataSource) {
		super(parentShell);
		this.dataSource = dataSource;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new CompositeConnectionSql(parent, SWT.NONE, false, sock);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return parent;
	}

	@Override
	protected void okPressed() {
		try {
			composite.setConnection();
			sock = composite.getConnection();
			if (dataSource != null){
				dataSource.addAlternateConnection(sock);
			}
			
			super.okPressed(); 
		} catch (SQLConnectionException e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogConnection_0, e.getMessage()); //$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogConnection_1, e.getMessage()); //$NON-NLS-1$
		}
		
		
	}
	
	public SQLConnection getConnection(){
		return sock;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
	}
}
