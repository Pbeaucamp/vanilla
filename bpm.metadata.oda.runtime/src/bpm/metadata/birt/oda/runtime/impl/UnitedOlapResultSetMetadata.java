package bpm.metadata.birt.oda.runtime.impl;

import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.metadata.layer.logical.IDataStreamElement;

public class UnitedOlapResultSetMetadata implements IResultSetMetaData {

	private bpm.metadata.query.UnitedOlapQuery query;
	
	public UnitedOlapResultSetMetadata(bpm.metadata.query.UnitedOlapQuery query) {
		this.query = query;
	}
	
	@Override
	public int getColumnCount() throws OdaException {
		return query.getSelect().size();
	}

	@Override
	public int getColumnDisplayLength(int arg0) throws OdaException {
		return 8;
	}

	@Override
	public String getColumnLabel(int arg0) throws OdaException {
		return query.getSelect().get(arg0 - 1).getOutputName();
	}

	@Override
	public String getColumnName(int arg0) throws OdaException {
		return query.getSelect().get(arg0 - 1).getName();
	}

	@Override
	public int getColumnType(int arg0) throws OdaException {
		if (query.getSelect().get(arg0 - 1).getType().getParentType() == IDataStreamElement.Type.MEASURE){
			return java.sql.Types.DOUBLE;
		}
		else{
			return java.sql.Types.VARCHAR;
		}
		
	}

	@Override
	public String getColumnTypeName(int arg0) throws OdaException {
		return Driver.getNativeDataTypeName( getColumnType(arg0) );
	}

	@Override
	public int getPrecision(int arg0) throws OdaException {
		return -1;
	}

	@Override
	public int getScale(int arg0) throws OdaException {
		return -1;
	}

	@Override
	public int isNullable(int arg0) throws OdaException {
		return IResultSetMetaData.columnNullableUnknown;
	}

}
