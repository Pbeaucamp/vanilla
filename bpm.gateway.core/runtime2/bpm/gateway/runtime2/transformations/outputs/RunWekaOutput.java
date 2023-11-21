package bpm.gateway.runtime2.transformations.outputs;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.outputs.FileOutputWeka;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.DateFormaterTool;
import bpm.gateway.runtime2.internal.Row;

public class RunWekaOutput extends RuntimeStep {
	
	private static final String DATE = "date";
	private static final String NUMERIC = "numeric";
	private static final String STRING = "string";

	private String separator;
	private PrintWriter writer;
	
	private List<String> types = new ArrayList<String>();

	public RunWekaOutput(FileOutputWeka transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		FileOutputWeka weka = (FileOutputWeka) getTransformation();
		separator = "" + weka.getSeparator();

		String fileName = null;
		try {
			fileName = weka.getDocument().getStringParser().getValue(getTransformation().getDocument(), weka.getDefinition());
		} catch (Exception e) {
			error(" error when getting/parsing fileName", e);
			throw e;
		}

		File f = new File(fileName);
		if (weka.getDelete() && f.exists()) {
			f.delete();
			info(" delete file " + f.getAbsolutePath());
		}

		if (!f.exists()) {
			try {
				f.createNewFile();
				info(" file " + f.getAbsolutePath() + " created");
			} catch (Exception e) {
				error(" cannot create file " + f.getName(), e);
				throw e;
			}
		}

		try {
			writer = new PrintWriter(f, weka.getEncoding());
			info(" Writer created");
		} catch (Exception e) {
			error(" cannot create writer", e);
		}

		if (weka.getDelete()) {
			try {
				writeHeader();
				info(" Weka Header writen");
			} catch (Exception e) {
				error(" error when writing Header", e);
				throw e;
			}

		}
		isInited = true;
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
				Thread.currentThread().interrupt(); // restore interrupted
													// status
			}
			return;
		}

		Row row = readRow();

		if (row == null) {
			return;
		}

		StringBuffer buf = new StringBuffer();
		boolean first = true;
		int i = 0;
		for (Object o : row) {
			if (first) {
				first = false;
			}
			else {
				buf.append(separator);
			}

			if (o == null) {
				if(types.get(i).equals(NUMERIC)) {
					buf.append("?");
				}
				else {
					buf.append("''");
				}
			}
			else if (o instanceof Date) {
				buf.append(DateFormaterTool.format((Date) o));
			}
			else if (types.get(i).equals(NUMERIC)) {
				buf.append(o.toString());
			}
			else if (types.get(i).equals(STRING))  {
				buf.append("'" + o.toString().replace("'", " ") + "'");
			}
			else {
				buf.append("'" + o.toString().replace("'", " ") + "'");
			}
			i++;
		}
		buf.append("\r\n");
		writer.write(buf.toString());
		writeRow(row);
		if (areInputStepAllProcessed()) {
			if (inputEmpty()) {
				setEnd();
			}
		}
	}

	@Override
	public void releaseResources() {
		if (writer != null) {
			writer.close();
			info(" close writer");
			writer = null;
		}
	}

	private void writeHeader() throws Exception {
		types = new ArrayList<String>();
		
		writer.write("@RELATION 'ETL Weka File'\r\n\r\n");

		for (StreamElement e : getTransformation().getDescriptor(getTransformation()).getStreamElements()) {
			String type = getType(e.type);
			types.add(type);
			writer.write("@ATTRIBUTE '" + e.name + "' " + type + "\r\n");
		}
		writer.write("\r\n");

		writer.write("@DATA\r\n");
	}

	private String getType(int type) {
		switch (type) {
		case Types.BOOLEAN:
			return NUMERIC;
		case Types.DATE:
			return DATE + " 'yyyy-MM-dd HH:mm:ss'";
		case Types.FLOAT:
			return NUMERIC;
		case Types.INTEGER:
			return NUMERIC;
		case Types.DOUBLE:
			return NUMERIC;
		case Types.VARCHAR:
			return STRING;
		default:
			return STRING;
		}
	}
}
