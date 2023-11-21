package bpm.gateway.core.server.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.inputs.D4CInput;
import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.gateway.core.transformations.inputs.JsonColumn;
import bpm.vanilla.platform.core.utils.UTF8ToAnsiUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

/**
 * Helper to get Metadata From a CSV file or get its values
 * 
 * @author LCA
 * 
 */
public class FileCSVHelper {
	
	private static final SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	public static CSVParser getParser(DataStream transfo) throws Exception {
		if (transfo instanceof D4CInput) {
			char separator = ((FileInputCSV) ((D4CInput) transfo).getFileTransfo()).getSeparator();
			CSVFormat format = CSVFormat.newFormat(separator).withQuote('"');
			try {
				String file = ((AbstractFileServer) transfo.getServer()).getFileName(transfo);
				file = IOUtils.toString(((AbstractFileServer) transfo.getServer()).getInpuStream(transfo), "UTF-8");
				return CSVParser.parse(file, format);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		else if (transfo instanceof FileCSV) {
			CSVFormat format = CSVFormat.newFormat(((FileCSV) transfo).getSeparator()).withQuote('"');
			try {
				// shitty trick for MDM
				String file = ((AbstractFileServer) transfo.getServer()).getFileName(transfo);
				Integer.parseInt(file);

				file = IOUtils.toString(((AbstractFileServer) transfo.getServer()).getInpuStream(transfo), "UTF-8");
				return CSVParser.parse(file, format);
			} catch (Exception e) {
				String file = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getDefinition());
				if (transfo instanceof FileInputCSV && ((FileInputCSV) transfo).isFromUrl()) {
					return CSVParser.parse(new URL(file), Charset.forName("UTF-8"), format);
				}
				else {
					return CSVParser.parse(new File(file), Charset.forName("UTF-8"), format);
				}
			}
		}
		return null;
	}
	
	public static InputStream getJsonInputStream(DataStream transfo) throws Exception {
		if (transfo instanceof FileCSV) {
			try {
				// shitty trick for MDM
				String file = ((AbstractFileServer) transfo.getServer()).getFileName(transfo);
				Integer.parseInt(file);

				return ((AbstractFileServer) transfo.getServer()).getInpuStream(transfo);
			} catch (Exception e) {
				String file = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), transfo.getDefinition());
				if (transfo instanceof FileInputCSV && ((FileInputCSV) transfo).isFromUrl()) {
					return new URL(file).openStream();
				}
				else {
					return new FileInputStream(file);
				}
			}
		}
		return null;
	}
	
	public static void createStreamDescriptor(DataStream transfo, int numberRow) throws Exception {
		if (transfo instanceof FileInputCSV && ((FileInputCSV) transfo).isJson()) {
			String jsonRootItem = ((FileInputCSV) transfo).getJsonRootItem();
			int jsonDepth = ((FileInputCSV) transfo).getJsonDepth();
			
			DefaultStreamDescriptor desc = createStreamDescriptor(transfo, jsonRootItem, jsonDepth);
			((FileCSV) transfo).setDescriptor(desc);
		}
		else {
			Reader in = null;
			try (CSVParser parser = getParser(transfo)) {
				DefaultStreamDescriptor desc = createStreamDescriptor(transfo.getName(), parser, numberRow);
				
				if (transfo instanceof D4CInput) {
					((FileInputCSV) ((D4CInput) transfo).getFileTransfo()).setDescriptor(desc);
				}
				else if (transfo instanceof FileCSV) {
					((FileCSV) transfo).setDescriptor(desc);
				}
			} catch (Exception e) {
				throw e;
	
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}
	}

	private static DefaultStreamDescriptor createStreamDescriptor(String transfoName, CSVParser parser, int numberRow) throws Exception {
		DefaultStreamDescriptor desc = new DefaultStreamDescriptor();

		Iterator<CSVRecord> csvIterator = parser.iterator();
		CSVRecord recordHeader = csvIterator.next();

		String[] headers = new String[recordHeader.size()];

		Iterator<String> line = recordHeader.iterator();

		int ind = 0;
		while (line.hasNext()) {
			String value = line.next();
			value = UTF8ToAnsiUtils.removeUTF8BOM(value);
			headers[ind] = value;
			ind++;
		}

		HashMap<String, StreamElement> structure = new HashMap<String, StreamElement>();
		HashMap<StreamElement, byte[]> flagsType = new HashMap<StreamElement, byte[]>();
		// Integer,Float,Double,Boolean
		int k = 0;
		for (String s : headers) {
			if (k < headers.length) {
				StreamElement e = new StreamElement();
				e.name = s;
				e.originTransfo = transfoName;
				e.transfoName = transfoName;
				structure.put(s, e);
				flagsType.put(e, new byte[] { -1, -1, -1, -1, -1 });

			}
			k++;

		}

		int counter = 0;
		while (csvIterator.hasNext() && ++counter <= numberRow) {
			CSVRecord recordData = csvIterator.next();
			String[] lineSplit = new String[recordData.size()];

			Iterator<String> lineData = recordData.iterator();

			int index = 0;
			while (lineData.hasNext()) {
				lineSplit[index] = lineData.next();
				index++;
			}

			for (int i = 0; i < lineSplit.length; i++) {
				StreamElement e = null;
				try {
					e = structure.get(headers[i]);
					if (lineSplit[i].trim().equals("") || lineSplit[i].trim().equals("NULL")) {
						continue;
					}

				} catch (Exception ex) {
					ex.printStackTrace();
					continue;
				}

				if (flagsType.get(e)[0] != 0) {
					try {
						Integer.parseInt(lineSplit[i]);
						if (lineSplit[i].equals("0") || !lineSplit[i].startsWith("0")) {
							flagsType.get(e)[0] = 1;
						}
						else {
							flagsType.get(e)[0] = 0;
						}
					} catch (NumberFormatException ex) {
						flagsType.get(e)[0] = 0;
					}
				}

				if (flagsType.get(e)[1] != 0) {
					try {
						Float.parseFloat(lineSplit[i]);
						if (lineSplit[i].equals("0") || !lineSplit[i].startsWith("0")) {
							flagsType.get(e)[1] = 1;
						}
						else {
							flagsType.get(e)[1] = 0;
						}

					} catch (NumberFormatException ex) {
						try {

							Float.parseFloat(lineSplit[i].replace(",", "."));
							if (lineSplit[i].equals("0") || !lineSplit[i].startsWith("0")) {
								flagsType.get(e)[1] = 1;
							}
							else {
								flagsType.get(e)[1] = 0;
							}
						} catch (NumberFormatException x) {
							flagsType.get(e)[1] = 0;
						}
					}
				}

				if (flagsType.get(e)[2] != 0) {
					try {

						Double.parseDouble(lineSplit[i]);
						if (lineSplit[i].equals("0") || !lineSplit[i].startsWith("0")) {
							flagsType.get(e)[2] = 1;
						}
						else {
							flagsType.get(e)[2] = 0;
						}

					} catch (NumberFormatException ex) {

						try {

							Double.parseDouble(lineSplit[i].replace(",", "."));
							flagsType.get(e)[2] = 1;

						} catch (NumberFormatException x) {
							flagsType.get(e)[2] = 0;
						}
					}
				}

				if (flagsType.get(e)[3] != 0) {
					try {
						if (isBoolean(lineSplit[i])) {
							flagsType.get(e)[3] = 1;
						}
						else {
							flagsType.get(e)[3] = 0;
						}

					} catch (NumberFormatException ex) {
						flagsType.get(e)[3] = 0;
					}
				}

				if (flagsType.get(e)[4] != 0) {
					try {
						if (isDate(lineSplit[i])) {
							flagsType.get(e)[4] = 1;
						}
						else {
							flagsType.get(e)[4] = 0;
						}

					} catch (NumberFormatException ex) {
						flagsType.get(e)[4] = 0;
					}
				}
			}

		}

		k = 0;
		for (String s : headers) {
			if (k < headers.length) {

				if (flagsType.get(structure.get(s))[2] == 1) {
					if (flagsType.get(structure.get(s))[0] == 1) {
						structure.get(s).className = Integer.class.getName();
					}
					else if (flagsType.get(structure.get(s))[1] == 1) {
						structure.get(s).className = Float.class.getName();
					}
					else {
						structure.get(s).className = Double.class.getName();
					}

				}
				else if (flagsType.get(structure.get(s))[1] == 1) {
					if (flagsType.get(structure.get(s))[0] == 1) {
						structure.get(s).className = Integer.class.getName();
					}
					else {
						structure.get(s).className = Float.class.getName();
					}
				}
				else if (flagsType.get(structure.get(s))[0] == 1) {
					structure.get(s).className = Integer.class.getName();
				}
				else if (flagsType.get(structure.get(s))[3] == 1) {
					structure.get(s).className = Boolean.class.getName();
				}
				else if (flagsType.get(structure.get(s))[4] == 1) {
					structure.get(s).className = Date.class.getName();
				}
				else {
					structure.get(s).className = String.class.getName();
				}

				desc.addColumn(structure.get(s));
			}

			k++;
		}

		return desc;
	}

	private static boolean isDate(String value) {
		if (value == null) {
			return true;
		}
		
		try {
			dft.parse(value);
		} catch(ParseException e) {
			try {
				df.parse(value);
			} catch (ParseException e1) {
				return false;
			}
		}
		
		return true;
	}

	private static boolean isBoolean(String s) {
		if (s == null || "true".equalsIgnoreCase(s.trim()) || "false".equalsIgnoreCase(s.trim()) || "1".equalsIgnoreCase(s.trim()) || "0".equals(s.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param transfo
	 * @return
	 * @throws Exception
	 */
	public static List<List<Object>> getValues(DataStream transfo, int firstRow, int maxRows) throws Exception {
		int readedRow = 0;
		int currentRow = 0;

		List<List<Object>> values = new ArrayList<List<Object>>();
		
		if (transfo instanceof FileInputCSV && ((FileInputCSV) transfo).isJson()) {
			String jsonRootItem = ((FileInputCSV) transfo).getJsonRootItem();
			int jsonDepth = ((FileInputCSV) transfo).getJsonDepth();

	        List<JsonColumn> columns = new ArrayList<JsonColumn>();
	        
	        try (InputStream jsonInputStream = getJsonInputStream(transfo)) {
				JsonNode jsonTree = new ObjectMapper().readTree(jsonInputStream);
		        JsonNode rootItem = jsonRootItem != null && !jsonRootItem.isEmpty() ? jsonTree.findValue(jsonRootItem) : jsonTree;
		        buildColumns(columns, rootItem, jsonDepth, 0);
		        buildCsv(columns, values, null, rootItem, jsonDepth, 0);
	        } catch (Exception e) {
				throw e;
			}
	
			return values;
		}
		else {

			CSVParser parser = getParser(transfo);
			Iterator<CSVRecord> csvIterator = parser.iterator();
	
			try {
				while (csvIterator.hasNext() && currentRow < maxRows) {
	
					CSVRecord recordData = csvIterator.next();
					String[] lineSplit = new String[recordData.size()];
	
					Iterator<String> lineData = recordData.iterator();
	
					int index = 0;
					while (lineData.hasNext()) {
						lineSplit[index] = lineData.next();
						index++;
					}
	
					readedRow++;
	
					if (readedRow <= firstRow) {
						continue;
					}
	
					List<Object> row = new ArrayList<Object>();
	
					for (int i = 0; i < lineSplit.length; i++) {
	
						if (lineSplit[i].trim().equals("")) {
							row.add(null);
							continue;
						}
	
						try {
							row.add(Integer.parseInt(lineSplit[i]));
							continue;
						} catch (NumberFormatException ex) {
	
						}
	
						try {
							row.add(Float.parseFloat(lineSplit[i]));
							continue;
						} catch (NumberFormatException ex) {
	
						}
	
						try {
							row.add(Double.parseDouble(lineSplit[i]));
							continue;
						} catch (NumberFormatException ex) {
	
						}
	
						try {
							if (isBoolean(lineSplit[i])) {
								row.add(Boolean.parseBoolean(lineSplit[i]));
								continue;
							}
	
						} catch (NumberFormatException ex) {
	
						}
	
						try {
							if (isDate(lineSplit[i])) {
								try {
									row.add(dft.parse(lineSplit[i]));
								} catch (ParseException ex) {
									row.add(df.parse(lineSplit[i]));
								}
								continue;
							}
	
						} catch (ParseException ex) {
	
						}
	
						row.add(lineSplit[i]);
					}
	
					values.add(row);
					currentRow++;
				}
	
			} catch (Exception e) {
				throw e;
			} finally {
				if (parser != null) {
					parser.close();
				}
			}
	
			return values;
		}
	}

	public static List<List<Object>> getCountDistinctFieldValues(StreamElement field, DataStream stream) throws Exception {
		int colPos = stream.getDescriptor(null).getStreamElements().indexOf(field);
		List<List<Object>> values = new ArrayList<List<Object>>();
		
		if (stream instanceof FileInputCSV && ((FileInputCSV) stream).isJson()) {
			String jsonRootItem = ((FileInputCSV) stream).getJsonRootItem();
			int jsonDepth = ((FileInputCSV) stream).getJsonDepth();

	        List<JsonColumn> columns = new ArrayList<JsonColumn>();
	        
	        try (InputStream jsonInputStream = getJsonInputStream(stream)) {
				JsonNode jsonTree = new ObjectMapper().readTree(jsonInputStream);
		        JsonNode rootItem = jsonRootItem != null && !jsonRootItem.isEmpty() ? jsonTree.findValue(jsonRootItem) : jsonTree;
		        buildColumns(columns, rootItem, jsonDepth, 0);
		        
		        List<List<Object>> jsonValues = new ArrayList<List<Object>>();
		        buildCsv(columns, jsonValues, null, rootItem, jsonDepth, 0);
		        
		        for (List<Object> line : jsonValues) {
		        	Object value = line.get(colPos);
		        	
		        	boolean present = false;
					for (List<Object> l : values) {
						if (l.get(0).equals(value)) {
							l.set(1, (Integer) l.get(1) + 1);
							present = true;
							break;
						}
					}
	
					if (!present) {
						List<Object> l = new ArrayList<Object>();
						l.add(value);
						l.add(1);
						values.add(l);
					}
		        }
	        } catch (Exception e) {
				throw e;
			}
	
			return values;
		}
		else {
			CSVParser parser = getParser(stream);
			Iterator<CSVRecord> csvIterator = parser.iterator();
			try {
				while (csvIterator.hasNext()) {
					CSVRecord recordData = csvIterator.next();
					String[] lineSplit = new String[recordData.size()];
	
					Iterator<String> lineData = recordData.iterator();
	
					int index = 0;
					while (lineData.hasNext()) {
						lineSplit[index] = lineData.next();
						index++;
					}
					// String[] lineSplit = line.split("\\" + stream.getSeparator()
					// + "", -1);
	
					String value = lineSplit[colPos];
					boolean present = false;
					for (List<Object> l : values) {
						if (l.get(0).equals(value)) {
							l.set(1, (Integer) l.get(1) + 1);
							present = true;
							break;
						}
					}
	
					if (!present) {
						List<Object> l = new ArrayList<Object>();
						l.add(value);
						l.add(1);
						values.add(l);
					}
	
				}
	
			} catch (Exception e) {
				throw e;
			} finally {
				if (parser != null) {
					parser.close();
				}
			}
	
			return values;
		}
	}
	
	
	
	
	/*** JSON PART ***/

	private static DefaultStreamDescriptor createStreamDescriptor(DataStream transfo, String jsonRootItem, int jsonDepth) throws Exception {
		try (InputStream jsonInputStream = getJsonInputStream(transfo)) {
			String transfoName = transfo.getName();
			
			DefaultStreamDescriptor desc = new DefaultStreamDescriptor();

			JsonNode jsonTree = new ObjectMapper().readTree(jsonInputStream);
	        
	        List<JsonColumn> columns = new ArrayList<JsonColumn>();
	        JsonNode rootItem = jsonRootItem != null && !jsonRootItem.isEmpty() ? jsonTree.findValue(jsonRootItem) : jsonTree;
	        buildColumns(columns, rootItem, jsonDepth, 0);

			for (JsonColumn column : columns) {
				StreamElement e = new StreamElement();
				e.name = column.getName();
				e.originTransfo = transfoName;
				e.transfoName = transfoName;
				e.className = column.getGatewayType();
				desc.addColumn(e);
			}

			return desc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static void buildCsv(List<JsonColumn> columns, List<List<Object>> values, List<Object> currentLine, JsonNode node, int maxDepth, int depth) {
		if (depth == 1) {
			//Depth == 0 -> We create a new Line
			currentLine = new ArrayList<>(Arrays.asList(new Object[columns.size()]));
			values.add(currentLine);
		}
		
		if (maxDepth > 0) {
			if (node.getNodeType() == JsonNodeType.ARRAY) {
		        Iterator<JsonNode> itElements = node.elements();

		        while (itElements.hasNext()) {
		        	JsonNode item = itElements.next();
		        	buildCsv(columns, values, currentLine, item, maxDepth-1, depth+1);
		        }
			}
			else {
		        Iterator<Entry<String, JsonNode>> itElements = node.fields();
	
		        while (itElements.hasNext()) {
		        	Entry<String, JsonNode> entry = itElements.next();
		        	String columnName = entry.getKey();
		        	JsonNode item = entry.getValue();
		        	
		        	Integer index = hasColumnValue(columns, columnName, depth, item.getNodeType(), item);
		        	if (index != null) {
		        		Object value = getJsonValue(node, columnName);
						currentLine.set(index, value);
		        	}
		        	else {
		        		buildCsv(columns, values, currentLine, item, maxDepth-1, depth+1);
		        	}
		        }
			}
		}
		else {
	        Iterator<Entry<String, JsonNode>> itElements = node.fields();
	    	
	        while (itElements.hasNext()) {
	        	Entry<String, JsonNode> entry = itElements.next();
	        	String columnName = entry.getKey();
	        	JsonNode item = entry.getValue();
	        	
	        	Integer index = hasColumnValue(columns, columnName, depth, item.getNodeType(), item);
	        	if (index != null) {
	        		Object value = getJsonValue(node, columnName);
					currentLine.set(index, value);
	        	}
	        }
		}
	}

	private static Integer hasColumnValue(List<JsonColumn> columns, String columnName, int depth, JsonNodeType type, JsonNode node) {
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).equals(new JsonColumn(columnName, depth, type))) {
				return i;
			}
		}
		return null;
	}
	
	private static boolean isColumnField(JsonNode node) {
		JsonNodeType type = node.getNodeType();
		switch (type) {
		case BOOLEAN:
		case NUMBER:
		case STRING:
		case NULL:
		case MISSING:
			return true;
		case OBJECT:
		case POJO:
		case ARRAY:
		case BINARY:
			//Not supported
			break;
		default:
			break;
		}
		return false;
	}

	private static Object getJsonValue(JsonNode node, String column) {
		JsonNodeType type = node.get(column).getNodeType();
		switch (type) {
		case BOOLEAN:
			return node.get(column).asBoolean();
		case NUMBER:
			return node.get(column).asDouble();
		case STRING:
			return node.get(column).asText();
		case ARRAY:
			return node.get(column).toString();
		case NULL:
		case MISSING:
		case OBJECT:
		case POJO:
		case BINARY:
			//Not supported
			break;
		default:
			break;
		}
		return null;
	}

	private static void buildColumns(List<JsonColumn> columns, JsonNode node, int maxDepth, int depth) {
		if (maxDepth > 0) {
			if (node.getNodeType() == JsonNodeType.ARRAY) {
		        Iterator<JsonNode> itElements = node.elements();

		        while (itElements.hasNext()) {
		        	JsonNode item = itElements.next();
	        		buildColumns(columns, item, maxDepth-1, depth+1);
		        }
			}
			else {
		        Iterator<Entry<String, JsonNode>> itElements = node.fields();
	
		        while (itElements.hasNext()) {
		        	Entry<String, JsonNode> entry = itElements.next();
		        	String fieldName = entry.getKey();
		        	JsonNode item = entry.getValue();
		        	
		        	if (isColumnField(item)) {
			        	JsonColumn column = new JsonColumn(fieldName, depth, item.getNodeType());
			        	addColumnOrVerifyType(columns, column);
		        	}
		        	else {
		        		buildColumns(columns, item, maxDepth-1, depth+1);
		        	}
		        }
			}
		}
		else {
	        Iterator<Entry<String, JsonNode>> itElements = node.fields();
	    	
	        while (itElements.hasNext()) {
	        	Entry<String, JsonNode> entry = itElements.next();
	        	String fieldName = entry.getKey();
	        	JsonNode item = entry.getValue();
	        	
	        	JsonColumn newColumn = new JsonColumn(fieldName, depth, item.getNodeType());
	        	addColumnOrVerifyType(columns, newColumn);
	        }
		}
	}

	private static void addColumnOrVerifyType(List<JsonColumn> columns, JsonColumn newColumn) {
		for (JsonColumn column : columns) {
			if (column.equals(newColumn)) {
				//Here we need to verify if the type is still good with the previous value
	    		//If not we put a more generic one (in order BOOLEAN -> STRING - INT -> STRING - etc...)
	    		column.verifyType(newColumn);
				return;
			}
		}
		columns.add(newColumn);
	}
	
//	public static void main(String[] args) {
//		createStreamDescriptor("D:/DATA/Test/0121/communes.json", "", 2);
//		createStreamDescriptor("D:/DATA/Test/0121/complexe.json", "medications", 3);
//		createStreamDescriptor("D:/DATA/Test/0121/sante_fr_g_ny_4.geojson", "features", 3);
//	}
//	
//	public static void createStreamDescriptor(String file, String rootElement, int maxDepth) {	
//		try {	
//	        JsonNode jsonTree = new ObjectMapper().readTree(new File(file));
//	        
//	        List<JsonColumn> columns = new ArrayList<JsonColumn>();
//	        JsonNode rootItem = rootElement != null && !rootElement.isEmpty() ? jsonTree.findValue(rootElement) : jsonTree;
//	        buildColumns(columns, rootItem, maxDepth, 0);
//	        
//	        for (JsonColumn column : columns) {
//		        System.out.print(column.getName() + "(" + column.getDepth() + ")");
//		        System.out.print(";");
//	        }
//	        System.out.println("");
//
//	        List<List<Object>> values = new ArrayList<List<Object>>();
//	        buildCsv(columns, values, null, rootItem, maxDepth, 0);
//	        for (List<Object> lines : values) {
//	        	for (Object value : lines) {
//			        System.out.print(value);
//			        System.out.print(";");
//	        	}
//		        System.out.println();
//	        }
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
