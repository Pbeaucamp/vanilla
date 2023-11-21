package bpm.gateway.core.server.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.Activator;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.ISCD;
import bpm.gateway.core.ITargetableStream;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.gid.Query;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;
import bpm.gateway.core.transformations.inputs.NagiosDb;
import bpm.gateway.core.transformations.nosql.SqoopTransformation;
import bpm.gateway.core.transformations.outputs.DataBaseOutputStream;
import bpm.vanilla.platform.core.IVanillaContext;

public class DataBaseHelper {
	/**
	 * this method return a Descriptor for the given InputStream
	 * 
	 * @param is
	 * @return
	 * @throws ServerException
	 */
	public static StreamDescriptor getDescriptor(DataStream is) throws Exception {

		if(!(is instanceof DataBaseInputStream || is instanceof DataBaseOutputStream 
				|| is instanceof ISCD || is instanceof NagiosDb || is instanceof SqoopTransformation)) {
			throw new ServerException(is.getClass().getName() + " not valid type for a DataBase Server", is.getServer());
		}

		if(is.getServer() == null) {
			return is.getDescriptor(null);
		}

		DataBaseConnection con = (DataBaseConnection) ((DataBaseServer) is.getServer()).getCurrentConnection(null);
		boolean conWasConnected = con.isOpened();
		if(!conWasConnected) {
			con.connect(is.getDocument());
		}
		
		String query;
		try {
			if(is.getDocument() != null) {
				query = new String(is.getDocument().getStringParser().getValue(is.getDocument(), is.getDefinition()));
			}
			else {
				// Means that the document is not initialized yet
				return is.getDescriptor(null);
			}
		} catch(Exception e1) {
			throw new Exception("Unable to parse query:" + is.getDefinition() + ", reason : " + e1.getMessage(), e1);
		}
//		if(query.toLowerCase().contains(" where ")) {
//			query = query.substring(0, query.toLowerCase().indexOf(" where ") + 7) + " 1=0 AND " + query.substring(query.toLowerCase().indexOf(" where ") + 7);
//
//		}
//		else {
//			boolean containsGroupBy = query.toLowerCase().indexOf(" group by ") != -1;
//			boolean orderGroupBy = query.toLowerCase().indexOf(" order by ") != -1;
//			boolean islimit = query.toLowerCase().indexOf(" limit ") != -1;
//
//			if(containsGroupBy) {
//				query = query.substring(0, query.toLowerCase().indexOf(" group by ")) + " where 1=0 " + query.substring(query.toLowerCase().indexOf(" group by "));
//			}
//			else if(orderGroupBy) {
//				query = query.substring(0, query.toLowerCase().indexOf(" order by ")) + " where 1=0 " + query.substring(query.toLowerCase().indexOf(" order by "));
//			}
//			else if(islimit) {
//				query = query.substring(0, query.toLowerCase().indexOf(" limit ")) + " where 1=0 " + query.substring(query.toLowerCase().indexOf(" limit "));
//			}
//			else {
//				query += " where 1=0";
//			}
//
//		}
		
		try {
			if(query.toLowerCase().indexOf("where ") != -1 && con.getFullUrl() == null || !con.getFullUrl().startsWith("jdbc:drill:zk")) {
				query = query.replaceAll("(?i)where", "WHERE 1=0 and ");
			}
			else if(con.getFullUrl() == null || !con.getFullUrl().startsWith("jdbc:drill:zk")) {
				query += " where 1=0";
			}
		} catch(Exception e) {
			
		}

		DefaultStreamDescriptor dsd = null;
		ResultSetMetaData rsmd = null;

		Connection sock = con.getSocket(is.getDocument());
		
		try {
//			query = query.replace("\\t", " ");
			//XXX
			PreparedStatement prepStat = sock.prepareStatement(query);
			try {
				if(con.getFullUrl() != null && con.getFullUrl().startsWith("jdbc:drill:zk")) {
					throw new Exception("Drill needs to execute the query to get the metadata but doesn't throw an exception");
				}
				rsmd = prepStat.getMetaData();
			} catch (Exception e2) {
				ResultSet rs = prepStat.executeQuery();
				rsmd = rs.getMetaData();
			}

			dsd = new DefaultStreamDescriptor();

			for(int i = 1; i <= rsmd.getColumnCount(); i++) {

				StreamElement e = new StreamElement();
				try {
					e.className = rsmd.getColumnClassName(i);
				} catch (Exception e1) {		
					e.className = getClassNameForBadJdbcDrivers(rsmd.getColumnType(i));
				}
				e.name = rsmd.getColumnLabel(i);
				try {
					e.tableName = rsmd.getTableName(i);
				} catch (Exception e1) {
				}
				e.transfoName = is.getName();
				e.originTransfo = is.getName();
				e.typeName = rsmd.getColumnTypeName(i);
				e.precision = rsmd.getPrecision(i);
				e.decimal = rsmd.getScale(i);

				try {
					getTableMetadata(sock, e);
				} catch(Exception exs) {

				}

				dsd.addColumn(e);
			}

			try {
				if(prepStat != null) {
					prepStat.close();
				}
			} catch(SQLException e1) {
				e1.printStackTrace();
			}

			if(conWasConnected) {
				con.disconnect();
			}
			
			return dsd;
		} catch (Exception e) {
			e.printStackTrace();
			Activator.getLogger().error("Unable to prepare statement, executing the all query.");
		}
		
		Statement stmt = null;
		ResultSet rs = null;
		Exception thrown = null;
		dsd = null;
		rsmd = null;
		
		try {

			stmt = sock.createStatement();
			stmt.setMaxRows(1);
			try {
				stmt.setFetchSize(1);
			} catch(Exception ex) {
				Activator.getLogger().warn("unable to set fetchsize - " + ex.getMessage(), ex);
			}

			/*
			 * we modify the query execute it faster as possible because all we want is the MetaData Information
			 */

			if(is.getDefinition() == null) {
				return is.getDescriptor(null);
			}

			rs = stmt.executeQuery(query);
			rsmd = rs.getMetaData();

			/*
			 * we create the DataStreamDescriptor
			 */
			dsd = new DefaultStreamDescriptor();

			for(int i = 1; i <= rsmd.getColumnCount(); i++) {

				StreamElement e = new StreamElement();
				e.className = rsmd.getColumnClassName(i);
				e.name = rsmd.getColumnLabel(i);
				e.tableName = rsmd.getTableName(i);
				e.transfoName = is.getName();
				e.typeName = rsmd.getColumnTypeName(i);
				e.precision = rsmd.getPrecision(i);
				e.decimal = rsmd.getScale(i);

				try {
					getTableMetadata(sock, e);
				} catch(Exception exs) {

				}

				dsd.addColumn(e);

			}

		} catch(SQLException e) {
			thrown = e;
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}

				if(stmt != null) {
					stmt.close();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}

			if(conWasConnected) {
				con.disconnect();
			}

		}

		if(thrown != null) {
			throw new ServerException("Error while getting DataStreamDescriptor for " + is.getName(), thrown, is.getServer());
		}

		return dsd;
	}

	private static String getClassNameForBadJdbcDrivers(int columnType) {
		switch(columnType){
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			return "java.lang.String";
		
		case Types.NUMERIC:
		case Types.DECIMAL:
			return "java.math.BigDecimal";
			
		case Types.BIT:
			return "java.lang.Boolean";
			
		case Types.TINYINT:
		case Types.SMALLINT:
		case Types.INTEGER:
			return "java.lang.Integer";
			
		case Types.BIGINT:
			return "java.lang.Long";
			
		case Types.REAL:
			return "java.lang.Float";
			
		case Types.FLOAT:
		case Types.DOUBLE:
			return "java.lang.Double";
			
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:	
		case Types.DATE:
			return "java.sql.Date";
			
		case Types.TIME:
			return "java.sql.Time";
			
		case Types.TIMESTAMP:
			return "java.sql.Timestamp";
			
								
		}
		return null;
	}

	private static void getTableMetadata(Connection sock, StreamElement e) throws SQLException {

		ResultSet rs = null;
		DatabaseMetaData metaData = sock.getMetaData();

		rs = metaData.getColumns(null, "%", e.tableName, e.name);
		while(rs.next()) {

			e.defaultValue = rs.getString("COLUMN_DEF");
			e.isNullable = rs.getBoolean("IS_NULLABLE");
			e.type = rs.getInt("DATA_TYPE");
			e.typeName = rs.getString("TYPE_NAME");
			e.size = rs.getInt("COLUMN_SIZE");

		}
		rs.close();

		rs = metaData.getPrimaryKeys(null, null, e.tableName);
		while(rs.next()) {
			if(e.name.equals(rs.getString("COLUMN_NAME"))) {
				e.isPrimaryKey = true;
			}

		}
		rs.close();

	}

	/**
	 * truncate the given ITargetableStream
	 * 
	 * @param os
	 * @throws Exception
	 */
	public static void truncate(ITargetableStream os) throws Exception {
		DataBaseConnection con = (DataBaseConnection) ((DataBaseServer) os.getServer()).getCurrentConnection(null);
		boolean conWasConnected = con.isOpened();
		if(!conWasConnected) {
			con.connect(os.getDocument());
		}

		Connection sock = con.getSocket(os.getDocument());
		Statement stmt = null;

		stmt = sock.createStatement();
		stmt.execute("truncate table " + os.getTableName());
		stmt.close();

	}

	/**
	 * 
	 * @param ds
	 * @param rowNumbers
	 * @return execute the query
	 * @throws Exception
	 */
	public static List<List<Object>> getValues(DataStream ds, int rowNumbers) throws Exception {

		DataBaseConnection con = (DataBaseConnection) ds.getServer().getCurrentConnection(null);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			if(!con.isOpened()) {
				con.connect(ds.getDocument());
			}

			stmt = con.getSocket(ds.getDocument()).createStatement();
			stmt.setMaxRows(rowNumbers);
			rs = stmt.executeQuery(ds.getDocument().getStringParser().getValue(ds.getDocument(), ds.getDefinition()));

			List<List<Object>> values = new ArrayList<List<Object>>();

			while(rs.next()) {
				List<Object> row = new ArrayList<Object>();

				for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					try {
						row.add(rs.getObject(i));
					} catch (NoClassDefFoundError e) {
						row.add(rs.getString(i));
					}

				}

				values.add(row);
			}
			rs.close();
			stmt.close();
			return values;
		} catch(Exception e) {
			if(rs != null) {
				rs.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			throw e;
		}

	}

	public static List<List<Object>> getCountDistinctFieldValues(IVanillaContext vanillaCtx, StreamElement field, DataStream ds) throws Exception {

		DataBaseConnection con = (DataBaseConnection) ds.getServer().getCurrentConnection(null);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			if(!con.isOpened()) {
				con.connect(ds.getDocument());
			}

			stmt = con.getSocket(ds.getDocument()).createStatement();

			String s = ds.getDocument().getStringParser().getValue(ds.getDocument(), ds.getDefinition());
			int stop = s.toLowerCase().indexOf("from ");
			int stop2 = s.toLowerCase().indexOf(" group by ");
			if(stop2 == -1) {
				stop2 = s.toLowerCase().indexOf(" order by ");
			}
			if(stop2 == -1) {
				stop2 = s.toLowerCase().indexOf(" union ");
			}
			if(stop2 != -1) {
				s = "select " + field.name + ", count(*) " + s.substring(stop, stop2) + " group by " + field.name;
			}
			else {
				s = "select " + field.name + ", count(*) " + s.substring(stop) + " group by " + field.name;
			}

			rs = stmt.executeQuery(s);

			List<List<Object>> values = new ArrayList<List<Object>>();

			while(rs.next()) {
				List<Object> row = new ArrayList<Object>();

				for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					row.add(rs.getObject(i));

				}

				values.add(row);
			}
			return values;
		} catch(Exception e) {
			if(rs != null) {
				rs.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			throw e;
		}

	}

	/**
	 * Helper method to analyse DBContent
	 * 
	 * @param sock
	 * @return
	 * @throws Exception
	 */
	public static List<DBSchema> getDataBaseSchemas(DataBaseConnection sock, DocumentGateway document) throws Exception {
		List<DBSchema> schemas = new ArrayList<DBSchema>();

		Connection con = sock.getSocket(document);
		ResultSet rs = null;
		Exception ex = null;
		try {
			DatabaseMetaData dbMd = con.getMetaData();

			rs = dbMd.getSchemas();
			while(rs.next()) {
				schemas.add(new DBSchema(rs.getString("TABLE_SCHEM"), false));
			}

			if(schemas.isEmpty()) {
				schemas.add(new DBSchema("", true));
			}

		} catch(Exception e) {
			ex = e;
		} finally {
			if(rs != null) {
				rs.close();
			}

			if(con != null) {
				con.close();
			}
		}

		if(ex != null) {
			throw ex;
		}

		return schemas;
	}

	public static List<DBTable> getSchemasTables(DataBaseConnection sock, DBSchema schema, DocumentGateway document) throws Exception {
		List<DBTable> tables = new ArrayList<DBTable>();

		Connection con = sock.getSocket(document);
		ResultSet rs = null;
		Exception ex = null;
		try {
			DatabaseMetaData dbMd = con.getMetaData();
			String shcName = "%";
			if(schema != null) {
				shcName = schema.getName();
			}
			rs = dbMd.getTables(con.getCatalog(), shcName, "%", new String[] { "TABLE", "VIEW" });
			while(rs.next()) {
				tables.add(new DBTable(schema, rs.getString("TABLE_NAME"), rs.getString("TABLE_TYPE")));
			}
			if(schema != null) {
				schema.setTableLoaded();
			}

		} catch(Exception e) {
			ex = e;
		} finally {
			if(rs != null) {
				rs.close();
			}

			if(con != null) {
				con.close();
			}
		}

		if(ex != null) {
			throw ex;
		}
		return tables;
	}

	public static List<DBColumn> getColumns(DataBaseConnection sock, DBSchema schema, DBTable table) throws Exception {
		List<DBColumn> columns = new ArrayList<DBColumn>();

		Connection con = sock.getSocket(null);
		ResultSet rs = null;
		Exception ex = null;

		try {
			DatabaseMetaData dbMd = con.getMetaData();
			String shcName = "%";

			String tableName = table.getName();

			if(schema != null) {
				shcName = schema.getName();
			}
			else if(table.getName().contains(".")) {
				String[] tmp = table.getName().split("\\.");
				tableName = tmp[1];
			}

			rs = dbMd.getColumns(con.getCatalog(), shcName, tableName, null);

			while(rs.next()) {
				columns.add(new DBColumn(table, rs.getString("COLUMN_NAME"), rs.getString("DATA_TYPE")));
			}

			if(schema != null) {
				schema.setTableLoaded();
			}

		} catch(Exception e) {
			ex = e;
		} finally {
			if(rs != null) {
				rs.close();
			}

			if(con != null) {
				con.close();
			}
		}

		if(ex != null) {
			throw ex;
		}

		return columns;
	}

	/**
	 * Helper method to create a StreamDescript from an SQL Query if the given server is null, the Descriptor will be empty if the Sql query is invalid an exception is thrown
	 * 
	 * @param server
	 * @param query
	 * @param document 
	 * @return
	 */
	public static DefaultStreamDescriptor createDescriptorFromQuery(String transfoName, DataBaseServer server, Query query, DocumentGateway document) throws Exception {

		if(server == null) {
			Activator.getLogger().warn("DataBasHelper created an empty from a SQLQuery because the DatabaseServer used is null");
			return new DefaultStreamDescriptor();
		}
		DataBaseConnection dbSock = (DataBaseConnection) server.getCurrentConnection(null);

		if(dbSock == null) {
			throw new Exception("DataBaseHelper cannot create a Descriptor for an Sql query from a DataBaseServer with no connection");
		}

		Connection sock = dbSock.getSocket(document);

		Statement stmt = sock.createStatement();
		stmt.setMaxRows(1);

		ResultSet rs = null;

		try {
			rs = stmt.executeQuery(query.dump());
		} catch(Exception ex) {

			stmt.close();

			ex.printStackTrace();
			throw new Exception("Error running Sql " + query.dump() + " : " + ex.getMessage(), ex);
		}

		DefaultStreamDescriptor dsd = null;
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			dsd = new DefaultStreamDescriptor();

			for(int i = 1; i <= rsmd.getColumnCount(); i++) {

				StreamElement e = new StreamElement();
				e.className = rsmd.getColumnClassName(i);
				e.name = rsmd.getColumnLabel(i);
				e.tableName = rsmd.getTableName(i);
				e.transfoName = transfoName;
				e.originTransfo = transfoName;
				e.typeName = rsmd.getColumnTypeName(i);
				e.precision = rsmd.getPrecision(i);
				e.decimal = rsmd.getScale(i);
				try {
					getTableMetadata(sock, e);
				} catch(Exception exs) {

				}

				dsd.addColumn(e);

			}
		} catch(Exception ex) {
			ex.printStackTrace();
			Activator.getLogger().error("Unabe to get column infos ", ex);
		} finally {
			if(rs != null) {
				rs.close();
			}
			if(stmt != null) {
				stmt.close();
			}
		}

		return dsd;
	}
	
	
	public static DefaultStreamDescriptor createDescriptorFromQuery(String transfoName, DataBaseServer server, String query, DocumentGateway document) throws Exception {

		if(server == null) {
			Activator.getLogger().warn("DataBasHelper created an empty from a SQLQuery because the DatabaseServer used is null");
			return new DefaultStreamDescriptor();
		}
		DataBaseConnection dbSock = (DataBaseConnection) server.getCurrentConnection(null);

		if(dbSock == null) {
			throw new Exception("DataBaseHelper cannot create a Descriptor for an Sql query from a DataBaseServer with no connection");
		}

		Connection sock = dbSock.getSocket(document);

		Statement stmt = sock.createStatement();
		stmt.setMaxRows(1);

		ResultSet rs = null;

		try {
			rs = stmt.executeQuery(query);
		} catch(Exception ex) {

			stmt.close();

			ex.printStackTrace();
			throw new Exception("Error running Sql " + query + " : " + ex.getMessage(), ex);
		}

		DefaultStreamDescriptor dsd = null;
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			dsd = new DefaultStreamDescriptor();

			for(int i = 1; i <= rsmd.getColumnCount(); i++) {

				StreamElement e = new StreamElement();
				e.className = rsmd.getColumnClassName(i);
				e.name = rsmd.getColumnLabel(i);
				e.tableName = rsmd.getTableName(i);
				e.transfoName = transfoName;
				e.typeName = rsmd.getColumnTypeName(i);
				e.precision = rsmd.getPrecision(i);
				e.decimal = rsmd.getScale(i);
				try {
					getTableMetadata(sock, e);
				} catch(Exception exs) {

				}

				dsd.addColumn(e);

			}
		} catch(Exception ex) {
			ex.printStackTrace();
			Activator.getLogger().error("Unabe to get column infos ", ex);
		} finally {
			if(rs != null) {
				rs.close();
			}
			if(stmt != null) {
				stmt.close();
			}
		}

		return dsd;
	}

}
