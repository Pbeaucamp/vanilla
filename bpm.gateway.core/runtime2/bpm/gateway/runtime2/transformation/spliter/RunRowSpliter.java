package bpm.gateway.runtime2.transformation.spliter;

import bpm.gateway.core.transformations.RowsFieldSplitter;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunRowSpliter extends RuntimeStep{
	private int indexToSplit;
	private String spliter;
	private boolean trim = false;
	private boolean keepSplitedField = false;
	
	public RunRowSpliter(RowsFieldSplitter transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		
	}

	@Override
	public void init(Object adapter) throws Exception {
		RowsFieldSplitter split = (RowsFieldSplitter)getTransformation();
		
		spliter = split.getSplitSequence();
		indexToSplit = split.getInputFieldIndexToSplit();
		trim = split.isTrim();
		keepSplitedField = split.isKeepOriginalFieldInOuput();
		
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
		
		Object o = row.get(indexToSplit);
		
		
		
		if (o == null){
			Row newRow = null;
			if (keepSplitedField){
				newRow =  RowFactory.createRow(this, row);
			}
			else{
				newRow =  RowFactory.createRow(this, row, row.getMeta().getSize() - 1);;
			}
			
			writeRow(newRow);
		}
		else{
			String[] _splits = o.toString().split(spliter);
			for(String s : _splits){
				Row newRow = null;
				if (keepSplitedField){
					newRow =  RowFactory.createRow(this, row);
				}
				else{
					newRow =  RowFactory.createRow(this, row, row.getMeta().getSize() - 1);;
				}
				if (trim){
					newRow.set(newRow.getMeta().getSize() - 1, s.trim());
				}
				else{
					newRow.set(newRow.getMeta().getSize() - 1, s);
				}
				writeRow(newRow);
			}
			
		}
		
		
		
	}

	@Override
	public void releaseResources() {
		info(" ressources released");
		
	}

}
