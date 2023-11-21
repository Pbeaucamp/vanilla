package bpm.nosql.oda.runtime.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.SortSpec;
import org.eclipse.datatools.connectivity.oda.spec.QuerySpecification;

import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;

public class DrillQuery implements IQuery {

	private DrillConnection connection;
	private VanillaPreparedStatement statement;
	
	private String query;
	
	private DrillParameterMetadata parameterMetadata;
	private DrillResultSetMetadata resultSetMetadata;
	
	public DrillQuery(DrillConnection drillConnection) {
		this.connection = drillConnection;
	}

	@Override
	public void cancel() throws OdaException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearInParameters() throws OdaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws OdaException {
		try {
			statement.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public IResultSet executeQuery() throws OdaException {

		try {
			return new DrillResultSet(this, statement.executeQuery());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public int findInParameter(String arg0) throws OdaException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getEffectiveQueryText() {
		return query;
	}

	@Override
	public int getMaxRows() throws OdaException {
		return 0;
	}

	@Override
	public IResultSetMetaData getMetaData() throws OdaException {
		if(resultSetMetadata == null) {
			resultSetMetadata = new DrillResultSetMetadata((DrillResultSet) executeQuery());
		}
		return resultSetMetadata;
	}

	@Override
	public IParameterMetaData getParameterMetaData() throws OdaException {
		if(parameterMetadata == null) {
			parameterMetadata = new DrillParameterMetadata(this);
		}
		return parameterMetadata;
	}

	@Override
	public SortSpec getSortSpec() throws OdaException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuerySpecification getSpecification() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prepare(String arg0) throws OdaException {
		resultSetMetadata = null;
		parameterMetadata = null;
		try {
			statement = connection.getVanillaConnection().prepareQuery(arg0);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void setAppContext(Object arg0) throws OdaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBigDecimal(String arg0, BigDecimal arg1) throws OdaException {
		try {
			statement.setParameter(Integer.parseInt(arg0), arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setBigDecimal(int arg0, BigDecimal arg1) throws OdaException {
		try {
			statement.setParameter(arg0, arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setBoolean(String arg0, boolean arg1) throws OdaException {
		try {
			statement.setParameter(Integer.parseInt(arg0), arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setBoolean(int arg0, boolean arg1) throws OdaException {
		try {
			statement.setParameter(arg0, arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setDate(String arg0, Date arg1) throws OdaException {
		try {
			statement.setParameter(Integer.parseInt(arg0), arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setDate(int arg0, Date arg1) throws OdaException {
		try {
			statement.setParameter(arg0, arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setDouble(String arg0, double arg1) throws OdaException {
		try {
			statement.setParameter(Integer.parseInt(arg0), arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setDouble(int arg0, double arg1) throws OdaException {
		try {
			statement.setParameter(arg0, arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setInt(String arg0, int arg1) throws OdaException {
		try {
			statement.setParameter(Integer.parseInt(arg0), arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setInt(int arg0, int arg1) throws OdaException {
		try {
			statement.setParameter(arg0, arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setMaxRows(int arg0) throws OdaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNull(String arg0) throws OdaException {
//		try {
//			statement.setParameter(Integer.parseInt(arg0), null);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
	}

	@Override
	public void setNull(int arg0) throws OdaException {
//		try {
//			statement.setParameter(Integer.parseInt(arg0), null);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
	}

	@Override
	public void setObject(String arg0, Object arg1) throws OdaException {
		try {
			statement.setParameter(Integer.parseInt(arg0), arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setObject(int arg0, Object arg1) throws OdaException {
		try {
			statement.setParameter(arg0, arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setProperty(String arg0, String arg1) throws OdaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSortSpec(SortSpec arg0) throws OdaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSpecification(QuerySpecification arg0) throws OdaException, UnsupportedOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setString(String arg0, String arg1) throws OdaException {
		try {
			statement.setParameter(Integer.parseInt(arg0), arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setString(int arg0, String arg1) throws OdaException {
		try {
			statement.setParameter(arg0, arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setTime(String arg0, Time arg1) throws OdaException {
		try {
			statement.setParameter(Integer.parseInt(arg0), arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setTime(int arg0, Time arg1) throws OdaException {
		try {
			statement.setParameter(arg0, arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setTimestamp(String arg0, Timestamp arg1) throws OdaException {
		try {
			statement.setParameter(Integer.parseInt(arg0), arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setTimestamp(int arg0, Timestamp arg1) throws OdaException {
		try {
			statement.setParameter(arg0, arg1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public VanillaPreparedStatement getStatement() {
		return statement;
	}

}
