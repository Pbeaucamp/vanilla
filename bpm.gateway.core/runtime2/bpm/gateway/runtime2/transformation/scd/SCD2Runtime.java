package bpm.gateway.runtime2.transformation.scd;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.SortedMap;
import java.util.TreeMap;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.transformations.outputs.SlowChangingDimension2;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class SCD2Runtime extends RunSCD1 {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private int currentKey = 0;
	private ParameterMetaData parameterMetadata;

	public SCD2Runtime(SlowChangingDimension2 transformation, int bufferSize) {
		super(transformation, bufferSize);
	}

	@Override
	protected Row performInsert(Row row) throws Exception {
		SlowChangingDimension2 iu = (SlowChangingDimension2) getTransformation();
		Row newRow = RowFactory.createRow(this);
		/*
		 * set keys field values
		 */
		int counter = 0;

		Row batchRow = RowFactory.createRow(this);

		for (int i = 0; i < row.getMeta().getSize(); i++) {
			Integer _k = iu.getTargetIndexFieldForInputIndex(i);
			if (_k != null && _k >= 0) {
				java.util.Date theDate = null;
				if (row.get(i) instanceof java.util.Date) {
					theDate = (java.util.Date) row.get(i);
				}
				else {
					// XXX
				}

				if (row.get(i) == null) {
					debug("Going to insert a null in position " + (counter + 1));
				}

				try {
					
					//XXX PGsql decided that sometimes he will return random types
					//like integer for a date or shit like that.
					//I'm starting to think the jdbc developers do that on purpose.
					//Every single fucking problem with jdbc are not problems with jdbc
					//they are problem with POSTGRESQL jdbc.
					//switch (parameterMetadata.getParameterType(counter + 1)) {
					switch (findType(newRow.getMeta().getJavaClasse(_k))) {
					
						case Types.TIMESTAMP:
							if (row.get(i) instanceof Timestamp) {
								insertStatement.setTimestamp(counter + 1, (Timestamp) row.get(i));
							}
							else if (row.get(i) instanceof java.util.Date) {
								insertStatement.setTimestamp(counter + 1, new Timestamp(theDate.getTime()));
							}
							else {
								if (row.get(i) == null) {
									insertStatement.setNull(counter + 1, Types.TIMESTAMP);
								}
								else {
									info("Found timestamp cell, but input is not " + "a recognisable format, inserting as String " + i + " : " + row.dump());
									insertStatement.setString(counter + 1, row.get(i) != null ? row.get(i).toString() : null);
								}
							}
							break;
						case Types.TIME:
							insertStatement.setTime(counter + 1, new Time(((Date) row.get(i)).getTime()));
							break;
						case Types.DATE:
							if(row.get(i) == null || row.get(i).toString().isEmpty() || row.get(i).toString().equalsIgnoreCase("null")) {
								insertStatement.setNull(counter + 1, Types.DATE);
							}
							else {
								insertStatement.setDate(counter + 1, (Date) row.get(i));
							}
							break;
						case Types.INTEGER:
							if (row.get(i) == null)
								insertStatement.setNull(counter + 1, Types.INTEGER);
							else if (row.get(i) instanceof Long)
								insertStatement.setLong(counter + 1, (Long) row.get(i));
							else if (row.get(i) instanceof Integer)
								insertStatement.setInt(counter + 1, (Integer) row.get(i));
							else if (row.get(i) instanceof BigDecimal)
								insertStatement.setBigDecimal(counter + 1, (BigDecimal) row.get(i));
							else if (row.get(i) instanceof String)
								try {
									insertStatement.setInt(counter + 1, Integer.parseInt((String) row.get(i)));
								} catch (Exception e) {
//									e.printStackTrace();
								}
							break;
						default:
							insertStatement.setObject(counter + 1, row.get(i));
					}

				} catch (Throwable t) {
					//FUCKING ORACLE
					if (row.get(i) instanceof Timestamp) {
						insertStatement.setTimestamp(counter + 1, (Timestamp) row.get(i));
					}
					else if (row.get(i) instanceof Long) {
						insertStatement.setLong(counter + 1, (Long) row.get(i));
					}
					else if (row.get(i) instanceof Integer) {
						insertStatement.setInt(counter + 1, (Integer) row.get(i));
					}
					else if (row.get(i) instanceof BigDecimal) {
						insertStatement.setBigDecimal(counter + 1, (BigDecimal) row.get(i));
					}
					else if (row.get(i) instanceof java.util.Date) {
						// TODO convert date into string - to check
//						String converted = sdf.format(row.get(i));
//						insertStatement.setString(counter + 1, converted);
						
						//Why ? It is a date already
						if(row.get(i) == null || row.get(i).toString().isEmpty() || row.get(i).toString().equalsIgnoreCase("null")) {
							insertStatement.setNull(counter + 1, Types.DATE);
						}
						else {
							insertStatement.setDate(counter + 1, (Date) row.get(i));
						}
						
					}
					else if(row.get(i) == null) {
						int type = findType(newRow.getMeta().getJavaClasse(counter + 1));
						insertStatement.setNull(counter + 1, type);
					}
					else if(row.get(i).toString().isEmpty() || row.get(i).toString().equalsIgnoreCase("null")) {
						int type = findType(newRow.getMeta().getJavaClasse(counter + 1));
						if(type == Types.DATE) {
							insertStatement.setNull(counter + 1, type);
						}
						else {
							insertStatement.setObject(counter + 1, row.get(i));
						}
					}
					else {
						insertStatement.setObject(counter + 1, row.get(i));
					}
				}

				batchRow.set(_k, row.get(i));
				counter++;
				newRow.set(_k, row.get(i));
			}
		}

		/*
		 * set keys field values
		 */

		for (int i = 0; i < row.getMeta().getSize(); i++) {

			Integer _k = iu.getTargetIndexKeyForInputIndex(i);
			if (_k != null && _k >= 0) {

				if (row.get(i) instanceof Integer && !startWithZero(row.get(i)))
					insertStatement.setInt(counter + 1, (Integer) row.get(i));
				else if (row.get(i) instanceof BigDecimal && !startWithZero(row.get(i))) {
					BigDecimal big = (BigDecimal) row.get(i);
					insertStatement.setInt(counter + 1, big.intValue());
				}
				else if (row.get(i) instanceof Long && !startWithZero(row.get(i)))
					insertStatement.setInt(counter + 1, ((Long) row.get(i)).intValue());
				else if (row.get(i) instanceof String) {
					try {
//						if (!startWithZero(row.get(i))) {
//							insertStatement.setInt(counter + 1, Integer.parseInt((String) row.get(i)));
//						}
//						else {
							insertStatement.setObject(counter + 1, row.get(i));
//						}
					} catch (Exception e) {
//						warn("Failed to configure insert statement key " + "for an Integer value : " + row.get(i));
						insertStatement.setObject(counter + 1, row.get(i));
					}
				}
				else {
					try {
						debug("inserting key item on position " + counter + 1 + ", type " + row.get(i).getClass().getName());
					} catch (Exception e) {
						debug("inserting key item on position " + counter + 1 + ", type " + row.get(i));
					}
					insertStatement.setObject(counter + 1, row.get(i));
				}
				batchRow.set(_k, row.get(i));
				counter++;
				newRow.set(_k, row.get(i));
			}
		}

		if (iu.getTargetActiveIndex() != null) {
			
			try {
				int val = Integer.parseInt(iu.getActiveValue());
				insertStatement.setInt(counter + 1, val);
			} catch(Exception e) {
				if(iu.getActiveValue().equalsIgnoreCase("true") || iu.getActiveValue().equalsIgnoreCase("false")) {
					boolean val = Boolean.parseBoolean(iu.getActiveValue());
					insertStatement.setBoolean(counter + 1, val);
				}
				else {
					insertStatement.setObject(counter + 1, iu.getActiveValue());
				}
			}
			newRow.set(iu.getTargetActiveIndex(), iu.getActiveValue());
			counter++;
		}

		if (iu.getTargetVersionIndex() != null) {
			int v = getLastVersion(row).lastVersion + 1;
			insertStatement.setObject(counter + 1, v);
			counter++;
			newRow.set(iu.getTargetVersionIndex(), v);
		}

		if (iu.getTargetStartDateIndex() != null) {
			if (iu.getInputDateField() == null || iu.getInputDateField() < 0) {
				String s = sdf.format(Calendar.getInstance().getTime());
				insertStatement.setDate(counter + 1, new java.sql.Date(Calendar.getInstance().getTime().getTime()));
				newRow.set(iu.getTargetStartDateIndex(), s);
			}
			else {
				insertStatement.setObject(counter + 1, row.get(iu.getInputDateField()));
				newRow.set(iu.getTargetStartDateIndex(), row.get(iu.getInputDateField()));
			}

			counter++;
		}
		if (iu.getTargetStopDateIndex() != null) {
			insertStatement.setObject(counter + 1, null);
			counter++;
			newRow.set(iu.getTargetStopDateIndex(), null);
		}

		if (iu.getTargetKeyIndex() != null) {
			insertStatement.setObject(counter + 1, ++currentKey);
			counter++;
			newRow.set(iu.getTargetKeyIndex(), currentKey);
		}

		insertStatement.addBatch();
		batchedRows.add(newRow);
		executeBatch();
		synchronized (batchRow) {
			insertStatement.clearBatch();
		}

		return newRow;
	}

	private boolean startWithZero(Object object) {
		try {
			String value = object.toString();
			return value.startsWith("0");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	protected Row performUpdate(Row row, Integer lastVersion) throws Exception {

		SlowChangingDimension2 iu = (SlowChangingDimension2) getTransformation();

		Row newRow = RowFactory.createRow(this);

		StreamDescriptor desc = iu.getDescriptor(iu);
		
		StringBuffer buf = new StringBuffer();

		buf.append("update  " + iu.getTableName() + " set ");
		boolean first = true;

		if (iu.getTargetActiveIndex() != null) {
			
			try {
				Integer.parseInt(iu.getInactiveValue());
				buf.append(desc.getStreamElements().get(iu.getTargetActiveIndex()).name + "=" + iu.getInactiveValue());
			} catch(Exception e) {
				if(iu.getInactiveValue().equalsIgnoreCase("true") || iu.getInactiveValue().equalsIgnoreCase("false")) {
					boolean val = Boolean.parseBoolean(iu.getInactiveValue());
					buf.append(desc.getStreamElements().get(iu.getTargetActiveIndex()).name + "=" + val);
				}
				else {
					buf.append(desc.getStreamElements().get(iu.getTargetActiveIndex()).name + "='" + iu.getInactiveValue()+"'");
				}
			}
			
			newRow.set(iu.getTargetActiveIndex(), iu.getInactiveValue());
		}

		if (iu.getTargetStopDateIndex() != null) {
//			String s = sdf.format(Calendar.getInstance().getTime());
			Integer index = iu.getInputDateField();
			String dateValue = null;
			if (index != null) {
				dateValue = row.get(index).toString();
			}
			else {
				dateValue = sdf.format(Calendar.getInstance().getTime());
			}
			if(isOracle) {
				buf.append(", " + desc.getStreamElements().get(iu.getTargetStopDateIndex()).name + "= to_date('" + dateValue + "', 'YYYY-MM-DD HH24:MI:SS')");
			}
			else {
				buf.append(", " + desc.getStreamElements().get(iu.getTargetStopDateIndex()).name + "= '" + dateValue + "'");
			}
			newRow.set(iu.getTargetStopDateIndex(), dateValue);
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
				if(row.get(_k) == null) {
					buf.append(desc.getStreamElements().get(i).name + " is null");
				}
				else {
					if(Integer.class.isAssignableFrom(row.get(_k).getClass()) ||
							Long.class.isAssignableFrom(row.get(_k).getClass()) ||
							Double.class.isAssignableFrom(row.get(_k).getClass())) {
						buf.append(desc.getStreamElements().get(i).name + "=" + row.get(_k));
					}
					else {
						try {
							if(((String)row.get(_k)).startsWith("'")) {
								buf.append(desc.getStreamElements().get(i).name + "=" + row.get(_k));
							}
							else {
								buf.append(desc.getStreamElements().get(i).name + "='" + ((String)row.get(_k)).replace("'", "''")                       + "'");
							}
						} catch(Exception e) {
							buf.append(desc.getStreamElements().get(i).name + "='" + row.get(_k) + "'");
						}
					}
				}

				newRow.set(i, row.get(_k));
			}
		}
		
		buf.append(" and " + desc.getStreamElements().get(iu.getTargetActiveIndex()).name + "=" + iu.getActiveValue());
		info("Update query : " + buf.toString());
		
		updateStatement.close();
		
		updateStatement = sqlConnection.prepareStatement(buf.toString());
		updateStatement.executeUpdate();
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		int counter = 0;
//
//		if (iu.getTargetActiveIndex() != null) {
//			
//			try {
//				int val = Integer.parseInt(iu.getInactiveValue());
//				updateStatement.setInt(counter + 1, val);
//			} catch(Exception e) {
//				updateStatement.setObject(counter + 1, iu.getInactiveValue());
//			}
//			
////			updateStatement.setObject(counter + 1, iu.getInactiveValue());
//			counter++;
//			newRow.set(iu.getTargetActiveIndex(), iu.getInactiveValue());
//		}
//		if (iu.getTargetStopDateIndex() != null) {
//			String s = sdf.format(Calendar.getInstance().getTime());
//			updateStatement.setDate(counter + 1, new java.sql.Date(Calendar.getInstance().getTime().getTime()));
//			counter++;
//			newRow.set(iu.getTargetStopDateIndex(), s);
//		}
//
//		for (int i = 0; i < desc.getColumnCount(); i++) {
//
//			Integer _k = iu.getInputIndexKeyFortargetIndex(i);
//			if (_k != null) {
////				if (row.get(_k) instanceof String) {
////					updateStatement.setInt(counter + 1, Integer.parseInt((String) row.get(_k)));
////				}
////				else if (row.get(_k) instanceof BigDecimal) {
////					updateStatement.setInt(counter + 1, ((BigDecimal) row.get(_k)).intValue());
////				}
////				else if (row.get(_k) instanceof Long) {
////					updateStatement.setInt(counter + 1, ((Long) row.get(_k)).intValue());
////				}
////				else {
//					// try at least, probably wont work
//					updateStatement.setObject(counter + 1, row.get(_k));
////				}
//				counter++;
//				newRow.set(i, row.get(_k));
//			}
//
//		}

//		for (int i = 0; i < desc.getColumnCount(); i++) {
//
//			Integer _k = iu.getInputIndexFieldFortargetIndex(i);
//			if (_k != null) {
//				newRow.set(i, row.get(_k));
//			}
//
//		}
//
//		if (batchSize < 10000) {
//			updateStatement.addBatch();
//			batchedRows.add(newRow);
//			batchSize++;
//		}
//		else {
//			updateStatement.addBatch();
//			batchedRows.add(newRow);
//			executeBatch();
//		}
		performInsert(row);
		return newRow;
	}

	protected String prepareInsertQuery() throws Exception {
		SlowChangingDimension2 iu = (SlowChangingDimension2) getTransformation();
		StreamDescriptor desc = iu.getDescriptor(iu);
		StringBuffer buf = new StringBuffer();

		buf.append("insert into " + iu.getTableName() + " (");
		int countValues = 0;
		boolean first = true;

		SortedMap<Integer, String> orderedFields = new TreeMap<Integer, String>();
		for (int i = 0; i < desc.getColumnCount(); i++) {
			Integer _k = iu.getInputIndexFieldFortargetIndex(i);
			if (_k != null) {
				orderedFields.put(_k, desc.getStreamElements().get(i).name);
			}
//				if (first) {
//					first = false;
//				}
//				else {
//					buf.append(", ");
//				}
//				buf.append(desc.getStreamElements().get(i).name);
//				countValues++;
//			}

		}
		
		for (Integer key : orderedFields.keySet()) {
			if (first) {
				first = false;
			}
			else {
				buf.append(", ");
			}
			buf.append(orderedFields.get(key));
			countValues++;
		}

		/*
		 * ere: Do keys, double pass needed
		 */

		SortedMap<Integer, String> orderedKeys = new TreeMap<Integer, String>();

		for (int i = 0; i < desc.getColumnCount(); i++) {
			Integer _k = iu.getInputIndexKeyFortargetIndex(i);

			if (_k != null) {
				orderedKeys.put(_k, desc.getStreamElements().get(i).name);
			}
		}

		for (Integer key : orderedKeys.keySet()) {
			if (first) {
				first = false;
			}
			else {
				buf.append(", ");
			}
			buf.append(orderedKeys.get(key));
			countValues++;
		}

		/*
		 * active field
		 */

		Integer _k = iu.getTargetActiveIndex();
		if (_k != null) {
			if (first) {
				first = false;
			}
			else {
				buf.append(", ");
			}
			buf.append(desc.getStreamElements().get(_k).name);
			countValues++;
		}

		_k = iu.getTargetVersionIndex();
		if (_k != null) {
			if (first) {
				first = false;
			}
			else {
				buf.append(", ");
			}
			buf.append(desc.getStreamElements().get(_k).name);
			countValues++;
		}

		_k = iu.getTargetStartDateIndex();
		if (_k != null) {
			if (first) {
				first = false;
			}
			else {
				buf.append(", ");
			}
			buf.append(desc.getStreamElements().get(_k).name);
			countValues++;
		}

		_k = iu.getTargetStopDateIndex();
		if (_k != null) {
			if (first) {
				first = false;
			}
			else {
				buf.append(", ");
			}
			buf.append(desc.getStreamElements().get(_k).name);
			countValues++;
		}

		_k = iu.getTargetKeyIndex();
		if (_k != null) {
			if (first) {
				first = false;
			}
			else {
				buf.append(", ");
			}
			buf.append(desc.getStreamElements().get(_k).name);
			countValues++;
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

	/*
	 * update parameters : values and keys
	 */
	protected String prepareUpdateQuery() throws Exception {
		SlowChangingDimension2 iu = (SlowChangingDimension2) getTransformation();
		StreamDescriptor desc = iu.getDescriptor(iu);
		StringBuffer buf = new StringBuffer();

		buf.append("update  " + iu.getTableName() + " set ");
		boolean first = true;

		if (iu.getTargetActiveIndex() != null) {
			buf.append(desc.getStreamElements().get(iu.getTargetActiveIndex()).name + "=?");
		}

		if (iu.getTargetStopDateIndex() != null) {
			buf.append(", " + desc.getStreamElements().get(iu.getTargetStopDateIndex()).name + "=?");
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
	public void init(Object adapter) throws Exception {
		super.init(adapter);

		try {
			parameterMetadata = insertStatement.getParameterMetaData();
		} catch (Exception e) {
			//e.printStackTrace();
		}

		if (((SlowChangingDimension2) getTransformation()).getTargetKeyIndex() != null && ((SlowChangingDimension2) getTransformation()).getTargetKeyIndex() >= 0) {
			isInited = false;
			Statement stmt = sqlConnection.createStatement();

			String colName = getTransformation().getDescriptor(getTransformation()).getColumnName(((SlowChangingDimension2) getTransformation()).getTargetKeyIndex());
			String tableName = ((SlowChangingDimension2) getTransformation()).getTableName();

			ResultSet rs = stmt.executeQuery("select max(" + colName + ") from " + tableName);
			while (rs.next()) {
				try {
					currentKey = new Long(rs.getLong(1)).intValue();
				} catch (Exception ex) {
					currentKey = rs.getInt(1);
				}
			}
			rs.close();
			stmt.close();
			info("Last Key found in target table = " + currentKey);
			isInited = false;
		}
		
	}
}
