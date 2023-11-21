

package bpm.google.spreadsheet.oda.driver.runtime.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleAllValuesSheet;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleProperties;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleSheet;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleSheetColumn;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleSheetRow;

import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.ibm.icu.util.ULocale;
/**
 * Implementation class of IConnection for an ODA runtime driver.
 */
public class Connection implements IConnection
{
	private boolean m_isOpen = false;
	private final static String URL_GET_SPREADSHEET = "http://spreadsheets.google.com/feeds/spreadsheets/private/full";

	private SpreadsheetEntry spreadSelected = null;
	private SpreadsheetService myService;

	private OdaGoogleAllValuesSheet listAllValuesSheet;
	private ArrayList<OdaGoogleSheet> listSheets;


	private DateTime lastRead = null;
	
	public Connection(){

	}

	public void open( final Properties connProperties ) throws OdaException
	{

		//*****************  Extract Properties
		String propUser = connProperties.getProperty(OdaGoogleProperties.P_USER);
		String propPass = connProperties.getProperty(OdaGoogleProperties.P_PASS);
		String propWork = connProperties.getProperty(OdaGoogleProperties.P_SPREADSHEET_SELECTED);

		//*****************  Get concerned WorkSheet
		try {
			myService = new SpreadsheetService("DataSource SpreadSheet");
			myService.setUserCredentials(propUser, propPass);

			URL metafeedUrl = new URL(URL_GET_SPREADSHEET);
			SpreadsheetFeed feed = myService.getFeed(metafeedUrl, SpreadsheetFeed.class);

			java.util.List<SpreadsheetEntry> spreadsheets = feed.getEntries();

			//Test with the worksheet name
			SpreadsheetEntry entry = null;
			for (int i = 0; i < spreadsheets.size(); i++) {
				entry = spreadsheets.get(i);
				if(entry.getTitle().getPlainText().equals(propWork)){
					spreadSelected = entry;
				}
			}
		} catch (AuthenticationException e) {
			e.printStackTrace();
		
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		//*****************  Extract Datas from the selected spreadSheet
		try {
			initListSheets();
		} catch (Exception e) {
			e.printStackTrace();
			throw new OdaException(e);
		}
		m_isOpen = true;        
	}

	private Class findType(String s) {
	
		boolean isInteger, isDouble;

	//Long
		try {
			Integer.valueOf(s);
			isInteger = true;
		} catch (Exception e) {
			isInteger = false;
		}
		
	//Double
		try {
			Double.valueOf(s);
			isDouble = true;
		} catch (Exception e) {
			isDouble = false;
		}
		
		if(isInteger){
			return (Integer.class);
		}
		
		else if(isDouble){
			return (Double.class);
		}
		
		else{
			return (String.class);
		}

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
	public IQuery newQuery( String dataSetType ) throws OdaException
	{
		// assumes that this driver supports only one type of data set,
		// ignores the specified dataSetType
		
		try{
			initListSheets();
		}catch(Exception ex){
			throw new OdaException(ex);
		}
		
		return new Query(listSheets);
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

	public ArrayList<OdaGoogleSheet> getListSheets() {
		return new ArrayList<OdaGoogleSheet>(listSheets);
	}

	public void setListSheets(ArrayList<OdaGoogleSheet> listSheets) {
		this.listSheets = listSheets;
	}

	public void setLocale(ULocale locale) throws OdaException {
		
		
	}

	
	
	private void initListSheets() throws Exception{
		
		if (lastRead == null){
			lastRead = new DateTime(new Date());
		}
		else{
			
			URL metafeedUrl = new URL(URL_GET_SPREADSHEET);
			SpreadsheetFeed feed = myService.getFeed(metafeedUrl, SpreadsheetFeed.class);

			java.util.List<SpreadsheetEntry> spreadsheets = feed.getEntries();

			//Test with the worksheet name
			SpreadsheetEntry entry = null;
			for (int i = 0; i < spreadsheets.size(); i++) {
				entry = spreadsheets.get(i);
				if(entry.getTitle().getPlainText().equals(spreadSelected.getTitle().getPlainText())){
					if (entry.getUpdated().compareTo(lastRead) <= 0 ){
						return;
					}
					else{
						spreadSelected = entry;
					}
				}
			}
		}
		
		
		
		java.util.List<WorksheetEntry> worksheets = null;
		
		try {
			worksheets = spreadSelected.getWorksheets();
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new Exception("Error when get Worksheets" + e1.getMessage(), e1);
		} 
		
		//List to get all value for each column for each sheet
		listAllValuesSheet = new OdaGoogleAllValuesSheet();
		listSheets = new ArrayList<OdaGoogleSheet>();


		//Parcours des sheets
		for (int i = 0; i < worksheets.size(); i++) {

			//Get one sheet
			WorksheetEntry worksheet = worksheets.get(i);
			

			//Extract title - row count and col count
			String title = worksheet.getTitle().getPlainText();
			int rowCount = worksheet.getRowCount();
			
			int colCount = worksheet.getColCount();

			//------- Add a new Sheet into the list
			try {

				//Query to get all values
				URL cellFeedUrl = worksheet.getCellFeedUrl();
				CellQuery query = new CellQuery(cellFeedUrl);

				query.setMaximumRow(rowCount);
				query.setMaximumCol(colCount);


				query.setReturnEmpty(true);

				CellFeed feed = myService.query(query, CellFeed.class);


				//Extract values
				listAllValuesSheet.setListValues(new ArrayList<String>());

				for(int indCell = 0; indCell < feed.getEntries().size(); indCell++){
					listAllValuesSheet.getListValues().add(feed.getEntries().get(indCell).getCell().getValue());
				
				}


				// Extract Columns ** Remove empty Columns ** Get the new count col
				listAllValuesSheet.extractColumns(colCount, rowCount);
				int newCountCol = listAllValuesSheet.getCountSortedCol();




				//------- Build correspondant sheet
				OdaGoogleSheet sheet = new OdaGoogleSheet(title,i,newCountCol,rowCount);

				//-------- **Column's Sheet
				OdaGoogleSheetColumn col;
				int indexColTemp = 0;

				for(String colName: listAllValuesSheet.getListSortedCol()){

					col = new  OdaGoogleSheetColumn(indexColTemp,i,colName);
					indexColTemp++;

					//Add the created col
					sheet.getSheetListCol().add(col);
				}

				//-------- **Values Sheet
				OdaGoogleSheetRow row;
				for(int indexRow = 0; indexRow < rowCount; indexRow++){

					row = new OdaGoogleSheetRow();

					for(OdaGoogleSheetColumn currentCol: sheet.getSheetListCol()){

						String cell = listAllValuesSheet.getListValues().get((indexRow*newCountCol)+currentCol.getColIndex());
						row.getRowListCell().add(cell);

					}

					sheet.getSheetListRow().add(row);

				}
				
			//find columns type > look for a full row
				
				boolean findRowComplete = false;
				int indexRow = 0;
				int indexRowComplete = -1;
				
				while((indexRow < sheet.getSheetCountRow()) && (indexRowComplete == -1)){
					
					
					boolean isCellsEmpty = false;
					for(String currentCell : sheet.getSheetListRow().get(indexRow).getRowListCell()){
						
						if (currentCell == null){
							isCellsEmpty = true;
						}
					}
					
					if(isCellsEmpty){
						findRowComplete = false;
					}
					else{
						findRowComplete = true;
						indexRowComplete = indexRow;
					}
					
					indexRow++;
					
				}
				
			//Find column's type with this row and set the column Class
				int indexCurrentColumn = 0;
				for(String ce : sheet.getSheetListRow().get(indexRowComplete).getRowListCell()){
					
					Class classCell = findType(ce);
					
					sheet.getSheetListCol().get(indexCurrentColumn).setColClass(classCell);
					
					indexCurrentColumn++;
				}
				
				//remove final empty rows
				
				boolean endSheet = false;
				int index = 0;
				int lastIndex = -1;
				
				while((!endSheet) && (index < sheet.getSheetCountRow())){
					
					int countEmtyCell = 0;
					for(String currentCell : sheet.getSheetListRow().get(index).getRowListCell()){
						if(currentCell == null){
							countEmtyCell++;
						}
						else{
							if(currentCell.length() == 0){
								countEmtyCell++;
							}
						}
					}
					
					if(countEmtyCell == sheet.getSheetCountCol()){
						endSheet = true;
						lastIndex = index;
					}
					
					index++;
				}
				
				ArrayList<OdaGoogleSheetRow> listRowToRemove = new ArrayList<OdaGoogleSheetRow>();
				if(lastIndex != -1){
					for(int ind = lastIndex; ind < sheet.getSheetCountRow(); ind++){
						listRowToRemove.add(sheet.getSheetListRow().get(ind));
					}
				}
				
				if(!listRowToRemove.isEmpty()){
					sheet.getSheetListRow().removeAll(listRowToRemove);
					sheet.setSheetCountRow(sheet.getSheetListRow().size());
				}
				
				//add the current sheet into the sheet list
				listSheets.add(sheet);


			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServiceException e) {
				e.printStackTrace();
			}

		}
	}
}
