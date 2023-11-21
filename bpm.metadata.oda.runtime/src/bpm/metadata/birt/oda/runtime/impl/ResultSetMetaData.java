/*
 *************************************************************************
 * Copyright (c) 2007 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.metadata.birt.oda.runtime.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.physical.sql.SQLColumn;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.query.Formula;
import bpm.metadata.query.QuerySql;

/**
 * Implementation class of IResultSetMetaData for an ODA runtime driver. <br>
 * For demo purpose, the auto-generated method stubs have hard-coded
 * implementation that returns a pre-defined set of meta-data and query results.
 * A custom ODA driver is expected to implement own data source specific
 * behavior in its place.
 */
public class ResultSetMetaData implements IResultSetMetaData {
	private static class SqlShortType {
		private int type;
		private String name;

		public SqlShortType(int code, String name) {
			this.type = code;
			this.name = name;
		}
	}

	private List<IDataStreamElement> select;
	private List<AggregateFormula> aggs;
	private List<Formula> formulas;

	private List<SqlShortType> types = new ArrayList<SqlShortType>();

	public ResultSetMetaData(QuerySql fmdtQuery, java.sql.ResultSetMetaData rsmd) {
		select = fmdtQuery.getSelect();
		aggs = fmdtQuery.getAggs();
		formulas = fmdtQuery.getFormulas();

		try {
			if (rsmd != null) {
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					types.add(new SqlShortType(rsmd.getColumnType(i + 1), rsmd.getColumnTypeName(i + 1)));
				}
			}
			else {
				for (IDataStreamElement e : select) {
					if (e.getOrigin() != null) {

						types.add(new SqlShortType(((SQLColumn) e.getOrigin()).getSqlTypeCode(), ((SQLColumn) e.getOrigin()).getSqlType()));
					}
					else if (e instanceof ICalculatedElement) {
						switch (((ICalculatedElement) e).getClassType()) {
						case ICalculatedElement.DATE:
							types.add(new SqlShortType(Types.TIMESTAMP, "TIMESTAMP"));
							break;
						case ICalculatedElement.DOUBLE:
							types.add(new SqlShortType(Types.DOUBLE, "DOUBLE"));
							break;
						case ICalculatedElement.INTEGER:
							types.add(new SqlShortType(Types.INTEGER, "INTEGER"));
							break;
						default:
							types.add(new SqlShortType(Types.VARCHAR, "VARCHAR"));
							break;
						}

					}
					else {
						// we force as a string to avoid issues, but we cant
						// possibily knno the type of this column
						types.add(new SqlShortType(Types.VARCHAR, "VARCHAR"));
					}
				}
				for (Formula f : formulas) {
					types.add(new SqlShortType(Types.VARCHAR, "VARCHAR"));
				}
				for (AggregateFormula a : aggs) {
					if (AggregateFormula.MAX.equals(a.getFunction()) || AggregateFormula.MIN.equals(a.getFunction())) {
						if (a.getCol().getOrigin() != null) {
							if (((SQLColumn) a.getCol().getOrigin()).getSqlTypeCode() == Types.VARCHAR) {
								types.add(new SqlShortType(Types.VARCHAR, "VARCHAR"));
							}
							else if (((SQLColumn) a.getCol().getOrigin()).getSqlTypeCode() == Types.DATE || ((SQLColumn) a.getCol().getOrigin()).getSqlTypeCode() == Types.TIMESTAMP || ((SQLColumn) a.getCol().getOrigin()).getSqlTypeCode() == Types.TIME) {
								types.add(new SqlShortType(Types.TIMESTAMP, "TIMESTAMP"));
							}
							else {
								types.add(new SqlShortType(Types.DOUBLE, "DOUBLE"));
							}
						}
						else if (a.getCol() instanceof ICalculatedElement) {
							int t = ((ICalculatedElement) a.getCol()).getClassType();
							switch (t) {
							case ICalculatedElement.DATE:
								types.add(new SqlShortType(Types.TIMESTAMP, "TIMESTAMP"));
								break;
							case ICalculatedElement.STRING:
								types.add(new SqlShortType(Types.VARCHAR, "VARCHAR"));
								break;
							default:
								types.add(new SqlShortType(Types.VARCHAR, "VARCHAR"));
							}

						}
						else {
							types.add(new SqlShortType(Types.DOUBLE, "DOUBLE"));
						}
					}
					else {
						types.add(new SqlShortType(Types.DOUBLE, "DOUBLE"));
					}

				}
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnCount
	 * ()
	 */
	public int getColumnCount() throws OdaException {
		int count = 0;

		if (select != null) {
			count += select.size();
		}

		if (aggs != null) {
			count += aggs.size();
		}

		if (formulas != null) {
			count += formulas.size();
		}

		return count;
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnName
	 * (int)
	 */
	public String getColumnName(int index) throws OdaException {
		if (index - 1 < select.size()) {
			return select.get(index - 1).getName();
		}
		else if (index - 1 < select.size() + formulas.size()) {
			return formulas.get(index - 1 - select.size()).getName();

		}
		else {
			return aggs.get(index - 1 - select.size() - formulas.size()).getOutputName();
		}

	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnLabel
	 * (int)
	 */
	public String getColumnLabel(int index) throws OdaException {
		String out = "";

		if (index - 1 < select.size()) {
			out = select.get(index - 1).getOuputName(Locale.getDefault());

			if (out == null || out.equals("")) {
				if (select.get(index - 1).getOutputName() != null && !select.get(index - 1).getOutputName().equals("")) {
					out = select.get(index - 1).getOutputName();
				}
				else {
					out = getColumnName(index);
				}
			}
		}
		else if (index - 1 < select.size() + formulas.size()) {
			return formulas.get(index - 1 - select.size()).getName();
		}
		else {
			out = aggs.get(index - 1 - select.size() - formulas.size()).getOutputName();
		}

		return out;
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnType
	 * (int)
	 */
	public int getColumnType(int index) throws OdaException {

		try {
			SqlShortType t = types.get(index - 1);
			return t.type;
		} catch (Exception ex) {

		}

		try {
			String c = null;

			if (index - 1 < select.size()) {
				if (select.get(index - 1) instanceof ICalculatedElement) {
					c = ((ICalculatedElement) select.get(index - 1)).getJavaClassName();

				}
				else {
					c = ((SQLColumn) select.get(index - 1).getOrigin()).getJavaClass().getName();
				}
			}
			else {
				return 3;
			}
			if (c.equals(java.lang.String.class.getName())) {
				return Types.VARCHAR;
			}
			else if (c.equals(java.lang.Integer.class.getName())) {
				return Types.INTEGER;
			}
			else if (c.equals(java.lang.Long.class.getName())) {
				return Types.BIGINT;
			}
			else if (c.equals(java.lang.Double.class.getName()) || c.equals(java.lang.Float.class.getName())) {

				return Types.DOUBLE;
			}
			else if (c.equals(java.math.BigDecimal.class.getName())) {
				return Types.DECIMAL;
			}
			else if (c.equals(java.util.Date.class.getName()) || c.equals(java.sql.Date.class.getName())) {
				return Types.DATE;
			}
			else if (c.equals(java.sql.Time.class.getName())) {

				return Types.TIME;
			}
			else if (c.equals(java.sql.Timestamp.class.getName())) {

				return Types.TIMESTAMP;
			}
			else if (c.equals(java.lang.Boolean.class.getName())) {

				return Types.BOOLEAN;
			}

		} catch (Exception e) {
			return 1;
		}

		return 1;
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnTypeName
	 * (int)
	 */
	public String getColumnTypeName(int index) throws OdaException {
		int nativeTypeCode = getColumnType(index);
		return Driver.getNativeDataTypeName(nativeTypeCode);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#
	 * getColumnDisplayLength(int)
	 */
	public int getColumnDisplayLength(int index) throws OdaException {

		// hard-coded for demo purpose
		return 8;
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getPrecision
	 * (int)
	 */
	public int getPrecision(int index) throws OdaException {

		return -1;
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getScale(int)
	 */
	public int getScale(int index) throws OdaException {

		return -1;
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IResultSetMetaData#isNullable(int)
	 */
	public int isNullable(int index) throws OdaException {

		return IResultSetMetaData.columnNullableUnknown;
	}

}
