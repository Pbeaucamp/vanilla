package bpm.gateway.runtime2.transformation.sorting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class Sorter {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String baseFileName;
	private String separator;
	private int fileSize;
	private Comparator<Row> comparator;
	
	private List<File> files = new ArrayList<File>();
	
	
	private List<SorterReader> readers = new ArrayList<SorterReader>();
	private boolean readersReady = false;
	private List<Row> datas = new ArrayList<Row>();
	private RuntimeStep step;
	
	private Logger logger;
	
	/**
	 * 
	 * @param fileSize
	 * @param comparator
	 * @param step
	 */
	public Sorter(int fileSize, Comparator<Row> comparator, RuntimeStep step, Logger logger){
		this.baseFileName =step.getTransformation().getTemporaryFilename();
		this.separator = step.getTransformation().getTemporarySpliterChar() + "";
		this.fileSize = fileSize;
		this.comparator = comparator;
		this.step = step;
		this.logger = logger;
		
		logger.info(step.getName() + " sorter created");
	}
	
	
	public void stopWriting() throws Exception{
		if (datas.size() != 0){
			Collections.sort(datas, comparator);
			writeFile();
			datas.clear();
		}
	}
	
	
	public void addRow(Row row) throws Exception{
		datas.add(row);
		if (datas.size() >= fileSize){
			Collections.sort(datas, comparator);
			writeFile();
			datas.clear();
		}
		
	}
	
	/**
	 * write the datas in a file
	 * @throws Exception
	 */
	private void writeFile() throws Exception{
		
		PrintWriter writer = null;
		File f = new File(baseFileName + files.size());
		if (!f.exists()){
			f.createNewFile();
		}
		logger.info(step.getName() + " sorter created file " + f.getName());
		
		writer = new PrintWriter(f);
		logger.info(step.getName() + " sorter opened file " + f.getName() + " in writing mode");
		files.add(f);
		
		
		
		for(Row row : datas){
			
			StringBuffer buf = new StringBuffer();

			boolean first = true;
			for(Object o : row){
				if (first){
					first = false;
				}
				else{
					buf.append(separator);
				}
				if (o == null){
					continue;
				}
				else if (o instanceof Number){
					if (o instanceof BigDecimal){
						buf.append(((BigDecimal)o).toPlainString());
					}
					else{
						buf.append(o.toString());
					}
					
					
				}
				else if (o instanceof Date){

					try{
						buf.append(sdf.format((Date)o));
					}catch(Exception e){
						
						throw e;
					}

						
				}
				else if (o instanceof String){
					buf.append((String)o);
				}
				
			}
			buf.append(separator + "\r\n");
			writer.write(buf.toString());
			
		}
		
		writer.close();
		logger.info(step.getName() + " sorter closed file " + f.getName() + " in writing mode");
	}

	
		
	
	public Row getMinRow() throws Exception{
		if (!readersReady){
			initReaders();
		}
		
		boolean readersClosed = true;
		for(SorterReader o : readers){
			if (!o.closed){
				readersClosed = false;
				break;
			}
		}
		
		if (readersClosed){
			
			return null;
		}
		for(SorterReader o : readers){
			
			if (o.have2Read && ! o.closed){
				
				Row r  = readLine(o.reader);
				if (r == null){
					o.closed = true;
					o.current = null;
					o.reader.close();
					
				}else{
					o.current = r;
					o.have2Read = false;
				}
			}
		}
		HashMap<Row, SorterReader> rows = new HashMap<Row, SorterReader>();
		for(SorterReader o : readers){
			if (o.current != null){
				rows.put(o.current, o);
			}
			
		}
		
		if (rows.isEmpty()){
			return null;
		}
		
		Collection c = rows.keySet();
		Row min = Collections.min(rows.keySet(), comparator);
		
		SorterReader r = rows.get(min);
		r.have2Read = true;
		
		return min;
		
	}
	
	
	private Row readLine(BufferedReader br) throws Exception{
		String line = br.readLine();
		if (line == null){
			return null;
		}
		Row row = RowFactory.createRow(step);
		String[] values = line.split(separator, -1);
		
		for(int i = 0; i < values.length - 1; i++){
			try {
				Class<?> c = row.getMeta().getJavaClasse(i);
				
				if ("".equals(values[i]) || values[i] == null){
					continue;
				}
				if (Date.class.isAssignableFrom(c)){
	
						try{
							row.set(i, sdf.parseObject(values[i]));
						}catch(Exception ex){
						}
	
					
				}
				else if (Byte.class.isAssignableFrom(c)){
					row.set(i, new Byte(values[i]));
				}
				else if (Short.class.isAssignableFrom(c)){
					row.set(i, new Short(values[i]));
				}
				else if (Integer.class.isAssignableFrom(c)){
					row.set(i, new Integer(values[i]));
				}
				else if (Long.class.isAssignableFrom(c)){
					row.set(i, new Long(values[i]));
				}
				else if (BigInteger.class.isAssignableFrom(c)){
					row.set(i, new BigInteger(values[i]));
				}
				else if (Float.class.isAssignableFrom(c)){
					row.set(i, new Float(values[i]));
				}
				else if (Double.class.isAssignableFrom(c)){
					row.set(i, new Double(values[i]));
				}
				else if (BigDecimal.class.isAssignableFrom(c)){
					row.set(i, new BigDecimal(values[i]));
				}else if (String.class.isAssignableFrom(c)){
					row.set(i, values[i]);
				}
				else{
					logger.warn(step.getName() + " Sorter read an unknown type value, replaced by a String with value :" + values[i]);
					row.set(i, values[i]);
				}
			} catch (Exception e) {
				
			}
		}
		return row;
	}
	
	private void initReaders() throws Exception{
		if (!datas.isEmpty()){
			Collections.sort(datas, comparator);
			writeFile();
		}
		
		for(File f : files){
			BufferedReader br = new BufferedReader(new FileReader(f));
			SorterReader r = new SorterReader();
			r.reader = br;
			r.have2Read = true;
			r.closed = false;
			readers.add(r);
		}
		
		readersReady = true;
	}
	
	public void releaseResources(){
		for(SorterReader r : readers){
			try{
				r.reader.close();
			}catch(Exception e){
				
			}
			r.current = null;
		}
		
		readers.clear();
		
		for(File f : files){
			if (f.delete()){
				logger.info(step.getName() + " Sorter close file : " + f.getAbsolutePath());
			}
		}
		datas.clear();
		datas = null;
		files.clear();
		files = null;
		
	}
	
	private class SorterReader{
		BufferedReader reader;
		boolean have2Read;
		boolean closed;
		Row current;
	}
}
