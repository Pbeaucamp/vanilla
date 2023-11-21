package bpm.gateway.runtime2.transformations.inputs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunDataBaseInput extends RuntimeStep{

	private Statement stmt;
	private ResultSet resultSet;
	private Connection con;
	private int bufferSize;
	
	public RunDataBaseInput(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		this.bufferSize = bufferSize;
	}

	private void createJdbcResources(DataBaseConnection c) throws Exception{
		if (!c.isUseFullUrl()){
			con = JdbcConnectionProvider.createSqlConnection(c.getDriverName(), c.getHost(), c.getPort(), c.getDataBaseName(), c.getLogin(), c.getPassword());
			con.setCatalog(c.getDataBaseName());
		}
		else{
			con = JdbcConnectionProvider.createSqlConnection(getTransformation().getDocument(), c.getDriverName(), c.getFullUrl(), c.getLogin(), c.getPassword());
		}
		
		try{
			//well well hive makes it even better than impala
			if(JdbcConnectionProvider.getDriver(c.getDriverName()).getClassName().equalsIgnoreCase("org.apache.hive.jdbc.HiveDriver")) {
				stmt = con.createStatement();
			}
			else {
				con.setAutoCommit(true);
				stmt = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
						java.sql.ResultSet.CONCUR_READ_ONLY);
				
				stmt.setFetchSize(Integer.MIN_VALUE);
				
				info( " DataBase connection created with fetch size at " + Integer.MIN_VALUE);
			}
			
		}catch(SQLException e){
			if (stmt == null){
				try {
					stmt = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
							java.sql.ResultSet.CONCUR_READ_ONLY);
				} catch (Exception e1) {
					//This is for the hive shits, because why support any method at all in the Driver ?
					stmt = con.createStatement();
				}
			}
			
			
			try{
				
				stmt.setFetchSize(bufferSize);
				info(" DataBase connection created  with fetch size at " + bufferSize);
			}catch(Exception ex){
				stmt.setFetchSize(0);
				info(" DataBase connection created  with fetch size at 0" );
			}
			
		}catch(Exception e){
			e.printStackTrace();
			error(" DataBase connection failed", e);
			throw e;
		}
		
		String query = getTransformation().getDocument().getStringParser().getValue(getTransformation().getDocument(), ((DataStream)getTransformation()).getDefinition());
		
		try{
			resultSet =  stmt.executeQuery(query);
			info(" Run query : " + query);
		}catch(Exception ex){
			warn(" Failed to execute query try to set JdbcStatement Fetch Size at 0");
			try{
				stmt.setFetchSize(0);
				resultSet =  stmt.executeQuery(query);
				info(" Run query : " + query);
			}catch(Exception e2){
				error( " Failed query : " + query, ex);
				throw e2;
			}
			
		}		
	}
	
	@Override
	public void init(Object adapter) throws Exception {
		
		DataBaseServer server = (DataBaseServer)((DataStream)getTransformation()).getServer();
		DataBaseConnection c = (DataBaseConnection)server.getCurrentConnection(adapter);
		try {
			createJdbcResources(c);
		} catch(Exception e) {
			List<IServerConnection> alts = server.getConnections();
			if(alts != null && !alts.isEmpty())  {
				List<IServerConnection> cons = new ArrayList<IServerConnection>(alts);
				for(IServerConnection co : alts) {
					try {
						createJdbcResources((DataBaseConnection) co);
						isInited = true;
						return;
					} catch(Exception e1) {
						e1.printStackTrace();
						throw e1;
					}
				}
			}
			else {
				e.printStackTrace();
				throw e;
			}
		}
		
		isInited = true;
	}
	
	
	
	@Override
	public void performRow() throws Exception {
		if (resultSet == null){
			throw new Exception("No ResultSet defined");
		}
		
		if (resultSet.next()){
			Row row = RowFactory.createRow(this);
			
			for(int i = 1; i <= row.getMeta().getSize(); i++){
				Object o = null;
				try {
					o = resultSet.getObject(i);
				} catch(Error e) {
					o = resultSet.getString(i);
				}
				row.set(i - 1, o);
			}
			
			writeRow(row);
		}
		else{
			if (!areInputsAlive()){
				if (areInputStepAllProcessed()){
					if (inputEmpty()){
						setEnd();
					}
				}
			}
			
		
			
		}
	}

	

	@Override
	public void releaseResources() {
		if (resultSet != null){
			
			try {
				resultSet.close();
				resultSet = null;
				info(" closed resultSet");
			} catch (SQLException e) {
				error(" error when closing resultSet", e);
			}
		}
		
		if (stmt != null){
			try {
				stmt.close();
				stmt = null;
				info( " closed Statement");
			} catch (SQLException e) {
				error( " error when closing Statement", e);
			}
		}
		
		if (con != null){
			try {
				if (!con.isClosed()){
					con.close();
					info(" closed Connection");
				}
				else{
					info("Connection already closed ");
				}
				
				con = null;
				
			} catch (SQLException e) {
				error(" error when closing Connection", e);
			}
		}
		
	}
	
	

}
