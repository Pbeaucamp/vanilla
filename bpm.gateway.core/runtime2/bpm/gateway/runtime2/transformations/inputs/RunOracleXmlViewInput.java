package bpm.gateway.runtime2.transformations.inputs;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import oracle.jdbc.OracleResultSet;
//import oracle.jdbc.driver.GeneratedResultSet;
import oracle.xdb.XMLType;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.JavaAdapter;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.gateway.core.transformations.calcul.Script;
import bpm.gateway.core.transformations.inputs.OracleXmlView;
import bpm.gateway.core.transformations.utils.DefinitionXSD;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunOracleXmlViewInput extends RuntimeStep {

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

	private OracleXmlView transfo;

	private DefinitionXSD rootElement;

	private Statement stmt;
	private ResultSet resultSet;
	private Connection con;
	private int bufferSize;

	public RunOracleXmlViewInput(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		this.bufferSize = bufferSize;
	}

	private void createJdbcResources(DataBaseConnection c) throws Exception {
		if (!c.isUseFullUrl()) {
			con = JdbcConnectionProvider.createSqlConnection(c.getDriverName(), c.getHost(), c.getPort(), c.getDataBaseName(), c.getLogin(), c.getPassword());
			con.setCatalog(c.getDataBaseName());
		}
		else {
			con = JdbcConnectionProvider.createSqlConnection(getTransformation().getDocument(), c.getDriverName(), c.getFullUrl(), c.getLogin(), c.getPassword());
		}

		con.setAutoCommit(true);
		try {
			// well well hortonworks makes it even better than impala
			stmt = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
//			stmt = con.createStatement();
			stmt.setFetchSize(Integer.MIN_VALUE);

			info(" DataBase connection created with fetch size at " + Integer.MIN_VALUE);

		} catch (SQLException e) {
			if (stmt == null) {
				try {
					stmt = con.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
//					stmt = con.createStatement();
				} catch (Exception e1) {
					// This is for the hive shits, because why support any
					// method at all in the Driver ?
					stmt = con.createStatement();
				}
			}

			try {
				stmt.setFetchSize(bufferSize);
				info(" DataBase connection created  with fetch size at " + bufferSize);
			} catch (Exception ex) {
				stmt.setFetchSize(0);
				info(" DataBase connection created  with fetch size at 0");
			}

		} catch (Exception e) {
			e.printStackTrace();
			error(" DataBase connection failed", e);
			throw e;
		}

		String query = getTransformation().getDocument().getStringParser().getValue(getTransformation().getDocument(), ((DataStream) getTransformation()).getDefinition());

		try {
			// resultSet = (OracleResultSet)
			// stmt.executeQuery("SELECT SYS_NC_ROWINFO$ FROM " + query);
			resultSet = stmt.executeQuery(query);

			info(" Run query : " + query);
		} catch (Exception e) {
			warn(" Failed to execute query try to set JdbcStatement Fetch Size at 0");
			try {
				stmt.setFetchSize(0);
				resultSet = stmt.executeQuery(query);
				info(" Run query : " + query);
			} catch (Exception e2) {
				error(" Failed query : " + query, e2);
				throw e2;
			}
		}
	}

	@Override
	public void init(Object adapter) throws Exception {
		this.transfo = (OracleXmlView) getTransformation();

		DataBaseServer server = (DataBaseServer) ((DataStream) getTransformation()).getServer();
		DataBaseConnection c = (DataBaseConnection) server.getCurrentConnection(adapter);
		try {
			createJdbcResources(c);
		} catch (Exception e) {
			List<IServerConnection> alts = server.getConnections();
			if (alts != null && !alts.isEmpty()) {
				for (IServerConnection co : alts) {
					try {
						createJdbcResources((DataBaseConnection) co);
						isInited = true;
						return;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			else {
				e.printStackTrace();
			}
		}

		this.rootElement = transfo.getRootElement();

		if (rootElement == null) {
			throw new Exception("unable to find Root Element from XSD");
		}
		else if (!rootElement.getType().equals(DefinitionXSD.ITERABLE)) {
			throw new Exception("The Root Element should be an Iterable node. Please modify it in the model.");
		}

		isInited = true;
	}

	@Override
	public void performRow() throws Exception {
		if (resultSet == null) {
			throw new Exception("No ResultSet defined");
		}

		if (resultSet.next()) {
			String xmlContent = resultSet.getString(1);

			Document doc = DocumentHelper.parseText(xmlContent);

			HashMap<Transformation, List<Row>> rowsTR = new HashMap<Transformation, List<Row>>();
			HashMap<Row, Script> additionnalRows = new HashMap<Row, Script>();
			HashMap<Script, String> formulas = new HashMap<Script, String>();
			
			Transformation selectedTransfo = findTransfo(transfo, rootElement.getOutputName());
			rowsTR.put(selectedTransfo, new ArrayList<Row>());

			List<Element> elements = (List<Element>) doc.selectNodes("/" + transfo.getRootElement().getElementPath());
			Element current = elements.get(0);
			
			buildFormulas(rootElement.getChilds(), current, rootElement.getColumnId(), formulas, false);
			buildRows(rowsTR, transfo, rootElement.getChilds(), current, selectedTransfo, rootElement.getColumnId(), additionnalRows, formulas, false);

			for (Transformation transfo : rowsTR.keySet()) {
				List<Row> rows = rowsTR.get(transfo);
				for (Row row : rows) {
					setIds(row, formulas);
					writeRow(row, transfo);
				}
			}

//			xml.close();
		}
		else {
			if (!areInputsAlive()) {
				if (areInputStepAllProcessed()) {
					if (inputEmpty()) {
						setEnd();
					}
				}
			}

		}
	}


	private void buildRows(HashMap<Transformation, List<Row>> rowsTR, OracleXmlView fileXml, List<DefinitionXSD> definitions, Element current, Transformation transfo, Script columnId, HashMap<Row, Script> additionnalRows, HashMap<Script, String> formulas, boolean isChild) throws Exception {

		if (definitions != null) {
			Row currentRow = RowFactory.createRow(this, transfo);

			boolean addCurrentRow = false;
			int index = 0;
			for (DefinitionXSD definition : definitions) {
				if (!definition.getType().equals(DefinitionXSD.EXCLUDE)) {

					if (definition.getType().equals(DefinitionXSD.ITERABLE)) {

						Transformation selectedTransfo = findTransfo(fileXml, definition.getOutputName());
						rowsTR.put(selectedTransfo, new ArrayList<Row>());

						Element element = current.element(definition.getName());
						buildRows(rowsTR, fileXml, definition.getChilds(), element, selectedTransfo, definition.getColumnId(), additionnalRows, formulas, false);
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
						if (!isChild && elements != null && (elements.size() == 1 || elements.size() > 1)) {

							if (index == 0) {
								currentRow.set(index, columnId);
								index++;
							}
							
							if (elements.size() > 1) {
								getLogger().info("Multiple elements found for " + definition.getName() + ". We only take the first one.");
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
//						else if (elements != null && elements.size() > 1) {
//
//							for (Element el : elements) {
//								Row newRow = RowFactory.createRow(this, transfo);
//								newRow.set(0, columnId);
//								newRow.set(1, cleanText(el.getText()));
//
//								if (definition.getChilds() != null) {
//									for (DefinitionXSD child : definition.getChilds()) {
//										if (child.isAttribute()) {
//											Attribute attribute = el.attribute(child.getName());
//
//											if (attribute != null) {
//												newRow.set(2, attribute.getText());
//											}
//										}
//									}
//								}
//
//								additionnalRows.put(newRow, columnId);
//								rowsTR.get(transfo).add(newRow);
//							}
//						}

						else if (elements == null || elements.isEmpty()) {

							if (index == 0) {
								currentRow.set(index, columnId);
								index++;
							}
							
							currentRow.set(index, null);
							index++;
							
							// addCurrentRow = true;
						}
					}
				}
			}

			if (addCurrentRow) {
				if (columnId != null) {
					buildFormulas(definitions, currentRow, additionnalRows, formulas);
				}

				rowsTR.get(transfo).add(currentRow);
			}
		}
	}

	public void buildFormulas(List<DefinitionXSD> definitions, Element current, Script columnId, HashMap<Script, String> formulas, boolean isChild) throws Exception {
		if (definitions != null) {
			
			boolean addCurrentRow = false;
			for (DefinitionXSD definition : definitions) {
				if (!definition.getType().equals(DefinitionXSD.EXCLUDE)) {

					if (definition.getType().equals(DefinitionXSD.ITERABLE)) {
						Element element = current.element(definition.getName());
						buildFormulas(definition.getChilds(), element, definition.getColumnId(), formulas, false);
					}
					else if (definition.isAttribute()) {
						addCurrentRow = true;
					}
					else {
						List<Element> elements = current.elements(definition.getName());
						if (!isChild && elements != null && elements.size() == 1) {
							addCurrentRow = true;
						}
					}
				}
			}

			if (addCurrentRow) {
				String scriptValue = getTransformation().getDocument().getStringParser().getValue(getTransformation().getDocument(), columnId.getScriptFunction());
				String formula = PREBUILT_FUNCTIONS + "\r\n" + scriptValue;
				formulas.put(columnId, formula);
			}
		}
	}

	private String cleanText(String text) {
		return text != null && !text.isEmpty() ? text : null;
	}

	private void buildFormulas(List<DefinitionXSD> definitions, Row currentRow, HashMap<Row, Script> rows, HashMap<Script, String> formulas) throws Exception {
		for (Script script : formulas.keySet()) {
			String formula = formulas.get(script);

			for (int index = 0; index < currentRow.getMeta().getSize(); index++) {
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

			formulas.put(script, formula);
			
//			for (Row childRow : rows.keySet()) {
//
//				int coef = 0;
//				for (int index = 0; index < currentRow.getMeta().getSize() - 1; index++) {
//					int i = index + 1 + coef;
//
//					if (definitions.size() <= index || definitions.get(index).getType().equals(DefinitionXSD.EXCLUDE) || definitions.get(index).getType().equals(DefinitionXSD.ITERABLE)) {
//						coef = coef - 1;
//						continue;
//					}
//
//					/*
//					 * replace the columns Field name owned by this Step
//					 */
//					if (Date.class.isAssignableFrom(currentRow.getMeta().getJavaClasse(i))) {
//						if (currentRow.get(i) != null) {
//							formula = formula.replace("{$" + definitions.get(index).getName() + "}", "new Date(" + ((Date) currentRow.get(i)).getTime() + ")");
//						}
//					}
//					else if (String.class.isAssignableFrom(currentRow.getMeta().getJavaClasse(i))) {
//						if (currentRow.get(i) == null) {
//							formula = formula.replace("{$" + definitions.get(index).getName() + "}", "undefined");
//						}
//						else {
//							if (currentRow.get(i).toString().contains("'")) {
//								formula = formula.replace("{$" + definitions.get(index).getName() + "}", "\"" + URLEncoder.encode(currentRow.get(i).toString(), "UTF-8") + "\"");
//							}
//							else {
//								formula = formula.replace("{$" + definitions.get(index).getName() + "}", "'" + URLEncoder.encode(currentRow.get(i).toString(), "UTF-8") + "'");
//							}
//						}
//					}
//					else {
//						formula = formula.replace("{$" + definitions.get(index).getName() + "}", currentRow.get(i) + "");
//					}
//				}
//
//				for (int index = 0; index < childRow.getMeta().getSize() - 1; index++) {
//					if (index == 0) {
//						continue;
//					}
//					int i = index - 1;
//					/*
//					 * replace the columns Field name owned by this Step
//					 */
//					if (Date.class.isAssignableFrom(childRow.getMeta().getJavaClasse(index))) {
//						if (childRow.get(index) != null) {
//							formula = formula.replace("{$" + definitions.get(i).getName() + "}", "new Date(" + ((Date) childRow.get(index)).getTime() + ")");
//						}
//					}
//					else if (String.class.isAssignableFrom(childRow.getMeta().getJavaClasse(index))) {
//						if (childRow.get(index) == null) {
//							formula = formula.replace("{$" + definitions.get(i).getName() + "}", "undefined");
//						}
//						else {
//							if (childRow.get(index).toString().contains("'")) {
//								formula = formula.replace("{$" + definitions.get(i).getName() + "}", "\"" + URLEncoder.encode(childRow.get(index).toString(), "UTF-8") + "\"");
//							}
//							else {
//								formula = formula.replace("{$" + definitions.get(i).getName() + "}", "'" + URLEncoder.encode(childRow.get(index).toString(), "UTF-8") + "'");
//							}
//						}
//					}
//					else {
//						formula = formula.replace("{$" + definitions.get(i).getName() + "}", childRow.get(index) + "");
//					}
//				}
//
//				if (formula != null && formula.contains("new Date()")) {
//					formula = formula.replace("new Date()", "new java.util.Date()");
//				}
//			}
			
			formulas.put(script, formula);
		}
	}

	public void setIds(Row currentRow, HashMap<Script, String> formulas) throws Exception {
		if (currentRow.get(0) != null && currentRow.get(0) instanceof Script) {
			Script script = (Script) currentRow.get(0);
			String formula = formulas.get(script);

			if (formula != null) {
				Class<?> c = currentRow.getMeta().getJavaClasse(0);
				Object o = getIdValue(formula, c);
				currentRow.set(0, o);
			}
		}
		else {
			getLogger().info("Didn't found script for current row with value at get(0) = " + currentRow.get(0));
		}
	}

	private Object getIdValue(String formula, Class<?> c) {
		Object result = null;
		try {
			result = mgr.eval(formula);
		} catch (Exception e) {
			e.printStackTrace();
		}

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
		return o;
	}

	@Override
	public void releaseResources() {
		if (resultSet != null) {

			try {
				resultSet.close();
				resultSet = null;
				info(" closed resultSet");
			} catch (SQLException e) {
				error(" error when closing resultSet", e);
			}
		}

		if (stmt != null) {
			try {
				stmt.close();
				stmt = null;
				info(" closed Statement");
			} catch (SQLException e) {
				error(" error when closing Statement", e);
			}
		}

		if (con != null) {
			try {
				if (!con.isClosed()) {
					con.close();
					info(" closed Connection");
				}
				else {
					info("Connection already closed ");
				}

				con = null;

			} catch (SQLException e) {
				error(" error when closing Connection", e);
			}
		}

	}

	private static Transformation findTransfo(OracleXmlView fileXml, String outputName) {
		if (fileXml.getOutputs() != null) {
			for (Transformation output : fileXml.getOutputs()) {
				if (output.getName().equals(outputName)) {
					return output;
				}
			}
		}
		return null;
	}
}
