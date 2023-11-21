package bpm.gateway.core.server.file;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.outputs.FileOutputWeka;

public class FileWekaHelper {

	private static boolean isBoolean(String s) {
		if (s == null || "true".equalsIgnoreCase(s.trim()) || "false".equalsIgnoreCase(s.trim()) || "1".equalsIgnoreCase(s.trim()) || "0".equals(s.trim())) {
			return true;
		}
		return false;
	}

	public static List<List<Object>> getValues(FileCSV transfo, int firstRow, int maxRows) throws Exception {
		int readedRow = 0;
		int currentRow = 0;

		List<List<Object>> values = new ArrayList<List<Object>>();
		BufferedReader br = null;
		try {
			Reader in = new InputStreamReader(((AbstractFileServer) transfo.getServer()).getInpuStream(transfo), transfo.getEncoding());
			br = new BufferedReader(in);

			boolean wellFormed = false;
			
			String line = null;
			while ((line = br.readLine()) != null) {
				if(line.toUpperCase().contains("@DATA")) {
					wellFormed = true;
					break;
				}
			}

			if(wellFormed) {
				while ((line = br.readLine()) != null && currentRow < maxRows) {
					readedRow++;
					
					if (readedRow <= firstRow){
						continue;
					}
	
					String[] lineSplit = line.split("\\" + transfo.getSeparator() + "", -1);
	
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
	
						row.add(lineSplit[i]);
					}
	
					values.add(row);
					currentRow++;
				}
			}
			else {
				throw new Exception("The ARFF file is not well formed.");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			if (br != null) {
				br.close();
			}
		}

		return values;
	}

	public static List<List<Object>> getCountDistinctFieldValues(StreamElement field, FileOutputWeka stream) throws Exception {
		int colPos = stream.getDescriptor(null).getStreamElements().indexOf(field);
		List<List<Object>> values = new ArrayList<List<Object>>();

		BufferedReader br = null;
		try {
			Reader in = new InputStreamReader(((AbstractFileServer) stream.getServer()).getInpuStream(stream), stream.getEncoding());
			br = new BufferedReader(in);

			boolean wellFormed = false;
			
			String line = null;
			while ((line = br.readLine()) != null) {
				if(line.toUpperCase().contains("@DATA")) {
					wellFormed = true;
					break;
				}
			}

			if(wellFormed) {
				while ((line = br.readLine()) != null) {
					String[] lineSplit = line.split("\\" + stream.getSeparator() + "", -1);
	
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
			}
			else {
				throw new Exception("The ARFF file is not well formed.");
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			if (br != null) {
				br.close();
			}
		}

		return values;
	}
}
