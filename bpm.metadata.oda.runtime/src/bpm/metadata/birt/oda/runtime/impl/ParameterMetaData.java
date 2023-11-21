/*
 *************************************************************************
 * Copyright (c) 2007 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.metadata.birt.oda.runtime.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.physical.sql.SQLColumn;
import bpm.metadata.resource.Prompt;

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

	
	private List<Object> parameters = new ArrayList<Object>();
	
	public ParameterMetaData(List<IDataStreamElement> columns, List<Prompt> prompts){
//		this.parameters.addAll(columns);
		this.parameters.addAll(prompts);
	}
	
	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getParameterCount()
	 */
	public int getParameterCount() throws OdaException 
	{
        if (parameters != null){
        	return parameters.size();
        }
        
        return 0;
	}

    /*
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getParameterMode(int)
	 */
	public int getParameterMode( int param ) throws OdaException 
	{
        
		if (parameters.get(param - 1) instanceof Prompt){
			return IParameterMetaData.parameterModeIn;
		}
		else if (parameters.get(param - 1) instanceof IDataStreamElement){
			return IParameterMetaData.parameterModeOut;
		}
		
		throw new OdaException("Class " + parameters.get(param - 1).getClass().getName() + " not supported in Query parameters");
	}

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getParameterName(int)
     */
    public String getParameterName( int param ) throws OdaException
    {
    	try{
    		if (parameters.get(param - 1) instanceof Prompt){
    		
    			return ((Prompt)parameters.get(param - 1)).getName();	    	
    		}else if (parameters.get(param - 1) instanceof IDataStreamElement){
	    		return ((IDataStreamElement)parameters.get(param - 1)).getName();	    	
	    	}
	    		
    	}catch(IndexOutOfBoundsException e){
    		throw new OdaException("Only " + parameters.size() + " parameters in the query, cannot get the parameter " + param);
    	}
    	throw new OdaException("Class " + parameters.get(param - 1).getClass().getName() + " not supported in Query parameters");
    }

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getParameterType(int)
	 */
	public int getParameterType( int param ) throws OdaException 
	{

    	try{
    		if (parameters.get(param - 1) instanceof Prompt){
    		
    			if(((Prompt)parameters.get(param - 1)).getGotoDataStreamElement() != null ) {
    			
    				return ((SQLColumn)((Prompt)parameters.get(param - 1)).getGotoDataStreamElement().getOrigin()).getSqlTypeCode();
    			}
    			else {
    				return ((Prompt)parameters.get(param - 1)).getPromptTypeCode();
    			}
    		}else if (parameters.get(param - 1) instanceof IDataStreamElement){
	    		return ((SQLColumn)((IDataStreamElement)parameters.get(param - 1)).getOrigin()).getSqlTypeCode();
	    	}
	    		
    	}catch(IndexOutOfBoundsException e){
    		throw new OdaException("Only " + parameters.size() + " parameters in the query, cannot get the parameter " + param);
    	}catch(Exception ex){
    		//ex.printStackTrace();
    		return java.sql.Types.VARCHAR;
    	}
    	throw new OdaException("Class " + parameters.get(param - 1).getClass().getName() + " not supported in Query parameters");

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
