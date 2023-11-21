package bpm.gateway.runtime2.transformation.normalisation;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.transformations.normalisation.Denormalisation;
import bpm.gateway.core.transformations.normalisation.NormaliserField;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunDenormalize extends RuntimeStep{
	private List<NormaliserField> fields;
	private List<Integer> groupByIndex;
	private Integer keyFieldIndex;
	private List<Object[]> pivotKeys = new ArrayList<Object[]>();
	
	public RunDenormalize(Denormalisation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		
	}

	@Override
	public void init(Object adapter) throws Exception {
		Denormalisation denorm = (Denormalisation)getTransformation();
		fields = denorm.getFields();
		groupByIndex = denorm.getGroupFieldIndex();
		keyFieldIndex = denorm.getInputKeyField();
		isInited = true;
		info(" inited");
		
	}

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
		
		Object[] key = extractPivotKey(row);
		
//		if (addPivotKey(key)){
			Row newrow = RowFactory.createRow(this);
			
			int pos = 0;
			boolean match = false;
			for(NormaliserField f : fields){
				if (f.getValue().equals(row.get(keyFieldIndex).toString())){
					newrow.set(pos++, row.get(f.getInputFieldValueIndex()));
					match = true;
				}
				else{
					newrow.set(pos++, null);
				}
				
			}
			if(!match){
				return;
			}
			for(Object o : key){
				newrow.set(pos++, o);
			}
			
			writeRow(newrow);
//		}
		
	}

	private Object[] extractPivotKey(Row row){
		Object[] k = new Object[groupByIndex.size()];
		
		int p  = 0;
		for(Integer i : groupByIndex){
			k[p++] = row.get(i);
		}
		return k;
	}
	
	/**
	 * 
	 * @param key
	 * @return true if the ey is nex and add it, return false and do nothing if keys already exists
	 */
	private boolean addPivotKey(Object[] key){
		boolean match = !pivotKeys.isEmpty();
		
		for(Object[] _k : pivotKeys){
			match = false;
			for(int i = 0; i < key.length; i++){
				if ((_k[i] ==  null && key[i] == null) ||
					(_k[i] !=  null && _k[i].equals(key[i])) ||
					(key[i] !=  null && key[i].equals(_k[i])) ){
					match = true;
					continue;
				}
				else{
					match = false;
					break;
				}
			}
			if (match){
				break;
			}
		}
		
		if (match){
			return false;
		}
		
		pivotKeys.add(key);
		return true;
	}
	
	@Override
	public void releaseResources() {
		fields = null;
		groupByIndex = null;
		pivotKeys.clear();
		pivotKeys = null;
		info(" resources released");
	}

}
