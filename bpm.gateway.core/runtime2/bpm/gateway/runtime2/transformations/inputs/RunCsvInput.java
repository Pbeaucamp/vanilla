package bpm.gateway.runtime2.transformations.inputs;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.server.file.FileCSVHelper;
import bpm.gateway.core.transformations.inputs.D4CInput;
import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.gateway.core.transformations.mdm.MdmContractFileInput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.remote.MdmRemote;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.meta.Meta.TypeMeta;
import bpm.vanilla.platform.core.beans.meta.MetaLink.TypeMetaLink;

public class RunCsvInput extends RuntimeStep {

	private static final SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	private String separator;
	private int rowLength = 0;

	private boolean handleError = false;
	private RuntimeStep errorHandler;
	private CSVParser parser;
	private Iterator<CSVRecord> csvIterator;
	
	private List<Object> additionnalData;

	//JSON PART
	private boolean isJson;
	private Iterator<List<Object>> jsonIterator;
	
	public RunCsvInput(IRepositoryContext repositoryContext, Transformation transformation, int bufferSize) {
		super(repositoryContext, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		String file = null;

		FileInputCSV csv = null;
		if (getTransformation() instanceof MdmContractFileInput) {
			csv = (FileInputCSV) ((MdmContractFileInput) getTransformation()).getFileTransfo();
			file = getFileName((AbstractFileServer) csv.getServer(), csv);
			
			this.additionnalData = buildAdditionnalData();

			if (csv.isJson()) {
				this.isJson = true;
				this.jsonIterator = loadJsonFile(csv);
			}
			else {
				CSVFormat format = CSVFormat.newFormat(csv.getSeparator()).withQuote('"');
				parser = getParser(file, format, csv);
			}
		}
		else if (getTransformation() instanceof D4CInput) {
			D4CInput input = (D4CInput) getTransformation();

			csv = (FileInputCSV) input.getFileTransfo();
			file = getFileName((AbstractFileServer) input.getServer(), input);

			if (csv.isJson()) {
				this.isJson = true;
				this.jsonIterator = loadJsonFile(csv);
			}
			else {
				CSVFormat format = CSVFormat.newFormat(csv.getSeparator()).withQuote('"');
				parser = getParser(file, format, input);
			}
		}
		else {
			csv = (FileInputCSV) getTransformation();
			file = getFileName((AbstractFileServer) csv.getServer(), csv);

			if (csv.isJson()) {
				this.isJson = true;
				this.jsonIterator = loadJsonFile(csv);
			}
			else {
				CSVFormat format = CSVFormat.newFormat(csv.getSeparator()).withQuote('"');
				parser = getParser(file, format, csv);
			}
		}

		this.separator = csv.getSeparator() + "";

		info(" seperator value : " + this.separator);
		if (csv.getTrashTransformation() != null) {
			for (RuntimeStep rs : getOutputs()) {
				if (rs.getTransformation() == csv.getTrashTransformation()) {
					handleError = true;
					errorHandler = rs;
					break;
				}
			}
		}

		if (parser != null) {
			csvIterator = parser.iterator();
			if (csv.isSkipFirstRow()) {
				csvIterator.next();
			}
		}
		rowLength = csv.getDescriptor(csv).getColumnCount();
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

	private String getFileName(AbstractFileServer server, DataStream transfo) throws Exception {
		return server.getFileName(transfo);
	}

	private CSVParser getParser(String file, CSVFormat format, FileInputCSV csv) throws MalformedURLException, IOException {
		try {
			// shitty trick for MDM
			int contractId = Integer.parseInt(file);

			IVanillaContext ctx = getRepositoryContext().getVanillaContext();
			MdmRemote mdmRemote = new MdmRemote(ctx.getLogin(), ctx.getPassword(), ctx.getVanillaUrl());
			Contract selectedContract = mdmRemote.getContract(contractId);

			info(" Architect: Use document version with ID = " + selectedContract.getVersionId());

			file = IOUtils.toString(((AbstractFileServer) csv.getServer()).getInpuStream(csv), "UTF-8");
			parser = CSVParser.parse(file, format);
		} catch (Exception e) {
			if (csv.isFromUrl()) {
				parser = CSVParser.parse(new URL(file), Charset.forName("UTF-8"), format);
			}
			else {
				parser = CSVParser.parse(new File(file), Charset.forName("UTF-8"), format);
			}
		}
		return parser;
	}

	private CSVParser getParser(String file, CSVFormat format, D4CInput transfo) throws MalformedURLException, IOException {
		try {
			file = ((AbstractFileServer) transfo.getServer()).getFileName(transfo);
			file = IOUtils.toString(((AbstractFileServer) transfo.getServer()).getInpuStream(transfo), "UTF-8");
			return CSVParser.parse(file, format);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Iterator<List<Object>> loadJsonFile(FileInputCSV csv) throws Exception {
		List<List<Object>> values = FileCSVHelper.getValues(csv, -1, -1);
		return values.iterator();
	}

	@Override
	public void performRow() throws Exception {
		if (isJson) {
			if (!jsonIterator.hasNext()) {
				setEnd();
				return;
			}
			List<Object> record = jsonIterator.next();
	
			readedRows++;
			Row row = RowFactory.createRow(this);
	
			Iterator<Object> line = record.iterator();
			try {
	
				int i = 0;
				while (line.hasNext()) {
					Object value = line.next();
					row.set(i, value);
					i++;
				}
				
				if (additionnalData != null && !additionnalData.isEmpty()) {
					addAdditionnalData(row, i);
				}
			} catch (IndexOutOfBoundsException ex) {
				// error(" problem when creating row for line {" + line + "}", ex);
				throw ex;
			}
			writeRow(row);
		}
		else {
			if (!csvIterator.hasNext()) {
				setEnd();
				return;
			}
			CSVRecord record = csvIterator.next();
	
			readedRows++;
			Row row = RowFactory.createRow(this);
	
			Iterator<String> line = record.iterator();
			try {
	
				int i = 0;
				while (line.hasNext()) {
					String valueAsString = line.next();
					Class<?> clazz = row.getMeta().getJavaClasse(i);
	
					Object value = manageValue(valueAsString, clazz);
					row.set(i, value);
					i++;
				}

				if (additionnalData != null && !additionnalData.isEmpty()) {
					addAdditionnalData(row, i);
				}
			} catch (IndexOutOfBoundsException ex) {
				// error(" problem when creating row for line {" + line + "}", ex);
				throw ex;
			}
			writeRow(row);
		}
	}

	private void addAdditionnalData(Row row, int index) {
		for (Object data : additionnalData) {
			row.set(index, data);
			index++;
		}
	}

	private Object manageValue(String valueAsString, Class<?> clazz) {
		if (valueAsString == null || valueAsString.equalsIgnoreCase("NULL")) {
			return null;
		}
			
		switch (findType(clazz)) {
		case Types.TIMESTAMP:
			try {
				if (valueAsString.isEmpty()) {
					return valueAsString;
				}
				return dft.parse(valueAsString);
			} catch(ParseException e) {
				try {
					return df.parse(valueAsString);
				} catch (ParseException e1) {
				}
			}
			return valueAsString;
		case Types.DATE:
			try {
				if (valueAsString.isEmpty()) {
					return valueAsString;
				}
				return dft.parse(valueAsString);
			} catch(ParseException e) {
				try {
					return df.parse(valueAsString);
				} catch (ParseException e1) {
				}
			}
			return valueAsString;
		case Types.INTEGER:
			try {
				if (valueAsString.equals("")) {
					return null;
				}
				return Integer.parseInt(valueAsString);
			} catch (NumberFormatException e) {
				return valueAsString;
			}
		case Types.FLOAT:
			try {
				if (valueAsString.equals("")) {
					return null;
				}
				return Float.parseFloat(valueAsString);
			} catch (NumberFormatException e) {
				return valueAsString;
			}
		case Types.DOUBLE:
			try {
				if (valueAsString.equals("")) {
					return null;
				}
				return Double.parseDouble(valueAsString);
			} catch (NumberFormatException e) {
				return valueAsString;
			}
		case Types.BOOLEAN:
			try {
				if (valueAsString.equals("")) {
					return false;
				}
				return valueAsString != null ? new Boolean(valueAsString.equals("1")) || Boolean.parseBoolean(valueAsString) : false;
			} catch (Exception e) {
				return valueAsString;
			}
		}
		return valueAsString;
	}

	private int findType(Class<?> javaClasse) {
		if (Date.class.isAssignableFrom(javaClasse)) {
			return Types.DATE;
		}
		else if (Integer.class.isAssignableFrom(javaClasse)) {
			return Types.INTEGER;
		}
		else if (Long.class.isAssignableFrom(javaClasse)) {
			return Types.INTEGER;
		}
		else if (BigDecimal.class.isAssignableFrom(javaClasse)) {
			return Types.DECIMAL;
		}
		else if (Double.class.isAssignableFrom(javaClasse)) {
			return Types.DOUBLE;
		}
		else if (Float.class.isAssignableFrom(javaClasse)) {
			return Types.FLOAT;
		}
		else if (Number.class.isAssignableFrom(javaClasse)) {
			return Types.INTEGER;
		}
		else if (Boolean.class.isAssignableFrom(javaClasse)) {
			return Types.BOOLEAN;
		}
		return Types.VARCHAR;
	}

	@Override
	public void releaseResources() {
		try {
			if (parser != null) {
				parser.close();
			}
			info(" FileReader closed");
		} catch (IOException e) {
			error(" problem when closing FileReader", e);
		}

	}

	@Override
	protected void writeRow(Row row) throws InterruptedException {
		boolean wrote = false;
		for (RuntimeStep r : getOutputs()) {
			if (r != errorHandler) {
				r.insertRow(row, this);
				wrote = true;
			}

		}
		if (wrote) {
			writedRows++;
		}

	}

	protected void writeErrorRow(Row row) throws InterruptedException {
		if (errorHandler != null) {
			errorHandler.insertRow(row, this);
			writedRows++;
		}
	}

}
