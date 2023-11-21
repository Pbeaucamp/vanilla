package bpm.gateway.runtime2.transformation.gid;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import bpm.gateway.core.Server;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.gateway.core.transformations.GlobalDefinitionInput;
import bpm.gateway.core.transformations.gid.GidNode;
import bpm.gateway.core.transformations.gid.GidTreeBuilder;
import bpm.gateway.core.transformations.gid.Query;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class GidRuntime extends RuntimeStep{
	private Statement stmt;
	private ResultSet resultSet;
	private Connection con;
	private int bufferSize;
	
	public GidRuntime(GlobalDefinitionInput transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		this.bufferSize = bufferSize;
	}

	private void createJdbcResources(DataBaseConnection c, String querySql) throws Exception{
		if (!c.isUseFullUrl()){
			con = JdbcConnectionProvider.createSqlConnection(c.getDriverName(), c.getHost(), c.getPort(), c.getDataBaseName(), c.getLogin(), c.getPassword());
			con.setCatalog(c.getDataBaseName());
		}
		else{
			con = JdbcConnectionProvider.createSqlConnection(getTransformation().getDocument(), c.getDriverName(), c.getFullUrl(), c.getLogin(), c.getPassword());
		}
		
		
		con.setAutoCommit(false);
		try{
			
			
			stmt = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			
			stmt.setFetchSize(Integer.MIN_VALUE);
			
			info( " DataBase connection created with fetch size at " + Integer.MIN_VALUE);
			
		}catch(SQLException e){
			if (stmt == null){
				stmt = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
						java.sql.ResultSet.CONCUR_READ_ONLY);
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
		
		
		try{
			resultSet =  stmt.executeQuery(querySql);
			info(" Run query : " + querySql);
		}catch(Exception ex){
			error( " Failed query : " + querySql, ex);
		}		
	}
	
	
	
	@Override
	public void init(Object adapter) throws Exception{
		
		
		GidTreeBuilder treeBuilder = new GidTreeBuilder();
		GidNode node = treeBuilder.buildTree((GlobalDefinitionInput)getTransformation());
		
	
		List<Server> servers = treeBuilder.getDataStreamsServers(node);
		
		if (servers.size() > 1){
			throw new Exception("Cannot have a Gid step using multiples DataBase server's");
		}
		
		DataBaseServer server = null;
		if (servers.size() > 0){
			server = (DataBaseServer)servers.get(0);
		}

		
		Query query = null;
		
		try{
			query = node.evaluteQuery();
		}catch(Exception ex){
			ex.printStackTrace();
			error("Unable to evaluate node query from Gid : " + ex.getMessage(), ex);
		}
		String q = query.dump().replace("\n", "");
		DataBaseConnection c = (DataBaseConnection)server.getCurrentConnection(null);
		if(((GlobalDefinitionInput)getTransformation()).getCustomQuery() != null && !((GlobalDefinitionInput)getTransformation()).getCustomQuery().isEmpty()) {
			q = ((GlobalDefinitionInput)getTransformation()).getCustomQuery();
		}
		createJdbcResources(c, q);
		
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
				Object o = resultSet.getObject(i);
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
					info("Connection already closed");
				}
				
				
				con = null;
				
			} catch (SQLException e) {
				error(" error when closing Connection", e);
			}
		}
		
	}
	
}
