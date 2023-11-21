/*
 *************************************************************************
 * Copyright (c) 2008 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.google.table.oda.driver.runtime.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.google.table.oda.driver.runtime.model.OdaGoogleTableColumn;
import bpm.google.table.oda.driver.runtime.model.OdaGoogleTableModel;
import bpm.google.table.oda.driver.runtime.model.OdaGoogleTableRow;

import com.google.gdata.client.ClientLoginAccountType;
import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Service.GDataRequest;
import com.google.gdata.client.Service.GDataRequest.RequestType;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;
import com.ibm.icu.util.ULocale;

public class Connection implements IConnection
{
    private boolean m_isOpen = false;
    
    private final static String QUERY_SELECT_ALL = "SELECT * FROM ";
    public final static String QUERY_COUNT_ALL = "SELECT COUNT() FROM ";
    
	public final static String URL_TABLE = "http://tables.googlelabs.com/api/query";
	public final static Pattern CSV_VALUE_PATTERN =
		Pattern.compile("([^,\\r\\n\"]*|\"(([^\"]*\"\")*[^\"]*)\")(,|\\r?\\n)");
	
    
	//******* Properties
    public static final String P_USER = "P_USER";
    public static final String P_PASS = "P_PASS";
    public static final String P_ID_TABLE = "P_ID_TABLE";
    
    public static final String P_COLUMN_NAME = "P_COLUMN_NAME";
    public static final String P_COLUMN_TYPE = "P_COLUMN_TYPE";
    public static final String P_COLUMN_COUNT = "P_COLUMN_COUNT";
    public static final String  P_ROW_COUNT = "P_ROW_COUNT";
    
    public static final String PROPERTIES_SEPARATOR = ";";
    
    private String user, pass, idTable, colNames, colTypes, colCount, rowCount;
    
    private GoogleService service;
    
    private OdaGoogleTableModel tableSelected;
    private ArrayList<String> listAllValues;
    
    public Connection(){
    	
    	
    	
    }
    
	public void open( Properties connProperties ) throws OdaException
	{
        
		if (connProperties == null){
			throw new OdaException("No Connection Properties Provided."); 
		}
		
		boolean hasChanged = false;
		
		
		if (user == null || !user.equals(connProperties.getProperty(P_USER))){
			user = connProperties.getProperty(P_USER);
			hasChanged = true;
		}
		
		if (pass == null || !pass.equals(connProperties.getProperty(P_PASS))){
			pass = connProperties.getProperty(P_PASS);
			hasChanged = true;
		}
		
		if (idTable == null || !idTable.equals(connProperties.getProperty(P_ID_TABLE))){
			idTable = connProperties.getProperty(P_ID_TABLE);
			hasChanged = true;
		}
		
		if (colNames == null || !colNames.equals(connProperties.getProperty(P_COLUMN_NAME))){
			colNames = connProperties.getProperty(P_COLUMN_NAME);
			hasChanged = true;
		}
		
		if (colTypes == null || !colTypes.equals(connProperties.getProperty(P_COLUMN_TYPE))){
			colTypes = connProperties.getProperty(P_COLUMN_TYPE);
			hasChanged = true;
		}
		
		if (colCount == null || !colCount.equals(connProperties.getProperty(P_COLUMN_COUNT))){
			colCount = connProperties.getProperty(P_COLUMN_COUNT);
			hasChanged = true;
		}
		
		if (rowCount == null || !rowCount.equals(connProperties.getProperty(P_ROW_COUNT))){
			rowCount = connProperties.getProperty(P_ROW_COUNT);
			hasChanged = true;
		}
		
		
		if (!hasChanged){
			  m_isOpen = true;
			  return;
		}
		else{
			
			//Connection
			try {
				service = new GoogleService("fusiontables", "fusiontables");
				service.setUserCredentials(user, pass, ClientLoginAccountType.GOOGLE);
			} catch (AuthenticationException e) {
				
				e.printStackTrace();
			}
			
			
			//Get datas
			getAllDatas();
			
			
			if(listAllValues != null){

				tableSelected = new OdaGoogleTableModel();

				//Build columns
				ArrayList<OdaGoogleTableColumn> listColumnTemp = new ArrayList<OdaGoogleTableColumn>();

				String[] tabColNames = colNames.split(PROPERTIES_SEPARATOR);
				String[] tabColTypes = colTypes.split(PROPERTIES_SEPARATOR);

				for(int i = 0; i < Integer.valueOf(colCount); i++){

					listColumnTemp.add(new OdaGoogleTableColumn(tabColNames[i], tabColTypes[i], i));

				}

				// Set the column list for current selected Table 
				tableSelected.setListColumns(listColumnTemp);

				// Set the column count for current selected Table 
				tableSelected.setTableCountCol(listColumnTemp.size());

				/* Row count: Google fusion table eliminates lines preceding the heading of columns. 
				 * So, the difference between the values list and the count row getting by the request
				 * must be removed.
				 */

				tableSelected.setTableCountRow(Integer.valueOf(rowCount));

				//Remove this difference: 
				int countRowToRemoved = (listAllValues.size() / tableSelected.getTableCountCol()) - tableSelected.getTableCountRow();
				int countCellsToRemoved = countRowToRemoved * tableSelected.getTableCountCol();
				
				ArrayList<String> tempListString = new ArrayList<String>();
				
				for(int i = 0; i < countCellsToRemoved; i++){
					tempListString.add(listAllValues.get(i));
				}
				
				listAllValues.removeAll(tempListString);
				
				//Init row list
				int indexRow = 0;
				int sizeListValues = listAllValues.size();
				int tableColCount = tableSelected.getTableCountCol();
				
				tempListString.clear();

				while(indexRow < sizeListValues){
					
					for(int indexCol = 0; indexCol < tableColCount; indexCol++){
						
						tempListString.add(listAllValues.get(indexRow + indexCol));
					}
					
					tableSelected.getListRows().add(new OdaGoogleTableRow(tempListString, false));
					
					tempListString.clear();
					
					indexRow = indexRow + tableColCount;
					
				}
				
				//Precise columns type > Number to Integer or Number to Double
				preciseColumnType();
				
			}

			m_isOpen = true;        
		}

 	}
	
	protected void getAllDatas(){
		//Request to get Datas
		  try {
			  //Request like : SELECT * FROM <Id-table>
			  String querySelect = QUERY_SELECT_ALL + idTable;
			  
			  URL url = new URL(
				        URL_TABLE + "?sql=" + URLEncoder.encode(querySelect, "UTF-8"));
				    GDataRequest request = service.getRequestFactory().getRequest(
				            RequestType.QUERY, url, ContentType.TEXT_PLAIN);

				    request.execute();
				    
				    listAllValues = new ArrayList<String>();
				 
				    Scanner scanner = new Scanner(request.getResponseStream());
				    
				  					    
				    while (scanner.hasNextLine()) {
				      scanner.findWithinHorizon(CSV_VALUE_PATTERN, 0);
				      MatchResult match = scanner.match();
				      String quotedString = match.group(2);
				      String decoded = quotedString == null ? match.group(1)
				          : quotedString.replaceAll("\"\"", "\"");
				      
				      listAllValues.add(decoded);
				      
				    }
				    
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
	}
	
	

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#setAppContext(java.lang.Object)
	 */
	public void setAppContext( Object context ) throws OdaException {
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
		return new DataSetMetaData(this);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#newQuery(java.lang.String)
	 */
	public IQuery newQuery( String dataSetType ) throws OdaException
	{
        // assumes that this driver supports only one type of data set,
        // ignores the specified dataSetType
		return new Query(tableSelected);
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

	public OdaGoogleTableModel getTableSelected() {
		return tableSelected;
	}

	public void setTableSelected(OdaGoogleTableModel tableSelected) {
		this.tableSelected = tableSelected;
	}
	
	private void preciseColumnType() {
		
		if(tableSelected != null){
			
			boolean cellNotNull;
			
			int i;
			String fullCell = null;
			
			for(OdaGoogleTableColumn col : tableSelected.getListColumns()){
			
				if(col.getColClass().equals(Number.class)){
					
					//Find full cell in this colum
					i = 0;
					String cellTemp = "";
					cellNotNull = false;
					
					while((cellNotNull == false) && (i < tableSelected.getTableCountRow())){
						
						cellTemp = tableSelected.getListRows().get(i).getListCells().get(col.getColIndex());
						if(cellTemp != null){
							if(cellTemp.length()!= 0){
								cellNotNull = true;
								fullCell = cellTemp;
							}
						}
						
						i++;
					}
					
					//Find the right type of data : Integer or Double
					
					if(fullCell.contains(".")){
						col.setColClass(Double.class);
					}
					else{
						col.setColClass(Integer.class);
					}
				}
				
			}
		}
	}

	public void setLocale(ULocale locale) throws OdaException {
		
		
	}
    
}
