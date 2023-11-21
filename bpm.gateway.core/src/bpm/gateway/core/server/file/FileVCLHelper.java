package bpm.gateway.core.server.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.inputs.FileInputVCL;


/**
 * Helper to get Metadata From a CSV file or get its values
 * @author LCA
 *
 */
public class FileVCLHelper {

	public static void createStreamDescriptor(FileInputVCL transfo, int numberRow) throws Exception{
		
		/*
		 * extract informations from the first line 
		 */
		
		try{

			DefaultStreamDescriptor desc = new DefaultStreamDescriptor(); 
			
			Reader in = new InputStreamReader(((AbstractFileServer)transfo.getServer()).getInpuStream(transfo), transfo.getEncoding());
			BufferedReader br = new BufferedReader(in);
			
			String line = br.readLine();
			
			
			
			
			
			List<String> headers = new ArrayList<String>();
			List<Integer> headersSz = new ArrayList<Integer>();
			
			String[] splited = line.split("[\\ ]");
			Integer sz = null;
			int count = -1;
			for(String s : splited){
				if (s.trim().equals("")){
					headersSz.set(count, headersSz.get(count) + 1);
				}
				else{
					headers.add(s);
					headersSz.add(s.length() + 1);
					count++;
				}
			}
			HashMap<String, StreamElement> structure = new HashMap<String, StreamElement>();
			
			int k = 0;
			for(String s : headers){
				StreamElement e = new StreamElement();
				e.name = s;
				e.transfoName = transfo.getName();
				e.originTransfo = transfo.getName();
				structure.put(s, e);

			}
			
			int counter = 0;
			while ((line = br.readLine()) != null && ++counter <= numberRow){
				int offset = 0;			
				for(int i = 0; i < headersSz.size(); i++){
					StreamElement  e = null;
					String _val = null;
					try{ 
						e = structure.get(headers.get(i));
						if (offset + headersSz.get(i) < line.length()){
							_val = line.substring(offset, offset + headersSz.get(i));
							offset += headersSz.get(i);
						}
						else{
							_val = line.substring(offset);
						}
						
						
						if (_val.trim().equals("")){
							continue;
						}
						
						
					}catch(Exception ex){
						ex.printStackTrace();
						continue;
					}
					
					_val = _val.trim();
					
					try{
						if (_val.startsWith("0")){
							e.className = "java.lang.String";
						}
						else{
							Integer.parseInt(_val);
							e.className = "java.lang.Integer";
							
						}
						continue;
					}catch(NumberFormatException ex){
						
					}
					
					try{
						Float.parseFloat(_val);
						e.className = "java.lang.Float";
						continue;
					}catch(NumberFormatException ex){
						try{
							Float.parseFloat(_val.replace(",", "."));
							e.className = "java.lang.Float";
							continue;
						}catch(NumberFormatException x){
							
						}
					}
					
					try{
						Double.parseDouble(_val);
						e.className = "java.lang.Double";
						continue;
					}catch(NumberFormatException ex){
						try{
							Double.parseDouble(_val.replace(",", "."));
							e.className = "java.lang.Double";
							continue;
						}catch(NumberFormatException x){
							
						}
					}
					
					
					
					try{
						if (isBoolean(_val)){
							e.className = "java.lang.Boolean";
							continue;
						}
												
					}catch(NumberFormatException ex){
						
					}
					
					e.className = "java.lang.String";
				}
				
				
				
			}

			k = 0;
			for(String s : headers){
				if (k < headers.size()){
					desc.addColumn(structure.get(s));
				}
				
				k++;
			}
			
			transfo.setDescriptor(desc);
			transfo.setColumnSizes(headersSz);
		}catch(Exception e){
			throw e;
			
		}

		
	}
	
	
	private static boolean isBoolean(String s){
		if (s == null || "true".equalsIgnoreCase(s.trim()) || "false".equalsIgnoreCase(s.trim())
				||
			"1".equalsIgnoreCase(s.trim()) || "0".equals(s.trim())){
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
	public static List<List<Object>> getValues(FileVCL transfo, int firstRow, int maxRows) throws Exception{
		int readedRow = 0;
		int currentRow = 0;
		
		List<List<Object>> values = new ArrayList<List<Object>>();
		BufferedReader br;
		try {
			Reader in = new InputStreamReader(((AbstractFileServer)transfo.getServer()).getInpuStream(transfo), transfo.getEncoding());
			br = new BufferedReader(in);			
			
			
			/*
			 * skip non valuable rows
			 */
			for(int i = 0; i  < transfo.getNumberOfRowToSkip(); i++){
				br.readLine();
			}
			
			String line = null;
			
			while ((line = br.readLine()) != null && currentRow < maxRows){
				readedRow ++;
				
				if (readedRow <= firstRow){
					continue;
				}
				
				List<Object> row = new ArrayList<Object>();
				
				int offset = 0;
				for(int i = 0; i < transfo.getDescriptor(null).getColumnCount(); i++){
					String _val = null;
					
					try{
						if (offset + transfo.getColumnSizes().get(i) < line.length()){
							_val = line.substring(offset, offset + transfo.getColumnSizes().get(i));
							offset += transfo.getColumnSizes().get(i)+1 ;
						}
						else{
							_val = line.substring(offset);
						}
						_val = _val.trim();
					}catch(Exception ex){
						row.add(null);
						continue;
					}
					
					
					if (_val.equals("")){
						row.add(null);
						continue;
					}
					
					
					try{
						if (_val.startsWith("0")){
							row.add(_val);
						}
						else{
							row.add(Integer.parseInt(_val));
						}
						
						continue;
					}catch(NumberFormatException ex){
						
					}
					
					try{
						row.add(Float.parseFloat(_val));
						continue;
					}catch(NumberFormatException ex){
						
					}
					
					try{
						row.add(Double.parseDouble(_val));
						continue;
					}catch(NumberFormatException ex){
						
					}
					
					
					
					try{
						if (isBoolean(_val)){
							row.add(Boolean.parseBoolean(_val));
							continue;
						}
												
					}catch(NumberFormatException ex){
						
					}
					
					
					row.add(_val);
				}
				
				values.add(row);
				currentRow++;
			}
			
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		
		
		return values;
	}


	public static List<List<Object>> getCountDistinctFieldValues(StreamElement field, FileVCL stream) throws Exception{
		int colPos = stream.getDescriptor(null).getStreamElements().indexOf(field);
		List<List<Object>> values = new ArrayList<List<Object>>();

		BufferedReader br;
		try {
			Reader in = new InputStreamReader(((AbstractFileServer)stream.getServer()).getInpuStream(stream), stream.getEncoding());
			br = new BufferedReader(in);			
					
			String line = null;
			
			int start = 0;
			int end = -1;
			
			for(int i = 0; i < colPos; i++){
				start += stream.getColumnSizes().get(i);
			}
			
			if (colPos != stream.getDescriptor(null).getStreamElements().size() - 1){
				end = start + stream.getColumnSizes().get(colPos);
			}
			
			/*
			 * skip non valuable rows
			 */
			for(int i = 0; i  < stream.getNumberOfRowToSkip(); i++){
				br.readLine();
			}
			
			while ((line = br.readLine()) != null && !"".equals(line.trim())){
				
				
				String value = null;
				
				if (end != -1){
					try{
						value = line.substring(start, end).trim();
					}catch(Exception ex){
						continue;
					}
				}
				else{
					value = line.substring(start).trim();
				}
				
				boolean present = false;
				for(List<Object> l : values){
					if (l.get(0).equals(value)){
						l.set(1, (Integer)l.get(1) + 1);
						present = true;
						break;
					}
				}
				
				if (!present){
					List<Object> l = new ArrayList<Object>();
					l.add(value);
					l.add(1);
					values.add(l);
				}
				
			}
			
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		
		return values;
	}
}
