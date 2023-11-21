package bpm.metadata.birt.oda.runtime.impl;

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

import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.query.UnitedOlapQuery;

public class UnitedOlapResultSet implements IResultSet {

	private int maxRow;
	private int m_currentRowId = - 1;
	
	private UnitedOlapQuery query;
	private List<List<Object>> values;
	
	private Object lastRead;
	
	public UnitedOlapResultSet(UnitedOlapQuery query, List<List<Object>> values) {
		this.query = query;
		this.values = values;
		
		if(values != null) {
			maxRow = values.size() - 1;
		}
	}
	
	@Override
	public void close() throws OdaException {
		
		
	}

	@Override
	public int findColumn(String arg0) throws OdaException {
		int i = 1;
		for(IDataStreamElement elem : query.getSelect()) {
			if(elem.getName().equals(arg0)) {
				return i;
			}
			i++;
		}
		return 0;
	}

    public boolean getBoolean( int index ) throws OdaException
    {
    	try{
    		
    		Object value = getObject(index);
    		if (value instanceof Boolean){
    			lastRead = value;
    		}
    		else if (value instanceof String){
    			lastRead = Boolean.valueOf((String)value);
    		}
    		return (Boolean)lastRead;
    		
    	}catch(Exception e){
    		lastRead = null;
    		return false;
    	}
    	
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBoolean(java.lang.String)
     */
    public boolean getBoolean( String columnName ) throws OdaException
    {
        return getBoolean( findColumn( columnName ) );
    }

    /* 
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBlob(int)
     */
    public IBlob getBlob( int index ) throws OdaException
    {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBlob(java.lang.String)
     */
    public IBlob getBlob( String columnName ) throws OdaException
    {
        return getBlob( findColumn( columnName ) );
    }

    /* 
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getClob(int)
     */
    public IClob getClob( int index ) throws OdaException
    {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see org.eclipse.datatools.connectivity.oda.IResultSet#getClob(java.lang.String)
     */
    public IClob getClob( String columnName ) throws OdaException
    {
        return getClob( findColumn( columnName ) );
    }

	public int getRow() throws OdaException
	{
		return m_currentRowId;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getString(int)
	 */
	public String getString( int index ) throws OdaException
	{

//		if (values.get(m_currentRowId).size() >= index-1){
//			
//			lastRead = values.get(m_currentRowId).get(index - 1);
//			
//			return values.get(m_currentRowId).get(index - 1);
//
//		}
//		throw new OdaException("No result in the query");
		lastRead= getObject(index);
		if (lastRead != null){
			return lastRead.toString();
		}
		return null;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getString(java.lang.String)
	 */
	public String getString( String columnName ) throws OdaException
	{
	    return getString( findColumn( columnName ) );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getInt(int)
	 */
	public int getInt( int index ) throws OdaException
	{
		try{
    		
    		Object value = getObject(index);
    		if (value instanceof Number){
    			lastRead = Integer.valueOf(value.toString());
    		}
    		else if (value instanceof String){
    			lastRead = Integer.valueOf((String)value);
    		}
    		return (Integer)lastRead;
    		
    	}catch(Exception e){
    		lastRead = null;
    		return 0;
    	}
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getInt(java.lang.String)
	 */
	public int getInt( String columnName ) throws OdaException
	{
	    return getInt( findColumn( columnName ) );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDouble(int)
	 */
	public double getDouble( int index ) throws OdaException
	{
//		try{
//			lastRead = Double.parseDouble(values.get(m_currentRowId).get(index - 1));
//			return Double.parseDouble(values.get(m_currentRowId).get(index - 1));
//		}catch(Exception e){
//			e.printStackTrace();
//			lastRead = null;
//			return 0.f;
//		}
		try{
    		
    		Object value = getObject(index);
    		if (value instanceof Number){
    			lastRead = Double.valueOf(value.toString());
    		}
    		else if (value instanceof String){
    			lastRead = Double.valueOf((String)value);
    		}
    		return (Double)lastRead;
    		
    	}catch(Exception e){
    		lastRead = null;
    		return 0.f;
    	}
		
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDouble(java.lang.String)
	 */
	public double getDouble( String columnName ) throws OdaException
	{
	    return getDouble( findColumn( columnName ) );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal( int index ) throws OdaException
	{
//		try{
//			
//			BigDecimal v = new BigDecimal(values.get(m_currentRowId).get(index - 1).replace(",", "."));
//
//			
//			lastRead = v; 
//		    return v;
//		}catch(Exception e){
//			lastRead = null;
//			return null;
//		}
		try{
			
    		Object value = getObject(index);
    		lastRead = BigDecimal.valueOf(Double.valueOf(value.toString()));
    		return (BigDecimal)lastRead;
    		
    	}catch(Exception e){
    		lastRead = null;
    		return null;
    	}
       
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal( String columnName ) throws OdaException
	{
	    return getBigDecimal( findColumn( columnName ) );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDate(int)
	 */
	public Date getDate( int index ) throws OdaException
	{
//		try{
//			lastRead = Date.valueOf(values.get(m_currentRowId).get(index - 1));
//			return Date.valueOf(values.get(m_currentRowId).get(index - 1));
//		}catch(Exception e){
//			lastRead = null;
//			return null;
//		}
		try{
			
    		Object value = getObject(index);
    		lastRead = Date.valueOf(value.toString());
    		return (Date)lastRead;
    		
    	}catch(Exception e){
    		lastRead = null;
    		return null;
    	}
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getDate(java.lang.String)
	 */
	public Date getDate( String columnName ) throws OdaException
	{
	    return getDate( findColumn( columnName ) );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTime(int)
	 */
	public Time getTime( int index ) throws OdaException
	{
//		try{
//			lastRead = Time.valueOf(values.get(m_currentRowId).get(index - 1));
//			return Time.valueOf(values.get(m_currentRowId).get(index - 1));
//		}catch(Exception e){
//			lastRead = null;
//			return null;
//		}
		try{
			
    		Object value = getObject(index);
    		lastRead = Time.valueOf(value.toString());
    		return (Time)lastRead;
    		
    	}catch(Exception e){
    		lastRead = null;
    		return null;
    	}
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSet#getTime(java.lang.String)
	 */
	public Time getTime( String columnName ) throws OdaException
	{
	    return getTime( findColumn( columnName ) );
	}

	@Override
	public Timestamp getTimestamp( int index ) throws OdaException
	{
//		try{
//			lastRead = Timestamp.valueOf(values.get(m_currentRowId).get(index - 1));
//	        return Timestamp.valueOf(values.get(m_currentRowId).get(index - 1));
//		}catch(Exception e){
//			lastRead = null;
//			return null;
//		}
//		
		try{
			
    		Object value = getObject(index);
    		lastRead = Timestamp.valueOf(value.toString());
    		return (Timestamp)lastRead;
    		
    	}catch(Exception e){
    		lastRead = null;
    		return null;
    	}
        
	}

	@Override
	public Timestamp getTimestamp( String columnName ) throws OdaException
	{
	    return getTimestamp( findColumn( columnName ) );
	}

	@Override
	public boolean next() throws OdaException {
      
      if( m_currentRowId < maxRow )
      {
          m_currentRowId++;
          return true;
      }
      
      return false;     
	}

	@Override
	public void setMaxRows(int arg0) throws OdaException {
		maxRow = arg0;
	}

	@Override
	public boolean wasNull() throws OdaException {
		return lastRead == null;
	}

	@Override
	public IResultSetMetaData getMetaData() throws OdaException {
		return new UnitedOlapResultSetMetadata(query);
	}

	public Object getObject(int arg0) throws OdaException {
		try{
			Object o = values.get(m_currentRowId).get(arg0 - 1);
			lastRead = o;
			return o;
		}catch(Exception ex){
			lastRead = null;
			return null;
		}
	}


	public Object getObject(String arg0) throws OdaException {
		 return getObject( findColumn( arg0 ) );
	}

}
