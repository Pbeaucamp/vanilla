package bpm.freemetrics.api.manager;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

import bpm.freemetrics.api.manager.routing.DataSourceRouter;

public class DSManager implements IDSManager{

	private BasicDataSource dataSource;

//	private DataSourceRouter dataSourceRouter;
	
	public DSManager(BasicDataSource dataSource) {
		this.dataSource = dataSource;
	}

//	public DSManager(DataSourceRouter dataSourceRouter) {
//		this.dataSourceRouter = dataSourceRouter;
//	}

	public Connection getDefaultConnection() throws SQLException {
		if (dataSource == null){
			return null;
		}
		return dataSource.getConnection() ;//!= null ? dataSource.getConnection() : dataSourceRouter.getConnection();
	}

}