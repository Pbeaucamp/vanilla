package bpm.nosql.oda.runtime.impl;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;

public class DataSetMetaData implements IDataSetMetaData {

	private IConnection c_connection;
	
	public DataSetMetaData(Connection connection) {
		this.c_connection = connection;
	}

	@Override
	public IConnection getConnection() throws OdaException {
		return c_connection;
	}

	@Override
	public int getDataSourceMajorVersion() throws OdaException {
		return 1;
	}

	@Override
	public int getDataSourceMinorVersion() throws OdaException {
		return 0;
	}

	@Override
	public IResultSet getDataSourceObjects(String catalog, String schema,
			String object, String version) throws OdaException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getDataSourceProductName() throws OdaException {
		return "Vanilla NOSQL Data Source";
	}

	@Override
	public String getDataSourceProductVersion() throws OdaException {
		return Integer.toString( getDataSourceMajorVersion() ) + "." +   //$NON-NLS-1$
		   Integer.toString( getDataSourceMinorVersion() );
	}

	@Override
	public int getSQLStateType() throws OdaException {
		return IDataSetMetaData.sqlStateSQL99;
	}

	@Override
	public int getSortMode() {
		return IDataSetMetaData.sortModeNone;
	}

	@Override
	public boolean supportsInParameters() throws OdaException {
		return true;
	}

	@Override
	public boolean supportsMultipleOpenResults() throws OdaException {
		return false;
	}

	@Override
	public boolean supportsMultipleResultSets() throws OdaException {
		return false;
	}

	@Override
	public boolean supportsNamedParameters() throws OdaException {
		return false;
	}

	@Override
	public boolean supportsNamedResultSets() throws OdaException {
		return false;
	}

	@Override
	public boolean supportsOutParameters() throws OdaException {
		return false;
	}

}
