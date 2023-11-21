/*
 *************************************************************************
 * Copyright (c) 2009 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.fm.oda.driver.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

/**
 * Implementation class of IParameterMetaData for an ODA runtime driver.
 * <br>
 * For demo purpose, the auto-generated method stubs have
 * hard-coded implementation that returns a pre-defined set
 * of meta-data and query results.
 * A custom ODA driver is expected to implement own data source specific
 * behavior in its place. 
 */
public class ParameterMetaData implements IParameterMetaData 
{
	public static final String P_START_DATE = "StartDate";
	public static final String P_END_DATE = "EndDate";
	public static final String P_METRIC_ID = "MetricId";
	public static final String P_AXE_ID = "ApplicationId";
	
	private List<String> parameters = new ArrayList<String>();
	
	public ParameterMetaData(boolean dateAsParameter, boolean metricAsParameter, boolean applicationAsParameter) {
		if(dateAsParameter) {
			parameters.add(P_START_DATE);
			parameters.add(P_END_DATE);
		}
		if(metricAsParameter) {
			parameters.add(P_METRIC_ID);
		}
		if(applicationAsParameter) {
			parameters.add(P_AXE_ID);
		}
	}

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getParameterCount()
	 */
	public int getParameterCount() throws OdaException 
	{
		return parameters.size();
	}

    /*
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getParameterMode(int)
	 */
	public int getParameterMode( int param ) throws OdaException 
	{
		return IParameterMetaData.parameterModeIn;
	}

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getParameterName(int)
     */
    public String getParameterName( int param ) throws OdaException
    {
    	if (parameters.size() >= param) {
    		return parameters.get(param - 1);
    	}
       
        return null;    // name is not available
    }

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getParameterType(int)
	 */
	public int getParameterType( int param ) throws OdaException 
	{
        String name = getParameterName(param);
        if(name.equals(P_START_DATE) || name.equals(P_END_DATE)) {
        	return java.sql.Types.DATE;
        }
        else {
        	return java.sql.Types.INTEGER;
        }
	}

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getParameterTypeName(int)
	 */
	public String getParameterTypeName( int param ) throws OdaException 
	{
        int nativeTypeCode = getParameterType( param );
        return Driver.getNativeDataTypeName( nativeTypeCode );
	}

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getPrecision(int)
	 */
	public int getPrecision( int param ) throws OdaException 
	{
		return -1;
	}

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getScale(int)
	 */
	public int getScale( int param ) throws OdaException 
	{
		return -1;
	}

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#isNullable(int)
	 */
	public int isNullable( int param ) throws OdaException 
	{
		return IParameterMetaData.parameterNullableUnknown;
	}

	public int getParameterPosition(String parameterName) {
		int i = 1;
		for(String p : parameters) {
			if(p.equals(parameterName)) {
				return i;
			}
			i++;
		}
		return -1;
	}

}
