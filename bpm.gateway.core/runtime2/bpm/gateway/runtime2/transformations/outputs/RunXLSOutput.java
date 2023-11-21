package bpm.gateway.runtime2.transformations.outputs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.server.file.MdmFileServer;
import bpm.gateway.core.tools.Utils;
import bpm.gateway.core.transformations.outputs.FileOutputXLS;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;

public class RunXLSOutput extends RuntimeStep {

	private FileOutputXLS xls;
	
	private File file;

	private Workbook workbook = null;
	private Sheet sheet = null;
	private int rowNumber = 0;

	public RunXLSOutput(FileOutputXLS transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		this.xls = (FileOutputXLS) this.getTransformation();

		// flag to decide if the Headers Should be Writed or not
		boolean fileCreated = false;
		if (xls.useMdm()) {
			file = Utils.createTmpFile(null);
			fileCreated = true;
		}
		else {
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
					fileCreated = true;
				} catch (IOException e) {
					error(" unable to create file " + fileName, e);
					throw e;
				}

			}
			else {
				if (xls.getDelete()) {
					if (file.delete()) {
						info("deleted file " + fileName);
					}

				}
			}
		}

		try (FileInputStream fis = new FileInputStream(file);) {
			this.workbook = new XSSFWorkbook(fis);
			info(" Workbook opened");
		} catch (Exception e) {
			info(" no workbook found in the file ");

			try {
				workbook = new XSSFWorkbook();
				info(" Workbook created");
			} catch (Exception ex) {
				error(" cannot create a Wrokbook in the specified file ", ex);
				throw e;
			}
		}
		
		sheet = workbook.getSheet(xls.getSheetName());

		if (sheet == null) {
			sheet = workbook.createSheet(xls.getSheetName());
			info(" sheet " + xls.getSheetName() + " created");
		}
		else {
			info(" sheet " + xls.getSheetName() + " found");
		}

		if (xls.isIncludeHeader() && !xls.isAppend()) {
			try {
				writeHeader();
				rowNumber = 1;
				info(" header writed");
			} catch (Exception e) {
				error(" unable to write header", e);
			}
		}
		else {
			if (fileCreated) {
				try {
					writeHeader();
					rowNumber = 1;
					info(" header writed");
				} catch (Exception e) {
					error(" unable to write header", e);
				}
			}
			else if (xls.isAppend()){
				rowNumber = sheet.getLastRowNum() + 1;
			}
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

		Row row = readRow();

		org.apache.poi.ss.usermodel.Row newRow = sheet.createRow(rowNumber++);

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
		writeRow(row);
	}

	@Override
	public void releaseResources() {
		try (FileOutputStream os = new FileOutputStream(file);) {
			workbook.write(os);
			workbook.close();
		} catch (Exception e) {
			error(" unable to write Workbook", e);
		}
		
		if (xls.useMdm()) {
			try {
				FileInputStream fileStream = new FileInputStream(file);
				((MdmFileServer) xls.getFileServer()).uploadFileToMDM(xls.getName() + ".xls", xls.getContractId(), fileStream);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void writeHeader() throws Exception {
		org.apache.poi.ss.usermodel.Row newRow = sheet.createRow(0);

		int cellNum = 0;
		for (StreamElement e : getTransformation().getDescriptor(getTransformation()).getStreamElements()) {
			Cell cell = newRow.createCell(cellNum++);
			cell.setCellValue(e.name);
		}
	}

}
