package bpm.gateway.ui.dialogs.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.ITargetableStream;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.ui.i18n.Messages;

public class DialogSqlScript extends Dialog{

	private String sqlScript;
	private Text sqlArea;
	private Shell parentShell;
	private ITargetableStream transformation;
	
	protected DialogSqlScript(Shell parentShell, String sqlScript, ITargetableStream transformation) {
		super(parentShell);
		this.parentShell = parentShell;
		this.transformation = transformation;
		this.sqlScript = sqlScript;
		this.parentShell.setEnabled(false);
		
		setShellStyle(SWT.RESIZE | SWT.CLOSE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText(Messages.DBCreationDialog_8);
		
		sqlArea = new Text(container, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		GridData gd_sqlArea = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_sqlArea.heightHint = 200;
		gd_sqlArea.widthHint = 300;
		sqlArea.setLayoutData(gd_sqlArea);
		sqlArea.setText(sqlScript);
		
		return parent;
	}
	
	@Override
	protected void okPressed() {
		boolean executed = false;
		
		Connection con = null;
		Statement stmt = null;
		
		try {
			con = ((DataBaseConnection)transformation.getServer().getCurrentConnection(null)).getSocket(transformation.getDocument());
			stmt = con.createStatement();
			stmt.execute(sqlArea.getText());
				
			executed = true;
			super.okPressed();
			MessageDialog.openInformation(getShell(), Messages.DialogDatabaseTableCreator_23, Messages.DialogDatabaseTableCreator_24);
			this.parentShell.setEnabled(true);
		} catch (SQLException e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogDatabaseTableCreator_25, e.getMessage());
			this.parentShell.setEnabled(true);
		}	
		
		finally{
			if (stmt != null){
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(executed){
			parentShell.close();
		}
		
	}
	
	@Override
	protected void cancelPressed() {
		
		super.cancelPressed();
		this.parentShell.setEnabled(true);
	}
}
