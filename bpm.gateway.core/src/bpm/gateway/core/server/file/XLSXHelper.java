package bpm.gateway.core.server.file;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import bpm.gateway.core.StreamElement;

public class XLSXHelper implements Closeable {

	private static final String CODE_MAX_ROWS = "29999";
	private static final String DATE_FORMAT = "dd/MM/yyyy";

	private File tmpFile;
	private OPCPackage xlsxPackage;

	public XLSXHelper(File file) throws Exception {
		this.tmpFile = file;
		this.xlsxPackage = OPCPackage.open(tmpFile);
	}

	public static File createTmpFile(InputStream fileStream) {
		try {
			File tmpFile = File.createTempFile("TmpFile_" + new Object().hashCode(), ".tmp");
			Files.copy(fileStream, Paths.get(tmpFile.getPath()), StandardCopyOption.REPLACE_EXISTING);
			return tmpFile;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<String> getSheets() throws IOException, OpenXML4JException, SAXException {
		XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
		XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();

		List<String> names = new ArrayList<>();
		while (iter.hasNext()) {
			InputStream stream = iter.next();
			names.add(iter.getSheetName());
			stream.close();
		}
		return names;
	}

	public HashMap<String, StreamElement> getColumnDefinitions(String transfoName, String sheetName) throws IOException, OpenXML4JException, SAXException {
		HashMap<String, StreamElement> structure = new LinkedHashMap<String, StreamElement>();

		Iterator<List<Cell>> rows = getValues(sheetName, 100);
		if (rows != null && rows.hasNext()) {
			List<String> headers = new ArrayList<String>();

			List<Cell> rowNames = rows.next();
			for (Cell col : rowNames) {
				StreamElement e = new StreamElement();
				e.name = col.getValue();
				e.transfoName = transfoName;
				e.originTransfo = transfoName;
				structure.put(e.name, e);
				headers.add(e.name);
			}

			/*
			 * check the dataType
			 */
			while (rows.hasNext()) {
				List<Cell> row = rows.next();
			
				int k = 0;
				for (Cell cell : row) {
					StreamElement e = structure.get(headers.get(k));
					k++;
					
					if (!"java.lang.String".equals(e.className) && (cell.getType() == CellType.NUMERIC /* || cell.getCellTypeEnum() == CellType.FORMULA */)) {
						e.className = "java.lang.Double";
						e.typeName = "NUMBER";
						continue;
					}
					else if (!"java.lang.Boolean".equals(e.className) && (cell.getType() == CellType.BOOLEAN/* || sheet.getCell(k, i).getType() == CellType.BOOLEAN_FORMULA */)) {
						e.className = "java.util.Boolean";
						e.typeName = "BOOLEAN";
						continue;
					}
					else if (cell.getType() == CellType.BLANK) {
						if (!rows.hasNext() && (e.className == null || e.className.equals(""))) {
							e.className = "java.lang.String";
							e.typeName = "STRING";
						}
						continue;
					}
					else {
						e.className = "java.lang.String";
						e.typeName = "STRING";
					}
				}
			}

		}
		return structure;
	}

	/**
	 * 
	 * @param sheetName
	 * @param maxRows
	 *            (use -1 for no limit)
	 * @return
	 * @throws IOException
	 * @throws OpenXML4JException
	 * @throws SAXException
	 */
	public Iterator<List<Cell>> getValues(String sheetName, int maxRows) throws IOException, OpenXML4JException, SAXException {
		SheetHandler sheetHandler = new SheetHandler(null, maxRows);

		ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(this.xlsxPackage);
		XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
		StylesTable styles = xssfReader.getStylesTable();
		XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
		while (iter.hasNext()) {
			InputStream stream = iter.next();
			if (sheetName.equals(iter.getSheetName())) {

				try {
					processSheet(styles, strings, sheetHandler, stream);
				} catch (Exception e) {
					if (e.getMessage() != null && e.getMessage().equals(CODE_MAX_ROWS)) {
						return sheetHandler.getRows().iterator();
					}
					throw e;
				}

				stream.close();
				break;
			}
			else {
				stream.close();
			}
		}
		return sheetHandler.getRows().iterator();
	}
	
	
	public void streamValues(XLSXRowManager rowManager, String sheetName, int maxRows) throws IOException, OpenXML4JException, SAXException {
		SheetHandler sheetHandler = new SheetHandler(rowManager, maxRows);

		ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(this.xlsxPackage);
		XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
		StylesTable styles = xssfReader.getStylesTable();
		for(short i : styles.getNumberFormats().keySet()) {
			if(styles.getNumberFormats().get(i).contains("#,##0.00")) {
				styles.putNumberFormat(i, "#0.00");
			}
		}
		XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
		while (iter.hasNext()) {
			InputStream stream = iter.next();
			if (sheetName.equals(iter.getSheetName())) {

				try {
					processSheet(styles, strings, sheetHandler, stream);
				} catch (Exception e) {
					if (e.getMessage() != null && e.getMessage().equals(CODE_MAX_ROWS)) {
						rowManager.end();
					}
					throw e;
				}

				stream.close();
				break;
			}
			else {
				stream.close();
			}
		}
		rowManager.end();
	}

	private void processSheet(StylesTable styles, ReadOnlySharedStringsTable strings, SheetHandler sheetHandler, InputStream sheetInputStream) throws IOException, SAXException {
		DataFormatter formatter = new CustomDataFormatter();
//		formatter.setDefaultNumberFormat(new DecimalFormat("###,##"));
		InputSource sheetSource = new InputSource(sheetInputStream);
		try {
			XMLReader sheetParser = SAXHelper.newXMLReader();
			ContentHandler handler = new MyXSSFSheetXMLHandler(styles, null, strings, sheetHandler, formatter, false);
			sheetParser.setContentHandler(handler);
			sheetParser.parse(sheetSource);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
		}
	}

	@Override
	public void close() throws IOException {
		if (xlsxPackage != null) {
			xlsxPackage.close();
		}

		//Do not delete because file is main file sometimes, not only temp. Ask SVI
//		if (tmpFile != null) {
//			tmpFile.delete();
//		}
	}

	private class SheetHandler implements SheetContentsHandler {

		private MyXSSFSheetXMLHandler sheetXMLHandler;
		private int maxRows;

		private boolean firstCellOfRow = false;

		private HashMap<Integer, CellType> colTypes = new HashMap<>();
		private List<List<Cell>> rows;
		private List<Cell> currentRow;
		private XLSXRowManager rowManager;

		private int rowSize = -1;
		private int currentRowNum = -1;
		private int currentColNum = -1;

		public SheetHandler(XLSXRowManager rowManager, int maxRows) {
			this.rowManager = rowManager;
			this.maxRows = maxRows;
			this.rows = new ArrayList<>();
		}

		public void setSheetXMLHandler(MyXSSFSheetXMLHandler sheetXMLHandler) {
			this.sheetXMLHandler = sheetXMLHandler;
		}

		@Override
		public void startRow(int rowNum) {
			// Prepare for this row
			firstCellOfRow = true;
			currentRowNum = rowNum;
			currentColNum = -1;

			currentRow = new ArrayList<>();
			if (rowManager == null) {
				rows.add(currentRow);
			}
		}

		@Override
		public void endRow(int rowNum) {
			if (rowSize == -1) {
				rowSize = currentRow.size();
			}
			
			if (currentRow.size() < rowSize) {
				for (int i = currentRow.size(); i < rowSize; i++) {
					addRowValue(new Cell(), currentColNum);
				}
			}
			
			if (rowManager != null) {
				try {
					rowManager.performRow(currentRow);
				} catch (Exception e) {
					e.printStackTrace();
					rowManager.raiseException(e);
				}
			}
			
			if (maxRows != -1 && rowNum >= maxRows) {
				sheetXMLHandler.setInterruptMaxRows(true);
			}
		}

		@Override
		public void cell(String cellReference, String formattedValue, XSSFComment comment) {
			if (firstCellOfRow) {
				firstCellOfRow = false;
			}

			// gracefully handle missing CellRef here in a similar way as XSSFCell does
			if (cellReference == null) {
				cellReference = new CellAddress(currentRowNum, currentColNum).formatAsString();
			}

			// Did we miss any cells?
			int thisCol = (new CellReference(cellReference)).getCol();
			int missedCols = thisCol - currentColNum - 1;
			for (int i = 0; i < missedCols; i++) {
				addRowValue(new Cell(), thisCol);
			}
			currentColNum = thisCol;

			// Type of cell
			if (formattedValue == null || formattedValue.isEmpty()) {
				addRowValue(new Cell(), currentColNum);
			}
			else {
				try {
					// noinspection ResultOfMethodCallIgnored
					addRowValue(new Cell(Double.parseDouble(formattedValue.replace(',','.')), formattedValue), currentColNum);
				} catch (NumberFormatException e) {
					if (formattedValue.equals("true") || formattedValue.equals("false")) {
						addRowValue(new Cell(Boolean.parseBoolean(formattedValue), formattedValue), currentColNum);
					}
					else {
						addRowValue(new Cell(formattedValue), currentColNum);
					}
				}
			}
		}
		
		private void addRowValue(Cell cell, int colNum) {
			currentRow.add(cell);
			
			CellType type = colTypes.get(colNum);
			type = getMainType(type, cell.getType());
			cell.setColType(type);
			colTypes.put(colNum, type);
		}

		//We try to see if all the value in the column are from the same type. If not the String get the power
		private CellType getMainType(CellType colType, CellType cellType) {
			switch (cellType) {
			case BLANK:
				return colType != null ? colType : cellType;
			case STRING:
				return cellType;
			case NUMERIC:
				return colType != null && colType == CellType.STRING ? colType : cellType;
			case BOOLEAN:
				return colType != null && colType == CellType.STRING ? colType : cellType;
			case ERROR:
			case FORMULA:
				return colType != null && colType == CellType.STRING ? colType : cellType;
			default:
				return colType != null ? colType : cellType;
			}
		}

		@Override
		public void headerFooter(String text, boolean isHeader, String tagName) {
		}

		public List<List<Cell>> getRows() {
			return rows;
		}
	}
	
	private class CustomDataFormatter extends DataFormatter {

	    @Override
	    public String formatRawCellContents(double value, int formatIndex, String formatString,
	            boolean use1904Windowing) {
	    	
	    	//Setting an exception for the format because it does not take the locale correctly
	        if (formatString != null && formatString.equals("m/d/yy")) {
	            if (DateUtil.isValidExcelDate(value)) {
	                Date d = DateUtil.getJavaDate(value, use1904Windowing);
	                try {
	                    return new SimpleDateFormat(DATE_FORMAT).format(d);
	                } catch (Exception e) {
						throw e;
	                }
	            }
	        }
	        return super.formatRawCellContents(value, formatIndex, formatString, use1904Windowing);
	    }
	}
	
	public interface XLSXRowManager {
		
		public void performRow(List<Cell> row) throws Exception;

		public void raiseException(Exception exception);

		public void end();
	}

	private class MyXSSFSheetXMLHandler extends XSSFSheetXMLHandler {

		private boolean interruptMaxRows;

		public MyXSSFSheetXMLHandler(StylesTable styles, CommentsTable comments, ReadOnlySharedStringsTable strings, SheetHandler sheetHandler, DataFormatter dataFormatter, boolean formulasNotResults) {
			super(styles, comments, strings, sheetHandler, dataFormatter, formulasNotResults);
			sheetHandler.setSheetXMLHandler(this);
		}

		public void setInterruptMaxRows(boolean interruptMaxRows) {
			this.interruptMaxRows = interruptMaxRows;
		}

		@Override
		public void endElement(String arg0, String arg1, String arg2) throws SAXException {
			super.endElement(arg0, arg1, arg2);
			if (interruptMaxRows) {
				throw new SAXException(CODE_MAX_ROWS);
			}
		}
	}

	public class Cell {

		private CellType type;
		private CellType colType;

		private String stringCellValue;
		private Double numericCellValue;
		private Boolean booleanCellValue;

		public Cell() {
			this.type = CellType.BLANK;
		}

		public Cell(String value) {
			this.type = CellType.STRING;
			this.stringCellValue = value;
		}

		public Cell(Double value, String stringValue) {
			this.type = CellType.NUMERIC;
			this.numericCellValue = value;
			this.stringCellValue = stringValue;
		}

		public Cell(Boolean value, String stringValue) {
			this.type = CellType.BOOLEAN;
			this.booleanCellValue = value;
			this.stringCellValue = stringValue;
		}

		public CellType getType() {
			return type;
		}

		public String getValue() {
			return stringCellValue != null ? stringCellValue : numericCellValue != null ? numericCellValue.toString() : booleanCellValue != null ? booleanCellValue.toString() : "";
		}

		public String getStringCellValue() {
			return stringCellValue;
		}

		public double getNumericCellValue() {
			return numericCellValue;
		}

		public boolean getBooleanCellValue() {
			return booleanCellValue;
		}

		public void setColType(CellType colType) {
			this.colType = colType;
		}
		
		public CellType getColType() {
			return colType;
		}
	}
}