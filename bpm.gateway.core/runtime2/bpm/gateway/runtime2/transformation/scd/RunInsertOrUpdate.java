package bpm.gateway.runtime2.transformation.scd;

import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.gateway.core.transformations.outputs.InsertOrUpdate;
import bpm.gateway.runtime2.RuntimeEngine;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunInsertOrUpdate extends RuntimeStep {

	protected Connection sqlConnection;
	protected Statement commitStmt;
	protected PreparedStatement selectStatement;
	protected PreparedStatement insertStatement;
	protected PreparedStatement updateStatement;
	protected int batchSize = 0;

	protected List<Row> currentInsertBatch = new ArrayList<Row>();

	public RunInsertOrUpdate(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	private void createJdbcResources(DataBaseConnection c) throws Exception {
		if (!c.isUseFullUrl()) {
			sqlConnection = JdbcConnectionProvider.createSqlConnection(c.getDriverName(), c.getHost(), c.getPort(), c.getDataBaseName(), c.getLogin(), c.getPassword());
		}
		else {
			sqlConnection = JdbcConnectionProvider.createSqlConnection(getTransformation().getDocument(), c.getDriverName(), c.getFullUrl(), c.getLogin(), c.getPassword());
		}
		sqlConnection.setCatalog(c.getDataBaseName());

		sqlConnection.setAutoCommit(false);

		/*
		 * insert
		 */
		String query = prepareInsertQuery();

		insertStatement = sqlConnection.prepareStatement(query);
		info(" created InsertStatement ");
		debug(" insert statement query : " + query);

		// /*
		// * update
		// */
		// query = prepareUpdateQuery();
		//
		// updateStatement = sqlConnection.prepareStatement(query);
		// info(" created UpdateStatement ");
		// debug(" update statement query : " + query);
		//
		// /*
		// * select
		// */
		// query = prepareSelectQuery();
		//
		// selectStatement = sqlConnection.prepareStatement(query);
		// info(" created selectStatement ");
		// debug(" select statement query : " + query);

		/*
		 * commit
		 */
		commitStmt = sqlConnection.createStatement();
		info(" created commitStatement ");

		commitStmt = sqlConnection.createStatement();
		info(" created prepared Statement for commit");
	}

	@Override
	public void init(Object adapter) throws Exception {
		DataBaseServer server = (DataBaseServer) ((InsertOrUpdate) getTransformation()).getServer();
		DataBaseConnection c = (DataBaseConnection) server.getCurrentConnection(adapter);
		createJdbcResources(c);

		isInited = true;
	}

	@Override
	public void performRow() throws Exception {
		if (areInputStepAllProcessed()) {
			if (inputEmpty()) {
				setEnd();
			}
		}

		if (isEnd() && inputEmpty()) {
			return;
		}

		if (!isEnd() && inputEmpty()) {
			try {
				Thread.sleep(10);
				return;
			} catch (Exception e) {

			}
		}

		Row row = readRow();

		if (needInsert(row)) {
			performInsert(row);
		}
		else if (needUpdate(row)) {
			performUpdate(row);
		}

	}

	private boolean needInsert(Row row) throws Exception {
		InsertOrUpdate iu = (InsertOrUpdate) getTransformation();
		StreamDescriptor desc = iu.getDescriptor(iu);

		// XXX change to String query for null values
		StringBuilder statement = new StringBuilder();
		statement.append("select ");
		statement.append("*");
		statement.append(" from " + iu.getTableName() + " where ");

		boolean first = true;

		debug("test insert");
		for (int i = 0; i < desc.getColumnCount(); i++) {

			Integer _k = iu.getInputIndexKeyFortargetIndex(i);
			if (_k != null && _k >= 0) {
				if (!iu.isIgnoreField(_k)) {

					if (first) {
						first = false;
					}
					else {
						statement.append(" AND ");
					}
					statement.append(desc.getStreamElements().get(i).name);

					Class<?> c = row.getMeta().getJavaClasse(_k);
					if (Integer.class.isAssignableFrom(c)) {
						try {
							if (row.get(_k) == null) {
								statement.append(" is null");
							}
							else {
								statement.append(" = " + row.get(_k));
							}
						} catch (Exception e) {
							statement.append(" = " + row.get(_k));
						}
					}
					else if (Long.class.isAssignableFrom(c)) {
						try {
							if (row.get(_k) == null) {
								statement.append(" is null");
							}
							else {
								statement.append(" = " + row.get(_k));
							}
						} catch (Exception e) {
							statement.append(" = " + row.get(_k));
						}
					}
					else if (Double.class.isAssignableFrom(c)) {
						try {
							if (row.get(_k) == null) {
								statement.append(" is null");
							}
							else {
								statement.append(" = " + row.get(_k));
							}
						} catch (Exception e) {
							statement.append(" = " + row.get(_k));
						}
					}
					else if (String.class.isAssignableFrom(c)) {
						try {
							if (row.get(_k) == null) {
								statement.append(" is null");
							}
							else {
								if (((String) row.get(_k)).startsWith("'")) {
									statement.append(" = " + row.get(_k));
								}
								else {
									statement.append(" = '" + ((String) row.get(_k)).replace("'", "''") + "'");
								}
							}
						} catch (Exception e) {
							statement.append(" = '" + row.get(_k) + "'");
						}
					}
					else {
						try {
							if (row.get(_k) == null) {
								statement.append(" is null");
							}
							else {

								statement.append(" = '" + ((String) row.get(_k)).replace("'", "''") + "'");

							}
						} catch (Exception e) {
							statement.append(" = '" + row.get(_k) + "'");
						}
					}

					// selectStatement.setObject(++counter, row.get(_k));
					// debug("set on select : pos " + counter + "=" + row.get(_k));
				}
			}
		}
		debug(statement.toString());
		selectStatement = sqlConnection.prepareStatement(statement.toString());

		ResultSet rs = selectStatement.executeQuery();

		while (rs.next()) {
			rs.close();
			debug("no need insert");
			return false;
		}
		rs.close();

		HashMap<Integer, Integer> colToCheck = new HashMap<Integer, Integer>();
		for (int i = 0; i < desc.getColumnCount(); i++) {

			Integer k = iu.getInputIndexKeyFortargetIndex(i);
			if (k != null && k >= 0) {
				if (!iu.isIgnoreField(k)) {

					colToCheck.put(i, k);
				}
			}
		}

		LOOK: for (Row r : currentInsertBatch) {

			for (int key : colToCheck.keySet()) {
				int value = colToCheck.get(key);
				if (r.get(value) == null) {
					if (row.get(value) == null) {
						continue LOOK;
					}
				}
				else {
					if (!r.get(value).equals(row.get(value))) {
						continue LOOK;
					}
				}
			}

			debug("no need insert");
			return false;
		}

		debug("need insert");
		return true;
	}

	/**
	 * 
	 * @param row
	 * @return true if an update must be performed or false otherwise
	 * @throws Exception
	 */
	private boolean needUpdate(Row row) throws Exception {
		InsertOrUpdate iu = (InsertOrUpdate) getTransformation();
		StreamDescriptor desc = iu.getDescriptor(iu);
		debug("test update");
		/*
		 * set parameters in select
		 */
		// XXX change to String query for null values
		StringBuilder statement = new StringBuilder();
		statement.append("select ");
		statement.append("*");
		statement.append(" from " + iu.getTableName() + " where ");

		boolean first = true;

		for (int i = 0; i < desc.getColumnCount(); i++) {

			Integer _k = iu.getInputIndexKeyFortargetIndex(i);
			if (_k != null && _k >= 0) {
				if (!iu.isIgnoreField(_k)) {

					if (first) {
						first = false;
					}
					else {
						statement.append(" AND ");
					}
					statement.append(desc.getStreamElements().get(i).name);

					Class<?> c = row.getMeta().getJavaClasse(_k);
					if (Integer.class.isAssignableFrom(c)) {
						try {
							if (row.get(_k) == null) {
								statement.append(" is null");
							}
							else {
								statement.append(" = " + row.get(_k));
							}
						} catch (Exception e) {
							statement.append(" = " + row.get(_k));
						}
					}
					else if (Long.class.isAssignableFrom(c)) {
						try {
							if (row.get(_k) == null) {
								statement.append(" is null");
							}
							else {
								statement.append(" = " + row.get(_k));
							}
						} catch (Exception e) {
							statement.append(" = " + row.get(_k));
						}
					}
					else if (Double.class.isAssignableFrom(c)) {
						try {
							if (row.get(_k) == null) {
								statement.append(" is null");
							}
							else {
								statement.append(" = " + row.get(_k));
							}
						} catch (Exception e) {
							statement.append(" = " + row.get(_k));
						}
					}
					else if (String.class.isAssignableFrom(c)) {
						try {
							if (row.get(_k) == null) {
								statement.append(" is null");
							}
							else {
								if (((String) row.get(_k)).startsWith("'")) {
									statement.append(" = " + row.get(_k));
								}
								else {
									statement.append(" = '" + ((String) row.get(_k)).replace("'", "''") + "'");
								}
							}
						} catch (Exception e) {
							statement.append(" = '" + row.get(_k) + "'");
						}
					}
					else {
						try {
							if (row.get(_k) == null) {
								statement.append(" is null");
							}
							else {

								statement.append(" = '" + ((String) row.get(_k)).replace("'", "''") + "'");

							}
						} catch (Exception e) {
							statement.append(" = '" + row.get(_k) + "'");
						}
					}

					// selectStatement.setObject(++counter, row.get(_k));
					// debug("set on select : pos " + counter + "=" + row.get(_k));
				}
			}
		}
		debug(statement.toString());
		selectStatement = sqlConnection.prepareStatement(statement.toString());

		ResultSet rs = selectStatement.executeQuery();

		boolean dataChanged = false;
		while (rs.next()) {

			for (int i = 0; i < row.getMeta().getSize(); i++) {

				Integer k = iu.getTargetIndexFieldForInputIndex(i);
				if (k != null && k >= 0) {
					if (!iu.isIgnoreField(i)) {

						if (row.get(i) != null) {

							if (rs.getObject(k + 1) == null) {
								dataChanged = true;
								break;
							}

							Object o1 = row.get(i);
							Object o2 = rs.getObject(k + 1);

							try {
								o1 = Integer.parseInt(row.get(i).toString());
								o2 = Integer.parseInt(rs.getObject(k + 1).toString());
							} catch (Exception e) {
								o1 = row.get(i);
								o2 = rs.getObject(k + 1);
							}

							if (!o1.equals(o2)) {
								if (o1 instanceof Double || o1 instanceof BigDecimal) {
									if (o1 instanceof Double && o2 instanceof BigDecimal) {
										dataChanged = new BigDecimal((Double) o1).equals(o2);
									}
									else if (o2 instanceof Double && o2 instanceof BigDecimal) {
										dataChanged = new BigDecimal((Double) o2).equals(o1);
									}
								}
								else if (o1 instanceof Timestamp && o2 instanceof Date) {
									dataChanged = ((Timestamp) o1).getTime() != ((Date) o2).getTime();
								}
								else if (o2 instanceof Timestamp && o1 instanceof Date) {
									dataChanged = ((Timestamp) o2).getTime() != ((Date) o1).getTime();
								}
								else {
									dataChanged = true;
									break;
								}
							}
						}
						else {
							if (rs.getObject(k + 1) != null) {
								dataChanged = true;
								break;
							}
						}

					}
				}

			}

			if (dataChanged) {
				break;
			}

			break;
		}

		rs.close();

		if (!dataChanged) {

			HashMap<Integer, Integer> colToCheck = new HashMap<Integer, Integer>();
			for (int i = 0; i < desc.getColumnCount(); i++) {

				Integer k = iu.getInputIndexKeyFortargetIndex(i);
				if (k != null && k >= 0) {
					if (!iu.isIgnoreField(k)) {

						colToCheck.put(i, k);
					}
				}
			}

			Row rowTmp = null;
			LOOK: for (Row r : currentInsertBatch) {

				for (int key : colToCheck.keySet()) {
					int value = colToCheck.get(key);

					if (r.get(value) == null) {
						if (row.get(value) == null) {
							continue LOOK;
						}
					}
					else {
						if (!r.get(value).equals(row.get(value))) {
							continue LOOK;
						}
					}
				}

				rowTmp = r;
			}

			// if(rowTmp != null) {
			// for(int i = 0; i < desc.getColumnCount(); i++) {
			//
			// Integer k = iu.getTargetIndexFieldForInputIndex(i);
			// if(k != null && k >= 0) {
			// if(!iu.isIgnoreField(i)) {
			// if(rowTmp.get(k) == null) {
			// if(row.get(k) == null) {
			// dataChanged = true;
			// }
			// }
			// else {
			// if(!rowTmp.get(k).equals(row.get(k))) {
			// dataChanged = true;
			// }
			// }
			// }
			// }
			// }
			// }
		}

		if (dataChanged) {
			debug("need update");
		}
		else {
			debug("no need update");
		}

		return dataChanged;

	}

	/**
	 * perform an update from the given row
	 * 
	 * @param row
	 */
	private void performUpdate(Row row) throws Exception {
		InsertOrUpdate iu = (InsertOrUpdate) getTransformation();
		StreamDescriptor desc = iu.getDescriptor(iu);
		int counter = 0;
		/*
		 * set keys field values
		 */
		debug("perform update");

		// XXX update query as String for null values
		StringBuffer statement = new StringBuffer();

		statement.append("update  " + iu.getTableName() + " set ");
		boolean first = true;
		
		List<Object> parameters = new ArrayList<Object>();

		for (int i = 0; i < desc.getColumnCount(); i++) {
			Integer _k = iu.getInputIndexFieldFortargetIndex(i);
			if (_k != null) {

				if (first) {
					first = false;
				}
				else {
					statement.append(",");
				}

				statement.append(desc.getStreamElements().get(i).name);

				Class<?> c = row.getMeta().getJavaClasse(_k);
				if (Double.class.isAssignableFrom(c) || BigDecimal.class.isAssignableFrom(c) || Long.class.isAssignableFrom(c) || Integer.class.isAssignableFrom(c)) {
					try {
						if (row.get(_k) == null) {
							statement.append(" = ?");
							parameters.add(null);
						}
						else {
							statement.append(" = ?");
							parameters.add(row.get(_k));
						}
					} catch (Exception e) {
						statement.append(" = ?");
						parameters.add(row.get(_k));
					}
				}
				else if (String.class.isAssignableFrom(c)) {
					try {
						if (row.get(_k) == null) {
							statement.append(" = ?");
							parameters.add(null);
						}
						else {
							if (((String) row.get(_k)).startsWith("'")) {
								statement.append(" = ?");
								parameters.add(row.get(_k));
							}
							else {
								statement.append(" = ?");
								//Not need to replace quote now that we use parameter
//								parameters.add(((String) row.get(_k)).replace("'", "''"));
								parameters.add((String)row.get(_k));
							}
						}
					} catch (Exception e) {
						statement.append(" = ?");
						parameters.add(row.get(_k));
					}
				}
				else {
					try {
						if (row.get(_k) == null) {
							statement.append(" = ?");
							parameters.add(null);
						}
						else if (row.get(_k) instanceof String) {
							statement.append(" = ?");
							//Not need to replace quote now that we use parameter
//							parameters.add(((String) row.get(_k)).replace("'", "''"));
							parameters.add((String)row.get(_k));
						}
						else {
							statement.append(" = ?");
							parameters.add(row.get(_k));
						}
					} catch (Exception e) {
						statement.append(" = ?");
						parameters.add(row.get(_k));
					}
				}

				// updateStatement.setObject(counter + 1, row.get(_k));
				// debug("set update pos=" + (counter + 1) + " " + row.get(_k));
				// counter++;
			}
		}
		first = true;
		/*
		 * set keys field values
		 */
		for (int i = 0; i < desc.getColumnCount(); i++) {
			Integer _k = iu.getInputIndexKeyFortargetIndex(i);
			if (_k != null) {

				if (first) {
					first = false;
					statement.append(" where ");
				}
				else {
					statement.append(" AND ");
				}
				if (row.get(_k) == null) {
					statement.append(desc.getStreamElements().get(i).name + " is null");
				}
				else {
					if (Integer.class.isAssignableFrom(row.get(_k).getClass()) || Long.class.isAssignableFrom(row.get(_k).getClass()) || Double.class.isAssignableFrom(row.get(_k).getClass())) {
						statement.append(desc.getStreamElements().get(i).name + "=" + row.get(_k));
					}
					else {
						try {
							if (((String) row.get(_k)).startsWith("'")) {
								statement.append(desc.getStreamElements().get(i).name + "=" + row.get(_k));
							}
							else {
								statement.append(desc.getStreamElements().get(i).name + "='" + ((String) row.get(_k)).replace("'", "''") + "'");
							}
						} catch (Exception e) {
							statement.append(desc.getStreamElements().get(i).name + "='" + row.get(_k) + "'");
						}
					}
				}

				// updateStatement.setObject(counter + 1, row.get(_k));
				// debug("set update pos=" + (counter + 1) + " " + row.get(_k));
				// counter++;
			}
		}

		debug(statement.toString());

		try {
			updateStatement = sqlConnection.prepareStatement(statement.toString());
			
			//Settings parameters
			int indexParam = 1;
			for (Object param : parameters) {
				updateStatement.setObject(indexParam, param);
				indexParam++;
			}
			
			int res = updateStatement.executeUpdate();

			updateStatement.close();
			commitStmt.execute("commit");
			writedRows += res;
		} catch (Exception e) {
			error(statement.toString());
			e.printStackTrace();
			
			if (e.getMessage().equals("Lock wait timeout exceeded; try restarting transaction")) {
				updateStatement.close();
				throw new Exception("Lock wait timeout exceeded.");
			}
		}

		// if(batchSize < RuntimeEngine.MAX_ROWS) {
		// updateStatement.addBatch();
		// batchSize++;
		// }
		// else {
		// updateStatement.addBatch();
		// executeBatch();
		// }
	}

	/**
	 * perform an inert from the given row
	 * 
	 * @param row
	 */
	protected void performInsert(Row row) throws Exception {
		InsertOrUpdate iu = (InsertOrUpdate) getTransformation();
		/*
		 * set keys field values
		 */
		int counter = 0;
		debug("perform insert");
		Row batchRow = RowFactory.createRow(this);

		/*
		 * set keys field values
		 */
		for (int i = 0; i < batchRow.getMeta().getSize(); i++) {

			Integer _k = iu.getInputIndexFieldFortargetIndex(i);
			if (_k != null && _k >= 0) {
				insertStatement.setObject(counter + 1, row.get(_k));
				debug("set insert pos=" + (counter + 1) + " " + row.get(_k));
				batchRow.set(i, row.get(_k));
				counter++;
			}
			else {
				_k = iu.getInputIndexKeyFortargetIndex(i);
				if (_k != null && _k >= 0) {
					insertStatement.setObject(counter + 1, row.get(_k));
					batchRow.set(i, row.get(_k));
					debug("set insert pos=" + (counter + 1) + " " + row.get(_k));
					counter++;
				}
			}
		}

		if (currentInsertBatch.size() < 2000) {
			insertStatement.addBatch();
			synchronized (batchRow) {
				currentInsertBatch.add(row);
			}
			batchSize++;
		}
		else {
			currentInsertBatch.clear();
			insertStatement.addBatch();
			executeBatch();
			synchronized (batchRow) {
				insertStatement.clearBatch();
			}

		}
	}

	protected void executeBatch() throws Exception {
		int[] result = null;

		result = insertStatement.executeBatch();
		for (int i : result) {
			writedRows += i;
		}
		commitStmt.execute("commit");
		debug(" batch executed");
		batchSize = 0;
	}

	@Override
	protected synchronized void setEnd() {
		if (batchSize > 0) {
			try {
				executeBatch();
			} catch (Exception e) {
				error(" error when performing last batch", e);

				if (e instanceof BatchUpdateException) {
					((BatchUpdateException) e).getNextException().printStackTrace();
				}
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
		} catch (Exception e) {
			// error(" problem when releasing commit Statement", e);
		}

		try {
			selectStatement.close();
			info(" released select PreparedStatement");
			selectStatement = null;
		} catch (Exception e) {
			// error(" problem when releasing select PreparedStatement");
		}

		try {
			insertStatement.close();
			info(" released insert PreparedStatement");
			insertStatement = null;
		} catch (Exception e) {
			// error(" problem when releasing insert PreparedStatement");
		}
		try {
			updateStatement.close();
			info(" released update PreparedStatement");
			updateStatement = null;
		} catch (Exception e) {
			// error(" problem when releasing update PreparedStatement");
		}

		try {
			if (!sqlConnection.isClosed()) {
				sqlConnection.close();
				info(" close connection to database");
			}
			else {
				info("Connection to database already closed");
			}
			sqlConnection = null;
		} catch (Exception e) {
			// error(" problem when closing connection to database", e);
		}

	}

//	private String prepareSelectQuery() throws Exception {
//		InsertOrUpdate iu = (InsertOrUpdate) getTransformation();
//		StreamDescriptor desc = iu.getDescriptor(iu);
//		StringBuffer buf = new StringBuffer();
//
//		buf.append("select * from " + iu.getTableName() + " where ");
//		boolean first = true;
//
//		for (int i = 0; i < desc.getColumnCount(); i++) {
//			Integer _k = iu.getInputIndexKeyFortargetIndex(i);
//			if (_k != null) {
//				if (!iu.isIgnoreField(_k)) {
//
//					if (first) {
//						first = false;
//					}
//					else {
//						buf.append(" AND ");
//					}
//					buf.append(desc.getStreamElements().get(i).name + "=?");
//				}
//			}
//		}
//
//		return buf.toString();
//
//	}

	/*
	 * update parameters : values and keys
	 */
	protected String prepareUpdateQuery() throws Exception {
		InsertOrUpdate iu = (InsertOrUpdate) getTransformation();
		StreamDescriptor desc = iu.getDescriptor(iu);
		StringBuffer buf = new StringBuffer();

		buf.append("update  " + iu.getTableName() + " set ");
		boolean first = true;

		for (int i = 0; i < desc.getColumnCount(); i++) {
			Integer _k = iu.getInputIndexFieldFortargetIndex(i);
			if (_k != null) {
				if (first) {
					first = false;
				}
				else {
					buf.append(", ");
				}
				buf.append(desc.getStreamElements().get(i).name + "=?");
			}
		}

		/*
		 * where
		 */

		first = true;

		for (int i = 0; i < desc.getColumnCount(); i++) {
			Integer _k = iu.getInputIndexKeyFortargetIndex(i);
			if (_k != null) {
				if (first) {
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
		InsertOrUpdate iu = (InsertOrUpdate) getTransformation();
		StreamDescriptor desc = iu.getDescriptor(iu);
		StringBuffer buf = new StringBuffer();

		buf.append("insert into " + iu.getTableName() + " (");
		int countValues = 0;
		boolean first = true;

		for (int i = 0; i < desc.getColumnCount(); i++) {
			Integer _k = iu.getInputIndexFieldFortargetIndex(i);
			if (_k != null) {
				if (first) {
					first = false;
				}
				else {
					buf.append(", ");
				}
				buf.append(desc.getStreamElements().get(i).name);
				countValues++;
			}
			else {
				_k = iu.getInputIndexKeyFortargetIndex(i);
				if (_k != null) {
					if (first) {
						first = false;
					}
					else {
						buf.append(", ");
					}
					buf.append(desc.getStreamElements().get(i).name);
					countValues++;
				}
			}
		}

		buf.append(") values(");
		first = true;
		for (int i = 0; i < countValues; i++) {
			if (first) {
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
}
