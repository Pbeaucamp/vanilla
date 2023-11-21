package bpm.gateway.runtime2.transformation.spliter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.FieldSplitter;
import bpm.gateway.core.transformations.SplitedField;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunFieldSpliter extends RuntimeStep{

	private HashMap<SplitedField, List<Integer>> splitInfos = new HashMap<SplitedField, List<Integer>>();
	
	public RunFieldSpliter(FieldSplitter transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		
	}

	@Override
	public void init(Object adapter) throws Exception {
		FieldSplitter spliter = (FieldSplitter)getTransformation();
		StreamDescriptor desc = spliter.getDescriptor(spliter);
		for(SplitedField f : spliter.getSplitedFields()){
			splitInfos.put(f, new ArrayList<Integer>());
			
			for(StreamElement e : f.getStreamElements()){
				splitInfos.get(f).add(desc.getStreamElements().indexOf(e));
			}
			f.setSplitedIndex(desc.getStreamElements().indexOf(f.getSplited()) + "");
		}
		
		
		info( " inited");
		
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
		
		Row newRow = RowFactory.createRow(this, row);
		
		for(SplitedField sf : splitInfos.keySet()){
			if (row.get(sf.getSplitedIndex()) != null){
				String[] splitedValues = row.get(sf.getSplitedIndex()).toString().split(sf.getSpliter());
				
				int splitIndex = 0;
				for(Integer i : splitInfos.get(sf)){
					if (splitIndex < splitedValues.length){
						newRow.set(i, splitedValues[splitIndex++]);
					}
				}
			}
		}
		
		writeRow(newRow);
		
	}

	@Override
	public void releaseResources() {
		info(" ressources released");
		
	}

}
