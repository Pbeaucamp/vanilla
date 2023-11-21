package bpm.nosql.oda.runtime.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import org.eclipse.datatools.connectivity.oda.IBlob;
import org.eclipse.datatools.connectivity.oda.IClob;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

public class DrillResultSet implements IResultSet{

	private DrillQuery query;
	private java.sql.ResultSet resultSet;
	
	public DrillResultSet(DrillQuery query, java.sql.ResultSet resultSet) {
		this.query = query;
		this.resultSet = resultSet;
	}
	
	public DrillQuery getQuery() {
		return query;
	}
	
	@Override
	public void close() throws OdaException {
		try {
			resultSet.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int findColumn(String arg0) throws OdaException {
		try {
			return resultSet.findColumn(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public BigDecimal getBigDecimal(int arg0) throws OdaException {
		try {
			return resultSet.getBigDecimal(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public BigDecimal getBigDecimal(String arg0) throws OdaException {
		try {
			return resultSet.getBigDecimal(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public IBlob getBlob(int arg0) throws OdaException {
		throw new OdaException("Doesn't exists in Drill Jdbc");
	}

	@Override
	public IBlob getBlob(String arg0) throws OdaException {
		throw new OdaException("Doesn't exists in Drill Jdbc");
	}

	@Override
	public boolean getBoolean(int arg0) throws OdaException {
		try {
			return resultSet.getBoolean(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public boolean getBoolean(String arg0) throws OdaException {
		try {
			return resultSet.getBoolean(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public IClob getClob(int arg0) throws OdaException {
		throw new OdaException("Doesn't exists in Drill Jdbc");
	}

	@Override
	public IClob getClob(String arg0) throws OdaException {
		throw new OdaException("Doesn't exists in Drill Jdbc");
	}

	@Override
	public Date getDate(int arg0) throws OdaException {
		try {
			return resultSet.getDate(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public Date getDate(String arg0) throws OdaException {
		try {
			return resultSet.getDate(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public double getDouble(int arg0) throws OdaException {
		try {
			return resultSet.getDouble(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public double getDouble(String arg0) throws OdaException {
		try {
			return resultSet.getDouble(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public int getInt(int arg0) throws OdaException {
		try {
			return resultSet.getInt(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public int getInt(String arg0) throws OdaException {
		try {
			return resultSet.getInt(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public IResultSetMetaData getMetaData() throws OdaException {
		return new DrillResultSetMetadata(this);
	}

	@Override
	public Object getObject(int arg0) throws OdaException {
		try {
			return resultSet.getObject(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public Object getObject(String arg0) throws OdaException {
		try {
			return resultSet.getObject(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public int getRow() throws OdaException {
		try {
			return resultSet.getRow();
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public String getString(int arg0) throws OdaException {
		try {
			return resultSet.getString(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public String getString(String arg0) throws OdaException {
		try {
			return resultSet.getString(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public Time getTime(int arg0) throws OdaException {
		try {
			return resultSet.getTime(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public Time getTime(String arg0) throws OdaException {
		try {
			return resultSet.getTime(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public Timestamp getTimestamp(int arg0) throws OdaException {
		try {
			return resultSet.getTimestamp(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public Timestamp getTimestamp(String arg0) throws OdaException {
		try {
			return resultSet.getTimestamp(arg0);
		} catch(SQLException e) {
			throw new OdaException(e);
		}
	}

	@Override
	public boolean next() throws OdaException {
		try {
			return resultSet.next();
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void setMaxRows(int arg0) throws OdaException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean wasNull() throws OdaException {
		// TODO Auto-generated method stub
		return false;
	}

}
