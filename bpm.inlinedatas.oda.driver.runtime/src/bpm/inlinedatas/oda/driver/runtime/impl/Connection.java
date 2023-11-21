/*
 *************************************************************************
 * Copyright (c) 2008 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.inlinedatas.oda.driver.runtime.impl;

import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.inlinedatas.oda.driver.runtime.structureProperties.PropertieColumn;

import com.ibm.icu.util.ULocale;

/**
 * Implementation class of IConnection for an ODA runtime driver.
 */
public class Connection implements IConnection
{
    private boolean m_isOpen = false;
    private ArrayList<PropertieColumn> listeProperties;
   
    
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#open(java.util.Properties)
	 */
	public void open( Properties connProperties ) throws OdaException
	{
		
		listeProperties = new ArrayList<PropertieColumn>();
        
	
		String strNames = connProperties.getProperty(PropertieColumn.PROPERTIES_NAME_COL);
		String strTypes = connProperties.getProperty(PropertieColumn.PROPERTIES_TYPE_COL);
		String strValues = connProperties.getProperty(PropertieColumn.PROPERTIES_VALUES_COL);
		
		int countCol = Integer.valueOf(connProperties.getProperty(PropertieColumn.PROPERTIES_COUNT_COL));
		
		String[] tabStrNames = strNames.split(";");
		String[] tabStrTypes = strTypes.split(";");
		String[] tabStrValues = strValues.split(";");
		
		
		String tempName, tempType = "";
		
		for (int i = 0; i < countCol; i++){
			
			//Extract 1 Columns name
			tempName = tabStrNames[i];
			
			//Extract Column 's Type
			tempType = tabStrTypes[i];
			
			// Columns temp for adding step by step the datas
			PropertieColumn colTemp = new PropertieColumn(tempName, tempType);
			
			
			//Extract all datas for this columns
			int indexValue = i;
			for(int j =  1; j <= tabStrValues.length / countCol; j++){
				
				//Add the data into the temp columns
				colTemp.addNewData(tabStrValues[indexValue]);
				
				indexValue = indexValue + countCol;
			}
			
	
			//-------- Add the columns into the arraylist
			listeProperties.add(colTemp);
			
			
		}

	    m_isOpen = true;        
 	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#setAppContext(java.lang.Object)
	 */
	public void setAppContext( Object context ) throws OdaException
	{
	    // do nothing; assumes no support for pass-through context
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#close()
	 */
	public void close() throws OdaException
	{
        
	    m_isOpen = false;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#isOpen()
	 */
	public boolean isOpen() throws OdaException
	{
        
		return m_isOpen;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#getMetaData(java.lang.String)
	 */
	public IDataSetMetaData getMetaData( String dataSetType ) throws OdaException
	{
	    // assumes that this driver supports only one type of data set,
        // ignores the specified dataSetType
		return new DataSetMetaData( this );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#newQuery(java.lang.String)
	 */
	public IQuery newQuery( String dataSetType) throws OdaException
	{
        // assumes that this driver supports only one type of data set,
        // ignores the specified dataSetType
		return new Query(listeProperties);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#getMaxQueries()
	 */
	public int getMaxQueries() throws OdaException
	{
		return 0;	// no limit
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#commit()
	 */
	public void commit() throws OdaException
	{
	    // do nothing; assumes no transaction support needed
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#rollback()
	 */
	public void rollback() throws OdaException
	{
        // do nothing; assumes no transaction support needed
	}

	public ArrayList<PropertieColumn> getListeProperties() {
		return listeProperties;
	}

	public void setListeProperties(ArrayList<PropertieColumn> listeProperties) {
		this.listeProperties = listeProperties;
	}

	public void setLocale(ULocale locale) throws OdaException {
		
		
	}
    
}
