/*
 *************************************************************************
 * Copyright (c) 2009 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.excel.oda.runtime.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

/**
 * Implementation class of IResultSetMetaData for an ODA runtime driver.
 * <br>
 * For demo purpose, the auto-generated method stubs have
 * hard-coded implementation that returns a pre-defined set
 * of meta-data and query results.
 * A custom ODA driver is expected to implement own data source specific
 * behavior in its place. 
 */
public class ResultSetMetaData implements IResultSetMetaData {
	private Query query;
	private Workbook workbookin;
//	private String error;
	private HashMap<Integer, Integer> columnsType = new HashMap<Integer, Integer>();
    
	public ResultSetMetaData(Query query) throws Exception{
		this.query = query;
		
		InputStream is;
		try {
			is = new FileInputStream(query.getFilePath());
			WorkbookSettings set = new WorkbookSettings();
			set.setInitialFileSize(is.available());
			workbookin = Workbook.getWorkbook(is, set);
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new Exception("Unable to find file - " + e.getMessage(), e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("Unable to read file - " + e.getMessage(), e);
		} catch (BiffException e) {
			e.printStackTrace();
			throw new Exception("Unable to parse file - " + e.getMessage(), e);
		}
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnCount()
	 */
	public int getColumnCount() throws OdaException {
        return query.getColumnIds().size();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnName(int)
	 */
	public String getColumnName( int index ) throws OdaException {
		if (workbookin == null) {
			throw new OdaException("null workbook");
		}
		Sheet sheet = workbookin.getSheet(query.getSheetName());
		Cell cell = sheet.getCell(query.getColumnIds().get(index -1), 0);
		
        return cell.getContents();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnLabel(int)
	 */
	public String getColumnLabel( int index ) throws OdaException {
		if (workbookin == null) {
			throw new OdaException("null workbook");
		}
		Sheet sheet = workbookin.getSheet(query.getSheetName());
		Cell cell = sheet.getCell(query.getColumnIds().get(index -1), 0);
		
        return cell.getContents();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnType(int)
	 */
	public int getColumnType( int index ) throws OdaException {
		if (workbookin == null) {
			throw new OdaException("null workbook");
		}
		index = query.getColumnIds().get(index -1);
		
		if (columnsType.get(index) == null) {
			Sheet sheet = workbookin.getSheet(query.getSheetName());

			CellType xlsType = null;
			int max = sheet.getRows();

			for (int i = 1; i < max; i++) {
//				if (xlsType == null) {
					if (sheet.getCell(index, i).getContents().equalsIgnoreCase("")) {
						continue;
					}
					xlsType = sheet.getCell(index, i).getType();
//				}
//				else 
				if (sheet.getCell(index, i).getContents().equalsIgnoreCase("") && xlsType == CellType.NUMBER) {
					columnsType.put(index, java.sql.Types.DECIMAL);
				}
				else if (sheet.getCell(index, i).getType() != xlsType && !sheet.getCell(index, i).getContents().equalsIgnoreCase("")){
					throw new OdaException("Differents data's types are present in col number " + index);
				}
			}

			if (columnsType.get(index) == null) {
				if (xlsType == CellType.DATE) {
					columnsType.put(index, java.sql.Types.DATE);
				}
				else if (xlsType == CellType.NUMBER) {
					columnsType.put(index, java.sql.Types.DOUBLE);
				}
				else if (xlsType == CellType.LABEL) {
					columnsType.put(index, java.sql.Types.CHAR);
				}
				else {
					columnsType.put(index, java.sql.Types.CHAR);
				}
			}
		}
		
		return columnsType.get(index);
		
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnTypeName(int)
	 */
	public String getColumnTypeName( int index ) throws OdaException
	{
        int nativeTypeCode = getColumnType( index );
        return Driver.getNativeDataTypeName(nativeTypeCode );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnDisplayLength(int)
	 */
	public int getColumnDisplayLength( int index ) throws OdaException {
		index = query.getColumnIds().get(index -1);
		return workbookin.getSheet(query.getSheetName()).getCell(index, 0).getContents().length();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getPrecision(int)
	 */
	public int getPrecision( int index ) throws OdaException
	{
        
		return -1;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getScale(int)
	 */
	public int getScale( int index ) throws OdaException
	{
        
		return -1;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#isNullable(int)
	 */
	public int isNullable( int index ) throws OdaException
	{
        
		return IResultSetMetaData.columnNullableUnknown;
	}
	
	public int getMaxRow() {
		int res = 0;
		for  (Sheet s : workbookin.getSheets()) {
			res = Math.max(res, s.getRows());
		}
		
		return res - 1;
	}
	
	public void close() {
		workbookin.close();
		workbookin = null;
	}
    
}
