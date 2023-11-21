package bpm.gateway.runtime2.transformation.sequence;

import java.util.List;

import bpm.gateway.core.transformations.SurrogateKey;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.vanilla.platform.core.utils.MD5Helper;

import com.ibm.icu.text.SimpleDateFormat;

public class RunSurrogateKey extends RuntimeStep {
//	private long min = 0;
//	private long max = 0;
//	private long offset = 0;
//	
//	private FileOutputStream fos;
//	private File file;
//	private boolean backuped = false;
//	private int bufSize;
//	private HashMap<Object[], Long> generatedKeys;
	private List<Integer> keyIndex;
	
//	private FileInputCSV dummyInput;
	
//	private Long lastKey;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public RunSurrogateKey(SurrogateKey transformation, int bufferSize) {
		super(null, transformation, bufferSize);
//		bufSize = bufferSize;
//		generatedKeys = new   HashMap<Object[], Long>(bufferSize);
	}
	
	@Override
	public void init(Object adapter) throws Exception {
		SurrogateKey seq = (SurrogateKey) getTransformation();

		keyIndex = seq.getFieldKeys();
//		
//		file = new File(StringParser.getValue(seq.getDocument(), seq.getTemporaryFilename()));
//		dummyInput = new FileInputCSV();
//		dummyInput.setDefinition(file.getAbsolutePath());
//		dummyInput.setSeparator(seq.getTemporarySpliterChar());
//		
//		lastKey = min - 1;
		
		info(" inited");
	}
	
	
	private Object[] extractKey(Row r){
		Object[] l = new Object[keyIndex.size()];
		int count = 0;
		for(Integer i : keyIndex){
			l[count++] = r.get(i);
		}
		
		return l;
	}
	
//	/**
//	 * return null or the generated SurrogateKey for the given Key value
//	 * @param key
//	 * @return
//	 */
//	private Long getSurrogateKey(Object[] key){
//		for(Object[] k : generatedKeys.keySet()){
//			
//			boolean found = true;
//			for(int i = 0; i < keyIndex.size(); i++){
//				
//				if (k[i] == null ){
//					if (key[i] != null){
//						found = false;
//						break;
//					}
//				}
//				else if (key[i] == null){
//					found = false;
//					break;
//				}
//				else{
//					if (!key[i].equals(k[i])){
//						found = false;
//						break;
//					}
//				}
//			}
//			
//			if (found){
//				return generatedKeys.get(k);
//			}
//		}
//		
//		return null;
//	}
	
//	/**
//	 * return null or the generated SurrogateKey for the given Key value
//	 * @param key
//	 * @return
//	 * @throws IOException 
//	 */
//	private Long getSurrogateKeyFromBackup(Object[] key) throws IOException{
//		Reader in = new InputStreamReader(new FileInputStream(file));
//		BufferedReader br  = new BufferedReader(in);		
//		String line = null;
//		Long surrogateKey = null;
//		
//		while ((line = br.readLine()) != null && surrogateKey == null){
//			
//			String[] lineSplit = null;
////			try{
//				lineSplit = line.split(getTransformation().getTemporarySpliterChar() + "", -1);
////			}catch(Exception ex){
////				ex.printStackTrace();
////				continue;
////			}
//			Object[] row = new Object[key.length + 1];
//
//			for(int i = 0; i < (key.length + 1 ); i++){
//				Object o = null;
////				try{
////					o = Integer.parseInt(lineSplit[i]);
////					row[i] = o;
////					continue;
////				}catch(NumberFormatException ex){
////					
////				}
//				
//				try{
//					o = Long.parseLong(lineSplit[i]);
//					row[i] = o;
//					continue;
//				}catch(NumberFormatException ex){
//					
//				}
//				
//				try{
//					o = Float.parseFloat(lineSplit[i]);
//					row[i] = o;
//					continue;
//				}catch(NumberFormatException ex){
//					
//				}
//				
//				try{
//					o = Double.parseDouble(lineSplit[i]);
//					row[i] = o;
//					continue;
//				}catch(NumberFormatException ex){
//					
//				}
//				
//				
//				
////				try{
////					o = Boolean.parseBoolean(lineSplit[i]);
////					row[i] = o;
////					continue;
////				}catch(NumberFormatException ex){
////					
////				}
////				
//				
//				o = lineSplit[i];
//				row[i] = o;
//			}
//			boolean found = true;
//			for(int i = 0; i < keyIndex.size(); i++){
//				if (row[i] == null ){
//					if (key[i] != null){
//						found = false;
//						break;
//					}
//				}
//				else if (key[i] == null){
//					found = false;
//					break;
//				}
//				else{
//					if (!key[i].equals(row[i])){
//						found = false;
//						break;
//					}
//				}
//			}
//			
//			if (found){
//				surrogateKey = (Long)row[keyIndex.size()];
//			}
//		}
//		
//		br.close();
//		return surrogateKey;
//	}
	
	
	@Override
	public void performRow() throws Exception {
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
		
		if (isEnd() && inputEmpty()){
			return;
		}
		
		if (!isEnd() && inputEmpty()){
			try{
				Thread.sleep(10);
				return;
			}catch(Exception e){
				
			}
		}
		
		Row row = readRow();
		
		Row newRow = RowFactory.createRow(this, row);

		Object[] key = extractKey(row);
		
		StringBuffer buf = new StringBuffer();
		
		for(Object o : key){
			if ( o != null){
				buf.append("" + o);
			}
			
		}
		
		String surrogateKey = MD5Helper.encode(buf.toString());
		
//		if (surrogateKey == null && backuped){
//			surrogateKey = getSurrogateKeyFromBackup(key);
//		}
//		
//		if (surrogateKey == null){
//			lastKey++;
//			generatedKeys.put(key, lastKey.longValue());
//			
//			if (generatedKeys.size() >= bufSize){
//				backup();
//			}
//			surrogateKey = lastKey.longValue();
//		}
		newRow.set(newRow.getMeta().getSize() - 1, surrogateKey);
		
		writeRow(newRow);
		
	}

	@Override
	public void releaseResources() {
//		if (file != null && file.exists()){
//			file.delete();
//		}
		if (keyIndex != null){
			keyIndex.clear();
		}
		keyIndex = null;
		info( " released");
		
	}
	
//	private void backup() throws Exception{
//		debug("Backup keys in temporary file");
//		
//		FileOutputStream fos = new FileOutputStream(file, true);
//		PrintWriter pw = new PrintWriter(fos);
//		
//		for(Object[] k : generatedKeys.keySet()){
//			StringBuffer buf = new StringBuffer();
//			for(int i = 0; i < k.length; i++){
//				if (i != 0){
//					buf.append(getTransformation().getTemporarySpliterChar());
//				}
//				if (k[i] == null){
//					
//				}
//				else if (k[i] instanceof Date){
//					try{
//						buf.append(sdf.format((Date)k[i]));
//					}catch(Exception ex){
//						
//					}
//				}
//				else{
//					buf.append(k[i].toString());
//				}
//			}
//			buf.append("" + getTransformation().getTemporarySpliterChar() + generatedKeys.get(k) + ""+ getTransformation().getTemporarySpliterChar() + "\n");
//			
//			pw.write(buf.toString());
//		}
//		pw.close();
//		backuped = true;
//	}
}
