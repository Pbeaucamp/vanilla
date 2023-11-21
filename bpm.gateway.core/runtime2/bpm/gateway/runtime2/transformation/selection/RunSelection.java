package bpm.gateway.runtime2.transformation.selection;

import java.util.List;

import bpm.gateway.core.transformations.SelectionTransformation;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunSelection extends RuntimeStep{
	
	private int[] columnIndices;
	
	
	public RunSelection(SelectionTransformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		
	}

	@Override
	public void init(Object adapter) throws Exception {
		SelectionTransformation select = (SelectionTransformation)getTransformation();
		if (select.getInputs().size() != 1){
			String message = " SelectionTransformation cannot have more or less than one Input at runtime";
			error(message);
			throw new Exception(message);
		}
		
		List<Integer> l = select.getOutputedFor(select.getInputs().get(0));
		columnIndices = new int[l.size()];
		
		for(int i = 0; i < columnIndices.length; i++){
			columnIndices[i] = l.get(i);
		}
		
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
			}catch(InterruptedException e){
				
			}
		}
		
		Row row = readRow();
		
		Row newRow = RowFactory.createRow(this);
		for(int i = 0; i < columnIndices.length; i++){
			newRow.set(i, row.get(columnIndices[i]));
		}
		
		writeRow(newRow);
		
	}

	@Override
	public void releaseResources() {
		info( " resources released");
		
	}

}
