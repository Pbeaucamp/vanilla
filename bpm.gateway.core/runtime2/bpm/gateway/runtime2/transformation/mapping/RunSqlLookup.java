package bpm.gateway.runtime2.transformation.mapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.gateway.core.transformations.Lookup;
import bpm.gateway.core.transformations.SimpleMappingTransformation;
import bpm.gateway.core.transformations.SqlLookup;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunSqlLookup extends RuntimeStep{

	private RuntimeStep trashOutput;
	private boolean keepNonMatchRows = false;
	private boolean trashNonMatchRows = false;
	
	private Connection con;
	private PreparedStatement stmt;
	private int bufferSize;
	private ResultSet resultSet;
	protected List<Integer> masterMap;
	private List<Integer> parameterMapIndex;
	
	public RunSqlLookup(SqlLookup transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		this.bufferSize = bufferSize;
	}
	private void createJdbcResources(DataBaseConnection c) throws Exception{
		
		keepNonMatchRows = !((Lookup)getTransformation()).isRemoveRowsWithoutLookupMatching();
		trashNonMatchRows = ((Lookup)getTransformation()).isTrashRowsWithoutLookupMatching();
		for(RuntimeStep rs : getOutputs()){
			if (rs.getTransformation() == ((Lookup)getTransformation()).getTrashTransformation()){
				trashOutput = rs;
			}
		}
		
		
		masterMap = new ArrayList<Integer>();
		
		for(int i = 0; i < getTransformation().getInputs().get(0).getDescriptor(getTransformation()).getColumnCount(); i++){
			
			boolean added = false;
			HashMap<String, String> maps = ((SimpleMappingTransformation)getTransformation()).getMappings();
			for(String input : maps.keySet()){
				
				String output = maps.get(input);
				
				int index = ((SimpleMappingTransformation)getTransformation()).getMappingValueForInputNum(input);
				if (index == i){
					if (output == null || output.isEmpty()){
						String s = " Bad mapping value for column(" + input + ", " + output + ")";
						Exception e = new Exception(getName() + s);
						error(s);
						throw e;
					}
					
					masterMap.add(((SimpleMappingTransformation)getTransformation()).getMappingValueForThisNum(output));
					added = true;
					break;
				}
			}
			if (!added){
				masterMap.add(null);
			}
		}
		
		String query = getTransformation().getDocument().getStringParser().getValue(getTransformation().getDocument(), ((DataStream)getTransformation()).getDefinition());
		query = generateQuery(query);

		
		
		if (!c.isUseFullUrl()){
			con = JdbcConnectionProvider.createSqlConnection(c.getDriverName(), c.getHost(), c.getPort(), c.getDataBaseName(), c.getLogin(), c.getPassword());
		}
		else{
			con = JdbcConnectionProvider.createSqlConnection(getTransformation().getDocument(), c.getDriverName(), c.getFullUrl(), c.getLogin(), c.getPassword());
		}
		
		con.setCatalog(c.getDataBaseName());
		con.setAutoCommit(false);
		try{
			
			stmt = con.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			
			stmt.setFetchSize(Integer.MIN_VALUE);
			
			info( " DataBase connection created with fetch size at " + Integer.MIN_VALUE);
			
		}catch(SQLException e){
			if (stmt == null){
				stmt = con.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY,
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
		
		
	}
	
	@Override
	public void init(Object adapter) throws Exception {
		DataBaseServer server = (DataBaseServer)((SqlLookup)getTransformation()).getServer();
		DataBaseConnection c = (DataBaseConnection)server.getCurrentConnection(adapter);
		createJdbcResources(c);
		
		isInited = true;
	}
	
	
	
	
	private String generateQuery(String query) throws Exception{
		int whereIndex = query.toLowerCase().indexOf(" where ");
		boolean hasWhere = whereIndex != -1;
		String endQuery = "";
		StringBuffer q = new StringBuffer();
		
		if (query.toLowerCase().contains(" group by ")){
			int endIndex = 	query.toLowerCase().indexOf(" group by ");		
			endQuery = query.substring(endIndex);
			q.append(query.substring(0, endIndex));
		}
		else if (query.toLowerCase().contains(" order by ")){
			int endIndex = 	query.toLowerCase().indexOf(" order by ");
			endQuery = query.substring(endIndex);
			q.append(query.substring(0, endIndex));
		}
		else if (query.toLowerCase().contains(" union ")){
			int endIndex = 	query.toLowerCase().indexOf(" union ");
			endQuery = query.substring(endIndex);
			q.append(query.substring(0, endIndex));
		}else{
			q.append(query);
		}
		parameterMapIndex = new ArrayList<Integer>();
		
		if (!hasWhere){
			q.append(" where ");
		}
		boolean first = true;
		for(int i = 0; i < getTransformation().getInputs().get(0).getDescriptor(getTransformation()).getStreamElements().size(); i++){
			
			if (masterMap.get(i) == null){
				continue;
			}
			if(first){
				first = false;
			}
			else{
				q.append(" AND ");
			}
			StreamElement e = getTransformation().getInputs().get(0).getDescriptor(getTransformation()).getStreamElements().get(i);
			
			if (e.tableName != null && !"".equals(e.tableName.trim())){
				q.append(e.tableName + "." + e.name + "=?");
			}
			else{
				q.append(e.name + "=?");
			}
			parameterMapIndex.add(((SqlLookup)getTransformation()).getMappingValueForInputNum(i));
		}
		q.append(endQuery);
		return q.toString();
		
	}

	
	@Override
	public void performRow() throws Exception {
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
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
			}catch(InterruptedException e){
				
			}
		}
		
		Row row = readRow();
		
		stmt.clearParameters();
		
//		int counter = 0;
//		for(int i = 0; i < masterMap.size(); i++){
//			if (masterMap.get(i) != null){
//				
//				stmt.setObject(++counter, row.get(masterMap.get(i)));
//				
//			}
//		}
		
		for(int i = 0; i < parameterMapIndex.size(); i++){
			stmt.setObject(i+1, row.get(parameterMapIndex.get(i)));
		}
		
		
		resultSet = stmt.executeQuery();
		
		boolean writed = false;
		
		while(resultSet.next()){
			Row newRow = RowFactory.createRow(this);
			int i = 0;
			for( i = 0; i < resultSet.getMetaData().getColumnCount(); i++){
				newRow.set(i, resultSet.getObject(i + 1));
			}
			
			for(Object o : row){
				newRow.set(i++, o);
			}
			// already found a match for that row
			//execption is thrown
			if (writed){
				try{
					throw new Exception("A match have already be found for the row " + row.dump());
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			else{
				writeRow(newRow);
				writed = true;
			}
			
			
		}
		
		resultSet.close();
		
		
		if (!writed){
			if (trashNonMatchRows && trashOutput != null){
				trashRow(RowFactory.createRow(this, row));
			}
			else{
				//we lose track of non matching row
				warn("The following row has no match and  wont be stored in the Backup Output" + row.dump());
			}
		}
		
		
	}
	
	private void trashRow(Row row)throws InterruptedException{
		if (trashOutput != null){
			trashOutput.insertRow(row, this);
			writedRows++;
		}
	}

	@Override
	public void releaseResources() {
		try{
			if (resultSet != null){
				resultSet.close();
				info("ResultSet released");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		try{
			if (stmt != null){
				stmt.close();
				info("Statement released");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		try{
			if (con != null){
				
				if (!con.isClosed()){
					con.close();
					info("Connection released");
				}
				else{
					info("Connection to database already closed");
				}
				
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
	}
	
	@Override
	protected void writeRow(Row row) throws InterruptedException {
		for(RuntimeStep r : getOutputs()){
			if (r != trashOutput){
				r.insertRow(row, this);
			}
		}
		writedRows++;
	}

}
