package bpm.odata.service.servlets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import bpm.metadata.layer.business.AbstractBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.odata.service.OpenDataComponent;
import bpm.vanilla.platform.core.beans.ged.constant.Formats;

public class DownloadDataServlet extends HttpServlet {

	public static final String DOWNLOAD_DATA_SERVLET = "/DownloadDataServlet";
	private static final long serialVersionUID = 1L;

	public static String METADATA_ID = "metadata_id";
	public static String MODEL_NAME = "model_name";
	public static String PACKAGE_NAME = "package_name";
	public static String TABLE_NAME = "table_name";

	public static String FORMAT = "format";
	public static String SEPARATOR = "separator";
	
	public static String FORMAT_XLS = "xls";
	public static String FORMAT_CSV = "csv";
	public static String FORMAT_WEKA = "weka";

	private OpenDataComponent openDataComponent;

	public DownloadDataServlet(OpenDataComponent openDataComponent) {
		this.openDataComponent = openDataComponent;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int metadataId = Integer.parseInt(req.getParameter(METADATA_ID));
		String modelName = req.getParameter(MODEL_NAME);
		String packageName = req.getParameter(PACKAGE_NAME);
		String tableName = req.getParameter(TABLE_NAME);
		
		String format = req.getParameter(FORMAT) != null ? req.getParameter(FORMAT) : FORMAT_CSV;
		String separator = req.getParameter(SEPARATOR) != null ? req.getParameter(SEPARATOR) : ";";
		
		ByteArrayInputStream content = null;
		try {
			if (format != null) {
				String mime = "";
				for (Formats f : Formats.values()) {
					if (f.getExtension().equals(format)) {
						mime = f.getMime();
						break;
					}
				}
				if (!mime.equals("")) {
					resp.setContentType(mime);
				}
			}
			resp.setHeader("Content-disposition", "attachment; filename=" + tableName + "." + format);

			content = buildFileContent(tableName, format, separator, metadataId, modelName, packageName, tableName);
			
			ServletOutputStream out = resp.getOutputStream();

			byte buffer[] = new byte[512 * 1024];
			int nbLecture;
			while ((nbLecture = content.read(buffer)) != -1) {
				out.write(buffer, 0, nbLecture);
			}
			content.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();

			InputStream errorStream = createHTMLErrorResponse(e);

			ServletOutputStream out = resp.getOutputStream();
			byte buffer[] = new byte[512 * 1024];
			int nbLecture;
			while ((nbLecture = errorStream.read(buffer)) != -1) {
				out.write(buffer, 0, nbLecture);
			}
			errorStream.close();
			out.close();
		} finally {
			if (content != null) {
				content.reset();
			}
		}
	}
	
	private InputStream createHTMLErrorResponse(Throwable caught) {
		StringBuffer buf = new StringBuffer();
		buf.append("<html>");
		buf.append("	<head></head>");
		buf.append("	<body>");
		buf.append("		<h1>Informations</h1>");
		buf.append("		<p>This document is not available at this time. Sorry for the inconveniance. <br/> The following informations can help you understand the problem :</p>");
		buf.append("		<p style=\"margin: 15px; background-color: #E6E6E6; padding: 20px; font-size: 12px;\">" + ExceptionUtils.getStackTrace(caught).replace("\n", "<br/>") + "</p>");
		buf.append("	</body>");
		buf.append("</html>");
		return IOUtils.toInputStream(buf.toString());
	}

	public ByteArrayInputStream buildFileContent(String name, String format, String separator, int metadataId, String modelName, 
			String packageName, String tableName) throws Exception {

		AbstractBusinessTable table = openDataComponent.getDataManager().getMetadataManager().getBusinessTable(metadataId, modelName, packageName, tableName);
		List<List<String>> datas = table.executeQuery(openDataComponent.getDataManager().getVanillaContext(), 0);

		//Build ColumnHeader
		List<String> columnNames = new ArrayList<>();
		for(IDataStreamElement column : table.getColumns()) {
			columnNames.add(column.getName());
		}
		datas.add(0, columnNames);
		
		if (format.equalsIgnoreCase(FORMAT_XLS)) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			WritableWorkbook workbook = null;
			try {
				workbook = Workbook.createWorkbook(outputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
			WritableSheet sheet = workbook.createSheet(name, 0);

			WritableCellFormat formatHeader = new WritableCellFormat();
			try {
				WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 10);
				cellFont.setBoldStyle(WritableFont.BOLD);

				formatHeader.setFont(cellFont);
				formatHeader.setAlignment(Alignment.LEFT);
			} catch (WriteException e2) {
				e2.printStackTrace();
			}

			WritableCellFormat formatCell = new WritableCellFormat();
			try {
				formatCell.setAlignment(Alignment.CENTRE);
				formatCell.setWrap(true);
			} catch (WriteException e2) {
				e2.printStackTrace();
			}
			sheet.setColumnView(0, 20);

			for (int i = 0; i < datas.size(); i++) {
				for (int j = 0; j < datas.get(i).size(); j++) {
					Label l = new Label(j, i, datas.get(i).get(j));
					if (i == 0)
						l.setCellFormat(formatHeader);
					else
						l.setCellFormat(formatCell);
					try {
						sheet.addCell(l);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}
				}
				sheet.setColumnView(i + 1, 20);
			}

			try {
				workbook.write();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				workbook.close();
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			ByteArrayInputStream is = new ByteArrayInputStream(outputStream.toByteArray());
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return is;
		}
		else if (format.equalsIgnoreCase(FORMAT_CSV)) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			StringBuffer firstLine = new StringBuffer();
			try {
				os.write(firstLine.toString().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < datas.size(); i++) {

				StringBuffer line = new StringBuffer();

				List<String> row = datas.get(i);
				for (int j = 0; j < row.size(); j++) {
					if (j != 0) {
						line.append(separator);
					}
					line.append(datas.get(i).get(j));
					if (j == datas.get(i).size() - 1) {
						line.append("\n");
					}
				}

				try {
					os.write(line.toString().getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			name = name + "_" + new Object().hashCode();

			ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(os.toByteArray());
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return byteArrayIs;
		}
		else if (format.equalsIgnoreCase(FORMAT_WEKA)) {
//			Instances data;
//			FastVector atts = new FastVector();
//			List<String> types = defineAttributes(datas, atts);
//			data = new Instances(tableName, atts, 0);
//
//			addAttributeValue(data, datas, types);
//			ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//			ArffSaver saver = new ArffSaver();
//			saver.setInstances(data);
//
//			try {
//				saver.setDestination(out);
//				saver.writeBatch();
//				out.flush();
//				out.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			name = name + "_" + new Object().hashCode();
//
//			ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(out.toByteArray());
//			try {
//				out.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
		
		return null;
	}
	
//	private List<String> defineAttributes(HashMap<String, List<String>> table, FastVector atts) {
//		List<String> attrTyrpe = new ArrayList<String>();
//		List<String> columnNames = table.getDatas().get(0).getValues();
//		for (int i = 0; i < columnNames.size(); i++) {
//			String colName = columnNames.get(i);
//			if (i < table.getDatabaseColumns().getColumnType().size()) {
//				String type = table.getDatabaseColumns().getColumnType().get(i);
//				switch (type) {
//
//				case "java.lang.String":
//					atts.addElement(new Attribute(colName, (FastVector) null));
//					attrTyrpe.add("STRING");
//					break;
//
//				case "java.math.BigDecimal":
//				case "java.lang.Boolean":
//				case "java.lang.Integer":
//				case "java.lang.Long":
//				case "java.lang.Float":
//				case "java.lang.Double":
//					atts.addElement(new Attribute(colName));
//					attrTyrpe.add("NUMERIC");
//					break;
//
//				case "java.sql.Date":
//				case "java.sql.Time":
//				case "java.sql.Timestamp":
//					String format = extractTimestampInput(table.getDatas().get(1).getValues().get(i));
//					if (format != null)
//						atts.addElement(new Attribute(colName, format));
//					else
//						atts.addElement(new Attribute(colName, (FastVector) null));
//
//					attrTyrpe.add("DATE");
//					break;
//
//				default:
//					atts.addElement(new Attribute(colName, (FastVector) null));
//					attrTyrpe.add("STRING");
//					break;
//				}
//			}
//			else {
//				for (FmdtData column : table.getColumns()) {
//					if (column.getLabel().equals(colName)) {
//						if (column instanceof FmdtAggregate) {
//							atts.addElement(new Attribute(colName));
//							attrTyrpe.add("NUMERIC");
//							break;
//						}
//						else if (column instanceof FmdtFormula) {
//							atts.addElement(new Attribute(colName, (FastVector) null));
//							attrTyrpe.add("STRING");
//							break;
//						}
//					}
//				}
//			}
//		}
//		return attrTyrpe;
//	}
//	
//	private void addAttributeValue(Instances data, HashMap<String, List<String>> datas, List<String> types) {
//		for (int i = 1; i < datas.size(); i++) {
//			List<String> row = datas.get(i);
//			double[] vals = new double[data.numAttributes()];
//
//			for (int j = 0; j < row.size(); j++) {
//				String type = types.get(j);
//				String value = row.get(j);
//
//				switch (type) {
//				case "NUMERIC":
//					try {
//						vals[j] = Double.parseDouble(value);
//					} catch (Exception e) {
//						vals[j] = 0;
//					}
//					break;
//
//				case "STRING":
//					try {
//						vals[j] = data.attribute(j).addStringValue(value);
//					} catch (Exception e) {
//						vals[j] = data.attribute(j).addStringValue("");
//					}
//					break;
//
//				case "DATE":
//					try {
//						vals[j] = data.attribute(j).parseDate(value);
//					} catch (Exception e) {
//						vals[j] = 0;
//					}
//					break;
//
//				default:
//					vals[j] = 0;
//					break;
//				}
//			}
//			Instance inst = new Instance(1.0, vals);
//			data.add(inst);
//		}
//	}
}
