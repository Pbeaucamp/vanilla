package bpm.vanillahub.runtime.managers;

import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public class DatabaseServerManager extends ResourceManager<DatabaseServer> {

	private static final String DATABASE_SERVER_FILE_NAME = "databases.xml";

	public DatabaseServerManager(String filePath) {
		super(filePath, DATABASE_SERVER_FILE_NAME, "Databases");
	}
	
	@Override
	protected void manageResourceForAdd(DatabaseServer resource) { }
	
	@Override
	protected void manageResourceForModification(DatabaseServer newResource, DatabaseServer oldResource) {
		String name = oldResource.getName();
		String driver = oldResource.getDriverJdbc();
		VariableString databaseUrl = oldResource.getDatabaseUrlVS();
		String login = oldResource.getLogin();
		String password = oldResource.getPassword();
		VariableString query = oldResource.getQueryVS();
		
		newResource.updateInfo(name, driver, databaseUrl, login, password, query);
	}
}
