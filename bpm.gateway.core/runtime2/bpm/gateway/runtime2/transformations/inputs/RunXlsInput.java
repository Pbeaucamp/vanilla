package bpm.gateway.runtime2.transformations.inputs;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.xml.sax.SAXException;

import bpm.gateway.core.Activator;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.server.file.XLSXHelper;
import bpm.gateway.core.server.file.XLSXHelper.Cell;
import bpm.gateway.core.server.file.XLSXHelper.XLSXRowManager;
import bpm.gateway.core.transformations.inputs.FileInputXLS;
import bpm.gateway.core.transformations.mdm.MdmContractFileInput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.Meta.TypeMeta;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaLink.TypeMetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaValue;
import bpm.vanilla.platform.core.config.Customer;
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
import jxl.read.biff.BiffException;

public class RunXlsInput extends RuntimeStep implements XLSXRowManager {
	
	private SimpleDateFormat df = new SimpleDateFormat(MetaValue.DATE_FORMAT);

	// For XLSX
	private FileInputXLS xls;
	private XLSXHelper xlsxHelper;
	private boolean isFirstRow = true;
//	private Iterator<List<Cell>> rowIterator;

	// For XLS
	private Workbook workbook;
	private List<Sheet> sheets;
	// private Sheet sheet;
	private int currentSheet = 0;
	private int currentGlobalRow = 0;
	private int currentRow = 0;

	private boolean skipFirstRow = false;
	private boolean isXlsx = false;
	
	private List<Object> additionnalData;

	public RunXlsInput(IRepositoryContext repositoryContext, Transformation transformation, int bufferSize) {
		super(repositoryContext, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		if (getTransformation() instanceof MdmContractFileInput) {
			this.xls = (FileInputXLS) ((MdmContractFileInput) getTransformation()).getFileTransfo();

			this.additionnalData = buildAdditionnalData();
		}
		else {
			this.xls = (FileInputXLS) getTransformation();
		}

		File tmpFile = XLSXHelper.createTmpFile(((AbstractFileServer) xls.getServer()).getInpuStream(xls));
		try {
			initXlsx(tmpFile, xls);
		} catch (Exception e) {
			try {
				initXls(tmpFile, xls);
			} catch (Exception ex) {
				error(" unable to open workbook");
				xlsxHelper.close();
				throw e;
			}
		}
	}

	private List<Object> buildAdditionnalData() throws Exception {
		int contractId = ((MdmContractFileInput) getTransformation()).getContractId();
		List<Meta> meta = ((MdmContractFileInput) getTransformation()).getMeta();
		
		List<MetaLink> links = ((MdmContractFileInput) getTransformation()).getDocument().getMdmHelper().getRepositoryApi().getMetaService().getMetaLinks(contractId, TypeMetaLink.ARCHITECT, true);
		
		List<Object> values = new ArrayList<Object>();
		for (Meta met : meta) {
			boolean found = false;
			if (links != null) {
				for (MetaLink link : links) {
					if (met.getKey().equals(link.getMeta().getKey())) {
						if (met.getType() == TypeMeta.DATE && link.getValue().getValue() != null) {
							if (link.getValue().getValue().isEmpty()) {
								values.add(null);
							}
							else {
								values.add(df.parseObject(link.getValue().getValue()));
							}
						}
						else {
							values.add(link.getValue().getValue());
						}
						found = true;
						break;
					}
				}
			}
			
			if (!found) {
				values.add("");
			}
		}
		return values;
	}

	private void initXlsx(File tmpFile, FileInputXLS xls) throws Exception {
		this.isXlsx = true;
		this.xlsxHelper = new XLSXHelper(tmpFile);
		info(" workbook opened");

		info(" sheet " + xls.getSheetName() + " selected");
		// this.rowIterator = xlsxHelper.getValues(xls.getSheetName(), -1);
		//
		// if (xls.getSkipFirstRow()) {
		// rowIterator.next();
		// }

		// Iterator<List<Cell>> mainRowIterator =
		// xlsxHelper.getValues(xls.getSheetName(), -1);
		//
		// chainIterator = new IteratorChain<List<Cell>>();
		// chainIterator.addIterator(mainRowIterator);
		//
		// Customer customer = Activator.getDefault().getCurrentCustomer();
		// if (customer != null && customer == Customer.VE) {
		// List<String> sheets = xlsxHelper.getSheets();
		// if (sheets != null) {
		// for (String sheet : sheets) {
		// if (!sheet.equals(xls.getSheetName()) &&
		// sheet.contains(xls.getSheetName())) {
		// Iterator<List<Cell>> rowIterator = xlsxHelper.getValues(sheet, -1);
		// chainIterator.addIterator(rowIterator);
		// }
		// }
		// }
		// }
		//
		// if (xls.getSkipFirstRow()) {
		// //TO DO: Need to skip for each iterator
		// chainIterator.next();
		// }

		isInited = true;
		info(" inited");
	}

	private void initXls(File tmpFile, FileInputXLS xls) throws Exception {
		this.isXlsx = false;
		try {
			WorkbookSettings workbookSettings = new WorkbookSettings();
			workbookSettings.setEncoding("Cp1252");

			workbook = Workbook.getWorkbook(tmpFile, workbookSettings);
			info(" workbook opened");
		} catch (BiffException e) {
			error(" unable to open workbook");
			throw e;
		} catch (IOException e) {
			error(" unable to open workbook");
			throw e;
		}

		Sheet mainSheet = workbook.getSheet(xls.getSheetName());

		if (mainSheet == null) {
			error(" unable to find the sheet " + xls.getSheetName());
			throw new Exception(" unable to find the sheet " + xls.getSheetName());
		}
		info(" sheet " + mainSheet.getName() + " selected");

		sheets = new ArrayList<>();
		sheets.add(mainSheet);

		skipFirstRow = xls.getSkipFirstRow();

		Customer customer = Activator.getDefault().getCurrentCustomer();
		if (customer != null && customer == Customer.VE) {
			String[] sheetNames = workbook.getSheetNames();
			if (sheetNames != null) {
				for (String sheetName : sheetNames) {
					if (!sheetName.equals(xls.getSheetName()) && sheetName.contains(xls.getSheetName())) {
						Sheet sheet = workbook.getSheet(xls.getSheetName());
						sheets.add(sheet);
					}
				}
			}
		}

		isInited = true;
		info(" inited");
	}

	@Override
	public void performRow() throws Exception {
		if (isXlsx) {
			// performRowXlsx();
			performStreamXlsx();
		}
		else {
			performRowXls();
		}
	}

	private void performStreamXlsx() throws IOException, OpenXML4JException, SAXException {
		xlsxHelper.streamValues(this, xls.getSheetName(), -1);
	}

	@Override
	public void performRow(List<Cell> record) throws Exception {
		if (xls.getSkipFirstRow() && isFirstRow) {
			isFirstRow = false;
			return;
		}
		
		readedRows++;
		
		if(readedRows < xls.getSkipLines()) {
			return;
		}

		Row row = RowFactory.createRow(this);

		int index = 0;
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
			
			index = i;
		}
		
		index++;
		
		if (additionnalData != null && !additionnalData.isEmpty()) {
			addAdditionnalData(row, index);
		}
		
		writeRow(row);
	}

	@Override
	public void raiseException(Exception exception) {
		super.raiseException(exception);
	}

	@Override
	public void end() {
		setEnd();
	}

	// private void performRowXlsx() throws Exception {
	// if (!rowIterator.hasNext()) {
	// setEnd();
	// return;
	// }
	//
	// List<Cell> record = rowIterator.next();
	// readedRows++;
	//
	// Row row = RowFactory.createRow(this);
	//
	// for (int i = 0; i < record.size(); i++) {
	// Object value = null;
	//
	// Cell cell = record.get(i);
	// switch (cell.getColType()) {
	// case BLANK:
	// value = null;
	// break;
	// case STRING:
	// value = cell.getStringCellValue();
	// break;
	// case NUMERIC:
	// value = cell.getNumericCellValue();
	// break;
	// case BOOLEAN:
	// value = cell.getBooleanCellValue();
	// break;
	// case ERROR:
	// case FORMULA:
	// value = cell.getStringCellValue();
	// break;
	// default:
	// value = cell.getStringCellValue();
	// break;
	// }
	// row.set(i, value);
	// }
	// writeRow(row);
	// }

	private void performRowXls() throws Exception {
		if (currentGlobalRow >= getRowsNumber()) {
			setEnd();
			return;
		}

		Sheet sheet = sheets.get(currentSheet);
		if (currentRow >= getFixNumberOfRows(sheet)) {
			currentSheet++;
			currentRow = 0;

			sheet = sheets.get(currentSheet);
		}

		if (currentRow == 0 && skipFirstRow) {
			currentRow++;
			currentGlobalRow++;
		}

		Row row = RowFactory.createRow(this);

		int index = 0;
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
			
			index = i;
		}
		
		if (additionnalData != null && !additionnalData.isEmpty()) {
			addAdditionnalData(row, index);
		}
		
		currentRow++;
		currentGlobalRow++;
		
		writeRow(row);
	}

	private void addAdditionnalData(Row row, int index) {
		for (Object data : additionnalData) {
			row.set(index, data);
			index++;
		}
	}

	private int getRowsNumber() {
		int nbRows = 0;
		if (sheets != null) {
			for (Sheet sheet : sheets) {
				nbRows += getFixNumberOfRows(sheet);
			}
		}
		return nbRows;
	}
	
	/**
	 * 
	 * Remove the last rows if they are blanks
	 * 
	 * @param sheet
	 * @return
	 */
	private int getFixNumberOfRows(Sheet sheet) {
        int numberCols = sheet.getColumns(); // Number of columns
        int numberRows = sheet.getRows(); // Rows
        
        int nullCellNum = 0;
        for (int j = 0; j < numberCols; j++) {
            String val = sheet.getCell(j, numberRows - 1).getContents();
            if (val == null || val.isEmpty())
                nullCellNum++;
        }
        
        if (nullCellNum >= numberCols) { // If nullCellNum is greater than or equal to the total number of columns
        	numberRows--; // Decrease the number of lines by one
        }
        return numberRows;
	}

	@Override
	public void releaseResources() {
		try {
//			rowIterator = null;
			if (xlsxHelper != null) {
				xlsxHelper.close();
			}

			if (workbook != null) {
				workbook.close();
			}
			info(" workbook closed");
			info(" resources released");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
