package bpm.gateway.core.server.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jxl.BooleanCell;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import bpm.gateway.core.Activator;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.tools.Utils;
import bpm.gateway.core.transformations.files.FileFolderReader;
import bpm.gateway.core.transformations.inputs.D4CInput;
import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.gateway.core.transformations.inputs.FileInputXLS;

/**
 * Helper that provide XLS description or its content
 * 
 * @author LCA
 * 
 */
public class FileXLSHelper {

	static private HashMap<FileXLS, _Wkb> fileBuffer = new HashMap<FileXLS, _Wkb>();

	static int maxRows = 100;

	static class _Wkb {
		Workbook book;
		long lastModified;
	}

	private static final int MAX_ROWS = 100;

	/**
	 * open the defined XLS file and look for Sheets Names
	 * 
	 * @param transfo
	 * @return the Sheets names of the given XLS file
	 * @throws Exception
	 */
	public static List<String> getWorkSheetsNames(DataStream transfo) throws Exception {
		return getWorkSheetsNames(((AbstractFileServer) transfo.getServer()).getInpuStream(transfo));
	}

	public static List<String> getWorkSheetsNames(InputStream is) throws Exception {
		File tmpFile = Utils.createTmpFile(is);
		try {
			return getWorkSheetsNamesXlsx(tmpFile);
		} catch (Exception e) {
			try {
				return getWorkSheetsNamesXls(tmpFile);
			} catch (Exception ex) {
				throw ex;
			}
		}
	}

	public static List<String> getWorkSheetsNames(FileFolderReader transfo) throws Exception {
		String folderPath = null;
		try {
			folderPath = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getFolderPath());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Unable to parse the folderPath:" + ex.getMessage(), ex);
		}

		File f = new File(folderPath);
		if (!f.exists()) {
			throw new Exception("the Folder " + folderPath + " does not exist");
		}

		if (!f.isDirectory()) {
			throw new Exception(folderPath + " is not a Folder");
		}

		for (String s : f.list()) {
			File _f = new File(f, s);

			if (!_f.isFile()) {
				continue;
			}

			if (s.matches(transfo.getFileNamePattern().replace("*", ".*"))) {
				FileInputXLS xls = new FileInputXLS();
				xls.setDefinition(_f.getAbsolutePath());
				return getWorkSheetsNames(xls);
			}
		}

		return new ArrayList<String>();
	}

	public static void createStreamDescriptor(DataStream transfo) throws Exception {
		FileInputXLS fileTransfo = null;
		if (transfo instanceof D4CInput && ((D4CInput) transfo).getFileTransfo() instanceof FileInputXLS) {
			fileTransfo = (FileInputXLS) ((D4CInput) transfo).getFileTransfo();
		}
		else if (transfo instanceof FileInputXLS) {
			fileTransfo = (FileInputXLS) transfo;
		}

		fileTransfo.setDescriptor(new DefaultStreamDescriptor());
		
		File tmpFile = Utils.createTmpFile(((AbstractFileServer) transfo.getServer()).getInpuStream(transfo));
		try {
			createStreamDescriptorXlsx(tmpFile, fileTransfo);
		} catch (Exception e) {
			try {
				createStreamDescriptorXls(tmpFile, fileTransfo);
			} catch (Exception ex) {
				throw ex;
			}
		}
	}

	/**
	 * return all the values for the given FileInputXLS
	 * 
	 * @param transfo
	 * @param maxRows
	 *            : number max of rows
	 * @param start
	 *            : first row Number
	 * @return
	 * @throws Exception
	 */
	public static List<List<Object>> getValues(DataStream transfo, int firstRow, int maxRows) throws Exception {
		FileXLS fileTransfo = null;
		if (transfo instanceof D4CInput && ((D4CInput) transfo).getFileTransfo() instanceof FileXLS) {
			fileTransfo = (FileXLS) ((D4CInput) transfo).getFileTransfo();
		}
		else if (transfo instanceof FileXLS) {
			fileTransfo = (FileInputXLS) transfo;
		}
		
		File tmpFile = Utils.createTmpFile(((AbstractFileServer) transfo.getServer()).getInpuStream(transfo));
		try {
			return getValuesXlsx(tmpFile, fileTransfo, firstRow, maxRows);
		} catch (Exception e) {
			try {
				return getValuesXls(tmpFile, fileTransfo, firstRow, maxRows);
			} catch (Exception ex) {
				throw ex;
			}
		}
	}
	
	public static List<List<Object>> getCountDistinctFieldValues(StreamElement field, DataStream stream) throws Exception {
		FileXLS fileTransfo = null;
		if (stream instanceof D4CInput && ((D4CInput) stream).getFileTransfo() instanceof FileXLS) {
			fileTransfo = (FileXLS) ((D4CInput) stream).getFileTransfo();
		}
		else if (stream instanceof FileXLS) {
			fileTransfo = (FileInputXLS) stream;
		}
		
		File tmpFile = Utils.createTmpFile(((AbstractFileServer) stream.getServer()).getInpuStream(stream));
		try {
			return getCountDistinctFieldValuesXlsx(tmpFile, field, fileTransfo);
		} catch (Exception e) {
			try {
				return getCountDistinctFieldValuesXls(tmpFile, field, fileTransfo);
			} catch (Exception ex) {
				throw ex;
			}
		}
	}

	private static List<String> getWorkSheetsNamesXlsx(File tmpFile) throws Exception {
		try (XLSXHelper xlsxHelper = new XLSXHelper(tmpFile)) {
			return xlsxHelper.getSheets();
		} catch (Exception e) {
			throw e;
		}
	}

	private static List<String> getWorkSheetsNamesXls(File tmpFile) throws Exception {
		WorkbookSettings workbookSettings = new WorkbookSettings();
		workbookSettings.setEncoding("Cp1252");
		Workbook workbook = Workbook.getWorkbook(tmpFile, workbookSettings);

		List<String> names = new ArrayList<String>();

		for (String s : workbook.getSheetNames()) {
			names.add(s);
		}

		workbook.close();
		return names;
	}

	/**
	 * create and set the Descriptor for this transformation by collecting for the given sheet name from the defined XLSFile
	 * 
	 * @param transfo
	 * @param worksheetName
	 * @throws Exception
	 */
	private static void createStreamDescriptorXlsx(File tmpFile, FileInputXLS transfo) throws Exception {
		try (XLSXHelper xlsxHelper = new XLSXHelper(tmpFile)) {
			HashMap<String, StreamElement> structure = xlsxHelper.getColumnDefinitions(transfo.getName(), transfo.getSheetName());

			for (String colName : structure.keySet()) {
				((DefaultStreamDescriptor) transfo.getDescriptor(null)).addColumn(structure.get(colName));
			}
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * create and set the Descriptor for this transformation by collecting for the given sheet name from the defined XLSFile
	 * 
	 * @param transfo
	 * @param worksheetName
	 * @throws Exception
	 */
	private static void createStreamDescriptorXls(File tmpFile, FileInputXLS transfo) throws Exception {
		transfo.setDescriptor(new DefaultStreamDescriptor());

		Workbook workbook = null;
		try {
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("Cp1252");

			workbook = Workbook.getWorkbook(tmpFile, workbookSettings);

			Sheet sheet = workbook.getSheet(transfo.getSheetName());

			if (sheet == null) {
				workbook.close();
				throw new Exception("The sheet named " + transfo.getSheetName() + " does not exist");
			}

			HashMap<String, StreamElement> structure = new HashMap<String, StreamElement>();
			List<String> headers = new ArrayList<String>();
			/*
			 * extract the columns names from the first row
			 */
			for (Cell c : sheet.getRow(0)) {

				StreamElement e = new StreamElement();
				e.name = c.getContents();
				e.originTransfo = transfo.getName();
				e.transfoName = transfo.getName();
				structure.put(e.name, e);
				headers.add(e.name);
			}

			/*
			 * check the dataType
			 */
			for (int i = 1; i < sheet.getRows() && i < maxRows; i++) {
				for (int k = 0; k < sheet.getColumns(); k++) {
					StreamElement e = structure.get(headers.get(k));

					if (!"java.lang.String".equals(e.className) && (sheet.getCell(k, i).getType() == CellType.NUMBER || sheet.getCell(k, i).getType() == CellType.NUMBER_FORMULA)) {
						e.className = "java.lang.Double";
						e.typeName = "NUMBER";
						continue;
					}

					else if (!"java.lang.String".equals(e.className) && (sheet.getCell(k, i).getType() == CellType.DATE || sheet.getCell(k, i).getType() == CellType.DATE_FORMULA)) {
						e.className = "java.util.Date";
						e.typeName = "DATE";
						continue;
					}
					else if (!"java.lang.Boolean".equals(e.className) && (sheet.getCell(k, i).getType() == CellType.BOOLEAN || sheet.getCell(k, i).getType() == CellType.BOOLEAN_FORMULA)) {
						e.className = "java.util.Boolean";
						e.typeName = "BOOLEAN";
						continue;
					}

					else if (sheet.getCell(k, i).getContents().trim().equals("") || sheet.getCell(k, i).getType() == CellType.EMPTY) {
						if ((i == sheet.getRows() - 1 || i == maxRows - 1) && (e.className == null || e.className.equals(""))) {
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

			for (String s : headers) {
				((DefaultStreamDescriptor) transfo.getDescriptor(null)).addColumn(structure.get(s));
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (workbook != null) {
				workbook.close();
			}
		}
	}

	private static List<List<Object>> getValuesXlsx(File tmpFile, FileXLS transfo, int firstRow, int maxRows) throws Exception {
		List<List<Object>> result = new ArrayList<List<Object>>();

		try (XLSXHelper xlsxHelper = new XLSXHelper(tmpFile)) {
			Iterator<List<XLSXHelper.Cell>> rowIterator = xlsxHelper.getValues(transfo.getSheetName(), MAX_ROWS);

			boolean skipFirstRow = true;
			if (transfo instanceof FileInputXLS) {
				skipFirstRow = ((FileInputXLS) transfo).getSkipFirstRow();
			}

			while (rowIterator.hasNext()) {
				List<XLSXHelper.Cell> row = rowIterator.next();

				if (skipFirstRow) {
					skipFirstRow = false;
					continue;
				}

				List<Object> newRow = new ArrayList<Object>();
				for (XLSXHelper.Cell cell : row) {
					switch (cell.getType()) {
					case BLANK:
						newRow.add(null);
						break;
					case STRING:
						newRow.add(cell.getStringCellValue());
						break;
					case NUMERIC:
						newRow.add(cell.getNumericCellValue());
						break;
					case BOOLEAN:
						newRow.add(cell.getBooleanCellValue());
						break;
					case ERROR:
					case FORMULA:
						newRow.add(cell.getStringCellValue());
						break;
					default:
						newRow.add(cell.getStringCellValue());
						break;
					}
				}
				result.add(newRow);
			}

			return result;
		} catch (Exception ex) {
			throw ex;
		}
	}

	private static List<List<Object>> getValuesXls(File tmpFile, FileXLS transfo, int firstRow, int maxRows) throws Exception {
		List<List<Object>> result = new ArrayList<List<Object>>();
		Workbook workbook = null;

		WorkbookSettings workbookSettings = new WorkbookSettings();
		workbookSettings.setEncoding("Cp1252");

		if (fileBuffer.get(transfo) == null) {
			workbook = Workbook.getWorkbook(tmpFile, workbookSettings);

			_Wkb _wkb = new _Wkb();
			_wkb.book = workbook;
			try {
				_wkb.lastModified = new File(transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getDefinition())).lastModified();
			} catch (Exception e) {
				e.printStackTrace();
			}
			fileBuffer.put(transfo, _wkb);
			try {
				Activator.getLogger().debug("XLS Helper open " + transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getDefinition()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			if (((AbstractFileServer) transfo.getServer()).getInpuStream(transfo) instanceof FileInputStream) {
				File f = new File(transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getDefinition()));
				if (f.lastModified() > fileBuffer.get(transfo).lastModified) {
					workbook = Workbook.getWorkbook(((AbstractFileServer) transfo.getServer()).getInpuStream(transfo), workbookSettings);
					_Wkb _wkb = new _Wkb();
					_wkb.book = workbook;
					_wkb.lastModified = new File(transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getDefinition())).lastModified();

					fileBuffer.put(transfo, _wkb);
					Activator.getLogger().debug("XLS Helper open " + transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getDefinition()));

				}
				else {
					workbook = Workbook.getWorkbook(((AbstractFileServer) transfo.getServer()).getInpuStream(transfo), workbookSettings);
				}
			}
			else {
				workbook = Workbook.getWorkbook(((AbstractFileServer) transfo.getServer()).getInpuStream(transfo), workbookSettings);
			}
		}

		if (workbook == null) {
			workbook = fileBuffer.get(transfo).book;
		}

		Sheet sheet = workbook.getSheet(transfo.getSheetName());

		if (sheet == null) {
			workbook.close();
			Activator.getLogger().debug("XLS Helper close " + transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getDefinition()));
			return result;
		}

		int i = 0;
		for (i = firstRow; i < sheet.getRows() && result.size() < maxRows; i++) {

			if (transfo instanceof FileInputXLS) {
				if (i == 0 && ((FileInputXLS) transfo).getSkipFirstRow()) {
					continue;
				}
			}
			else {
				if (i == 0) {
					continue;
				}
			}

			List<Object> row = new ArrayList<Object>();

			for (int k = 0; k < sheet.getColumns(); k++) {

				try {
					Cell c = sheet.getCell(k, i);

					if (c.getType() == CellType.NUMBER) {
						row.add(((NumberCell) c).getValue());
					}
					else if (c.getType() == CellType.BOOLEAN) {
						row.add(((BooleanCell) c).getValue());
					}
					else if (c.getType() == CellType.DATE) {
						row.add(((DateCell) c).getDate());
					}
					else {
						row.add(c.getContents());
					}
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
					Activator.getLogger().warn("XLS Helper Error line" + i + " column " + k);
					row.add(null);
				}

			}

			result.add(row);

		}

		if (firstRow + i == sheet.getRows() - 1) {
			workbook.close();
			fileBuffer.remove(transfo);
			Activator.getLogger().debug("XLS Helper close " + transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getDefinition()));
		}
		else {
			workbook.close();
		}

		return result;
	}

	private static List<List<Object>> getCountDistinctFieldValuesXlsx(File tmpFile, StreamElement field, FileXLS stream) throws Exception {
		int colPos = stream.getDescriptor(null).getStreamElements().indexOf(field);
		try (XLSXHelper xlsxHelper = new XLSXHelper(tmpFile)) {
			Iterator<List<XLSXHelper.Cell>> rowIterator = xlsxHelper.getValues(stream.getSheetName(), MAX_ROWS);

			List<List<Object>> result = new ArrayList<List<Object>>();

			boolean skipFirstRow = false;
			if (stream instanceof FileInputXLS) {
				skipFirstRow = ((FileInputXLS) stream).getSkipFirstRow();
			}

			while (rowIterator.hasNext()) {
				List<XLSXHelper.Cell> row = rowIterator.next();

				if (skipFirstRow) {
					skipFirstRow = false;
					continue;
				}

				XLSXHelper.Cell cell = row.get(colPos);

				if (cell == null) {
					continue;
				}

				boolean present = false;
				for (List<Object> l : result) {
					if (l.get(0).equals(cell.getValue())) {
						l.set(1, (Integer) l.get(1) + 1);
						present = true;
						break;
					}
				}

				if (!present) {
					List<Object> l = new ArrayList<Object>();
					l.add(cell.getValue());
					l.add(1);
					result.add(l);
				}
			}
			return result;
		} catch (Exception ex) {
			throw ex;
		}
	}

	private static List<List<Object>> getCountDistinctFieldValuesXls(File tmpFile, StreamElement field, FileXLS stream) throws Exception {
		int colPos = stream.getDescriptor(null).getStreamElements().indexOf(field);
		List<List<Object>> values = new ArrayList<List<Object>>();

		Workbook workbook = null;

		if (fileBuffer.get(stream) == null) {
			workbook = Workbook.getWorkbook(tmpFile);

			_Wkb _wkb = new _Wkb();
			_wkb.book = workbook;
			try {
				_wkb.lastModified = new File(stream.getDocument().getStringParser().getValue(stream.getDocument(), stream.getDefinition())).lastModified();
			} catch (Exception e) {
			}
			fileBuffer.put(stream, _wkb);
		}
		else {
			if (((AbstractFileServer) stream.getServer()).getInpuStream(stream) instanceof FileInputStream) {
				File f = new File(stream.getDocument().getStringParser().getValue(stream.getDocument(), stream.getDefinition()));
				if (f.lastModified() > fileBuffer.get(stream).lastModified) {
					workbook = Workbook.getWorkbook(((AbstractFileServer) stream.getServer()).getInpuStream(stream));
					_Wkb _wkb = new _Wkb();
					_wkb.book = workbook;
					_wkb.lastModified = new File(stream.getDocument().getStringParser().getValue(stream.getDocument(), stream.getDefinition())).lastModified();

					fileBuffer.put(stream, _wkb);
					Activator.getLogger().debug("XLS Helper open " + stream.getDocument().getStringParser().getValue(stream.getDocument(), stream.getDefinition()));

				}
				else {
					workbook = Workbook.getWorkbook(((AbstractFileServer) stream.getServer()).getInpuStream(stream));
				}
			}
			else {
				workbook = Workbook.getWorkbook(((AbstractFileServer) stream.getServer()).getInpuStream(stream));
			}
		}

		Sheet sheet = workbook.getSheet(stream.getSheetName());

		if (sheet == null) {
			workbook.close();
			throw new Exception("Sheet " + stream.getSheetName() + " not found.");
		}

		int start = 0;
		if (stream instanceof FileInputXLS) {
			start = ((FileInputXLS) stream).getSkipFirstRow() ? 1 : 0;
		}
		for (int i = start; i < sheet.getRows(); i++) {
			Cell c = sheet.getCell(colPos, i);

			boolean present = false;
			for (List<Object> l : values) {
				if (l.get(0).equals(c.getContents())) {
					l.set(1, (Integer) l.get(1) + 1);
					present = true;
					break;
				}
			}

			if (!present) {
				List<Object> l = new ArrayList<Object>();
				l.add(c.getContents());
				l.add(1);
				values.add(l);
			}
		}

		workbook.close();
		fileBuffer.remove(stream);

		return values;
	}
}
