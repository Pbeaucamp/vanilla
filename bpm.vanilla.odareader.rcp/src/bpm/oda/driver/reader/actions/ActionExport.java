package bpm.oda.driver.reader.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.oda.driver.reader.Activator;
import bpm.oda.driver.reader.model.dataset.DataSet;
import bpm.oda.driver.reader.model.datasource.DataSource;

public class ActionExport {
	
	//++++ General Constants 
	public static final String lineSep = System.getProperty("line.separator");

	//++++ Constants for XML File
	private static final String TABLE = "table";
	private static final String FIELD = "field";
	private static final String RECORD = "record";
	private static final String NAME = "name";
	private static final String TYPE = "type";
	private static final String VALUE = "value";

	//++++ Constants for CSV File.
	public static final String CSV_SEPARATOR = ";";
	
	//++++ Constants for definition XML file
	public static final String DATA = "data";
	
	public static final String DATASOURCE = "datasource";
	public static final String DATASOURCE_NAME = "name";
	public static final String DATASOURCE_ODA_EXTENSION_DATASOURCE_ID = "odaExtensionDataSourceId";
	public static final String DATASOURCE_ODA_EXTENSION_ID = "odaExtensionId";
	
	public static final String DATASET = "dataset";
	public static final String DATASET_NAME = "name";
	public static final String DATASET_ODA_EXTENSION_DATASET_ID = "odaExtensionDataSetId";
	public static final String DATASET_ODA_EXTENSION_DATASOURCE_ID = "odaExtensionDataSourceId";
	public static final String DATASET_DATASOURCE_NAME = "dataSourceName";
	public static final String DATASET_QUERY = "query";

	public static final String PROPERTIE_PUBLIC = "publicproperties";
	public static final String PROPERTIE_PRIVATE = "publicproperties";
	public static final String PROPERTIE_NAME = "name";
	public static final String PROPERTIE_VALUE = "value";
	
	
/*
 * Method to build an XML file to save the result set
 */
	public static boolean buildXmlFile(IResultSet resultSet, File file){

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder constructor = null;
		try {
			constructor = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		}
		
		Document doc = DocumentHelper.createDocument();
		Element mainElement = DocumentHelper.createElement(TABLE);
		
		//-- Values
		try{
			IResultSetMetaData mtd = resultSet.getMetaData();

			// Rows:
			while(resultSet.next()){
				
				Element elementField = mainElement.addElement(FIELD);

				//columns:
				for(int i = 1; i <= mtd.getColumnCount(); i++){
					
					Element elementRecord = elementField.addElement(RECORD);

					String columnName = mtd.getColumnName(i);
					String columnType = mtd.getColumnTypeName(i);
					String value = resultSet.getString(i);
					
					elementRecord.addAttribute(NAME,columnName);
					elementRecord.addAttribute(TYPE,columnType);
					elementRecord.addAttribute(VALUE,value);
				}
			}
			doc.add(mainElement);

			//Build the XML file
			transformerXml(doc,file);
			resultSet.close();

		}catch(Exception exc){
			exc.printStackTrace();
			return false;
		}

		return true;
	}

/*
 * Method to build an CSV file with ";" separator.
 */
	public static boolean buildCSVFile(IResultSet resultSet, File file){

		//-- Writer
		FileWriter writer = null;

		try {
			writer = new FileWriter(file, true);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}

		//First line : all columns

		String lineColumns  = "";
		IResultSetMetaData mtd;
		try {
			mtd = resultSet.getMetaData();

			for(int i = 1; i <= mtd.getColumnCount(); i++){
				lineColumns += mtd.getColumnName(i) + CSV_SEPARATOR;
			}

			lineColumns += lineSep;

		} catch (OdaException e) {
			e.printStackTrace();
			return false;
		}

		//Write Columns line
		try {
			writer.write(lineColumns,0,lineColumns.length());
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}

		//Records rows
		try {
			while(resultSet.next()){

				String values = "";
				for(int i = 1; i <= mtd.getColumnCount(); i++){
					values += resultSet.getString(i) + CSV_SEPARATOR;
				}

				values += lineSep;

				//Write records line
				try {
					writer.write(values,0,values.length());
				} catch (IOException e1) {
					e1.printStackTrace();
					return false;
				}
			}

			try {
				writer.close();
				resultSet.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

		} catch (OdaException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

/*
 * Method to build a snapshot. 
 * 		-First, a simple file, for disconnected mode.
 * 		-Second, a XML file to save the data source and data set definition, if
 * 			the user wants to refresh a snapshot.
 */
	public static boolean buildSnapshotFile(DataSet dataSet, File fileValue, File fileXML){

	//++++++++ First : build file for disconnected mode.
		FileWriter writer = null;
		try {
			writer = new FileWriter(fileValue, true);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		
		//First line : all columns
		String lineColumns  = "";
		IResultSetMetaData mtd;
		try {
			mtd = dataSet.getResultSet().getMetaData();

			for(int i = 1; i <= mtd.getColumnCount(); i++){
				lineColumns += mtd.getColumnName(i) + CSV_SEPARATOR;
			}

			lineColumns += lineSep;

		} catch (OdaException e) {
			e.printStackTrace();
			return false;
		}

		//Write Columns line
		try {
			writer.write(lineColumns,0,lineColumns.length());
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}

		//Records lines
		try {
			while(dataSet.getResultSet().next()){

				String values = "";
				for(int i = 1; i <= mtd.getColumnCount(); i++){
					values += dataSet.getResultSet().getString(i) + CSV_SEPARATOR;
				}

				values += lineSep;

				//Write records line
				try {
					writer.write(values,0,values.length());
				} catch (IOException e1) {
					e1.printStackTrace();
					return false;
				}

			}

			try {
				writer.close();
				dataSet.getResultSet().close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

		} catch (OdaException e) {
			e.printStackTrace();
			return false;
		}
		
	//++++++++ Second part : Save data set and data source definition
		return saveDefinition(dataSet, fileXML);
	}

/*
 * Method to build an XLS file (version : Excel  97-2003).
 */
	
	public static boolean buildXlsFile(IResultSet resultSet, File file) {

		WritableWorkbook workbook;
		try {
			workbook = Workbook.createWorkbook(file);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} 

		WritableSheet sheet = workbook.createSheet("First Sheet", 0); 

		IResultSetMetaData mtd;
		try {
			mtd = resultSet.getMetaData();
		} catch (OdaException e1) {
			e1.printStackTrace();
			return false;
		}

		//Write columns
		try {
			for(int i = 1; i <= mtd.getColumnCount(); i++){

				Label label = new Label(i-1, 0, mtd.getColumnName(i));
				sheet.addCell(label); 

			}
			
		} catch (RowsExceededException e) {
			e.printStackTrace();
			return false;
		} catch (WriteException e) {
			e.printStackTrace();
			return false;
		} catch (OdaException e) {
			e.printStackTrace();
			return false;
		}

		//Write cells :  difference between String values and numeric values
		try {
			int row = 1;
			while(resultSet.next()){

				for(int i = 1; i <= mtd.getColumnCount(); i++){

					String className = mtd.getColumnTypeName(i);
					String value = resultSet.getString(i);

					if (className.equals(Integer.class.getSimpleName())){

						WritableCellFormat integerFormat = new WritableCellFormat (NumberFormats.INTEGER); 
						jxl.write.Number number2 = new jxl.write.Number(i-1, row, Double.valueOf(value), integerFormat);
						sheet.addCell(number2);  
					}

					else if ((className.equals(Double.class.getSimpleName())) || (className.equals(Float.class.getSimpleName()))){

						WritableCellFormat doubleFormat = new WritableCellFormat (NumberFormats.FLOAT); 
						jxl.write.Number number2 = new jxl.write.Number(i-1, row, Double.valueOf(value), doubleFormat);
						sheet.addCell(number2);  
					}

					else{
						Label label = new Label(i-1, row, resultSet.getString(i));
						sheet.addCell(label); 
					}
				}
				row++;
			}
			
		} catch (RowsExceededException e1) {
			e1.printStackTrace();
			return false;
		} catch (WriteException e1) {
			e1.printStackTrace();
			return false;
		} catch (OdaException e1) {
			e1.printStackTrace();
			return false;
		}

	// Write and close the file.
		try {
			workbook.write();
			workbook.close();

		} catch (WriteException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} 

		return true;
	}

/*
 * Method to save the data source/set definition.
 */

	public static boolean saveDefinition(DataSet dataSet, File fileXML) {
		
		//Save the definition in a XML file
		DataSource dataSource = Activator.getInstance().getDataSource(dataSet);
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder constructor = null;
		try {
			constructor = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		}
		
		Document doc = DocumentHelper.createDocument();

		Element elementData = DocumentHelper.createElement(DATA);

		//++ First element : data source
		Element elementDataSource = elementData.addElement(DATASOURCE);
		
			//-- Attributes
			elementDataSource.addAttribute(DATASOURCE_NAME,dataSource.getName());
			elementDataSource.addAttribute(DATASOURCE_ODA_EXTENSION_DATASOURCE_ID,dataSource.getOdaExtensionDataSourceId());
			elementDataSource.addAttribute(DATASOURCE_ODA_EXTENSION_ID,dataSource.getOdaExtensionId());

			
			//-- Public properties
			Enumeration<Object> enumKeys = dataSource.getPublicProperties().keys();
			Enumeration<Object> enumValues = dataSource.getPublicProperties().elements();
			
			while(enumKeys.hasMoreElements()){
				Element publicProp = elementDataSource.addElement(PROPERTIE_PUBLIC);
				publicProp.addAttribute(PROPERTIE_NAME, enumKeys.nextElement().toString());
				publicProp.addAttribute(PROPERTIE_VALUE, enumValues.nextElement().toString());
			}
			
			//-- Private properties
			enumKeys = dataSource.getPrivateProperties().keys();
			enumValues = dataSource.getPrivateProperties().elements();
			
			while(enumKeys.hasMoreElements()){
				Element publicProp = elementDataSource.addElement(PROPERTIE_PRIVATE);
				publicProp.addAttribute(PROPERTIE_NAME, enumKeys.nextElement().toString());
				publicProp.addAttribute(PROPERTIE_VALUE, enumValues.nextElement().toString());
			}

			
		//++ Second element : data set
		Element elementDataset = elementData.addElement(DATASET);
		
			//-- Attributes
			elementDataset.addAttribute(DATASET_NAME,dataSet.getName());
			elementDataset.addAttribute(DATASET_DATASOURCE_NAME,dataSet.getDataSourceName());
			elementDataset.addAttribute(DATASET_ODA_EXTENSION_DATASET_ID,dataSet.getOdaExtensionDataSetId());
			elementDataset.addAttribute(DATASET_ODA_EXTENSION_DATASOURCE_ID,dataSet.getOdaExtensionDataSourceId());
			elementDataset.addAttribute(DATASET_QUERY,dataSet.getQueryText());

			//-- Public properties
			enumKeys = dataSet.getPublicProperties().keys();
			enumValues = dataSet.getPublicProperties().elements();
			
			while(enumKeys.hasMoreElements()){
				Element publicProp = elementDataset.addElement(PROPERTIE_PUBLIC);
				publicProp.addAttribute(PROPERTIE_NAME, enumKeys.nextElement().toString());
				publicProp.addAttribute(PROPERTIE_VALUE, enumValues.nextElement().toString());
			}
			
			//-- Private properties
			enumKeys = dataSet.getPrivateProperties().keys();
			enumValues = dataSet.getPrivateProperties().elements();
			
			while(enumKeys.hasMoreElements()){
				Element publicProp = elementDataset.addElement(PROPERTIE_PRIVATE);
				publicProp.addAttribute(PROPERTIE_NAME, enumKeys.nextElement().toString());
				publicProp.addAttribute(PROPERTIE_VALUE, enumValues.nextElement().toString());
			}
			
		//Add the node
		doc.add(elementData);

		//Build the XML file
		transformerXml(doc, fileXML);
		
		return true;
	}
	
/*
 * Method to build the final XML file.
 */

	public static void transformerXml(Document document, File pFile) {

		XMLWriter writer;
		try {
			writer = new XMLWriter(new FileOutputStream(pFile), OutputFormat.createPrettyPrint());

			writer.write(document);
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}