package bpm.sqldesigner.query.services;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.exception.JdbcException;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.Table;

public abstract class AbstractDatabaseService {
	protected DataBaseConnection dbc = null;

	public AbstractDatabaseService(DataBaseConnection dbc) {
		this.dbc = dbc;
	}

	public AbstractDatabaseService(String host, String port, String login,
			String password, String dataBaseName, String driverName,
			String driversPath, String driversXMLFile) {

		dbc = new DataBaseConnection();
		dbc.setHost(host);
		dbc.setPort(port);
		dbc.setLogin(login);
		dbc.setPassword(password);
		dbc.setDataBaseName(dataBaseName);
		dbc.setDriverName(driverName);
//		JdbcConnectionProvider.init(driversPath, driversXMLFile);
	}

	public DataBaseConnection connect(DocumentGateway document) throws ServerException {
		if (!dbc.isOpened()) {
			dbc.connect(document);
		}
		return dbc;
	}

	abstract public List<Table> extractTables() ;

	abstract public List<Column> extractColumns(String tableName);
	public boolean disconnect() {

		boolean b;
		try {
			dbc.disconnect();
			b = true;
		} catch (JdbcException e) {
			b = false;
			e.printStackTrace();
		}
		return b;
	}

	public boolean isConnected() {
		if (dbc != null) {
			return dbc.isOpened();
		} else
			return false;
	}

	public boolean testQuery(String query) {
		Statement stmt = null;
		try {
			stmt = dbc.getSocket(null).createStatement();
			stmt.setMaxRows(1);
			stmt.execute(query);
		} catch (SQLException e) {
			
			if (stmt != null){
				try {
					stmt.close();
				} catch (SQLException e1) {
					
					e1.printStackTrace();
				}
			}
			return false;
		}
		if (stmt != null){
			try {
				stmt.close();
			} catch (SQLException e1) {
				
				e1.printStackTrace();
			}
		}
		return true;
	}
}
