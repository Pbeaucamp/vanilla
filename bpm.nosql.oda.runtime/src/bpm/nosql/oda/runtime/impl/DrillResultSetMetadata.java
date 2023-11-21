package bpm.nosql.oda.runtime.impl;

import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

public class DrillResultSetMetadata implements IResultSetMetaData{

	private DrillResultSet resultSet;
	
	public DrillResultSetMetadata(DrillResultSet resultSet) {
		this.resultSet = resultSet;
	}
	
	@Override
	public int getColumnCount() throws OdaException {
		try {
			return resultSet.getQuery().getStatement().getQueryMetadata("").getColumnCount();
		} catch(Exception e) {
			throw new OdaException(e);
		}
	}

	@Override
	public int getColumnDisplayLength(int arg0) throws OdaException {
		try {
			return resultSet.getQuery().getStatement().getQueryMetadata("").getColumnDisplaySize(arg0);
		} catch(Exception e) {
			throw new OdaException(e);
		}
	}

	@Override
	public String getColumnLabel(int arg0) throws OdaException {
		try {
			return resultSet.getQuery().getStatement().getQueryMetadata("").getColumnLabel(arg0);
		} catch(Exception e) {
			throw new OdaException(e);
		}
	}

	@Override
	public String getColumnName(int arg0) throws OdaException {
		try {
			return resultSet.getQuery().getStatement().getQueryMetadata("").getColumnName(arg0);
		} catch(Exception e) {
			throw new OdaException(e);
		}
	}

	@Override
	public int getColumnType(int arg0) throws OdaException {
		try {
			return resultSet.getQuery().getStatement().getQueryMetadata("").getColumnType(arg0);
		} catch(Exception e) {
			throw new OdaException(e);
		}
	}

	@Override
	public String getColumnTypeName(int arg0) throws OdaException {
		try {
			return resultSet.getQuery().getStatement().getQueryMetadata("").getColumnTypeName(arg0);
		} catch(Exception e) {
			throw new OdaException(e);
		}
	}

	@Override
	public int getPrecision(int arg0) throws OdaException {
		try {
			return resultSet.getQuery().getStatement().getQueryMetadata("").getPrecision(arg0);
		} catch(Exception e) {
			throw new OdaException(e);
		}
	}

	@Override
	public int getScale(int arg0) throws OdaException {
		try {
			return resultSet.getQuery().getStatement().getQueryMetadata("").getScale(arg0);
		} catch(Exception e) {
			throw new OdaException(e);
		}
	}

	@Override
	public int isNullable(int arg0) throws OdaException {
		try {
			return resultSet.getQuery().getStatement().getQueryMetadata("").isNullable(arg0);
		} catch(Exception e) {
			throw new OdaException(e);
		}
	}

}
