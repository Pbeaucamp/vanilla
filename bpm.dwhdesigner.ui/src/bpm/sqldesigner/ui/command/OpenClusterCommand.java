package bpm.sqldesigner.ui.command;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;

import bpm.gateway.core.exception.ServerException;
import bpm.sqldesigner.api.database.DataBaseConnection;
import bpm.sqldesigner.api.database.ExtractData;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.view.TreeView;

public class OpenClusterCommand extends Command {

	private String host;
	private String port;
	private String login;
	private String pass;
	private String dBName;
	private String driver;
	private String fileName;
	private String clusterName;
	private TreeView treeView;
	private boolean canExecute = false;
	private DataBaseConnection dataBaseConnection;
	private DatabaseCluster databaseCluster;

	public OpenClusterCommand(Request renameReq) {
		host = (String) renameReq.getExtendedData().get("host"); //$NON-NLS-1$
		port = (String) renameReq.getExtendedData().get("port"); //$NON-NLS-1$
		login = (String) renameReq.getExtendedData().get("login"); //$NON-NLS-1$
		pass = (String) renameReq.getExtendedData().get("pass"); //$NON-NLS-1$
		dBName = (String) renameReq.getExtendedData().get("dBName"); //$NON-NLS-1$
		driver = (String) renameReq.getExtendedData().get("driver"); //$NON-NLS-1$
		fileName = (String)renameReq.getExtendedData().get("fileName"); //$NON-NLS-1$
		clusterName = (String)renameReq.getExtendedData().get("clusterName"); //$NON-NLS-1$
		treeView = (TreeView) renameReq.getExtendedData().get("workbenchPart"); //$NON-NLS-1$

		dataBaseConnection = new DataBaseConnection();
		dataBaseConnection.setHost(host);
		dataBaseConnection.setPort(port);
		dataBaseConnection.setLogin(login);
		dataBaseConnection.setPassword(pass);
		dataBaseConnection.setDataBaseName(dBName);
		dataBaseConnection.setDriverName(driver);

		if ((databaseCluster = treeView.dbExists(host, port, null)) != null) {
			canExecute = true;
			return;
		}

		try {
			dataBaseConnection.connect();
			canExecute = true;
		} catch (Exception e) {
			e.printStackTrace();
			setLabel(e.getMessage());
			canExecute = false;
		}

	}

	@Override
	public boolean canExecute() {
		return canExecute;
	}
	
	private String errors = null;

	@Override
	public void execute() {
		if (databaseCluster != null)
			return;

		databaseCluster = new DatabaseCluster();
		databaseCluster.setName(clusterName);
		databaseCluster.setFileName(fileName);
		databaseCluster.setDatabaseConnection(dataBaseConnection);
		try {
			ExtractData.extractCatalogs(databaseCluster);
			ExtractData.extractTypes(databaseCluster);
			

			Activator.getDefault().getWorkspace().addDatabaseCluster(databaseCluster);
//			((RequestsView) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(RequestsView.ID)).addTab(databaseCluster);
//			treeView.addDb(databaseCluster);
		} catch (Exception e) {
			e.printStackTrace();
			 errors = e.getMessage();
			 
			 MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.OpenClusterCommand_9, e.getMessage());
			 return;
		}

		
	}

	public String getErrors(){
		return errors;
	}
	
	public DatabaseCluster getDatabaseCluster() {
		return databaseCluster;
	}
}
