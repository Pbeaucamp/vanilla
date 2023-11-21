package bpm.united.olap.runtime.projection;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IBlob;
import org.eclipse.datatools.connectivity.oda.IClob;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

public class ProjectionResultSet implements IResultSet {

	private List<CrossMembersValues> values;
	private int actualRow = -1;
	private int maxRow = -1;
	
	public ProjectionResultSet(List<CrossMembersValues> values) {
		this.values = values;
		
	}
	
	@Override
	public void close() throws OdaException {
		
		
	}

	@Override
	public int findColumn(String arg0) throws OdaException {
		
		return 0;
	}

	@Override
	public BigDecimal getBigDecimal(int arg0) throws OdaException {
		
		return null;
	}

	@Override
	public BigDecimal getBigDecimal(String arg0) throws OdaException {
		
		return null;
	}

	@Override
	public IBlob getBlob(int arg0) throws OdaException {
		
		return null;
	}

	@Override
	public IBlob getBlob(String arg0) throws OdaException {
		
		return null;
	}

	@Override
	public boolean getBoolean(int arg0) throws OdaException {
		
		return false;
	}

	@Override
	public boolean getBoolean(String arg0) throws OdaException {
		
		return false;
	}

	@Override
	public IClob getClob(int arg0) throws OdaException {
		
		return null;
	}

	@Override
	public IClob getClob(String arg0) throws OdaException {
		
		return null;
	}

	@Override
	public Date getDate(int arg0) throws OdaException {
		
		return null;
	}

	@Override
	public Date getDate(String arg0) throws OdaException {
		
		return null;
	}

	@Override
	public double getDouble(int arg0) throws OdaException {
		return values.get(actualRow).getOnlyOneValue();
	}

	@Override
	public double getDouble(String arg0) throws OdaException {
		return values.get(actualRow).getOnlyOneValue();
	}

	@Override
	public int getInt(int arg0) throws OdaException {
		
		return 0;
	}

	@Override
	public int getInt(String arg0) throws OdaException {
		
		return 0;
	}

	@Override
	public IResultSetMetaData getMetaData() throws OdaException {
		
		return null;
	}

	@Override
	public Object getObject(int arg0) throws OdaException {
		
		return null;
	}

	@Override
	public Object getObject(String arg0) throws OdaException {
		
		return null;
	}

	@Override
	public int getRow() throws OdaException {
		
		return 0;
	}

	@Override
	public String getString(int arg0) throws OdaException {
		arg0 = arg0 - 1;

		return values.get(actualRow).getMemberUnames().get(arg0);
	}

	@Override
	public String getString(String arg0) throws OdaException {
		
		return null;
	}

	@Override
	public Time getTime(int arg0) throws OdaException {
		
		return null;
	}

	@Override
	public Time getTime(String arg0) throws OdaException {
		
		return null;
	}

	@Override
	public Timestamp getTimestamp(int arg0) throws OdaException {
		
		return null;
	}

	@Override
	public Timestamp getTimestamp(String arg0) throws OdaException {
		
		return null;
	}

	@Override
	public boolean next() throws OdaException {
		actualRow++;
		if(actualRow >= values.size()) {
			return false;
		}
		return true;
		
	}

	@Override
	public void setMaxRows(int arg0) throws OdaException {
		this.maxRow = arg0;
	}

	@Override
	public boolean wasNull() throws OdaException {
		
		return false;
	}

}
