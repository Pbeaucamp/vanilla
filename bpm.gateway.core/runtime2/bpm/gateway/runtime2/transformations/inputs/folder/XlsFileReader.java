package bpm.gateway.runtime2.transformations.inputs.folder;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import jxl.BooleanCell;
import jxl.BooleanFormulaCell;
import jxl.CellType;
import jxl.DateCell;
import jxl.DateFormulaCell;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.NumberFormulaCell;
import jxl.Sheet;
import jxl.StringFormulaCell;
import jxl.Workbook;
import jxl.WorkbookSettings;
import bpm.gateway.core.server.file.XLSXHelper;
import bpm.gateway.core.server.file.XLSXHelper.Cell;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class XlsFileReader implements IFileReader {

	// For XLSX
	private XLSXHelper xlsxHelper;
	private Iterator<List<Cell>> rowIterator;

	// For XLS
	private Workbook workbook;
	private Sheet sheet;
	private int currentRow = -1;

	private boolean isXlsx = false;

	private RuntimeStep runtimeStep;

	public XlsFileReader(RuntimeStep runtimeStep, File file, String sheetName, String encoding, boolean skipFirstRow) throws Exception {
		this.runtimeStep = runtimeStep;
		try {
			initXlsx(file, sheetName, skipFirstRow);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				initXls(file, encoding, sheetName, skipFirstRow);
			} catch (Exception ex) {
				throw ex;
			}
		}
	}

	private void initXlsx(File file, String sheetName, boolean skipFirstRow) throws Exception {
		this.isXlsx = true;
		this.xlsxHelper = new XLSXHelper(file);
		this.rowIterator = xlsxHelper.getValues(sheetName, -1);
		if (skipFirstRow) {
			rowIterator.next();
		}
	}

	private void initXls(File file, String encoding, String sheetName, boolean skipFirstRow) throws Exception {
		this.isXlsx = false;

		WorkbookSettings set = new WorkbookSettings();
		set.setEncoding(encoding);
		workbook = Workbook.getWorkbook(file, set);

		sheet = workbook.getSheet(sheetName);
		if (sheet == null) {
			throw new Exception(" unable to find the sheet " + sheetName);
		}

		if (skipFirstRow) {
			currentRow = 1;
		}
		else {
			currentRow = 0;
		}
	}

	public boolean isAllRead() {
		return isXlsx ? !rowIterator.hasNext() : currentRow >= sheet.getRows();
	}

	public Row readRow() throws Exception {
		if (isXlsx) {
			return readRowXlsx();
		}
		else {
			return readRowXls();
		}
	}

	private Row readRowXlsx() throws Exception {
		Row row = RowFactory.createRow(runtimeStep);

		List<Cell> record = rowIterator.next();

		for (int i = 0; i < record.size(); i++) {
			Object value = null;

			Cell cell = record.get(i);
			switch (cell.getType()) {
			case BLANK:
				value = null;
				break;
			case STRING:
				value = cell.getStringCellValue();
				break;
			case NUMERIC:
				value = cell.getNumericCellValue();
				break;
			case BOOLEAN:
				value = cell.getBooleanCellValue();
				break;
			case ERROR:
			case FORMULA:
				value = cell.getStringCellValue();
				break;
			default:
				value = cell.getStringCellValue();
				break;
			}
			row.set(i, value);
			i++;
		}
		return row;
	}

	private Row readRowXls() throws Exception {
		Row row = RowFactory.createRow(runtimeStep);

		for (int i = 0; i < sheet.getColumns(); i++) {
			jxl.Cell c = sheet.getCell(i, currentRow);
			Object value = null;
			if (c.getType() == CellType.BOOLEAN) {
				value = ((BooleanCell) c).getValue();
			}
			else if (c.getType() == CellType.BOOLEAN_FORMULA) {
				value = ((BooleanFormulaCell) c).getValue();
			}
			else if (c.getType() == CellType.DATE) {
				value = ((DateCell) c).getDate();
			}
			else if (c.getType() == CellType.DATE_FORMULA) {
				value = ((DateFormulaCell) c).getDate();
			}
			else if (c.getType() == CellType.EMPTY) {
				value = null;
			}
			else if (c.getType() == CellType.LABEL) {
				value = ((LabelCell) c).getString();
			}
			else if (c.getType() == CellType.STRING_FORMULA) {
				value = ((StringFormulaCell) c).getString();
			}
			else if (c.getType() == CellType.NUMBER) {
				value = ((NumberCell) c).getValue();
			}
			else if (c.getType() == CellType.NUMBER_FORMULA) {
				value = ((NumberFormulaCell) c).getValue();
			}
			else {
				value = c.getContents();
			}
			row.set(i, value);
		}
		currentRow++;
		return row;
	}

	public void releaseResources() {
		try {
			rowIterator = null;
			if (xlsxHelper != null) {
				xlsxHelper.close();
			}

			sheet = null;
			if (workbook != null) {
				workbook.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
