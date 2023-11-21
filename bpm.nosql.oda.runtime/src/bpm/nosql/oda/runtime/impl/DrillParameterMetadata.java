package bpm.nosql.oda.runtime.impl;

import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

public class DrillParameterMetadata implements IParameterMetaData{

	private DrillQuery query;

	public DrillParameterMetadata(DrillQuery drillQuery) {
		this.query = drillQuery;
	}

	@Override
	public int getParameterCount() throws OdaException {
		try {
			return query.getStatement().getParameterMetadata("").getParameterCount();
		} catch(Exception e) {
			throw new OdaException(e);
		}
	}

	@Override
	public int getParameterMode(int arg0) throws OdaException {
		try {
			return query.getStatement().getParameterMetadata("").getParameterMode(arg0);
		} catch(Exception e) {
			throw new OdaException(e);
		}
	}

	@Override
	public String getParameterName(int arg0) throws OdaException {
		try {
			return "parameter_"+arg0;
		} catch(Exception e) {
			throw new OdaException(e);
		}
	}

	@Override
	public int getParameterType(int arg0) throws OdaException {
		try {
			return query.getStatement().getParameterMetadata("").getParameterType(arg0);
		} catch(Exception e) {
			throw new OdaException(e);
		}
	}

	@Override
	public String getParameterTypeName(int arg0) throws OdaException {
		try {
			return query.getStatement().getParameterMetadata("").getParameterTypeName(arg0);
		} catch(Exception e) {
			throw new OdaException(e);
		}
	}

	@Override
	public int getPrecision(int arg0) throws OdaException {
		try {
			return query.getStatement().getParameterMetadata("").getPrecision(arg0);
		} catch(Exception e) {
			throw new OdaException(e);
		}
	}

	@Override
	public int getScale(int arg0) throws OdaException {
		try {
			return query.getStatement().getParameterMetadata("").getScale(arg0);
		} catch(Exception e) {
			throw new OdaException(e);
		}
	}

	@Override
	public int isNullable(int arg0) throws OdaException {
		try {
			return query.getStatement().getParameterMetadata("").isNullable(arg0);
		} catch(Exception e) {
			throw new OdaException(e);
		}
	}

}
