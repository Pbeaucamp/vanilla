package bpm.gateway.runtime2.transformation.scd;

import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.gateway.core.transformations.outputs.SlowChangingDimension2;
import bpm.gateway.runtime2.RuntimeEngine;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunSCD1 extends RuntimeStep {

	enum Version {

		NEED_INSERT, NEED_UPDATE, NEED_NOTHING;
		int lastVersion;
	}

	protected Integer versionIndex;
	protected Connection sqlConnection;
	private Statement commitStmt;
	protected PreparedStatement selectMaxStatement;
	protected PreparedStatement selectForMaxStatement;
	protected PreparedStatement selectStatement;
	protected PreparedStatement insertStatement;
	protected PreparedStatement updateStatement;
	protected int batchSize = 0;

	protected boolean handleError = false;
	protected RuntimeStep errorHandler;
	protected List<Row> batchedRows;
	protected boolean isOracle;

	public RunSCD1(SlowChangingDimension2 transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void performRow() throws Exception {
		if(areInputStepAllProcessed()) {
			if(inputEmpty()) {
				setEnd();
			}
		}

		if(isEnd() && inputEmpty()) {
			return;
		}

		if(!isEnd() && inputEmpty()) {
			try {
				Thread.sleep(10);
				return;
			} catch(Exception e) {

			}
		}

		Row row = readRow();

		// in jdbc you cannot use a preparedStatement with a parameter in the where condition with a null value
		// so the select queries will be rewritten every iteration
		Version lastVersion = getLastVersion(row);
		Row newRow = null;

		if(lastVersion == Version.NEED_INSERT) {
			newRow = performInsert(row);
		}
		else if(lastVersion == Version.NEED_UPDATE) {
			newRow = performUpdate(row, lastVersion.lastVersion);
		}
	}

	protected Version getLastVersion(Row row) throws Exception {
		SlowChangingDimension2 iu = (SlowChangingDimension2) getTransformation();
		StreamDescriptor desc = iu.getDescriptor(iu);

		StringBuilder maxStatement = new StringBuilder();
		StringBuilder statement = new StringBuilder();

		maxStatement.append("select max(" + desc.getStreamElements().get(versionIndex).name + ") from " + iu.getTableName() + " where ");

		statement.append("select ");
		statement.append("*");
		statement.append(" from " + iu.getTableName() + " where ");
		boolean first = true;
		int counter = 0;
		for(int i = 0; i < row.getMeta().getSize(); i++) {

			Integer k = iu.getTargetIndexKeyForInputIndex(i);
			if(k != null && k >= 0) {
				if(!iu.isIgnoreField(i)) {

					if(first) {
						first = false;
					}
					else {
						maxStatement.append(" AND ");
						statement.append(" AND ");
					}
					
					//FIXME

					maxStatement.append(desc.getStreamElements().get(k).name);
					statement.append(desc.getStreamElements().get(k).name);

					Class<?> c = row.getMeta().getJavaClasse(i);
					if(Integer.class.isAssignableFrom(c)) {
						try {
							if(row.get(i) == null) {
								maxStatement.append(" is null");
								statement.append(" is null");
//								selectMaxStatement.setNull(++counter, Types.INTEGER);
//								selectStatement.setNull(counter, Types.INTEGER);
							}
							else {
								maxStatement.append(" = " + row.get(i));
								statement.append(" = " + row.get(i));
//								selectMaxStatement.setInt(++counter, (Integer) row.get(i));
//								selectStatement.setInt(counter, (Integer) row.get(i));
							}
						} catch(Exception e) {
							maxStatement.append(" = " + row.get(i));
							statement.append(" = " + row.get(i));
//							selectMaxStatement.setInt(counter, Integer.parseInt((String) row.get(i)));
//							selectStatement.setInt(counter, Integer.parseInt((String) row.get(i)));
						}
					}
					else if(Long.class.isAssignableFrom(c)) {
						try {
							if(row.get(i) == null) {
								maxStatement.append(" is null");
								statement.append(" is null");
//								selectMaxStatement.setNull(++counter, Types.INTEGER);
//								selectStatement.setNull(counter, Types.INTEGER);
							}
							else {
								maxStatement.append(" = " + row.get(i));
								statement.append(" = " + row.get(i));
//								selectMaxStatement.setLong(++counter, (Long) row.get(i));
//								selectStatement.setLong(counter, (Long) row.get(i));
							}
						} catch(Exception e) {
							maxStatement.append(" = " + row.get(i));
							statement.append(" = " + row.get(i));
//							selectMaxStatement.setLong(counter, Long.parseLong((String) row.get(i)));
//							selectStatement.setLong(counter, Long.parseLong((String) row.get(i)));
						}
					}
					else if(Double.class.isAssignableFrom(c)) {
						try {
							if(row.get(i) == null) {
								maxStatement.append(" is null");
								statement.append(" is null");
//								selectMaxStatement.setNull(++counter, Types.DOUBLE);
//								selectStatement.setNull(counter, Types.DOUBLE);
							}
							else {
								maxStatement.append(" = " + row.get(i));
								statement.append(" = " + row.get(i));
//								selectMaxStatement.setDouble(++counter, (Double) row.get(i));
//								selectStatement.setDouble(counter, (Double) row.get(i));
							}
						} catch(Exception e) {
							maxStatement.append(" = " + row.get(i));
							statement.append(" = " + row.get(i));
//							selectMaxStatement.setDouble(counter, Double.parseDouble((String) row.get(i)));
//							selectStatement.setDouble(counter, Double.parseDouble((String) row.get(i)));
						}
					}
					else if(String.class.isAssignableFrom(c)) {
						try {
							if(row.get(i) == null) {
								maxStatement.append(" is null");
								statement.append(" is null");
//								selectMaxStatement.setNull(++counter, Types.VARCHAR);
//								selectStatement.setNull(counter, Types.VARCHAR);
							}
							else {
								if(((String)row.get(i)).startsWith("'")) {
									maxStatement.append(" = " + row.get(i));
									statement.append(" = " + row.get(i));
								}
								else {
									maxStatement.append(" = '" + ((String)row.get(i)).replace("'", "''") + "'");
									statement.append(" = '" + ((String)row.get(i)).replace("'", "''") + "'");
								}
//								selectMaxStatement.setString(++counter, (String) row.get(i));
//								selectStatement.setString(counter, (String) row.get(i));
							}
						} catch(Exception e) {
							maxStatement.append(" = '" + row.get(i) + "'");
							statement.append(" = '" + row.get(i) + "'");
//							selectMaxStatement.setObject(counter, row.get(i));
//							selectStatement.setObject(counter, row.get(i));
						}
					}
					else {
						maxStatement.append(" = '" + row.get(i) + "'");
						statement.append(" = '" + row.get(i) + "'");
//						selectMaxStatement.setObject(++counter, row.get(i));
//						selectStatement.setObject(counter, row.get(i));
					}
				}
			}
		}
		// XXX

		selectMaxStatement.close();
		selectMaxStatement = sqlConnection.prepareStatement(maxStatement.toString());
		selectStatement.close();
		selectStatement = sqlConnection.prepareStatement(statement.toString());
		
		ResultSet rs;
		try {
			rs = selectMaxStatement.executeQuery();
		} catch(Exception e) {
			// selectStatement.executeQuery()
			error("error executing : " + maxStatement.toString());
			error("on database " + selectMaxStatement.getConnection().toString());
			throw e;
		}
		Integer result = null;

		while(rs.next()) {
			result = rs.getInt(1);
		}

		rs.close();

		if(result == null || result.intValue() == 0) {
			// warn("GetLastVersion: Needing update : " + result == null ? "result null" : "" + result.intValue());
			return Version.NEED_INSERT;
		}

		/*
		 * we set result at null if no changes have been made
		 */
		rs = selectStatement.executeQuery();

		while(rs.next()) {
			if(rs.getInt(iu.getTargetVersionIndex() + 1) == result.intValue()) {
				boolean changed = false;

				for(int i = 0; i < row.getMeta().getSize(); i++) {
					Integer k = iu.getTargetIndexFieldForInputIndex(i);
					if(k != null && k >= 0) {
						if(!iu.isIgnoreField(i)) {

							if(rs.getObject(k + 1) == null) {
								if(row.get(i) != null) {
									// warn("GetLastVersion: Updated cause row is null");
									info("Changed value with null from : " + row.get(i) + " to : " + rs.getObject(k + 1));
									changed = true;
									break;
								}
							}
							else {
								if(row.get(i) instanceof Date) {
									try {
										Date inDate = rs.getDate(k + 1);
										Date outDate = (Date) row.get(i);

										changed = !((outDate).equals(inDate));
										if(changed) {
											try {
												changed = row.get(i).equals(rs.getObject(k + 1));
											} catch(Exception e) {
											}
											if(changed) {
												info("Changed date from : " + row.get(i) + " to : " + rs.getObject(k + 1));
											}
										}
//										break;
									} catch(Exception eDate) {

										// warn("problem with dates, contact your date guru",
										// eDate);
//										break;
									}
								}
								else if(!rs.getObject(k + 1).equals(row.get(i))) {
									if(rs.getObject(k + 1) instanceof Integer) {
										int in = (Integer) rs.getObject(k + 1);
										if(row.get(i) instanceof BigDecimal) {
											int out = ((BigDecimal) row.get(i)).intValue();
											changed = !(in == out);
										}
										else if(row.get(i) instanceof Long) {
											int out = ((Long) row.get(i)).intValue();
											changed = !(in == out);
										}
										else if(row.get(i) instanceof String) {
											int out = Integer.parseInt(((String) row.get(i)));
											changed = !(in == out);
										}
										else if(row.get(i) instanceof Integer) {
											int out = (Integer) row.get(i);
											changed = !(in == out);
										}
										else {
											if(row.get(i) == null)
												warn("Comparing Integer to null");
											else
												warn("Error comparing Integer to " + row.get(i).getClass().getName());
										}
										if(changed) {
											info("Changed number from : " + row.get(i) + " to : " + rs.getObject(k + 1));
										}
									}
									else {
										try {
											changed = !row.get(i).equals(rs.getObject(k + 1));
											if(changed == true) {
												changed = !row.get(i).toString().equals(rs.getObject(k + 1).toString());
											}
										} catch(Exception e) {
											changed = true;
										}
										
									}
									if(changed) {
										info("Changed value from : " + row.get(i) + " to : " + rs.getObject(k + 1));
										break;
									}
								}
							}
						}
					}
				}
				if(changed) {
					rs.close();
					Version.NEED_UPDATE.lastVersion = result;
//					info("Changed value from : " + row.get(i) + " to : " + );
					return Version.NEED_UPDATE;
				}
			}
		}

		rs.close();

		return Version.NEED_NOTHING;
	}

	@Override
	public void init(Object adapter) throws Exception {
		DataBaseServer server = (DataBaseServer) ((SlowChangingDimension2) getTransformation()).getServer();
		DataBaseConnection c = (DataBaseConnection) server.getCurrentConnection(adapter);
		createJdbcResources(c);

		isInited = true;
	}

	private void createJdbcResources(DataBaseConnection c) throws Exception {
		if(c.isUseFullUrl()) {
			if(c.getFullUrl().startsWith("jdbc:oracle")) {
				isOracle = true;
			}
			if(c.getFullUrl().indexOf("?") > 0) {
				String url = c.getFullUrl();// +
											// "&generateSimpleParameterMetadata=true";
				sqlConnection = JdbcConnectionProvider.createSqlConnection(getTransformation().getDocument(), c.getDriverName(), url, c.getLogin(), c.getPassword());
			}
			else {
				String url = c.getFullUrl();// +
											// "?generateSimpleParameterMetadata=true";
				sqlConnection = JdbcConnectionProvider.createSqlConnection(getTransformation().getDocument(), c.getDriverName(), url, c.getLogin(), c.getPassword());
			}
		}
		else {

			String db = c.getDataBaseName() + "?generateSimpleParameterMetadata=true";

			sqlConnection = JdbcConnectionProvider.createSqlConnection(c.getDriverName(), c.getHost(), c.getPort(), db, c.getLogin(), c.getPassword());
			sqlConnection.setCatalog(c.getDataBaseName());
		}

		sqlConnection.setAutoCommit(false);

		versionIndex = ((SlowChangingDimension2) getTransformation()).getTargetVersionIndex();
		if(versionIndex == null) {
			throw new Exception("Version Index must be define in SCD");
		}

		/*
		 * insert
		 */
		String query = prepareInsertQuery();

		insertStatement = sqlConnection.prepareStatement(query);
		info(" created InsertStatement ");
		info(" insert statement query : " + query);

		/*
		 * update
		 */
		query = prepareUpdateQuery();

		updateStatement = sqlConnection.prepareStatement(query);
		info(" created UpdateStatement ");
		debug(" update statement query : " + query);

		/*
		 * select Max
		 */
		query = prepareMaxSelectQuery();

		selectMaxStatement = sqlConnection.prepareStatement(query);
		info(" created select Max Statement ");
		debug(" select Max statement query : " + query);

		/*
		 * select From Max
		 */
		query = prepareSelectForMaxQuery();

		selectForMaxStatement = sqlConnection.prepareStatement(query);
		info(" created select for Max Statement ");
		debug(" select for Max statement query : " + query);

		/*
		 * select
		 */
		query = prepareSelectQuery();

		selectStatement = sqlConnection.prepareStatement(query);
		info(" created select Statement ");
		debug(" select statement query : " + query);

		/*
		 * commit
		 */
		commitStmt = sqlConnection.createStatement();
		info(" created commitStatement ");

		/*
		 * error handler init
		 */
		if(((Trashable) getTransformation()).getTrashTransformation() != null) {
			handleError = true;

			for(RuntimeStep rs : getOutputs()) {
				if(rs.getTransformation() == ((Trashable) getTransformation()).getTrashTransformation()) {
					errorHandler = rs;
					break;
				}
			}

		}
		batchedRows = new ArrayList<Row>(RuntimeEngine.MAX_ROWS);

		commitStmt = sqlConnection.createStatement();
		info(" created prepared Statement for commit");

	}

	/**
	 * perform an update from the given row
	 * 
	 * @param row
	 */
	protected Row performUpdate(Row row, Integer lastVersion) throws Exception {
		SlowChangingDimension2 iu = (SlowChangingDimension2) getTransformation();

		Row newRow = RowFactory.createRow(this);

		StreamDescriptor desc = iu.getDescriptor(iu);
		int counter = 0;
		/*
		 * set keys field values
		 */
		for(int i = 0; i < desc.getColumnCount(); i++) {
			Integer _k = iu.getInputIndexFieldFortargetIndex(i);
			if(_k != null) {

				updateStatement.setObject(counter + 1, row.get(_k));
				// System.out.println("update pos=" + (counter+1) + ";" +
				// row.get(_k));
				counter++;
				newRow.set(i, row.get(i));
			}
		}

		if(versionIndex != null) {
			updateStatement.setObject(counter + 1, lastVersion + 1);
			// System.out.println("update pos=" + (counter+1) + ";" +
			// (lastVersion + 1));
			counter++;
			newRow.set(versionIndex, lastVersion + 1);
		}

		/*
		 * set keys field values
		 */
		for(int i = 0; i < desc.getColumnCount(); i++) {
			Integer _k = iu.getInputIndexKeyFortargetIndex(i);
			if(_k != null) {

				updateStatement.setObject(counter + 1, row.get(_k));
				// System.out.println("update pos=" + (counter+1) + ";" +
				// row.get(_k));
				counter++;
				newRow.set(i, row.get(i));
			}
		}

		if(batchSize < 10000) {
			updateStatement.addBatch();
			batchedRows.add(newRow);
			batchSize++;
		}
		else {
			updateStatement.addBatch();
			batchedRows.add(newRow);
			executeBatch();
		}

		return newRow;
	}

	/**
	 * perform an inert from the given row
	 * 
	 * @param row
	 * @return the outputRow
	 */
	protected Row performInsert(Row row) throws Exception {
		SlowChangingDimension2 iu = (SlowChangingDimension2) getTransformation();

		Row newRow = RowFactory.createRow(this);

		/*
		 * set keys field values
		 */
		int counter = 0;
		for(int i = 0; i < row.getMeta().getSize(); i++) {
			Integer _k = iu.getTargetIndexFieldForInputIndex(i);
			if(_k != null && _k >= 0) {
				insertStatement.setObject(counter + 1, row.get(i));
				counter++;
				newRow.set(_k, row.get(i));
			}
		}

		/*
		 * set keys field values
		 */
		for(int i = 0; i < row.getMeta().getSize(); i++) {
			Integer _k = iu.getTargetIndexKeyForInputIndex(i);
			if(_k != null && _k >= 0) {
				insertStatement.setObject(counter + 1, row.get(i));
				counter++;
				newRow.set(_k, row.get(i));
			}
		}

		if(versionIndex != null) {
			newRow.set(versionIndex, 1);
		}

		if(batchSize <= 10000) {
			insertStatement.addBatch();
			batchedRows.add(newRow);
			batchSize++;
		}
		else {
			insertStatement.addBatch();
			batchedRows.add(newRow);
			executeBatch();
			insertStatement.clearBatch();

		}
		return row;
	}

	protected void executeBatch() throws Exception {
		try {
			int[] result = null;
			result = updateStatement.executeBatch();

			// for(int i : result){
			// writedRows += i;
			// }
			result = insertStatement.executeBatch();
			// for(int i : result){
			// writedRows += i;
			// }
			commitStmt.execute("commit");
			debug(" batch executed");
			batchSize = 0;

			for(Row _r : batchedRows) {
				writeRow(_r);
			}

			batchedRows.clear();
		} catch(Exception ex) {
			if(handleError) {
				for(Row _r : batchedRows) {
					writeErrorRow(_r);
				}

				batchedRows.clear();
				warn("A Problem occured when performing an SQL batch, rows inserted in TrashOutput", ex);
				commitStmt.execute("rollback");

			}
			else {
				//For postgres again
				//best exception handling ever
				if(ex instanceof BatchUpdateException) {
					SQLException sqlEx = ((BatchUpdateException) ex).getNextException();
					if(sqlEx != null) {
						sqlEx.printStackTrace();
					}
				}
				
				warn("Failed to execute batch : \n" + "Insert query : " + insertStatement.toString());
				throw ex;
			}
		}

	}

	@Override
	protected synchronized void setEnd() {
		if(batchSize > 0) {
			try {
				executeBatch();
			} catch(Exception e) {
				e.printStackTrace();
				try {
					((java.sql.BatchUpdateException) e).getNextException().printStackTrace();
				} catch(ClassCastException e1) {

				}
				error(" error when performing last batch", e);
			}
		}
		super.setEnd();
	}

	@Override
	public void releaseResources() {
		try {
			commitStmt.close();
			info(" released commit Statement");
			commitStmt = null;
		} catch(SQLException e) {
			error(" problem when releasing commit Statement", e);
		}

		try {
			selectMaxStatement.close();
			info(" released select Max PreparedStatement");
			selectMaxStatement = null;
		} catch(SQLException e) {
			error(" problem when releasing selectMax PreparedStatement");
		}

		try {
			selectStatement.close();
			info(" released select PreparedStatement");
			selectStatement = null;
		} catch(SQLException e) {
			error(" problem when releasing select PreparedStatement");
		}

		try {
			insertStatement.close();
			info(" released insert PreparedStatement");
			insertStatement = null;
		} catch(SQLException e) {
			error(" problem when releasing insert PreparedStatement");
		}
		try {
			updateStatement.close();
			info(" released update PreparedStatement");
			updateStatement = null;
		} catch(SQLException e) {
			error(" problem when releasing update PreparedStatement");
		}

		try {
			if(!sqlConnection.isClosed()) {
				sqlConnection.close();
				info(" close connection to database");
			}
			else {
				info("Connection to database already closed");
			}
			sqlConnection = null;

		} catch(SQLException e) {
			error(" problem when closing connection to database", e);
		}

		if(batchedRows != null) {
			batchedRows.clear();
			batchedRows = null;

		}
		info("Resources released");
	}

	private String prepareMaxSelectQuery() throws Exception {
		SlowChangingDimension2 iu = (SlowChangingDimension2) getTransformation();
		StreamDescriptor desc = iu.getDescriptor(iu);
		StringBuffer buf = new StringBuffer();

		buf.append("select max(" + desc.getStreamElements().get(versionIndex).name + ") from " + iu.getTableName() + " where ");
		// boolean first = true;
		//
		// for(int i = 0; i < desc.getColumnCount(); i++){
		// Integer _k = iu.getInputIndexKeyFortargetIndex(i);
		//
		//
		// if (_k != null){
		// if (first){
		// first = false;
		// }
		// else{
		// buf.append(" AND ");
		// }
		// buf.append(desc.getStreamElements().get(i).name + "=?");
		// }
		// }
		boolean first = true;
		SortedMap<Integer, String> orderedKeys = new TreeMap<Integer, String>();

		for(int i = 0; i < desc.getColumnCount(); i++) {

			Integer _k = iu.getInputIndexKeyFortargetIndex(i);
			if(_k != null) {
				if(!iu.isIgnoreField(_k)) {

					orderedKeys.put(_k, desc.getStreamElements().get(i).name + "=?");
				}
			}
		}
		for(Integer key : orderedKeys.keySet()) {
			if(first) {
				first = false;
			}
			else {
				buf.append(" AND ");
			}
			buf.append(orderedKeys.get(key));
		}

		return buf.toString();

	}

	private String prepareSelectForMaxQuery() throws Exception {
		SlowChangingDimension2 iu = (SlowChangingDimension2) getTransformation();
		StreamDescriptor desc = iu.getDescriptor(iu);
		StringBuffer buf = new StringBuffer();

		buf.append("select ");
		boolean first = true;
		for(int i = 0; i < desc.getColumnCount(); i++) {
			Integer _k = iu.getInputIndexKeyFortargetIndex(i);
			if(_k != null) {
				if(!iu.isIgnoreField(_k)) {
					if(first) {
						first = false;
					}
					else {
						buf.append(",");
					}
					buf.append(desc.getStreamElements().get(i).name);
				}
			}
		}
		buf.append(" from " + iu.getTableName() + " where ");

		first = true;
		SortedMap<Integer, String> orderedKeys = new TreeMap<Integer, String>();

		for(int i = 0; i < desc.getColumnCount(); i++) {

			Integer _k = iu.getInputIndexKeyFortargetIndex(i);
			if(_k != null) {
				if(!iu.isIgnoreField(_k)) {

					orderedKeys.put(_k, desc.getStreamElements().get(i).name + "=?");
				}
			}
		}
		for(Integer key : orderedKeys.keySet()) {
			if(first) {
				first = false;
			}
			else {
				buf.append(" AND ");
			}
			buf.append(orderedKeys.get(key));
		}

		buf.append(" AND " + desc.getStreamElements().get(iu.getTargetVersionIndex()).name + "=?");

		return buf.toString();

	}

	private String prepareSelectQuery() throws Exception {
		SlowChangingDimension2 iu = (SlowChangingDimension2) getTransformation();
		StreamDescriptor desc = iu.getDescriptor(iu);
		StringBuffer buf = new StringBuffer();

		buf.append("select ");
		// buf.append(desc.getStreamElements().get(versionIndex).name);
		// boolean first = false;
		//
		// for(int i = 0; i < desc.getColumnCount(); i++){
		// Integer _k = iu.getInputIndexFieldFortargetIndex(i);
		//
		//
		// if (_k != null){
		// if (first){
		// first = false;
		// }
		// else{
		// buf.append(", ");
		// }
		// buf.append(desc.getStreamElements().get(i).name );
		// }
		// }
		buf.append("*");
		buf.append(" from " + iu.getTableName() + " where ");
		boolean first = true;

		SortedMap<Integer, String> orderedKeys = new TreeMap<Integer, String>();

		for(int i = 0; i < desc.getColumnCount(); i++) {

			Integer _k = iu.getInputIndexKeyFortargetIndex(i);
			if(_k != null) {
				if(!iu.isIgnoreField(_k)) {

					orderedKeys.put(_k, desc.getStreamElements().get(i).name + "=?");
				}
			}
		}
		for(Integer key : orderedKeys.keySet()) {
			if(first) {
				first = false;
			}
			else {
				buf.append(" AND ");
			}
			buf.append(orderedKeys.get(key));
		}

		return buf.toString();

	}

	/*
	 * update parameters : values and keys
	 */
	protected String prepareUpdateQuery() throws Exception {
		SlowChangingDimension2 iu = (SlowChangingDimension2) getTransformation();
		StreamDescriptor desc = iu.getDescriptor(iu);
		StringBuffer buf = new StringBuffer();

		buf.append("update  " + iu.getTableName() + " set ");
		boolean first = true;

		for(int i = 0; i < desc.getColumnCount(); i++) {
			Integer _k = iu.getInputIndexFieldFortargetIndex(i);
			if(_k != null) {
				if(first) {
					first = false;
				}
				else {
					buf.append(", ");
				}
				buf.append(desc.getStreamElements().get(i).name + "=?");
			}
		}

		if(versionIndex != null) {
			buf.append(", " + desc.getStreamElements().get(versionIndex).name + "=?");
		}

		/*
		 * where
		 */

		first = true;

		for(int i = 0; i < desc.getColumnCount(); i++) {
			Integer _k = iu.getInputIndexKeyFortargetIndex(i);
			if(_k != null) {
				if(first) {
					first = false;
					buf.append(" where ");
				}
				else {
					buf.append(" AND ");
				}
				buf.append(desc.getStreamElements().get(i).name + "=?");
			}
		}
		return buf.toString();

	}

	protected String prepareInsertQuery() throws Exception {
		SlowChangingDimension2 iu = (SlowChangingDimension2) getTransformation();
		StreamDescriptor desc = iu.getDescriptor(iu);
		StringBuffer buf = new StringBuffer();

		buf.append("insert into " + iu.getTableName() + " (");
		int countValues = 0;
		boolean first = true;
		if(versionIndex != null) {
			buf.append(desc.getStreamElements().get(versionIndex).name);
			first = false;

		}
		for(int i = 0; i < desc.getColumnCount(); i++) {
			Integer _k = iu.getInputIndexFieldFortargetIndex(i);
			if(_k != null) {
				if(first) {
					first = false;
				}
				else {
					buf.append(", ");
				}
				buf.append(desc.getStreamElements().get(i).name);
				countValues++;
			}
		}

		for(int i = 0; i < desc.getColumnCount(); i++) {
			Integer _k = iu.getInputIndexKeyFortargetIndex(i);
			if(_k != null) {
				if(first) {
					first = false;
				}
				else {
					buf.append(", ");
				}
				buf.append(desc.getStreamElements().get(i).name);
				countValues++;
			}
		}

		buf.append(") values(");
		first = true;
		if(versionIndex != null) {
			buf.append("1");
			first = false;
		}
		for(int i = 0; i < countValues; i++) {
			if(first) {
				first = false;
			}
			else {
				buf.append(", ");
			}
			buf.append("?");
		}

		buf.append(")");

		return buf.toString();
	}

	protected void writeErrorRow(Row row) throws InterruptedException {
		if(errorHandler != null) {
			errorHandler.insertRow(row, this);
			writedRows++;
		}
	}

	@Override
	protected void writeRow(Row row) throws InterruptedException {
		boolean wrote = false;
		for(RuntimeStep r : getOutputs()) {
			if(r != errorHandler) {
				r.insertRow(row, this);
				wrote = true;
			}

		}
		if(wrote) {
			writedRows++;
		}

	}
}
