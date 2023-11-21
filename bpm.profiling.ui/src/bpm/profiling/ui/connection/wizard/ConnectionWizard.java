package bpm.profiling.ui.connection.wizard;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.profiling.runtime.core.Connection;
import bpm.profiling.ui.Activator;

public class ConnectionWizard extends Wizard implements INewWizard {

	

	private DefinitionTypePage typePage;
	private JdbcPage jdbcPage;
	private FmdtPage fmdtPage;
	protected Connection connection;
	
	public ConnectionWizard() {
		
	}

	@Override
	public boolean performFinish() {
		if (jdbcPage.isCurrentPage()){
			connection = jdbcPage.getConnection();
			try {
				connection.connect();
				Activator.helper.getConnectionManager().addConnection(connection);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		else if (fmdtPage.isCurrentPage()){
			try {
				
				connection = buildFromFmdt(fmdtPage.getFmdtDataSource(), fmdtPage.getFmdtConnection());
				
				String urlBakcup = connection.getHost();
				String loginBakcup = connection.getLogin();
				String pwdBakcup = connection.getPassword();
				
				fmdtPage.setConnection(connection);
				Activator.helper.getConnectionManager().addConnection(connection);
				connection.setHost(urlBakcup);
				connection.setLogin(loginBakcup);
				connection.setPassword(pwdBakcup);
				
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), "Unable to build Connection from fmdt", e.getMessage());
				return false;
			}
		}
		return true;
	}

	

	public Connection getConnection(){
		return connection;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		

	}

	@Override
	public void addPages() {
		typePage = new DefinitionTypePage("Connection Origin");
		typePage.setTitle("");
		typePage.setDescription(""); 
		addPage(typePage);
		
		jdbcPage = new JdbcPage("Jdbc Connection"); 
		jdbcPage.setTitle("Jdbc Connection"); 
		jdbcPage.setDescription("Define the COnnection  parameters"); 
		addPage(jdbcPage);
		
		fmdtPage = new FmdtPage("Choose Fmdt"); 
		fmdtPage.setTitle("Choose Fmdt"); 
		fmdtPage.setDescription("Choose Fmdt for the repository"); 
		addPage(fmdtPage);


	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == typePage){
			if (typePage.getType() == DefinitionTypePage.USER_DEFINED){
				return jdbcPage;
			}
			else{
				return fmdtPage;
			}
		}
		if (page == jdbcPage || page == fmdtPage){
			return null;
		}
			return super.getNextPage(page);
	}
	
	
	private Connection buildFromFmdt(IDataSource fmdtDataSource,
			IConnection fmdtConnection) throws Exception{
		
		SQLConnection sqlCon = (SQLConnection)fmdtConnection;
		SQLDataSource ds = (SQLDataSource)fmdtDataSource;
		
		Connection con = new Connection();
//		con.setDatabaseName(sqlCon.getDataBaseName());
//		con.setDriverName(sqlCon.getDriverName());
//		con.setHost(sqlCon.getHost());
//		con.setLogin(sqlCon.getUsername());
//		con.setName(sqlCon.getName());
//		con.setPassword(sqlCon.getPassword());
//		con.setPort(sqlCon.getPortNumber());
//		con.setSchemaName(sqlCon.getSchemaName());
		
		
		con.connect(ds);
		
		con.setDriverName(sqlCon.getDriverName());
		con.setHost(sqlCon.getHost());
		con.setPort(sqlCon.getPortNumber());
		con.setPassword(sqlCon.getPassword());
		con.setLogin(sqlCon.getUsername());
		con.setDatabaseName(sqlCon.getDataBaseName());
		con.setName(ds.getName());
		return con;
	}
	
	@Override
	public boolean canFinish() {
		if (fmdtPage.isCurrentPage() && fmdtPage.isPageComplete()){
			return true;
		}
		if (jdbcPage.isCurrentPage() && jdbcPage.isPageComplete()){
			return true;
		}
		return super.canFinish();
	}

}
