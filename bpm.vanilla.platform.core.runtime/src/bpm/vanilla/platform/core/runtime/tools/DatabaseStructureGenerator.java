package bpm.vanilla.platform.core.runtime.tools;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.tools.MetadataLoader;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceFmdt;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class DatabaseStructureGenerator {

	public static boolean testConnection(Datasource datasource) throws Exception {
		try {
			DatasourceJdbc datasourceJdbc = (DatasourceJdbc) datasource.getObject();
			VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(datasourceJdbc);
			ConnectionManager.getInstance().returnJdbcConnection(connection);
		} catch (Exception e) {
			return false;
		}

		return true;
	}
	
	public static List<DatabaseTable> createDatabaseStructure(Datasource datasource, boolean managePostgres) throws Exception {
		if (datasource.getType() == DatasourceType.JDBC) {

			DatasourceJdbc datasourceJdbc = (DatasourceJdbc) datasource.getObject();

			List<DatabaseTable> result = new ArrayList<>();

			VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(datasourceJdbc);

			DatabaseMetaData metadata = connection.getMetaData();
			if (datasourceJdbc.getDriver().equals("org.postgresql.Driver")) {
				ResultSet rs = metadata.getTables(connection.getCatalog(), "public", "%", new String[] { "TABLE" });
				while (rs.next()) {
					String schemaName = rs.getString(2);
					String tableName = rs.getString(3);
					
					DatabaseTable table = new DatabaseTable();
					if (managePostgres) {
						table.setName(tableName);
					}
					else {
						table.setName(schemaName + "." + tableName);
					}

					ResultSet colRs = metadata.getColumns(connection.getCatalog(), "public", tableName, "%");
					while (colRs.next()) {
						DatabaseColumn col = new DatabaseColumn();
//						if (managePostgres) {
							col.setName(colRs.getString(4));
//						}
//						else {
//							col.setName(colRs.getString(2) + "." + colRs.getString(4));
//						}
						col.setType(colRs.getString(5));
						col.setParentSQLOriginName(table.getName());
						table.addColumn(col);
					}
					result.add(table);

					colRs.close();
				}
				rs.close();
				ConnectionManager.getInstance().returnJdbcConnection(connection);
			}
			else {
				
				ResultSet schemas = metadata.getSchemas();
				boolean hasSchema = false;
					
				while(schemas.next()){
					hasSchema = true;
					String schemaName = schemas.getString("TABLE_SCHEM");
//					System.out.println("Found schema " + schemaName);
					ResultSet rs = metadata.getTables(connection.getCatalog(), schemaName, "%", new String[]{"TABLE"});
					while (rs.next()) {
//						String schemaName = rs.getString(2);
						String tableName = rs.getString(3);
//						System.out.println("Found tableName " + tableName);
						DatabaseTable table = new DatabaseTable();
						if (managePostgres) {
							table.setName(tableName);
						}
						else {
							table.setName(schemaName != null ? schemaName + "." + tableName : tableName);
						}
	
						ResultSet colRs = metadata.getColumns(connection.getCatalog(), null, tableName, "%");
						while (colRs.next()) {
							DatabaseColumn col = new DatabaseColumn();
							col.setName(colRs.getString(4));
							col.setType(colRs.getString(5));
							table.addColumn(col);
						}
						result.add(table);
	
						colRs.close();
					}
					rs.close();
					
					
				}
				if(hasSchema) {
					ConnectionManager.getInstance().returnJdbcConnection(connection);
				}
				else {
					ResultSet rs = metadata.getTables(connection.getCatalog(), null, "%", new String[]{"TABLE"});
					while (rs.next()) {
						String schemaName = rs.getString(2);
						String tableName = rs.getString(3);
						
//						System.out.println("No schema " + schemaName);
//						System.out.println("No schema " + tableName);
						
						DatabaseTable table = new DatabaseTable();
						if (managePostgres) {
							table.setName(tableName);
						}
						else {
							table.setName(schemaName != null ? schemaName + "." + tableName : tableName);
						}
	
						ResultSet colRs = metadata.getColumns(connection.getCatalog(), null, tableName, "%");
						while (colRs.next()) {
							DatabaseColumn col = new DatabaseColumn();
							col.setName(colRs.getString(4));
							col.setType(colRs.getString(5));
							table.addColumn(col);
						}
						result.add(table);
	
						colRs.close();
					}
					rs.close();
					ConnectionManager.getInstance().returnJdbcConnection(connection);
				}
			}

			return result;
		}
		else {
			return createFmdtStructure(datasource);
		}
	}

	private static List<DatabaseTable> createFmdtStructure(Datasource datasource) throws Exception {

		DatasourceFmdt datasourceFmdt = (DatasourceFmdt) datasource.getObject();

		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), datasourceFmdt.getUser(), datasourceFmdt.getPassword());

		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(datasourceFmdt.getGroupId());

		IBusinessPackage pack = MetadataLoader.loadMetadata(datasourceFmdt);

		List<DatabaseTable> result = new ArrayList<>();

		for (IBusinessTable table : pack.getBusinessTables(group.getName())) {

			DatabaseTable t = new DatabaseTable();
			t.setName(table.getName());

			for (IDataStreamElement elem : table.getColumns(group.getName())) {
				DatabaseColumn col = new DatabaseColumn();
				col.setName(elem.getName());
				col.setType(elem.getJavaClassName());
				t.addColumn(col);
			}

			result.add(t);

		}

		return result;
	}

}
