/*
 *************************************************************************
 * Copyright (c) 2009 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.csv.oda.runtime.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.csv.oda.runtime.datas.CsvColumn;

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
	private List<CsvColumn> columns = new ArrayList<CsvColumn>();
	private int numberOfRow = 0;


	public ResultSetMetaData(Query query) throws OdaException {
		FileInputStream is = null;
		BufferedReader reader = null;
		try {
			this.query = query;
			File f = new File(this.query.getFilePath());
			is = new FileInputStream(f);
			reader = new BufferedReader(new InputStreamReader(is, query.getEncoding()));

			String line = reader.readLine();
			String[] headers = line.split("\\" + query.getSeparator() + "", -1);

			int pos = 0;
			for (String s : headers) {
				CsvColumn c = new CsvColumn();
				c.setName(s);
				c.setPosition(pos);
				pos++;
				columns.add(c);
			}


			int index = 1;
			while ((line = reader.readLine()) != null) {
				numberOfRow++;
				if (index < 100) {
					index++;
					String[] lineSplit = line.split("\\" + query.getSeparator() + "", -1);

					for(int i = 0; i < lineSplit.length - 1; i++) {
						if (lineSplit[i].equalsIgnoreCase("")) {
							continue;
						}
						else {
							try{
								Integer.parseInt(lineSplit[i]);
								setType(columns.get(i), Types.INTEGER);
								continue;
							}catch(NumberFormatException ex){

							}

							try{
								Float.parseFloat(lineSplit[i]);
								setType(columns.get(i), Types.FLOAT);
								continue;
							}catch(NumberFormatException ex){
								try{
									Float.parseFloat(lineSplit[i].replace(",", "."));
									setType(columns.get(i), Types.FLOAT);;
									continue;
								}catch(NumberFormatException x){

								}
							}

							try{
								Double.parseDouble(lineSplit[i]);
								setType(columns.get(i), Types.DOUBLE);;
								continue;
							}catch(NumberFormatException ex){
								try{
									Double.parseDouble(lineSplit[i].replace(",", "."));
									setType(columns.get(i), Types.DOUBLE);;
									continue;
								}catch(NumberFormatException x){

								}
							}



							try{
								if (isBoolean(lineSplit[i])){
									setType(columns.get(i), Types.BOOLEAN);;
									continue;
								}

							}catch(NumberFormatException ex){

							}
							
							
							
							setType(columns.get(i), Types.CHAR);
						}



					}
				}
			}





		}
		catch (Exception e) {
			e.printStackTrace();
			throw new OdaException(e.getMessage());
		}
		finally {
			try {
				if (reader != null)
					reader.close();
				if (is != null)
					is.close();
			} catch (IOException e) {
				
			}

		}



	}
	private void setType(CsvColumn csvColumn, int integer) throws Exception {
		//		if (csvColumn.getType() != -1 && csvColumn.getType() != integer) {
		//			throw new Exception("Type cannont been found for column " + csvColumn.getPosition());
		//		}
		if (csvColumn.getType() == Types.CHAR) {
			return;
		}
		csvColumn.setType(integer);

	}
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnCount()
	 */
	public int getColumnCount() throws OdaException {
		if(query.getColumnIds().isEmpty()) {
			return columns.size();
		}
		return query.getColumnIds().size();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnName(int)
	 */
	public String getColumnName( int index ) throws OdaException {
		if(query.getColumnIds().isEmpty()) {
			return columns.get(index).getName();
		}
		index = query.getColumnIds().get(index - 1);
		return columns.get(index).getName();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnLabel(int)
	 */
	public String getColumnLabel( int index ) throws OdaException {
		return getColumnName( index );		// default
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnType(int)
	 */
	public int getColumnType( int index ) throws OdaException {
		if(query.getColumnIds().isEmpty()) {
			return columns.get(index).getType();
		}
		index = query.getColumnIds().get(index - 1);
		return columns.get(index).getType();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnTypeName(int)
	 */
	public String getColumnTypeName( int index ) throws OdaException
	{
		int nativeTypeCode = getColumnType( index );
		return Driver.getNativeDataTypeName( nativeTypeCode );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IResultSetMetaData#getColumnDisplayLength(int)
	 */
	public int getColumnDisplayLength( int index ) throws OdaException {
		if(query.getColumnIds().isEmpty()) {
			return columns.get(index).getName().length();
		}
		index = query.getColumnIds().get(index - 1);
		return columns.get(index).getName().length();
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

	private static boolean isBoolean(String s){
		if (s == null || "true".equalsIgnoreCase(s.trim()) || "false".equalsIgnoreCase(s.trim())
				||
				"1".equalsIgnoreCase(s.trim()) || "0".equals(s.trim())){
			return true;
		}
		return false;
	}
	public List<CsvColumn> getColumns() {
		return columns;
	}

	public int getMaxRow() {
		return numberOfRow;
	}



}
