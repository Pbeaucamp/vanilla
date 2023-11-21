package bpm.gateway.runtime2.transformations.outputs;

import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.postgis.PGgeometry;
import org.postgresql.util.PGobject;

import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.gateway.core.transformations.outputs.DataBaseOutputStream;
import bpm.gateway.core.transformations.outputs.IOutput;
import bpm.gateway.runtime2.RuntimeEngine;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunDataBaseOutput extends RuntimeStep{
	protected Connection sqlConnection;
	protected HashMap<Transformation, BlockingQueue<Row>> datasMap = new HashMap<Transformation, BlockingQueue<Row>>();
	protected HashMap<Transformation, String> queries = new HashMap<Transformation, String>();
	protected HashMap<Transformation, PreparedStatement> statements = new HashMap<Transformation, PreparedStatement>();
	 
	protected Statement commitStmt;
	
	protected boolean handleError = false;
	protected RuntimeStep errorHandler;
	private List<Row> batchedRows = new ArrayList<Row>() ;
	private static List<RuntimeStep> outputs = new ArrayList<RuntimeStep>();

	public static void setOutputs(List<RuntimeStep> output) {
		outputs = output;
	}

	public RunDataBaseOutput(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}
	
	private void createJdbcResources(DataBaseConnection c) throws Exception{
		if (c.isUseFullUrl()){
			sqlConnection = JdbcConnectionProvider.createSqlConnection(getTransformation().getDocument(), c.getDriverName(), c.getFullUrl(), c.getLogin(), c.getPassword());
		}
		else{
			sqlConnection = JdbcConnectionProvider.createSqlConnection(c.getDriverName(), c.getHost(), c.getPort(), c.getDataBaseName(), c.getLogin(), c.getPassword());
		}
		
		// If Postgres we add geometry class to connection
		if (c.getDriverName().toLowerCase().contains("postgres")) {
			((org.postgresql.PGConnection)sqlConnection).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
		}
		
		try {
			sqlConnection.setCatalog(c.getDataBaseName());
		} catch (Exception e) {
		}		
		
		sqlConnection.setAutoCommit(false);
		for(Transformation in : getTransformation().getInputs()){
			String query = prepareQuery(in);
			queries.put(in, query);
			PreparedStatement stmt = sqlConnection.prepareStatement(query);
			info(" created prepared Statement for " + in.getName());
			debug(" statement query for  " + in.getName() + " : " + query);

			statements.put(in, stmt);
			datasMap.put(in, new ArrayBlockingQueue<Row>(100));
		
		}
		
		if (((DataBaseOutputStream)getTransformation()).isTruncate()){
			info(" need truncate");
			DataBaseOutputStream t = (DataBaseOutputStream)getTransformation();
			Statement stmt = sqlConnection.createStatement();
			if(c.getDriverName().toLowerCase().contains("oracle") || c.getDriverName().toLowerCase().contains("h2")) {
				stmt.execute("truncate table " + t.getTableName());
			}
			else {
				stmt.execute("truncate " + t.getTableName());
			}
			info(" table "  + t.getTableName() + " truncated");
			stmt.close();
		}
		
		if (((DataBaseOutputStream)getTransformation()).getTrashTransformation() != null){
			handleError = true;
			
			for(RuntimeStep rs : getOutputs()){
				if (rs.getTransformation() == ((DataBaseOutputStream)getTransformation()).getTrashTransformation()){
					errorHandler = rs;
					break;
				}
			}
			
			
		}
		batchedRows = new ArrayList<Row>(RuntimeEngine.MAX_ROWS);
		commitStmt = sqlConnection.createStatement();
		info(" created prepared Statement for commit");
		info(" inited");
	}
	
	@Override
	public void init(Object adapter) throws Exception {
		DataBaseServer server = (DataBaseServer)((DataBaseOutputStream)getTransformation()).getServer();
		DataBaseConnection c = (DataBaseConnection)server.getCurrentConnection(adapter);
		try {
			createJdbcResources(c);
		} catch(Exception e) {
			List<IServerConnection> alts = server.getConnections();
			if(alts != null && !alts.isEmpty())  {
				List<IServerConnection> cons = new ArrayList<IServerConnection>(alts);
				for(IServerConnection co : cons) {
					try {
						createJdbcResources((DataBaseConnection) co);
						isInited = true;
						return;
					} catch(Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			else {
				e.printStackTrace();
			}
		}
		
		isInited = true;
	}
	


	/*
	 * prepare for insert 
	 */
	protected String prepareQuery(Transformation tr) throws Exception{
		/*
		 * insertion query creation
		 * 
		 * we will use some prepared statement JDBC features
		 */
		StringBuffer insertionBaseSql = new StringBuffer();
		insertionBaseSql.append("insert into ");
		insertionBaseSql.append(((DataBaseOutputStream)getTransformation()).getTableName());
		insertionBaseSql.append("(");
		
		IOutput db = (IOutput)getTransformation();
		
		boolean isFirst = true;
		StreamDescriptor desc = getTransformation().getDescriptor(getTransformation());
		for(StreamElement e : desc.getStreamElements()){
			HashMap<String, String> maps = db.getMappingsFor(tr);
			for(String key : maps.keySet()){
				if (maps.get(key).equals(e.name)){
					if (isFirst){
						isFirst = false;
					}
					else{
						insertionBaseSql.append(", ");
					}
					insertionBaseSql.append(e.name);
				}
				
			}
			
			
			
		}
		
		insertionBaseSql.append(") values(");
		isFirst = true;
		for(int i = 0; i < db.getMappingsFor(tr).size(); i++){
			if (isFirst){
				isFirst = false;
			}
			else{
				insertionBaseSql.append(", ");
			}
			
			insertionBaseSql.append("?");
		}
		insertionBaseSql.append(")");
		
		
		return insertionBaseSql.toString();
		
	}
	
	int sz = 0;
	
	private Row readRow(RuntimeStep caller) throws Exception{
		if (inputEmpty(caller.getTransformation())){
			return null;
		}
		Row r = datasMap.get(caller.getTransformation()).take();
		readedRows ++;
		return r;
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
			try {
				Thread.sleep(10);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // restore interrupted status
			}
			return;
		}
		
		
		IOutput mapper = (IOutput)getTransformation();
		
		for(RuntimeStep rs : inputs){
			String query = queries.get(rs.getTransformation());
			if (query == null) {
				continue;
			}
			
						
			PreparedStatement stmt = statements.get(rs.getTransformation());
			
			if (stmt == null){
				continue;
			}
			
			if (inputEmpty(rs.getTransformation())){
				try{
					Thread.sleep(10);
				}catch(Exception e){
					
				}
				Logger.getLogger(getClass()).debug("skipped input " + rs.getTransformation().getName() + " because no input");
				continue;
			}
			
						
			Row r = null;
			try{
				r = readRow(rs);
			}catch(Exception e){
				return;
			}
			

			int counter = 0;
//			System.out.println(r.dump());
			Row newRow = RowFactory.createRow(this);
			
			for(int i = 0; i < newRow.getMeta().getSize(); i++){
				Integer index = mapper.getMappingValueForThisNum(rs.getTransformation(), i);
				
				if (index != null){
					Object o = r.get(index);
					if (o == null){
						//Because postgres checks the type when you set a null value...
						//Yeah because null in String and null in int is not the same thing
						//So they have to validate that the null is an "integer null"
						//You never now, maybe somebody would put a "string null" in there, better check that out
						int type = findType(newRow.getMeta().getJavaClasse(i));
						stmt.setNull(++counter, type);
					}
					else{
						Class<?> javaClasse = r.getMeta().getJavaClasse(index);
						//Modified for CSVInput: sometimes it send a String back instead of the type of the column (check history to get back the previous code)
						if (o instanceof Timestamp) {
							stmt.setTimestamp(++counter, (Timestamp) o, Calendar.getInstance());
						}
						else if(o instanceof Date) {
							stmt.setTimestamp(++counter, new Timestamp(((Date)o).getTime()));
						}
						else if (PGgeometry.class.isAssignableFrom(javaClasse)) {
							stmt.setObject(++counter, o);
						}
						else {
							switch (findType(javaClasse)) {
							case Types.INTEGER:
								if (o.toString().isEmpty()) {
									stmt.setNull(counter+1, Types.INTEGER);
								}
								else {
									try {
										stmt.setInt(counter+1, Integer.parseInt(o.toString()));
									} catch (Exception e) {
										try {
											stmt.setInt(counter+1, (int)Double.parseDouble(o.toString()));
										} catch (Exception e2) {
											stmt.setString(counter+1, o.toString());
										}
									}
								}
								++counter;
								break;
							case Types.DECIMAL:
								if (o.toString().isEmpty()) {
									stmt.setNull(counter+1, Types.DECIMAL);
								}
								else {
									try {
										//We check if there is a decimal because if not it put XX.0 which can break insert
										if (o.toString().contains(",") || o.toString().contains(".")) {
											stmt.setFloat(counter+1, Float.parseFloat(o.toString()));
										}
										else {
											// We change int to double because it breaks for big number 
//											stmt.setInt(counter+1, Integer.parseInt(o.toString()));
//											stmt.setDouble(counter+1, Double.parseDouble(o.toString()));
											stmt.setBigDecimal(counter+1, new BigDecimal(o.toString()));
										}
									} catch (Exception e) {
										stmt.setString(counter+1, o.toString());
									}
								}
								++counter;
								break;
							case Types.DATE:
								//Should only be happening if the value is empty
								stmt.setNull(counter+1, Types.DATE);
								++counter;
								break;
							case Types.VARCHAR:
								if (o instanceof String){
									//trick for the postgres driver because it is doing some strange things
									o = ((String)o).replace("\0", "");
									stmt.setObject(++counter, o);
								}
								else {
									stmt.setString(++counter, o.toString());
								}
								break;
							default:
								if (o instanceof String){
									//trick for the postgres driver because it is doing some strange things
									o = ((String)o).replace("\0", "");
									stmt.setObject(++counter, o);
								}
								else {
									stmt.setObject(++counter, o);
								}
								break;
							}
							
							
							
						}
					}
					
					newRow.set(i, r.get(index));
				}
			}
			
			try{

				if (sz < RuntimeEngine.MAX_ROWS){
					stmt.addBatch();
					sz++; 
//					if (handleError){
						batchedRows.add(newRow);
//					}
				}
				else{
					stmt.addBatch();
//					if (handleError){
						batchedRows.add(newRow);
//					}
					
					
					try{
						int c = 0;
						int[] a = stmt.executeBatch();
						for (int i : a){
							c += i;
						}
						commitStmt.execute("commit");
						
						debug(" executed batch " + c + " row inserted on " + a.length);
						sz = 0;
						
						for(Row _r : batchedRows){
							writeRow(_r);
						}
						
						batchedRows.clear();
					}catch(Exception ex){
						if (handleError){
							for(Row _r : batchedRows){
								writeErrorRow(_r);
							}
							
							if(ex instanceof BatchUpdateException) {
								SQLException sqlEx = ((BatchUpdateException) ex).getNextException();
								if(sqlEx.getMessage().contains("syntaxe en entr")) {
									System.out.println();
								}
								if(sqlEx != null) {
									sqlEx.printStackTrace();
								}
							}
							
							batchedRows.clear();
							warn("A Problem occured when performing an SQL batch, rows inserted in TrashOutput", ex);
							commitStmt.execute("rollback");
							
						}
						else{
							//For postgres again
							//best exception handling ever
							if(ex instanceof BatchUpdateException) {
								SQLException sqlEx = ((BatchUpdateException) ex).getNextException();
								if(sqlEx != null) {
									sqlEx.printStackTrace();
								}
							}
							for(Row _r : batchedRows){
//								System.out.println(_r.dump());
							}
							throw ex;
						}
					}
					
					
				}
				
				
			}catch(SQLException e){
				error(" error when performing database operation", e);
				throw e;
			}catch(Exception e) {
				if (r != null){
					error( " error when inserting row with values : " + r.dump(), e);
				}
				else{
					error( " error when inserting row", e);
				}
				throw e;
			}
			
			
		}
	}
	
	private int findType(Class<?> javaClasse) {
		if (Date.class.isAssignableFrom(javaClasse)) {
			return Types.DATE;
		}
		else if (Integer.class.isAssignableFrom(javaClasse)) {
			return Types.INTEGER;
		}
		else if (Long.class.isAssignableFrom(javaClasse)) {
			return Types.INTEGER;
		}
		else if (BigDecimal.class.isAssignableFrom(javaClasse)) {
			return Types.DECIMAL;
		}
		else if (Double.class.isAssignableFrom(javaClasse)) {
			return Types.DECIMAL;
		}
		else if (Float.class.isAssignableFrom(javaClasse)) {
			return Types.DECIMAL;
		}
		else if (Number.class.isAssignableFrom(javaClasse)) {
			return Types.INTEGER;
		}
		else if (Boolean.class.isAssignableFrom(javaClasse)) {
			return Types.BOOLEAN;
		}
		return Types.VARCHAR;
	}
	
	
	
	@Override
	public boolean inputEmpty() throws InterruptedException {
		for(BlockingQueue<Row> q : datasMap.values()){
			if (!q.isEmpty()){
				return false;
			}
		}
		return true;
	}
	
	
	private boolean inputEmpty(Transformation transformation) throws InterruptedException {
		boolean b = datasMap.get(transformation).isEmpty();
		return b;
	}

	@Override
	protected void setEnd(){
		for(PreparedStatement ps : statements.values()){
			try {
				
				for(RuntimeStep i : inputs){
					if (!datasMap.get(i.getTransformation()).isEmpty()){
						return;
					}
				}
				
			
				try{
					int c = 0;
					int[] a = ps.executeBatch();
					for (int i : a){
						c += i;
					}
					commitStmt.execute("commit");
					
					debug(" executed batch " + c + " row inserted on " + a.length);
					sz = 0;
					
					for(Row _r : batchedRows){
						writeRow(_r);
					}
					
					batchedRows.clear();
				} catch(Exception ex){
					if (handleError){
						for(Row _r : batchedRows){
							writeErrorRow(_r);
						}
						
						batchedRows.clear();
						warn("A Problem occured when performing an SQL batch, rows inserted in TrashOutput", ex);
						commitStmt.execute("rollback");
						
					}
					else{
						if(ex instanceof BatchUpdateException) {
							SQLException sqlEx = ((BatchUpdateException) ex).getNextException();
							if(sqlEx != null) {
								sqlEx.printStackTrace();
							}
						}
						throw ex;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				error(" error on last batch execution ", e);
				
			}
		}
		
		super.setEnd();
	}

	@Override
	public void releaseResources() {
		try {
			if (commitStmt != null) {
				commitStmt.close();
			}
			info(" released commit Statement");
		}
		catch (SQLException e) {
			error(" problem when releasing commit Statement", e);
		}
		for(Transformation s : statements.keySet()){
			try {
				statements.get(s).close();
				info(" released PreparedStatement from " + s.getName());
			} catch (SQLException e) {
				error( " problem when releasing PreparedStatement from " + s.getName());
			
		}
			try {
				if (!sqlConnection.isClosed()){
					sqlConnection.close();
					info(" close connection to database");
				}
				else{
					info("Connection to database already closed");
				}
				
			} catch (SQLException e) {
				error(" problem when closing connection to database", e);
			}
		}
	}
	
	@Override
	public void insertRow(final Row data, final RuntimeStep caller) throws InterruptedException {
		datasMap.get(caller.getTransformation()).put(data);

	}
	
	@Override
	protected void writeRow(Row row) throws InterruptedException{
	
		for(RuntimeStep r : getOutputs()){
			if (r != errorHandler){
				r.insertRow(row, this);
				
			}
			
			
		}
		
		writedRows++;
		
		
	}
	
	protected void writeErrorRow(Row row) throws InterruptedException{
		if (errorHandler != null){
			errorHandler.insertRow(row, this);
			writedRows++;
		}
	}

}
