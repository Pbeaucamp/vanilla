package bpm.gateway.runtime2.transformations.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.gateway.core.transformations.SqlScript;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.tools.StringParser;

public class RunSqlScript extends RuntimeStep{

	private Statement stmt;
	private Connection con;
	private boolean scriptExecuted = false;
	
	public RunSqlScript(SqlScript transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		
	}
	
	private void createJdbcResources(DataBaseConnection c) throws Exception{
		if (c.isUseFullUrl()){
			con = JdbcConnectionProvider.createSqlConnection(getTransformation().getDocument(), c.getDriverName(), c.getFullUrl(), c.getLogin(), c.getPassword());
			
		}
		else{
			con = JdbcConnectionProvider.createSqlConnection(c.getDriverName(), c.getHost(), c.getPort(), c.getDataBaseName(), c.getLogin(), c.getPassword());
			con.setCatalog(c.getDataBaseName());
		}
		
		
		

		try{
			stmt = con.createStatement();
		}catch(Exception e){
			e.printStackTrace();
			error(" SQL Statement not created during init", e);
			
			throw e;
		}
		info(" SQL Statement created during init");
	}
	
	@Override
	public void init(Object adapter) throws Exception {
		DataBaseServer server = (DataBaseServer)((SqlScript)getTransformation()).getServer();
		DataBaseConnection c = (DataBaseConnection)server.getCurrentConnection(adapter);
		createJdbcResources(c);
		
		isInited = true;
	}
	

	
	private void executeScript() throws Exception{
		String query = getTransformation().getDocument().getStringParser().getValue(getTransformation().getDocument(), ((DataStream)getTransformation()).getDefinition());
		
		for(String s : query.split(";")){
			try{
				stmt.execute(s);
				info(" executed : " + s);
			}catch(Exception e){
				error(" failed on {" + s + "}", e);
				throw new Exception("failed on : " + s, e);
			}
			
		}
	}
	
	@Override
	public void performRow() throws Exception {
//		while(!areInputStepAllProcessed()){
//			
//			try{
//				Thread.sleep(500);
//			}catch(Exception ex){
//				
//			}
//		}
//		//TODO : use input as parameter for running the script if there are
//		if (!scriptExecuted){
//			executeScript();
//			scriptExecuted = true;
//		}
//		
//		if (areInputStepAllProcessed()){
//			if (inputEmpty()){
//				setEnd();
//			}
//		}
//		
//		if (isEnd() && inputEmpty()){
//			return;
//		}
//		
//		if (!isEnd() && inputEmpty()){
//			Thread.sleep(10);
//			return;
//		}
//		
//		Row r = null;
//		try{
//			r = readRow();
//		}catch(Exception e){
//			return;
//		}
//		
//		writeRow(r);
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				if (!scriptExecuted){
					executeScript();
					scriptExecuted = true;
				}
				setEnd();
			}
		}
		
		if (isEnd() && inputEmpty()){
			return;
		}
		
		if (!isEnd() && inputEmpty()){
			try{
				Thread.sleep(10);
				return;
			}catch(Exception e){
				
			}
		}
		
		Row row = readRow();
		writeRow(row);
	}

	@Override
	public void releaseResources() {
		if (stmt != null){
			try {
				stmt.close();
				stmt = null;
				info( " closed Statement");
			} catch (SQLException e) {
				error(" error when closing Statement", e);
			}
		}
		
		if (con != null){
			try {
				con.close();
				con = null;
				info(" closed Connection");
			} catch (SQLException e) {
				error(" error when closing Connection", e);
			}
		}
	
		
	}

}
