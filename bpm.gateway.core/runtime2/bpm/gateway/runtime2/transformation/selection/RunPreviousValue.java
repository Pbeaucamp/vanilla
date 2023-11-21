package bpm.gateway.runtime2.transformation.selection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import bpm.gateway.core.transformations.utils.PreviousValueTransformation;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunPreviousValue extends RuntimeStep{

	private List<Integer> keyIndexes = new ArrayList<Integer>();
	private List<Integer> previousIndexes = new ArrayList<Integer>();
	
	private PreviousValueTransformation transformation;
	
	private HashMap<ValueKey, HashMap<Integer, Object>> previousValues;
	
	private int originalRowSize;
	
	public RunPreviousValue(PreviousValueTransformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		this.transformation = transformation;
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
			}catch(InterruptedException e){
				
			}
		}
		
		Row row = readRow();
		
		Row newRow = RowFactory.createRow(this);
		
		//create the key object
		List<Object> keyObjects = new ArrayList<Object>();
		for(int i : keyIndexes) {
			keyObjects.add(row.get(i));
		}
		ValueKey key = new ValueKey(keyObjects);
		
		//check if the key exists
		HashMap<Integer, Object> prevValue = previousValues.get(key);
		if(prevValue == null) {
			prevValue = new HashMap<>();
		}
		
		//create the new previous value
		HashMap<Integer, Object> newPrevValue = new LinkedHashMap<>();
		for(int i : previousIndexes) {
			newPrevValue.put(i, row.get(i));
		}
		previousValues.put(key, newPrevValue);
		
		//create the row
		for(int i = 0 ; i < originalRowSize ; i++) {
			newRow.set(i, row.get(i));
		}
		for(int i = originalRowSize ; i < previousIndexes.size() + originalRowSize ; i++) {
			Object o = prevValue.get(previousIndexes.get(i - originalRowSize));
			newRow.set(i, o);
		}
		
		writeRow(newRow);
	}

	@Override
	public void releaseResources() {
		keyIndexes = null;
		previousIndexes = null;
		previousValues = null;
		originalRowSize = 0;
	}

	@Override
	public void init(Object adapter) throws Exception {
		keyIndexes = transformation.getKeyIndexes();
		previousIndexes = transformation.getPreviousIndexes();
		
		previousValues = new HashMap<>();
		
		originalRowSize = transformation.getOriginalDescriptor().getColumnCount();
	}

	private class ValueKey {
		private List<Object> keys;
		
		public ValueKey(List<Object> keys) {
			this.keys = keys;
		}
		
		@Override
		public boolean equals(Object obj) {
			
			ValueKey objKeys = (ValueKey) obj;
			for(int i = 0 ; i < keys.size() ; i++) {
				Object key = keys.get(i);
				Object objKey = objKeys.getKeys().get(i);
				if(!key.equals(objKey)) {
					return false;
				}
			}
			
			return true;
		}
		
		@Override
		public int hashCode() {
			return 1;
		}
		
		public List<Object> getKeys() {
			return keys;
		}
	}
	
}
