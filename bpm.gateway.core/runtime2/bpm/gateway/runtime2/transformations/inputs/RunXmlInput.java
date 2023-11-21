package bpm.gateway.runtime2.transformations.inputs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.io.IOUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.JavaAdapter;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.server.file.FileXML;
import bpm.gateway.core.transformations.calcul.Script;
import bpm.gateway.core.transformations.inputs.FileInputXML;
import bpm.gateway.core.transformations.utils.DefinitionXSD;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.gateway.runtime2.tools.NameSpaceCleaner;
import bpm.vanilla.platform.core.utils.PGPFileProcessor;

public class RunXmlInput extends RuntimeStep {

	private static String PREBUILT_FUNCTIONS = "";

	static {
		StringBuffer buf = new StringBuffer();
		buf.append("function dateDifference(date1, date2){\n");
		buf.append("     var d1 = new Date();\n");
		buf.append("     d1.setTime(date1);\n");
		buf.append("     var d2 = new Date();\n");
		buf.append("     d2.setTime(date2);\n");
		buf.append("     var d3 = new Date();\n");
		buf.append("     d3.setTime(date1-date2);\n");
		buf.append("     return d3;\n");
		buf.append("}\n");

		PREBUILT_FUNCTIONS = buf.toString();
	}

	private static ScriptEngine mgr = new ScriptEngineManager().getEngineByName("JavaScript");
	private Class<?>[] integerClasses = { Integer.class, Long.class, BigDecimal.class };

	private InputStream fis;
	private Document document;
	private Iterator<Element> rowIterator;
	private String[] elementColumnNames;

	private FileInputXML fXml;
	private DefinitionXSD rootElement;
	
	private String defaultAttributeName;

	public RunXmlInput(FileInputXML transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		this.fXml = (FileInputXML) getTransformation();

		if (fXml.isEncrypting() && (fXml.getPrivateKeyPath() == null || fXml.getPrivateKeyPath().isEmpty() || fXml.getPassword() == null || fXml.getPassword().isEmpty())) {
			error(" impossible to decrypt the file: The Private Key or the Password is empty.");
			throw new Exception("Impossible to decrypt the file: The Public Key is empty.");
		}

		if (fXml.isEncrypting()) {
			try {
				String privateKeyPath = fXml.getDocument().getStringParser().getValue(fXml.getDocument(), fXml.getPrivateKeyPath());
				fis = decryptFilePGP(((AbstractFileServer) fXml.getServer()).getInpuStream(fXml), privateKeyPath, fXml.getPassword());
			} catch (Throwable e) {
				error("unable to open file ", e);
				throw new Exception(e);
			}
		}
		else {
			try {
				if (fXml.isFromUrl()) {
					String fileUrl = fXml.getDocument().getStringParser().getValue(fXml.getDocument(), fXml.getDefinition());
					fis = new URL(fileUrl).openStream();
				}
				else {
					fis = ((AbstractFileServer) fXml.getServer()).getInpuStream(fXml);
				}
			} catch (Exception e) {
				error("unable to open file ", e);
				throw e;
			}
		}
		
		try {
			String xml = buildXml(fis);
			document = DocumentHelper.parseText(xml);
			info(" XML document rebuilt");
		} catch (Exception e) {
			error("unable build XML Document from file ", e);
			throw e;
		}

		if (fXml.isFromXSD()) {
			this.rootElement = fXml.getRootElement();

			if (rootElement == null) {
				throw new Exception("unable to find Root Element from XSD");
			}
			else if (!rootElement.getType().equals(DefinitionXSD.ITERABLE)) {
				throw new Exception("The Root Element should be an Iterable node. Please modify it in the model.");
			}

			rowIterator = document.selectNodes("/" + fXml.getRootElement().getElementPath()).iterator();
		}
		else {
			Element root = document.getRootElement();
			Element rows = fXml.getRootTag() != null && !fXml.getRootTag().isEmpty() ? ((!root.getName().equals(fXml.getRootTag())) ? root.element(fXml.getRootTag()) : root) : root;

			if (rows == null) {
				error(" unable to find rows Root");
				throw new Exception("unable to find rows Root");
			}
			
			if (rows.getName().equals(fXml.getRowTag())) {
				List<Element> elements = new ArrayList<Element>();
				elements.add(rows);
				
				rowIterator = elements.iterator();
			}
			else {
				rowIterator = rows.elements(fXml.getRowTag()).iterator();
			}
			
			/*
			 * get the column names
			 */
			elementColumnNames = new String[fXml.getDescriptor(fXml).getColumnCount()];
			int i = 0;
			for (StreamElement e : fXml.getDescriptor(fXml).getStreamElements()) {
				elementColumnNames[i++] = new String(e.name);
			}
			
			//Get the attribute name, if values are only put a custom attribute (like V="")
			this.defaultAttributeName = fXml.getDefaultAttributeName();
		}

		isInited = true;
		info(" inited");
	}

	private String buildXml(InputStream fis) throws DocumentException, IOException {
		String xml = IOUtils.toString(fis, fXml.getEncoding());
		
		SAXReader reader = new SAXReader();
		Document document = reader.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));
		document.accept(new NameSpaceCleaner());
		return document.asXML();
	}

	private static InputStream decryptFilePGP(InputStream encodedFile, String secretKeyPath, String password) throws Throwable {
		ByteArrayOutputStream bos = PGPFileProcessor.decrypt(secretKeyPath, encodedFile, password);

		ByteArrayInputStream is = new ByteArrayInputStream(bos.toByteArray());
		bos.close();
		return is;
	}

	@Override
	public void performRow() throws Exception {
		if (fXml.isFromXSD()) {
			if (!rowIterator.hasNext()) {
				setEnd();
				return;
			}

			Element current = rowIterator.next();

			HashMap<Transformation, List<Row>> rowsTR = new HashMap<Transformation, List<Row>>();
			HashMap<Row, Script> additionnalRows = new HashMap<Row, Script>();

			Transformation selectedTransfo = findTransfo(fXml, rootElement.getOutputName());
			rowsTR.put(selectedTransfo, new ArrayList<Row>());

			buildRows(rowsTR, fXml, rootElement.getChilds(), current, selectedTransfo, rootElement.getColumnId(), additionnalRows, false);

			for (Transformation transfo : rowsTR.keySet()) {
				List<Row> rows = rowsTR.get(transfo);
				for (Row row : rows) {
					writeRow(row, transfo);
				}
			}
		}
		else {
			if (!rowIterator.hasNext()) {
				setEnd();
				return;
			}

			Element current = rowIterator.next();

			Row row = RowFactory.createRow(this);

			for (int i = 0; i < row.getMeta().getSize(); i++) {
				Element e = current.element(elementColumnNames[i]);
				if (e != null) {
					if (e.getText() != null && !e.getText().isEmpty()) {
						row.set(i, cleanText(e.getText()));
					}
					else if (defaultAttributeName != null) {
						row.set(i, cleanText(e.attributeValue(defaultAttributeName)));
					}
					else {
						row.set(i, "");
					}
				}
			}

			readedRows++;
			writeRow(row);
		}
	}

	private String cleanText(String text) {
		return text != null && !text.isEmpty() ? text : null;
	}

	private static Transformation findTransfo(FileXML fileXml, String outputName) {
		if (fileXml.getOutputs() != null) {
			for (Transformation output : fileXml.getOutputs()) {
				if (output.getName().equals(outputName)) {
					return output;
				}
			}
		}
		return null;
	}

	private void buildRows(HashMap<Transformation, List<Row>> rowsTR, FileXML fileXml, List<DefinitionXSD> definitions, Element current, Transformation transfo, Script columnId, HashMap<Row, Script> additionnalRows, boolean isChild) throws Exception {

		if (definitions != null) {
			Row currentRow = RowFactory.createRow(this, transfo);

			boolean addCurrentRow = false;
			Integer index = new Integer(0);
			for (DefinitionXSD definition : definitions) {
				if (!definition.getType().equals(DefinitionXSD.EXCLUDE)) {

					if (definition.getType().equals(DefinitionXSD.ITERABLE)) {

						Transformation selectedTransfo = findTransfo(fileXml, definition.getOutputName());
						rowsTR.put(selectedTransfo, new ArrayList<Row>());

						Element element = current.element(definition.getName());
						if(element != null) {
							buildRows(rowsTR, fileXml, definition.getChilds(), element, selectedTransfo, definition.getColumnId(), additionnalRows, true);
						}
						else {
//							index = setNullValuesForIterable(definition, currentRow, index);
						}
					}
					else if (definition.isAttribute()) {
						Attribute attribute = null;
						try {
							attribute = current.attribute(definition.getName());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						if (index == 0 && columnId != null) {
							currentRow.set(index, columnId);
							index++;
						}

						try {
							currentRow.set(index, attribute.getText());
						} catch (Exception e) {
							// XXX can happen if the attribute is optional
							currentRow.set(index, null);
						}
						index++;
						addCurrentRow = true;
					}
					else {
						List<Element> elements = current.elements(definition.getName());
						if (!isChild && elements != null && elements.size() == 1) {
							if (index == 0) {
								currentRow.set(index, columnId);
								index++;
							}

							currentRow.set(index, cleanText(elements.get(0).getText()));
							index++;
							addCurrentRow = true;

							if (definition.getChilds() != null) {
								for (DefinitionXSD child : definition.getChilds()) {
									if (child.isAttribute()) {
										Attribute attribute = elements.get(0).attribute(child.getName());

										if (attribute != null) {
											currentRow.set(index, attribute.getText());
											index++;
										}
									}
								}
							}
						}
						else if (isChild && elements != null && elements.size() == 1) {
							Row newRow = RowFactory.createRow(this, transfo);
							newRow.set(0, columnId);
							newRow.set(1, cleanText(elements.get(0).getText()));

							if (definition.getChilds() != null) {
								for (DefinitionXSD child : definition.getChilds()) {
									if (child.isAttribute()) {
										Attribute attribute = elements.get(0).attribute(child.getName());

										if (attribute != null) {
											newRow.set(2, attribute.getText());
										}
									}
								}
							}

							additionnalRows.put(newRow, columnId);
							rowsTR.get(transfo).add(newRow);
						}
						else if (elements != null && elements.size() > 1) {
							for (Element el : elements) {
								Row newRow = RowFactory.createRow(this, transfo);
								newRow.set(0, columnId);
								newRow.set(1, cleanText(el.getText()));

								if (definition.getChilds() != null) {
									for (DefinitionXSD child : definition.getChilds()) {
										if (child.isAttribute()) {
											Attribute attribute = el.attribute(child.getName());

											if (attribute != null) {
												newRow.set(2, attribute.getText());
											}
										}
									}
								}

								additionnalRows.put(newRow, columnId);
								rowsTR.get(transfo).add(newRow);
							}
						}

						else if (elements == null || elements.isEmpty()) {
							if (index == 0) {
								currentRow.set(index, columnId);
								index++;
							}
							currentRow.set(index, null);
							index++;
							// addCurrentRow = true;
						}

						else if (elements == null || elements.isEmpty()) {
							currentRow.set(index, null);
							index++;
							// addCurrentRow = true;
						}

						if (elements != null && !elements.isEmpty()) {
							// buildRows(rowsTR, fileXml,
							// definition.getChilds(), elements.get(0), transfo,
							// columnId, additionnalRows, true);
						}
					}
				}
			}

			if (addCurrentRow) {
				if(columnId != null) {
					buildIds(definitions, currentRow, columnId, additionnalRows);
				}

				rowsTR.get(transfo).add(currentRow);
			}
		}
	}

	private int setNullValuesForIterable(DefinitionXSD definition, Row currentRow, Integer index) {
		for(DefinitionXSD d : definition.getChilds()) {
			if (d.getType().equals(DefinitionXSD.ITERABLE)) {
				index = setNullValuesForIterable(d, currentRow, index);
			}
			else {
				currentRow.set(index, null);
				index = index + 1;
			}
		}
		return index;
	}

	public void buildIds(List<DefinitionXSD> definitions, Row currentRow, Script currentScript, HashMap<Row, Script> rows) throws Exception {
		String test = getTransformation().getDocument().getStringParser().getValue(getTransformation().getDocument(), currentScript.getScriptFunction());
		String formula = PREBUILT_FUNCTIONS + "\r\n" + test;

		for (int index = 0; index < currentRow.getMeta().getSize() - 1; index++) {
			if (index == 0) {
				continue;
			}
			int i = index - 1;
			/*
			 * replace the columns Field name owned by this Step
			 */
			if (Date.class.isAssignableFrom(currentRow.getMeta().getJavaClasse(index))) {
				if (currentRow.get(index) != null) {
					formula = formula.replace("{$" + definitions.get(i).getName() + "}", "new Date(" + ((Date) currentRow.get(index)).getTime() + ")");
				}
			}
			else if (String.class.isAssignableFrom(currentRow.getMeta().getJavaClasse(index))) {
				if (currentRow.get(index) == null) {
					formula = formula.replace("{$" + definitions.get(i).getName() + "}", "undefined");
				}
				else {
					if (currentRow.get(index).toString().contains("'")) {
						formula = formula.replace("{$" + definitions.get(i).getName() + "}", "\"" + URLEncoder.encode(currentRow.get(index).toString(), "UTF-8") + "\"");
					}
					else {
						formula = formula.replace("{$" + definitions.get(i).getName() + "}", "'" + URLEncoder.encode(currentRow.get(index).toString(), "UTF-8") + "'");
					}
				}
			}
			else {
				formula = formula.replace("{$" + definitions.get(i).getName() + "}", currentRow.get(index) + "");
			}
		}

		if (formula != null && formula.contains("new Date()")) {
			formula = formula.replace("new Date()", "new java.util.Date()");
		}

		Object result = mgr.eval(formula);
		Class<?> c = currentRow.getMeta().getJavaClasse(0);

		Object o = null;
		try {
			if (c == BigInteger.class) {
				for (int i = 0; i < integerClasses.length && o == null; i++) {
					try {
						o = JavaAdapter.convertResult(result, integerClasses[i]);
					} catch (EvaluatorException ex) {

					}
				}
				if (o == null) {
					try {
						o = JavaAdapter.convertResult(result, c);
					} catch (EvaluatorException ex) {

					}

					if (o instanceof Double) {
						o = ((Double) o).longValue();
					}
				}
			}
			else {
				o = JavaAdapter.convertResult(result, c);
			}

		} catch (EvaluatorException ex) {
			try {
				if (Number.class.isAssignableFrom(c)) {
					o = JavaAdapter.convertResult(result, Date.class);
					o = ((Date) o).getTime();
				}
			} catch (Exception ex2) {

			}
		}
		if (o instanceof String) {
			try {
				o = URLDecoder.decode((String) o, "UTF-8");
			} catch (Exception ex) {
				warn("UTF-8 decryption problems encountered on " + o + " : " + ex.getMessage(), ex);
			}
		}
		currentRow.set(0, o);

		for (Row childRow : rows.keySet()) {
			Script sc = rows.get(childRow);

			String test2 = getTransformation().getDocument().getStringParser().getValue(getTransformation().getDocument(), sc.getScriptFunction());
			String formula2 = PREBUILT_FUNCTIONS + "\r\n" + test2;

			int coef = 0;
			for (int index = 0; index < currentRow.getMeta().getSize() - 1; index++) {
				int i = index + 1 + coef;

				if (definitions.size() <= index || definitions.get(index).getType().equals(DefinitionXSD.EXCLUDE) || definitions.get(index).getType().equals(DefinitionXSD.ITERABLE)) {
					coef = coef - 1;
					continue;
				}

				/*
				 * replace the columns Field name owned by this Step
				 */
				if (Date.class.isAssignableFrom(currentRow.getMeta().getJavaClasse(i))) {
					if (currentRow.get(i) != null) {
						formula2 = formula2.replace("{$" + definitions.get(index).getName() + "}", "new Date(" + ((Date) currentRow.get(i)).getTime() + ")");
					}
				}
				else if (String.class.isAssignableFrom(currentRow.getMeta().getJavaClasse(i))) {
					if (currentRow.get(i) == null) {
						formula2 = formula2.replace("{$" + definitions.get(index).getName() + "}", "undefined");
					}
					else {
						if (currentRow.get(i).toString().contains("'")) {
							formula2 = formula2.replace("{$" + definitions.get(index).getName() + "}", "\"" + URLEncoder.encode(currentRow.get(i).toString(), "UTF-8") + "\"");
						}
						else {
							formula2 = formula2.replace("{$" + definitions.get(index).getName() + "}", "'" + URLEncoder.encode(currentRow.get(i).toString(), "UTF-8") + "'");
						}
					}
				}
				else {
					formula2 = formula2.replace("{$" + definitions.get(index).getName() + "}", currentRow.get(i) + "");
				}
			}

			for (int index = 0; index < childRow.getMeta().getSize() - 1; index++) {
				if (index == 0) {
					continue;
				}
				int i = index - 1;
				/*
				 * replace the columns Field name owned by this Step
				 */
				if (Date.class.isAssignableFrom(childRow.getMeta().getJavaClasse(index))) {
					if (childRow.get(index) != null) {
						formula2 = formula2.replace("{$" + definitions.get(i).getName() + "}", "new Date(" + ((Date) childRow.get(index)).getTime() + ")");
					}
				}
				else if (String.class.isAssignableFrom(childRow.getMeta().getJavaClasse(index))) {
					if (childRow.get(index) == null) {
						formula2 = formula2.replace("{$" + definitions.get(i).getName() + "}", "undefined");
					}
					else {
						if (childRow.get(index).toString().contains("'")) {
							formula2 = formula2.replace("{$" + definitions.get(i).getName() + "}", "\"" + URLEncoder.encode(childRow.get(index).toString(), "UTF-8") + "\"");
						}
						else {
							formula2 = formula2.replace("{$" + definitions.get(i).getName() + "}", "'" + URLEncoder.encode(childRow.get(index).toString(), "UTF-8") + "'");
						}
					}
				}
				else {
					formula2 = formula2.replace("{$" + definitions.get(i).getName() + "}", childRow.get(index) + "");
				}
			}

			if (formula2 != null && formula2.contains("new Date()")) {
				formula2 = formula2.replace("new Date()", "new java.util.Date()");
			}

			Object result2 = null;
			try {
				result2 = mgr.eval(formula2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Class<?> c2 = childRow.getMeta().getJavaClasse(0);

			Object o2 = null;
			try {
				if (c2 == BigInteger.class) {
					for (int i = 0; i < integerClasses.length && o2 == null; i++) {
						try {
							o2 = JavaAdapter.convertResult(result2, integerClasses[i]);
						} catch (EvaluatorException ex) {

						}
					}
					if (o2 == null) {
						try {
							o2 = JavaAdapter.convertResult(result2, c2);
						} catch (EvaluatorException ex) {

						}

						if (o2 instanceof Double) {
							o2 = ((Double) o2).longValue();
						}
					}
				}
				else {
					o2 = JavaAdapter.convertResult(result2, c2);
				}

			} catch (EvaluatorException ex) {
				try {
					if (Number.class.isAssignableFrom(c2)) {
						o2 = JavaAdapter.convertResult(result2, Date.class);
						o2 = ((Date) o2).getTime();
					}
				} catch (Exception ex2) {

				}
			}
			if (o2 instanceof String) {
				try {
					o2 = URLDecoder.decode((String) o2, "UTF-8");
				} catch (Exception ex) {
					warn("UTF-8 decryption problems encountered on " + o2 + " : " + ex.getMessage(), ex);
				}
			}
			childRow.set(0, o2);
		}
	}

	@Override
	public void releaseResources() {
		rowIterator = null;
		document = null;
		try {
			fis.close();
		} catch (Exception ex) {

		}
		info(" resources released");

	}

}
