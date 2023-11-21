/*
 *************************************************************************
 * Copyright (c) 2008 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.google.spreadsheet.oda.driver.runtime.impl;

import java.util.ArrayList;

import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleParameter;

/**
 * Implementation class of IParameterMetaData for an ODA runtime driver.
 */
public class ParameterMetaData implements IParameterMetaData 
{
	private ArrayList<OdaGoogleParameter> listParameters;

	public ParameterMetaData(ArrayList<OdaGoogleParameter> listQueryParameters) {
		listParameters = new ArrayList<OdaGoogleParameter>();
		
		listParameters.addAll(listQueryParameters);
		
		
	}

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getParameterCount()
	 */
	public int getParameterCount() throws OdaException 
	{
		if(listParameters != null){
			return listParameters.size();
		}
		
		else{
			return 0;
		}
        
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
    	
    	try{
    		return listParameters.get(param-1).getParamName();
	    		
    	}catch(IndexOutOfBoundsException e){
    		
    		throw new OdaException("Only " + listParameters.size() + " parameters in the query, cannot get the parameter " + param);
    	}
 
        
    }

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getParameterType(int)
	 */
	public int getParameterType( int param ) throws OdaException 
	{

		Class typeCol = listParameters.get(param-1).getParamClass();
		int odaScalarTypeReturned = 1;
		
		if(typeCol == Integer.class){
			odaScalarTypeReturned =  4;
		}
		
		else if(typeCol == Double.class){
			odaScalarTypeReturned =  8;
		}
		else{
			odaScalarTypeReturned =  1;
		}
		
		return odaScalarTypeReturned;

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

}
