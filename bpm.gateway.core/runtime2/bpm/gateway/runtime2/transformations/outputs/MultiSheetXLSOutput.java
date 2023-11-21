package bpm.gateway.runtime2.transformations.outputs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.outputs.MultiFolderXLS;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;

public class MultiSheetXLSOutput extends RuntimeStep {
	private class SheetInfo {
		private Sheet sheet;
		private int rowNumber;
		private RuntimeStep input;

		public SheetInfo(RuntimeStep input, Sheet sheet, int rowNumber) {
			this.sheet = sheet;
			this.input = input;
		}

		/**
		 * @return the sheet
		 */
		public Sheet getSheet() {
			return sheet;
		}

		/**
		 * @return the rowNumber
		 */
		public int getRowNumber() {
			return rowNumber;
		}

		public void newRow() {
			rowNumber++;
		}

		/**
		 * @return the input
		 */
		public RuntimeStep getInput() {
			return input;
		}

	}

	private Workbook workbook = null;
	private List<SheetInfo> sheets = new ArrayList<SheetInfo>();

	protected HashMap<Transformation, BlockingQueue<Row>> datasMap = new HashMap<Transformation, BlockingQueue<Row>>();

	private File file;

	public MultiSheetXLSOutput(MultiFolderXLS transformation, int bufferSize) {
		super(null, transformation, bufferSize);

	}

	@Override
	public void init(Object adapter) throws Exception {
		MultiFolderXLS xls = (MultiFolderXLS) this.getTransformation();

		String fileName = null;
		try {
			fileName = getTransformation().getDocument().getStringParser().getValue(xls.getDocument(), xls.getDefinition());
		} catch (Exception e) {
			error(" error when parsing fileName", e);
			throw e;
		}

		/*
		 * file management (creation, deletion)
		 */
		this.file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();

				info(" File " + fileName + " created");
			} catch (IOException e) {
				error(" unable to create file " + fileName, e);
				throw e;
			}

		}
		else {
			if (xls.isDelete()) {
				if (file.delete()) {
					info("deleted file " + fileName);
				}

			}
		}

		try (FileInputStream fis = new FileInputStream(file);) {
			this.workbook = new XSSFWorkbook(fis);
			info(" Workbook opened");
		} catch (Exception e) {
			warn(" no workbook found in the file ");

			try {
				workbook = new XSSFWorkbook();
				info(" Workbook created");
			} catch (Exception ex) {
				error(" cannot create a Wrokbook in the specified file ", ex);
				throw e;
			}
		}

		for (String sheetName : xls.getSheetNames()) {
			Sheet sheet = workbook.getSheet(sheetName);
			SheetInfo info = null;

			Transformation _t = xls.getTransformation(sheetName);
			RuntimeStep runInput = null;
			for (RuntimeStep rs : inputs) {
				if (rs.getTransformation() == _t) {
					runInput = rs;
					break;
				}
			}

			if (sheet == null) {

				sheet = workbook.createSheet(sheetName);
				info(" sheet " + sheetName + " created");
				if (xls.isIncludeHeader()) {
					try {

						info(" header writed");
						info = new SheetInfo(runInput, sheet, 1);
						writeHeader(info);
					} catch (Exception e) {
						error(" unable to write header", e);
					}

				}
			}
			else {
				info(" sheet " + sheetName + " found");
				int rowNum = 0;
				if (!xls.isAppend()) {
					if (xls.isIncludeHeader()) {
						try {

							info(" header writed");
							info = new SheetInfo(runInput, sheet, 1);
							writeHeader(info);
						} catch (Exception e) {
							error(" unable to write header", e);
						}
					}
				}
				else {
					rowNum = sheet.getLastRowNum() + 1;
				}

				info = new SheetInfo(runInput, sheet, rowNum);
			}

			sheets.add(info);
			datasMap.put(runInput.getTransformation(), new ArrayBlockingQueue<Row>(getBufferSize()));
		}

		isInited = true;
		info(" inited");

	}

	@Override
	public void performRow() throws Exception {
		if (areInputStepAllProcessed()) {
			if (inputEmpty()) {
				setEnd();
			}
		}

		if (isEnd() && inputEmpty()) {
			return;
		}

		if (!isEnd() && inputEmpty()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // restore interrupted status
			}
			return;
		}

		for (SheetInfo sheetInf : sheets) {
			if (!inputEmpty(sheetInf.getInput().getTransformation())) {
				Row row = readRow(sheetInf.getInput());
				org.apache.poi.ss.usermodel.Row newRow = sheetInf.getSheet().createRow(sheetInf.getRowNumber() + 1);

				int cellNum = 0;
				for (Object o : row) {
					Cell cell = newRow.createCell(cellNum++);
					if (o == null) {
						// Do nothing
					}
					else if (o instanceof String) {
						cell.setCellValue(o.toString());
					}
					else if (o instanceof Number) {
						cell.setCellValue(Double.parseDouble(o.toString()));
					}
					else if (o instanceof Boolean) {
						cell.setCellValue(Boolean.parseBoolean(o.toString()));
					}
					else if (o instanceof Date) {
						cell.setCellValue((Date) o);
					}
					else {
						cell.setCellValue(o.toString());
					}
				}
				sheetInf.newRow();
				writeRow(row);
			}
		}

	}

	@Override
	public void releaseResources() {
		try (FileOutputStream os = new FileOutputStream(file);) {
			workbook.write(os);
			workbook.close();
		} catch (Exception e) {
			error(" unable to write Workbook", e);
		}
	}

	private void writeHeader(SheetInfo sheet) throws Exception {
		org.apache.poi.ss.usermodel.Row newRow = sheet.getSheet().createRow(0);

		int cellNum = 0;
		for (StreamElement e : sheet.getInput().getTransformation().getDescriptor(getTransformation()).getStreamElements()) {
			Cell cell = newRow.createCell(cellNum++);
			cell.setCellValue(e.name);
		}
	}

	@Override
	public void insertRow(final Row data, final RuntimeStep caller) throws InterruptedException {

		// test in case of an input that is not mapped to a sheet
		if (datasMap.get(caller.getTransformation()) != null) {
			datasMap.get(caller.getTransformation()).put(data);
		}
		else {
			warn("The input " + caller.getTransformation().getName() + " is not attached to a sheet.");
		}

	}

	private Row readRow(RuntimeStep caller) throws Exception {
		if (inputEmpty(caller.getTransformation())) {

			if (!caller.isEnd() && inputEmpty(caller.getTransformation())) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				return readRow(caller);
			}
			else if (inputEmpty(caller.getTransformation())) {
				setEnd();
				return null;
			}

		}
		Row r = datasMap.get(caller.getTransformation()).take();
		readedRows++;
		return r;
	}

	private boolean inputEmpty(Transformation transformation) throws InterruptedException {
		boolean b = datasMap.get(transformation).isEmpty();
		return b;
	}

	@Override
	public boolean inputEmpty() throws InterruptedException {
		for (Transformation t : datasMap.keySet()) {
			if (!inputEmpty(t)) {
				return false;
			}
		}
		return true;
	}
}
