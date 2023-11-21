package bpm.fd.runtime.model.datas;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.eclipse.datatools.connectivity.oda.IBlob;
import org.eclipse.datatools.connectivity.oda.IClob;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.fd.api.core.model.components.definition.slicer.SlicerData;
import bpm.fd.runtime.model.SlicerState;

public class FilteredResultSet implements IResultSet{
	private SlicerState state;
	private SlicerData data;
	private IResultSet rs;
	
	public FilteredResultSet(SlicerData data, SlicerState state, IResultSet wrapped){
		this.state = state;
		this.rs = wrapped;
		this.data = data;
	}

	@Override
	public void close() throws OdaException {
		rs.close();
		
	}

	@Override
	public int findColumn(String columnName) throws OdaException {
		return rs.findColumn(columnName);
	}

	@Override
	public BigDecimal getBigDecimal(int index) throws OdaException {
		return rs.getBigDecimal(index);
	}

	@Override
	public BigDecimal getBigDecimal(String columnName) throws OdaException {
		return rs.getBigDecimal(columnName);
	}

	@Override
	public IBlob getBlob(int index) throws OdaException {
		return rs.getBlob(index);
	}

	@Override
	public IBlob getBlob(String columnName) throws OdaException {
		return rs.getBlob(columnName);
	}

	@Override
	public boolean getBoolean(int index) throws OdaException {
		
		return rs.getBoolean(index);
	}

	@Override
	public boolean getBoolean(String columnName) throws OdaException {
		
		return rs.getBoolean(columnName);
	}

	@Override
	public IClob getClob(int index) throws OdaException {
		
		return rs.getClob(index);
	}

	@Override
	public IClob getClob(String columnName) throws OdaException {
		
		return rs.getClob(columnName);
	}

	@Override
	public Date getDate(int index) throws OdaException {
		
		return rs.getDate(index);
	}

	@Override
	public Date getDate(String columnName) throws OdaException {
		
		return rs.getDate(columnName);
	}

	@Override
	public double getDouble(int index) throws OdaException {
		
		return rs.getDouble(index);
	}

	@Override
	public double getDouble(String columnName) throws OdaException {
		
		return rs.getDouble(columnName);
	}

	@Override
	public int getInt(int index) throws OdaException {
		
		return rs.getInt(index);
	}

	@Override
	public int getInt(String columnName) throws OdaException {
		
		return rs.getInt(columnName);
	}

	@Override
	public IResultSetMetaData getMetaData() throws OdaException {
		
		return rs.getMetaData();
	}

	@Override
	public Object getObject(int index) throws OdaException {
		
		return rs.getObject(index);
	}

	@Override
	public Object getObject(String columnName) throws OdaException {
		
		return rs.getObject(columnName);
	}

	@Override
	public int getRow() throws OdaException {
		
		return rs.getRow();
	}

	@Override
	public String getString(int index) throws OdaException {
		
		return rs.getString(index);
	}

	@Override
	public String getString(String columnName) throws OdaException {
		
		return rs.getString(columnName);
	}

	@Override
	public Time getTime(int index) throws OdaException {
		
		return rs.getTime(index);
	}

	@Override
	public Time getTime(String columnName) throws OdaException {
		
		return rs.getTime(columnName);
	}

	@Override
	public Timestamp getTimestamp(int index) throws OdaException {
		
		return rs.getTimestamp(index);
	}

	@Override
	public Timestamp getTimestamp(String columnName) throws OdaException {
		
		return rs.getTimestamp(columnName);
	}

	@Override
	public boolean next() throws OdaException {
		boolean  b = true;
		
		do{
			b = rs.next();
			
		}while(b && !(checkValidity()));
			
		return b;
	}

	private boolean checkValidity() throws OdaException{
		
		for (int i = 0; i < data.getLevelCategoriesIndex().size(); i++){
			boolean levelValidated = false;
			if (state.getLevelValues(i) == null){
				return false;
			}
			for(String s : state.getLevelValues(i)){
				
				String val = rs.getString(data.getLevelCategoriesIndex().get(i).getFieldIndex() + 1);
				if (val != null){
					if (val.equals(s)){
						levelValidated = true;
						break;
					}
				}
			}
			if (!levelValidated){
				return false;
			}
		}
		
		return true;
	}

	@Override
	public void setMaxRows(int max) throws OdaException {
		rs.setMaxRows(max);
		
	}

	@Override
	public boolean wasNull() throws OdaException {
		
		return rs.wasNull();
	}
	
	
}
